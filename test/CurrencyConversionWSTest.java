/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import DOCwebServices.CurrencyConversionWS;
import java.util.List;
import org.junit.Test;
import junit.framework.*;
import org.json.simple.JSONObject;
import org.me.currency.Currency;
import org.me.currency.exception.ProviderNotAvailableException;
/**
 *
 * @author brave
 */
public class CurrencyConversionWSTest {
    @Test
    public void testGetCurrencyCodes() {
        CurrencyConversionWS service = new CurrencyConversionWS();
        List<String> listCodes = service.getCurrencyCodes();
        for(String code : listCodes) {
            System.out.println(code);
        }
    }
//    @Test
//    public void testListLatestCurrencyRates() {
//        CurrencyConversionWS service = new CurrencyConversionWS();
//        try {
//            List<Currency> listCurrency = service.listLatestCurrencyRates();
//            System.out.println(listCurrency.size());
//        } catch (ProviderNotAvailableException e) {
//            System.out.println(e);
//        }
//        
//    }
}
