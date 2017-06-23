/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bp.signin.exceptions;

import com.mycompany.bp.signin.dto.ViolationDTO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dato
 */
public class GlobalException extends Exception {
    
    private List<ViolationDTO> errors = new ArrayList<>();

    public List<ViolationDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ViolationDTO> errors) {
        this.errors = errors;
    }

    public void addError(ViolationDTO error){
        errors.add(error);
    }

    @Override
    public String toString() {
        String result = "";
        for (ViolationDTO error : errors) {
            result += (error + "\n");
        }
        return result;
    }
    
}
