/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DOCwebServices;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.json.simple.JSONObject;
import org.me.currency.Currencies;
import org.me.currency.Currency;
import org.me.currency.exception.CurrencyNotFoundException;
import org.me.currency.exception.ProviderNotAvailableException;
//import org.me.currency.provider.DefaultExchangeRateProvider;
import org.me.currency.provider.ExchangeRateProvider;
import org.me.currency.provider.JAXBExchangeRateProvider;
import org.me.currency.provider.RestApiExchangeRateProvider;

/**
 *
 * @author brave
 */
@WebService(serviceName = "CurrencyConversionWS")
public class CurrencyConversionWS {

    /**
     * @param cur1 String Currency to be converted
     * @param cur2 String Target currency
     * @return double rate
     * @throws CurrencyNotFoundException 
     */
    @WebMethod(operationName = "getConversionRate")
    public double getConversionRate(@WebParam(name = "cur1") String cur1, @WebParam(name = "cur2") String cur2) throws CurrencyNotFoundException {
        ExchangeRateProvider provider = null;
        try {
            // call live up-to-date rest api
            provider = new RestApiExchangeRateProvider();
            return provider.getExchangeRate(cur1, cur2);
        } catch(ProviderNotAvailableException e) {
            // fall back to offline mode when rest api is not supported
            try {
                provider = new JAXBExchangeRateProvider();
                return provider.getExchangeRate(cur1, cur2);
            } catch(ProviderNotAvailableException ex) {
                ex.printStackTrace();
                return 0.0;
            }
        }
    }

    /**
     * Gets all supported currency codes
     * @return List<String>
     */
    @WebMethod(operationName = "getCurrencyCodes")
    public List<String> getCurrencyCodes() {
        ExchangeRateProvider provider = null;
        List<String> codeList = null;
        try {
            // call live up-to-date rest api
            provider = new RestApiExchangeRateProvider();
            codeList = provider.getCurrencyCodes();
            return codeList;
        } catch(ProviderNotAvailableException e) {
            try {
                // fall back to offline mode when rest api is not supported
                provider = new JAXBExchangeRateProvider();
                return provider.getCurrencyCodes();
            } catch (ProviderNotAvailableException ex) {
                return null;
            }
        }
    }

    /**
     * Call RESTful Api to retrieve latest currency rates based on GBP
     * @return Currencies
     * @throws ProviderNotAvailableException 
     */
    @WebMethod(operationName = "listLatestCurrencyRates")
    public Currencies listLatestCurrencyRates() throws ProviderNotAvailableException {
        RestApiExchangeRateProvider provider = new RestApiExchangeRateProvider();
        JSONObject curData = (JSONObject) provider.getRatesData();
        JSONObject ratesData = (JSONObject) curData.get("rates");
        Currencies currencies = new Currencies();
        List<Currency> currencyList = currencies.getCurrencyList();
        Set<Object> keySet = ratesData.keySet();
        for (Object key : keySet) {
            Currency currency = new Currency();
            // cast key to String, key is actually currency name
            currency.setName("" + key);
            // cast object to double
            Object rateObj = ratesData.get("" + key);
            double rateVal = Double.parseDouble(rateObj.toString());
            // set rate of currency, rate has been casted into double
            currency.setRate(rateVal);
            // set base, base will always be GBP
            currency.setBase(curData.get("base").toString());
            // set currency updated date
            currency.setLastUpdated(string2XMLGrgCalendar(curData.get("date").toString()));
            currencyList.add(currency);
        }
        
        return currencies;
    }
    
    /**
     * Converts given string to XMLGregorianCalendar. It is used when marshalling a bean.
     * @param str String to be converted
     * @return XMLGregorianCalendar
     */
    protected XMLGregorianCalendar string2XMLGrgCalendar(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar xmlGregCal = null;
        try {
            xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);   
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        return xmlGregCal;
    }

    /**
     * Save given currency name and rate to Currency.xml
     * @param curName String currency name
     * @param rate double 
     * @return Currency saved currency
     */
    @WebMethod(operationName = "saveCurrencyRate")
    public Currency saveCurrencyRate(@WebParam(name = "curName") String curName, @WebParam(name = "rate") double rate) {
        JAXBExchangeRateProvider provider = new JAXBExchangeRateProvider();
        Currencies currencies = provider.getCurrencies();
        List<Currency> currencyList = currencies.getCurrencyList();
        XMLGregorianCalendar xmlGrgCalendar = null;
        Currency currency = null;
        // check given currency already exists
        boolean isNew = true;
        for (Iterator<Currency> it = currencyList.iterator(); it.hasNext();) {
            Currency tmpCurrency = it.next();
            if (curName.equals(tmpCurrency.getName())) {
                currency = tmpCurrency;
                isNew = false;
            }
        }
        if (currency == null) {
            currency = new Currency();
            currency.setName(curName);
            currency.setBase("GBP");
        }
        currency.setRate(rate);
        try {
            xmlGrgCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();    
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        // set update date as now
        currency.setLastUpdated(xmlGrgCalendar);
        // if currency is a new one, it will add to existing currency list
        if (isNew) {
            currencyList.add(currency);
        }
        provider.saveCurrencies(currencies);
        return currency;
    }    

    /**
     * Get all currencies saved in Currency.xml
     * @return Currencies
     */
    @WebMethod(operationName = "listSavedCurrencies")
    public Currencies listSavedCurrencies() {
        JAXBExchangeRateProvider provider = new JAXBExchangeRateProvider();
        return provider.getCurrencies();
    }
}
