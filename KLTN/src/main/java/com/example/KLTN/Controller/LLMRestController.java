package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.LLMRequest;
import com.example.KLTN.DTOS.Response.LLMResponse;
import com.example.KLTN.Service.OllamaLLMService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/llm")
public class LLMRestController {

    private OllamaLLMService ollamaLLMService;

    public LLMRestController(OllamaLLMService ollamaLLMService) {
        this.ollamaLLMService = ollamaLLMService;

    }

    @PostMapping("/chat")
    public ResponseEntity<LLMResponse> chat(@RequestBody LLMRequest llmRequest) {
        String chatResponse = ollamaLLMService.chat(llmRequest.getQuery());

        LLMResponse llmResponse = new LLMResponse("Success", chatResponse);

        return ResponseEntity.ok(llmResponse);

    }
}
