package com.mysociety.authentication.services;

import com.mysociety.authentication.dto.request.RegisterUserRequest;
import com.mysociety.authentication.dto.shared.SocietyDTO;
import com.mysociety.authentication.entities.CustomUserPrincipal;
import com.mysociety.authentication.entities.User;
import com.mysociety.authentication.enums.Role;
import com.mysociety.authentication.exceptions.AuthenticationFailedException;
import com.mysociety.authentication.exceptions.InvalidRequestFieldException;
import com.mysociety.authentication.exceptions.OtpValidationException;
import com.mysociety.authentication.exceptions.UserAlreadyExistException;
import com.mysociety.authentication.jwtauth.JWTService;
import com.mysociety.authentication.repositories.UserAuthRepository;
import com.mysociety.authentication.token.OpaqueToken;
import com.mysociety.authentication.token.TokenRepository;
import com.mysociety.authentication.utils.Auth;
import com.mysociety.authentication.utils.Constants;
import com.mysociety.authentication.utils.TokenGenerator;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class UserAuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    public void generateOtpForUser(String phoneNumber){
        //check for user in db
        Optional<User> optionalUser=
                userAuthRepository.findByPhoneNumber(phoneNumber);
        String otp = Auth.generateOtp();
        System.out.println("Otp for " + phoneNumber + "  : " + otp);
        otp = bCryptPasswordEncoder.encode(otp);
        String value;
        int attempts=0;
        String[] values;
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            value = user.getToken();
            if(value != null){
                values = value.split(":");
                attempts = (Integer.parseInt(values[0]));
                if( (Constants.MAX_OTP_ATTEMPT - attempts) < 1){
                    throw new OtpValidationException("otp request limit exceeded, try after sometime");
                }
            }
            attempts++;
            otp = attempts+":"+otp;
            userAuthRepository.updateUserTokenByPhoneNumber(phoneNumber, otp, Constants.getOtpExpirationTime());
        }else{
            String key = "new_user:" + phoneNumber;
            value = (String)redisTemplate.opsForValue().get(key);
            if(value != null){
                values = value.split(":");
                attempts = (Integer.parseInt(values[0]));
                if( (Constants.MAX_OTP_ATTEMPT - attempts) < 1){
                    throw new OtpValidationException("otp request limit exceeded, try after sometime");
                }
            }
            attempts++;
            redisTemplate.opsForValue().set(
                    key,
                    attempts + ":" + otp,
                    Duration.ofMillis(Constants.getOtpExpirationTime()));
        }
    }



    @Override
    public CustomUserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userAuthRepository.findById(username).get();
        return CustomUserPrincipal.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .societyName(user.getSocietyName())
                .societyId(user.getSocietyId())
                .flatNo(user.getFlatNo())
                .blockNo(user.getBlockNo())
                .roles(user.getRoles())
                .build();
    }

    public String verifyUserOtpThenLogin(String phoneNumber,
                                         String otp,
                                         String countryCode,
                                         HttpServletRequest request) {
        Optional<User> optionalUser=
                userAuthRepository.findByPhoneNumber(phoneNumber);
        String encodedOtp;
        if(optionalUser.isPresent()){
            if(optionalUser.get().getToken() != null && !optionalUser.get().getToken().isBlank()){
                encodedOtp = optionalUser.get().getToken().split(":")[1];
                if(optionalUser.get().getTokenExpiredAt()
                        .before(new Date(System.currentTimeMillis())))
                {
                    throw new AuthenticationFailedException("otp expired, request again");
                }
            }else{
                throw new AuthenticationFailedException("otp not request for this user");
            }
        }else{
            String key = "new_user:" + phoneNumber;
            Object valObj = redisTemplate.opsForValue().get(key);
            if(valObj == null){
                throw new AuthenticationFailedException("otp not request for this user");
            }else{
                encodedOtp = valObj.toString().split(":")[1];
            }
        }
        System.out.println("hash otp : " + encodedOtp);
        if(!bCryptPasswordEncoder.matches(otp, encodedOtp )){
            throw new AuthenticationFailedException("invalid otp");
        }
        User user;
        if(optionalUser.isPresent()){
            user = optionalUser.get();
            userAuthRepository.refreshTokenByPhoneNumber(user.getPhoneNumber());
        }else{
            redisTemplate.opsForValue().getAndDelete("user@" + phoneNumber);
             user= User.builder()
                                    .phoneNumber(phoneNumber)
                                    .countryCode(countryCode)
                                    .createdAt(new Date(System.currentTimeMillis()))
                                    .build();
            userAuthRepository.save(user);
            String info = "phoneNumber : " + phoneNumber + " user created !!";
            logger.info(info);
        }
        return generateJwtAndRefToken(user.getId(), request);
    }

    public String loginUser(String username, String password){
        Optional<User> user =
                userAuthRepository.findByEmail(username);
        if(user.isEmpty()) return null;
        if(bCryptPasswordEncoder.matches(password, user.get().getPassword())){
            return "";
        }
        return null;
    }


    public void updateDetails(String username, String key, Object value){
        Query query = Query.query(Criteria.where("email").is(username));
        Update update = new Update().set(key, value);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    public String generateJwtAndRefToken(String username, HttpServletRequest request){
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);

        String os = userAgent.getOperatingSystem().getName();
        String ipAddress = request.getRemoteAddr();

        String token = jwtService.generateToken(null, username);
        String opaqueTokenString = TokenGenerator.generateUUIDToken();
        OpaqueToken opaqueToken = OpaqueToken.builder()
                .tokenAlias(opaqueTokenString)
                .tokenValue(token)
                .createdAt(new Date(System.currentTimeMillis()))
                .issuedBy("AUTH_SERVICE")
                .isActive(true)
                .ipAddress(ipAddress)
                .deviceType(os)
                .build();
        tokenRepository.save(opaqueToken);
        return opaqueTokenString;
    }




}
