/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.currency.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.me.currency.exception.ProviderNotAvailableException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.me.currency.exception.CurrencyNotFoundException;
/**
 * RestApiExchangeRateProvider will be used when live up-to-date currency rates are requested
 * @author brave
 */
public class RestApiExchangeRateProvider implements ExchangeRateProvider{
    
//    public static final String apiUrl = "https://api.exchangerate-api.com/v4/latest/GBP";
    // this local php will imitate real-time RESTful API
    public static final String apiUrl = "http://localhost/restapi/exchangerate.php";
    protected final CloseableHttpClient httpClient;
    
    public RestApiExchangeRateProvider() {
        this.httpClient = HttpClients.createDefault();
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
        JSONObject ratesData = (JSONObject) getRatesData().get("rates");
        double rate1 = getCurRateFromJsonObject(cur1, ratesData);
        double rate2 = getCurRateFromJsonObject(cur2, ratesData);
        return rate2 / rate1;
    }
    
    /**
     * Returns all supported currency codes
     * @return List<String>
     * @throws ProviderNotAvailableException 
     */
    public List<String> getCurrencyCodes() throws ProviderNotAvailableException {
        JSONObject ratesData = (JSONObject) getRatesData().get("rates");
        // get key codes from rates object
        Set<Object> currencySet = ratesData.keySet();
        List<String> currencyCodes = new ArrayList<String>(currencySet.size());
        for (Object o : currencySet) {
            String code = o.toString();
            currencyCodes.add(code);
        }
        return currencyCodes;
    }
    
    /**
     * Gets the api url
     * @return String
     */
    public String getApiUrl() {
        return apiUrl;
    }
    
    /**
     * Parse JSON string from GET request
     * @return JSONObject
     * @throws ProviderNotAvailableException 
     */
    public JSONObject getRatesData() throws ProviderNotAvailableException {
        try {
            // get json response from rest api service
            String jsonData = getResponse(apiUrl);
            // parse json
            Object obj = new JSONParser().parse(jsonData);
            JSONObject ratesData = (JSONObject) obj;
            // get rates object from parsed JSONObject
            return ratesData;
        } catch(ParseException ex) {
            throw new ProviderNotAvailableException(ex.getMessage());
        } catch(IOException ex) {
            throw new ProviderNotAvailableException(ex.getMessage());
        }
    } 

    /**
     * Close HttpClient
     * @throws IOException 
     */
    protected void close() throws IOException {
        httpClient.close();
    }
    
    /**
     * Utility function that retrieves response of a GET request.
     * @param address String
     * @return String
     * @throws IOException
     * @throws ProviderNotAvailableException 
     */
    protected String getResponse(String address) throws IOException, ProviderNotAvailableException{
//        
//         try {
//            URL url = new URL(address);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            if (conn != null) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                StringBuilder sb = new StringBuilder();
//                String input;
//                while((input = br.readLine())!= null) {
//                    sb.append(input);
//                }
//                br.close();
//                return sb.toString();
//            } else {
//                throw new ProviderNotAvailableException("Connection is null");
//            }
//         } catch(Exception e) {
//             e.printStackTrace();
//         }
//         return "";
        HttpGet request = new HttpGet(address);
        

        CloseableHttpResponse response = null;
        
        try {
            response = httpClient.execute(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("entity got");
        
        HttpEntity entity = response.getEntity();
        
        if (entity != null) {
            String result = EntityUtils.toString(entity);
            return result;
        } else {
            throw new ProviderNotAvailableException("Http entity is null.");
        }

    }
    
    /**
     * gets rate from given json object, if not found 
     * @param key String currency name
     * @param obj JSONObject
     * @return double
     * @throws CurrencyNotFoundException 
     */
    protected double getCurRateFromJsonObject(String key, JSONObject obj) throws CurrencyNotFoundException {
        Object tempObj = obj.get(key);
        if (tempObj == null) {
            throw new CurrencyNotFoundException("JSONObject does not hold such key as: " + key);
        }
        return Double.parseDouble(tempObj.toString());
    }
    
}
