package com.example.KLTN.Service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OllamaLLMService {
    private OllamaChatModel chatModel;

    OllamaLLMService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String query) {

        String response = chatModel.call(query);

        return response;
    }
}
