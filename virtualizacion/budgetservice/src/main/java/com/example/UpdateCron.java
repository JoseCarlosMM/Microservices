package com.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by josec on 6/11/2016.
 */
@Component
public class UpdateCron {
    @Scheduled(fixedRate = 10000)
    public void reportCurrentTime() {
        UpdateHandler handler = new UpdateHandler();
        handler.execute();
        System.out.println("update passed");
    }
}
