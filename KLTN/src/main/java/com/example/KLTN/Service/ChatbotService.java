package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.LLMRequest;
import com.example.KLTN.DTOS.Response.LLMResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
@Service
public class ChatbotService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String Chatbot_API_URL = "https://1c2f-34-13-200-63.ngrok-free.app/generate";
    public String callFlaskApi(String prompt) {
        try {
            // Tạo Header cho yêu cầu HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo Body cho yêu cầu HTTP
            String requestBody = new ObjectMapper().writeValueAsString(Map.of("prompt", prompt));

            // Tạo HttpEntity (Header + Body)
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // Gửi yêu cầu POST đến Flask API
            String response = restTemplate.postForObject(Chatbot_API_URL, requestEntity, String.class);

            return response; // Trả về phản hồi từ Flask API
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
