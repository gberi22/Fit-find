package com.fitfind.fitfind.ai.service;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AIService {

    private final ChatClient openaiClient;

    public AIService(ChatClient.Builder builder) {
        this.openaiClient = builder.build();
    }

}
