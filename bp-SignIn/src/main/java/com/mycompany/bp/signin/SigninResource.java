/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin;

import com.mycompany.bp.signin.dao.User;
import com.mycompany.bp.signin.db.DBManager;
import com.mycompany.bp.signin.db.DBManagerReal;
import com.mycompany.bp.signin.dto.SignInRequestDTO;
import com.mycompany.bp.signin.dto.SignUpRequestDTO;
import com.mycompany.bp.signin.exceptions.GlobalException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Dato
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces("application/json; charset=UTF-8")
public class SigninResource {

    @Context
    private UriInfo context;
    
    DBManager dbManager = DBManagerReal.instance;

    /**
     * Creates a new instance of SigninResource
     */
    public SigninResource() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.bp.signin.SigninResource
     * @return an instance of java.lang.String
     */
    @GET
    public String getXml() {
        //TODO return proper representation object
        return "bla";
    }

    /**
     * PUT method for updating or creating an instance of SigninResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @RolesAllowed("uploader")
    public void putXml(String content) {
    }
    
    
    @POST
    @Path("/signin")
    public Response signIn(SignInRequestDTO request){
        System.out.println(" ------------  movida (signIn) user: " + request.toString() + " ---------------");
        
        User user = dbManager.getUser(request.getUsername(), request.getPassword());
        if (user == null){
            System.out.println("------------ araa magnairi user-i da he axla...");
            return Response.status(400).type(MediaType.TEXT_PLAIN).entity("username ან პაროლი არასწორია").build();
        }
        JwtHelper helper = JwtHelper.getInstance(Constants.ISSUER_KEY, Constants.SECRET_KEY);
        String token = helper.getToken(user);
        dbManager.saveToken(token, user.getId());
        System.out.println("--------------- shevinaxet token " + token + "   --> user-ze: " + user.getId());
        return Response.status(200).header("x-token", token).entity(user).build();
    }
    
    @POST
    @Path("/signup")
    public Response signUp(SignUpRequestDTO request){
        try {
            String requestStr = request.toString();
            System.out.println("-------------- movida user: " + requestStr + " --------------");
            
            User user = dbManager.saveUser(request);
            System.out.println("-------------- shevinaxet user: " + request.toString() + " --------------");
            
            SignInRequestDTO signInReq = new SignInRequestDTO();
            signInReq.setUsername(request.getUsername());
            signInReq.setPassword(request.getPassword());
            return signIn(signInReq);
        } catch (GlobalException ex) {
            Logger.getLogger(SigninResource.class.getName()).log(Level.SEVERE, null, ex);
            
            System.out.println("-------------- moxda error: \n" + ex.toString() );
            return Response.status(400).entity(ex.getErrors()).build();
        }
    }
    
    
}
