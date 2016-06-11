package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by josec on 6/10/2016.
 */
@Service
public class GetImpressionHandler {
    @Autowired
    ElasticService elasticService;
    public ImpressionDto execute(String sessionId){
        System.out.print(sessionId);
        return elasticService.findBySession(sessionId).get(0);
    }
}
