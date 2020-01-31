/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.currency.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.me.currency.Currencies;
import org.me.currency.Currency;
import org.me.currency.exception.CurrencyNotFoundException;
import org.me.currency.exception.ProviderNotAvailableException;

/**
 * CurrencyConversionWS will use JAXBExchangeRateProvider when dealing with XML-based operation(marshalling & unmarshalling etc)
 * @author brave
 */
public class JAXBExchangeRateProvider implements ExchangeRateProvider {
    
    // base directory of this project
    public static final String baseDir = "E:\\freelancer\\qtr\\ghanim\\CurrencyConvertor";
    // Currencies.xml
    public File xmlFile = new File(baseDir + "/src/java/Currencies.xml");
    
    public JAXBExchangeRateProvider() {
        if(!xmlFile.exists()) {
            try{
                xmlFile.createNewFile();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * 
     * @param cur1 String name of currency to be converted
     * @param cur2 String name of target currency
     * @return double rate
     * @throws ProviderNotAvailableException
     * @throws CurrencyNotFoundException 
     */
    public double getExchangeRate(String cur1, String cur2) throws ProviderNotAvailableException, CurrencyNotFoundException {
        List<Currency> currencyList = getCurrencies().getCurrencyList();
        double rate1 = -1;
        double rate2 = -1;
        for (Iterator<Currency> it = currencyList.iterator(); it.hasNext();) {
            Currency currency = it.next();
            if (cur1.equals(currency.getName())) {
                rate1 = currency.getRate();
            }
            if (cur2.equals(currency.getName())) {
                rate2 = currency.getRate();
            }
        }
        if (rate1 == -1 || rate2 == -1) {
            throw new CurrencyNotFoundException("Currency not found.");
        }
        return rate2 / rate1;
    }
    
    /**
     * Returns all supported currency codes
     * @return List<String>
     * @throws ProviderNotAvailableException 
     */
    public List<String> getCurrencyCodes() throws ProviderNotAvailableException {
        List<Currency> currencyList = getCurrencies().getCurrencyList();
        List<String> codeList = new ArrayList<String>();
        for (Iterator<Currency> it = currencyList.iterator(); it.hasNext();) {
            Currency currency = it.next();
            codeList.add(currency.getName());
        }
        return codeList;
    }
    
    /**
     * Gets the entire currencies object
     * @return 
     */
    public Currencies getCurrencies() {
        Currencies currencies = new Currencies();
        JAXBContext jaxbCtx = null;
        Unmarshaller unmarshaller = null;
        try {
            jaxbCtx = JAXBContext.newInstance(currencies.getClass().getPackage().getName());
            unmarshaller = jaxbCtx.createUnmarshaller();
            currencies = (Currencies) unmarshaller.unmarshal(xmlFile);
            return currencies;
        } catch (Exception ex) {
            // if unmarshalling is failed, it will return empty Currencies object
            ex.printStackTrace();
            currencies = new Currencies();
            return currencies;
//            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }
    
    /**
     * Save given Currencies
     * @param currencies Currencies to be saved
     * @return 
     */
    public boolean saveCurrencies(Currencies currencies) {
        JAXBContext jaxbCtx = null;
        try {
            jaxbCtx =  JAXBContext.newInstance(currencies.getClass().getPackage().getName());
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(currencies, new FileOutputStream(xmlFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
