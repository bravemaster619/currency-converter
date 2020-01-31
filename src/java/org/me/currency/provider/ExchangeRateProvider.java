/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.currency.provider;

import java.util.List;
import org.me.currency.exception.CurrencyNotFoundException;
import org.me.currency.exception.ProviderNotAvailableException;

/**
 * CurrencyConversionWS will use JAXBExchangeRateProvider when dealing with XML-based operation(marshalling & unmarshalling etc)
 * RestApiExchangeRateProvider will be used when live up-to-date currency rates are requested
 * @author brave
 */
public interface ExchangeRateProvider {

    public double getExchangeRate(String cur1, String cur2) throws ProviderNotAvailableException, CurrencyNotFoundException;
    
    public List<String> getCurrencyCodes() throws ProviderNotAvailableException;

//    public void persist();
}
