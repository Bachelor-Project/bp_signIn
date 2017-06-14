/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 *
 * @author Dato
 */
@ApplicationPath("app")
public class Configuration extends ResourceConfig {
    
    public Configuration(){
        super();
        register(SigninResource.class);
        register(SecurityCheckerFilter.class);
        register(RolesAllowedDynamicFeature.class);
    }
}
