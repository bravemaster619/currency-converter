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
public class CurrencyNotFoundException extends Exception {
    protected String message;
    
    public CurrencyNotFoundException() {
        super();
    }
    
    public CurrencyNotFoundException(String message) {
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
