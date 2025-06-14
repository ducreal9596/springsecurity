package com.ducdv38.springsecurity.service.impl;

import com.ducdv38.springsecurity.dto.request.AuthenticationRequest;
import com.ducdv38.springsecurity.dto.request.IntrospectRequest;
import com.ducdv38.springsecurity.dto.request.LogoutRequest;
import com.ducdv38.springsecurity.dto.request.RefreshRequest;
import com.ducdv38.springsecurity.dto.response.AuthenticationResponse;
import com.ducdv38.springsecurity.dto.response.IntrospectResponse;
import com.ducdv38.springsecurity.entity.InvalidateToken;
import com.ducdv38.springsecurity.entity.User;
import com.ducdv38.springsecurity.exceptionhandle.AppException;
import com.ducdv38.springsecurity.exceptionhandle.ErrorCode;
import com.ducdv38.springsecurity.repository.InvalidateRepository;
import com.ducdv38.springsecurity.repository.UserRepository;
import com.ducdv38.springsecurity.utils.Constants;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    @NonFinal
    @Value("${jwt.key}")
    protected String key;

    @NonFinal
    @Value("${jwt.validDuration}")
    protected long accessTokenValidDuration;  // seconds

    @NonFinal
    @Value("${jwt.refreshableDuration}")
    protected long refreshTokenValidDuration; // seconds

    InvalidateRepository invalidateTokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    // ======================= Authenticate: trả về access + refresh token =======================
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String accessToken = generateToken(user, Constants.EXPECT_TOKEN_ACCESS, accessTokenValidDuration);
        String refreshToken = generateToken(user, Constants.EXPECT_TOKEN_REFRESH, refreshTokenValidDuration);
        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .isAuthenticated(true)
                .build();
    }

    // ======================= Kiểm tra token (access hoặc refresh) =======================
    public SignedJWT verifyToken(String token, String expectedTokenType) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(key.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Verify signature
        boolean verified = signedJWT.verify(verifier);
        if (!verified) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Kiểm tra token type claim
        String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("token_type");
        if (tokenType == null || !tokenType.equals(expectedTokenType)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Kiểm tra thời hạn hết hạn
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime == null || !expiryTime.after(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Kiểm tra token đã bị invalidate chưa. nếu trong table invalidToken tồn tại jti thì invalid
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        if (invalidateTokenRepository.existsById(jti)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    // ======================= Introspect token =======================
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
        boolean isValid = true;
        try {
            // Chỉ cần kiểm tra access token
            verifyToken(introspectRequest.getToken(), Constants.EXPECT_TOKEN_ACCESS);
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    // ======================= Logout (invalidate token) =======================
    public void logout(LogoutRequest logoutRequest) {
        try {
            var signedToken = SignedJWT.parse(logoutRequest.getToken());
            String jti = signedToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();

            InvalidateToken invalidateToken = InvalidateToken.builder()
                    .id(jti)
                    .expiryTime(expiryTime)
                    .build();
// (STR) 2025-06-14 K23-840 DEV DucDV38 MOD Category18
// invalidateTokenRepository.save(invalidateToken);
                      invalidateTokenRepository.save(invalidateToken);
// (END) 2025-06-14 K23-840 DEV DucDV38 MODCategory18
        } catch (Exception e) {
            log.info("Token already expired or invalid");
        }
    }

    // ======================= Refresh token =======================
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // 1. Xác thực refresh token
        SignedJWT refreshJwt = verifyToken(request.getToken(), Constants.EXPECT_TOKEN_REFRESH);

        // 2. Invalidate refresh token cũ
        String jti = refreshJwt.getJWTClaimsSet().getJWTID();
        Date expiryTime = refreshJwt.getJWTClaimsSet().getExpirationTime();
        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();
        invalidateTokenRepository.save(invalidateToken);

        // 3. Tạo access token và refresh token mới
        String username = refreshJwt.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        String newAccessToken = generateToken(user, Constants.EXPECT_TOKEN_ACCESS, accessTokenValidDuration);
        String newRefreshToken = generateToken(user, Constants.EXPECT_TOKEN_REFRESH, refreshTokenValidDuration);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .isAuthenticated(true)
                .build();
    }

    // ======================= Tạo token =======================
    private String generateToken(User user, String tokenType, long validDurationInSeconds) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        Instant now = Instant.now();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("ducdv38fpt")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(validDurationInSeconds, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user)) // set role của user
                .claim("token_type", tokenType)   // set type for token
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(key.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Could not sign JWT object", e);
            throw new RuntimeException(e);
        }
    }

    // ======================= Build scope =======================
    private String buildScope(User user) {
        StringJoiner scopeJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                scopeJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        scopeJoiner.add(permission.getName());
                    });
                }
            });
        }
        return scopeJoiner.toString();
    }
}
