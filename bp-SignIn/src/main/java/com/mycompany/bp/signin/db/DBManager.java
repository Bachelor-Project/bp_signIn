/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin.db;

import com.mycompany.bp.signin.dao.User;
import com.mycompany.bp.signin.dto.SignUpRequestDTO;
import com.mycompany.bp.signin.exceptions.GlobalException;

/**
 *
 * @author Dato
 */
public interface DBManager {
    
    public User getUser(String username, String password);
    public User saveUser(SignUpRequestDTO request) throws GlobalException;
    
}
