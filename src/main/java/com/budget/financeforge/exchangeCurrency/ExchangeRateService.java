package com.budget.financeforge.exchangeCurrency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class ExchangeRateService {


    private final RestTemplate restTemplate;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getBidValueForPln(String currency) {



        String apiUrl = "http://api.nbp.pl/api/exchangerates/rates/c/"+ currency + "/?format=json";
        String jsonResponse = restTemplate.getForObject(apiUrl, String.class);


        int bidIndex = jsonResponse.indexOf("\"bid\":") + 6;
        int commaIndex = jsonResponse.indexOf(",", bidIndex);
        String bidString = jsonResponse.substring(bidIndex, commaIndex);

        return new BigDecimal(bidString);
    }
}