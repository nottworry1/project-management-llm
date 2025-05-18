package org.kupchenko.projectmanagementllm.service.impl;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.service.LlmService;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LlmServiceImpl implements LlmService {
    private final OpenAiChatModel openAiChatModel;

    @Override
    public String getResponse(String prompt) {
        return openAiChatModel.call(prompt).trim();
    }
}
