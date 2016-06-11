package com.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by josec on 6/11/2016.
 */
@Component
public class BudgetCron {
    @Scheduled(fixedRate = 100000)
    public void reportCurrentTime() {
        BudgetHandler handler = new BudgetHandler();
        handler.execute();
        System.out.println("hour passed");

    }
}
