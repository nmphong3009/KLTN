package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.LLMRequest;
import com.example.KLTN.DTOS.Response.LLMResponse;
import com.example.KLTN.Service.ChatbotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping("/generate-text")
    public String generateText(@RequestBody String prompt) {
        String response = chatbotService.callFlaskApi(prompt);
        return StringEscapeUtils.unescapeJava(response); // Giải mã Unicode thành ký tự thông thường
    }
}
