package com.znsio.teswiz.businessLayer.cryptoAPI;

import com.znsio.teswiz.runner.Runner;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.log4j.Logger;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoAPIBL {
    private static final Logger LOGGER = Logger.getLogger(CryptoAPIBL.class.getName());
    private final Map<String, Object> testData = Runner.getTestDataAsMap("Crypto_API");
    private final String base_URL = testData.get("url").toString();

    public HttpResponse<JsonNode> getCryptoData(String symbol) {
        LOGGER.info("Getting crypto currency data for last 24-Hrs");
        HttpResponse<JsonNode> jsonResponse
                = Unirest.get(base_URL)
                .header("accept", "application/json")
                .queryString("symbol", symbol)
                .asJson();

        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(200);
        return jsonResponse;
    }

    public void verifyMaxPriceChange(HttpResponse<JsonNode> jsonResponse, int maxPriceChange) {
        LOGGER.info("Verifying price change is less than "+maxPriceChange);
        double priceChange = Double.parseDouble(jsonResponse.getBody().getObject().getString("priceChange"));
        assertThat(priceChange)
                .as("Price change value more than expected maximum value!")
                .isLessThan(maxPriceChange);
    }

    public void verifyMaxPriceChangePercent(HttpResponse<JsonNode> jsonResponse, int maxPriceChangePercent) {
        LOGGER.info("Verifying price change percent is less than "+maxPriceChangePercent);
        double priceChangePercent = Double.parseDouble(jsonResponse.getBody().getObject().getString("priceChangePercent"));
        assertThat(priceChangePercent)
                .as("Price change percent value more than expected maximum value!")
                .isLessThan(maxPriceChangePercent);
    }
}