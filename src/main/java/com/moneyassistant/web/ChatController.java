package com.moneyassistant.web;

import com.moneyassistant.dto.ChatQuery;
import com.moneyassistant.dto.ChatReply;
import com.moneyassistant.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    @PostMapping
    public ChatReply chat(@Valid @RequestBody ChatQuery query) {
        return service.chat(query.message());
    }
}
