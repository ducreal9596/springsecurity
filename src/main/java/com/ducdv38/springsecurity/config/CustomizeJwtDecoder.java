package com.ducdv38.springsecurity.config;

import com.ducdv38.springsecurity.dto.request.IntrospectRequest;
import com.ducdv38.springsecurity.service.impl.AuthenticationService;
import com.ducdv38.springsecurity.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomizeJwtDecoder implements JwtDecoder {
    @Value("${jwt.key}")
    private String key;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
           var response =  authenticationService.introspect(IntrospectRequest.builder()
                    .token(token)
                    .build());
           if(!response.isValid()){
               throw new BadJwtException("invalid token");
           }
        } catch (Exception e) {
            throw new BadJwtException (e.getMessage());
        }
        if (Objects.isNull(nimbusJwtDecoder)) {
            synchronized (this) {
                if (Objects.isNull(nimbusJwtDecoder)) {
                    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), Constants.HS_512);
                    nimbusJwtDecoder = NimbusJwtDecoder
                            .withSecretKey(secretKeySpec)
                            .macAlgorithm(MacAlgorithm.HS512)
                            .build();
                }
            }
        }
        System.out.println(nimbusJwtDecoder);
        return nimbusJwtDecoder.decode(token);
    }
}
