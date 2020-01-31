/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.currency.exception;

/**
 *
 * @author brave
 */
public class ProviderNotAvailableException extends Exception {
    
    protected String message;
    
    public ProviderNotAvailableException() {
        super();
    }
    
    public ProviderNotAvailableException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return message;
    }
    
}
