/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.currency.provider;

import DOCwebServices.CurrencyConversionWS;
import java.util.List;
import org.junit.Test;
import junit.framework.*;
import org.json.simple.JSONObject;

/**
 *
 * @author brave
 */
public class RestApiExchangeRateProviderTest extends TestCase {
    
    @Test
    public void testGetResponse() {
        RestApiExchangeRateProvider provider = new RestApiExchangeRateProvider();
        try {
            String json = provider.getResponse(provider.getApiUrl() + "?base=GBP");
            
            System.out.println("Response is as follows:");
            System.out.println(json);
            
            System.out.println("Supported currency codes are as follows:");
            JSONObject ratesData = (JSONObject)provider.getRatesData().get("rates");
            List<String> curCodes = provider.getCurrencyCodes();
            for(String curCode : curCodes) {
                System.out.print(curCode + ", ");
            }
            
            System.out.println("Currency rates based on GBP are as follows:");
            for(String curCode: curCodes) {
                System.out.println(curCode + ": " + ratesData.get(curCode));
            }
            
            String cur1 = "CNY";
            String cur2 = "USD";
            System.out.println("Allow me calculate " + cur1 + " and " + cur2 + " exchange rate.");
            System.out.println(provider.getExchangeRate("CNY", "USD"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testListCurrencies() {
        CurrencyConversionWS service = new CurrencyConversionWS();
        try {
            service.listLatestCurrencyRates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
