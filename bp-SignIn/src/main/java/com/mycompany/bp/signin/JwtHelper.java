/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mycompany.bp.signin.dao.User;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dato
 */
public class JwtHelper {

    private long intervalMs;
    private final Builder builder;
    private final JWTVerifier verifier;
    private final Algorithm algorithm;
    
    private static JwtHelper instance;
    
    public static JwtHelper getInstance(String issuer, String secret){
        if (instance == null){
            try {
                instance = new JwtHelper(issuer, secret);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(JwtHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    private JwtHelper(String issuer, String secret) throws UnsupportedEncodingException {
        algorithm = Algorithm.HMAC256(secret);
        builder = JWT.create()
                .withIssuer(issuer);

        verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        intervalMs = Long.valueOf(30) * 24 * 60 * 60 * 1000;
    }

    public String getToken(User user) {
        Date expDate = new Date(new Date().getTime() + intervalMs);
        System.out.println("date: "+expDate);
        return builder
                .withExpiresAt(expDate)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withArrayClaim("roles", user.getRoles())
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    public Builder builder() {
        return builder;
    }

    public JwtHelper withInterval(long interval) {
        this.intervalMs = interval;
        return this;
    }

}
