/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Dato
 */
@Priority(Priorities.AUTHENTICATION)
public class SecurityCheckerFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = requestContext.getHeaderString("Authentication");
        System.out.println("header: "+token);
        if (token == null){
            return;
        }
        try {
            DecodedJWT jwt = JwtHelper.getInstance(Constants.ISSUER_KEY, Constants.SECRET_KEY)
                    .decodeToken(token);
            final String username = jwt.getClaim("username").asString();
            final String[] roles = jwt.getClaim("roles").asArray(String.class);
            System.out.println("roles: "+Arrays.toString(roles));
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return username;
                        }
                    };
                }
                
                @Override
                public boolean isUserInRole(final String role) {
                    return roles != null && Arrays.stream(roles)
                            .filter((String t) -> t.equals(role)).findAny()
                            .isPresent();
                }

                @Override
                public boolean isSecure() {
                    return false;
                }

                @Override
                public String getAuthenticationScheme() {
                    return null;
                }
            });
        } catch (JWTVerificationException ex) {
            ex.printStackTrace();
        }
    }

}
