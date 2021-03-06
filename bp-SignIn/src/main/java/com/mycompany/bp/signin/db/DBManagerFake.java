/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin.db;

import com.mycompany.bp.signin.dao.User;
import com.mycompany.bp.signin.dto.SignUpRequestDTO;
import com.mycompany.bp.signin.dto.ViolationDTO;
import com.mycompany.bp.signin.exceptions.GlobalException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dato
 */
public class DBManagerFake implements DBManager {

    public static final DBManager instance = new DBManagerFake();

    private Map<String, User> db = new HashMap<>();
    private Map<String, String> passwords = new HashMap<>();
    private Map<String, Integer> tokens = new HashMap<>();
    private int count;

    @Override
    public User getUser(String username, String password) {
        if (password != null && password.equals(passwords.get(username))) {
            return db.get(username);
        }
        return null;
    }

    @Override
    public User saveUser(SignUpRequestDTO request) throws GlobalException {
        synchronized (db) {
            validate(request);
            User user = new User();
            user.setUsername(request.getUsername());
            user.setId(count++);
            user.setRoles(request.getUsername().equals("dato") ? 
                    new String[]{"uploader", "user"} 
                    : new String[]{"user"});
            db.put(request.getUsername(), user);
            passwords.put(request.getUsername(), request.getPassword());
            return user;
        }
    }

    private void validate(SignUpRequestDTO request) throws GlobalException {
        GlobalException exception = new GlobalException();
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            exception.addError(new ViolationDTO("username", "არ უნდა იყოს ცარიელი!"));
        } else if (db.containsKey(request.getUsername())) {
            exception.addError(new ViolationDTO("username", "ასეთი username უკვე არსებობს."));
        }
        exception.addError(new ViolationDTO("password", "არ უნდა იყოს ცარიელი!"));
        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    @Override
    public void saveToken(String token, int id) {
        tokens.put(token, id);
    }

    @Override
    public void removeToken(String token) {
        if (tokens.containsKey(token)){
            tokens.remove(token);
        }
    }

    
}
