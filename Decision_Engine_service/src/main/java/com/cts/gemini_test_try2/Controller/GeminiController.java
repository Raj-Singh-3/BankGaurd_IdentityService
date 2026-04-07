package com.cts.gemini_test_try2.Controller;

import com.cts.gemini_test_try2.Service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
class GeminiController{

    private final GeminiService geminiService;

    @GetMapping("/ask")
    public String askGeminiApi(@RequestBody String prompt) throws HttpException, IOException {
         return geminiService.askGemini(prompt);
    }

}