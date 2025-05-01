package com.mysociety.authentication.filters;


import com.mysociety.authentication.dto.response.ApiResponse;
import com.mysociety.authentication.dto.response.ResponseWriter;
import com.mysociety.authentication.jwtauth.JWTService;
import com.mysociety.authentication.token.OpaqueToken;
import com.mysociety.authentication.token.TokenService;
import com.mysociety.authentication.entities.CustomUserPrincipal;
import com.mysociety.authentication.services.UserAuthService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        try{
            String token = authHeader.substring(7);
            OpaqueToken opaqueToken = tokenService.getTokenDetails(token);
            if(opaqueToken == null){
                ResponseWriter.writeJson(
                        response,
                        HttpStatus.UNAUTHORIZED,
                        new ApiResponse<>("invalid token", null)
                );
                return;
            }
            //getting jwt token from opaque token
            token = opaqueToken.getTokenValue();
            final String username = jwtService.extractUsername(token);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(username != null && auth == null){
                CustomUserPrincipal userDetails = userAuthService.loadUserByUsername(username);
                userDetails.setToken(opaqueToken.getTokenAlias());
                if(jwtService.isTokenValid(token, userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new
                            UsernamePasswordAuthenticationToken(userDetails, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("user authenticated");
                }
            }
            filterChain.doFilter(request, response);
        }catch (MalformedJwtException e ){
            ResponseWriter.writeJson(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    new ApiResponse<>("invalid token", null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWriter.writeJson(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    new ApiResponse<>("invalid token", null)
            );
        }
    }
}
