package com.fileencryption.service.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SmsService {

    @Value("${sms.api.key}")
    private String apiKey;
    
    @Value("${sms.api.host}")
    private String apiHost;
    
    public boolean sendSms(String phoneNumber, String message) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://" + apiHost + "/sms/send");
            
            // Set headers
            httpPost.setHeader("content-type", "application/json");
            httpPost.setHeader("X-RapidAPI-Key", apiKey);
            httpPost.setHeader("X-RapidAPI-Host", apiHost);
            
            // Prepare JSON payload
            JSONObject payload = new JSONObject();
            payload.put("sms", message);
            payload.put("to", phoneNumber);
            payload.put("type", "turkish"); // Use appropriate type based on country
            
            StringEntity entity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            
            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                return statusCode >= 200 && statusCode < 300;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}