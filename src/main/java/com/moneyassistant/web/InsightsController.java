package com.moneyassistant.web;

import com.moneyassistant.dto.InsightsResponse;
import com.moneyassistant.service.InsightsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    private final InsightsService service;

    public InsightsController(InsightsService service) {
        this.service = service;
    }

    @GetMapping
    public InsightsResponse insights(@RequestParam(required = false) String month) {
        String target = month != null ? month : YearMonth.now().toString();
        return service.forMonth(target);
    }
}
