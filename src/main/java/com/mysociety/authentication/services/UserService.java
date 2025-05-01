package com.mysociety.authentication.services;

import com.mysociety.authentication.dto.request.RegisterUserRequest;
import com.mysociety.authentication.dto.shared.SocietyDTO;
import com.mysociety.authentication.entities.CustomUserPrincipal;
import com.mysociety.authentication.enums.Role;
import com.mysociety.authentication.exceptions.AuthenticationFailedException;
import com.mysociety.authentication.exceptions.InvalidRequestFieldException;
import com.mysociety.authentication.exceptions.OtpValidationException;
import com.mysociety.authentication.exceptions.UserAlreadyExistException;
import com.mysociety.authentication.repositories.UserAuthRepository;
import com.mysociety.authentication.utils.Auth;
import com.mysociety.authentication.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisTemplate redisTemplate;


    public void registerUser(RegisterUserRequest request){
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if(user.getName() != null && !user.getName().isBlank()){
//            logger.info("user existed for " + request.getPhoneNumber());
            throw new UserAlreadyExistException("user already registered");
        }
        Map<String, Object> updateMap = new HashMap<>();
        //get society data from Society service using rest
        //start
        SocietyDTO society = SocietyDTO.builder()
                .societyName("XYZ apartments")
                .societyCity("ghaziabad")
                .societyAddress("Plot C1, Shalimar Garden Ext - 2")
                .societyState("Uttar Pradesh")
                .build();
        //end
        if(society != null){
            if(request.getFlatNo() == null){
                throw new InvalidRequestFieldException("society provided but flat no not provided");
            }else{
                updateMap.put("flat_no", request.getFlatNo());
            }
            updateMap.put("roles", List.of(Role.RESIDENT) );
            updateMap.put("society_id", request.getSocietyId());
            updateMap.put("society_name", society.getSocietyName());
            updateMap.put("block_no", request.getBlockNo());
        }
        if(request.getEmail() != null) updateMap.put("email", request.getEmail());
        updateMap.put("name", request.getName());
        userAuthRepository.updateUserFieldsById(user.getId(), updateMap);
    }

    public void updateUserDetails(String id, Map<String, Object> fieldMap){
        if(fieldMap.get("society_id") != null){
            // request society details
            SocietyDTO societyDTO = new SocietyDTO();
            if(societyDTO != null){
                if(fieldMap.get("flatNo") == null){
                    throw new InvalidRequestFieldException("society id provide but not flat no");
                }
            }
        }
        userAuthRepository.updateUserFieldsById(id, fieldMap);
    }

    public void generateOtpForNewPhoneNumber(String id, String newPhoneNumber){
        String key = "update:phone_number:" + id +":" + newPhoneNumber;
        String otp = Auth.generateOtp();
        //opt send to user
        System.out.println("Phone number : "  + newPhoneNumber  + " : " + otp);
        //end
        otp = bCryptPasswordEncoder.encode(otp);
        String value = (String) redisTemplate.opsForValue().get(key);
        int attempts = 0;
        if(value != null){
            attempts = Integer.parseInt(value.split(":")[0]);
            if((Constants.MAX_OTP_ATTEMPT - attempts) < 1){
                throw new OtpValidationException("otp request limit reached, try after sometime");
            }
        }
        attempts++;
        otp = attempts + ":" + otp;
        redisTemplate.opsForValue().set(key, otp, Duration.ofMillis(Constants.getOtpExpirationTime()));
    }
    public void verifyOtpForNewPhoneNumber(String id, String newPhoneNumber, String otp){
        String key = "update:phone_number:" + id +":" + newPhoneNumber;
        String otpHash = (String)redisTemplate.opsForValue().get(key);
        otpHash = otpHash.split(":")[1];
        if(otpHash == null){
            throw new OtpValidationException("update phone number request not initiated");
        }
        if(!bCryptPasswordEncoder.matches(otp, otpHash)){
            throw new AuthenticationFailedException("invalid otp");
        }
        userAuthRepository.updateUserFieldsById(id, Map.of("phone_number", newPhoneNumber));
    }

}
