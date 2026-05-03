package com.fitfind.fitfind.security.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fitfind.fitfind.security.auth.model.DecodedJwtData;
import com.fitfind.fitfind.systemconfig.model.SystemConfiguration;
import com.fitfind.fitfind.systemconfig.repository.SystemConfigurationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final static String ISSUER = "fit-find";
    private final static String AUTHORITIES = "authorities";

    private static Algorithm algorithm;
    private static JWTVerifier verifier;
    private final SystemConfigurationRepository systemConfigurationRepository;

    public JwtService(
            @Value("classpath:security/keys/public.der") Resource publicKeyResource,
            @Value("classpath:security/keys/private.der") Resource privateKeyResource,
            SystemConfigurationRepository systemConfigurationRepository
    ) throws Exception{
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey;
        RSAPrivateKey privateKey;

        if (publicKeyResource != null && privateKeyResource != null) {
            publicKey = (RSAPublicKey) keyFactory.generatePublic(
                    new X509EncodedKeySpec(publicKeyResource.getContentAsByteArray())
            );
            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(privateKeyResource.getContentAsByteArray())
            );
        } else {
            throw new IllegalStateException("Couldn't read keys!");
        }

        algorithm = Algorithm.RSA256(publicKey, privateKey);
        verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        this.systemConfigurationRepository = systemConfigurationRepository;
    }

    public String generateToken(String email, List<String> authorities) {
        Instant issuedAt = Instant.now();
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findFirstByOrderByCreatedAtDesc();
        Long expirationMinutes = systemConfiguration.getTokenValidityMinutes();
        long expirationSeconds = expirationMinutes * 60;

        var builder = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(email)
                .withIssuedAt(issuedAt)
                .withClaim(AUTHORITIES, authorities)
                .withExpiresAt(issuedAt.plusSeconds(expirationSeconds));

        return builder.sign(algorithm);
    }

    public DecodedJwtData validateToken (String token) {
        final DecodedJWT decodedJWT = verifier.verify(token);
        return new DecodedJwtData(
                decodedJWT.getSubject(),
                decodedJWT.getClaim(AUTHORITIES).asList(String.class)
        );
    }
}
