package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by josec on 6/10/2016.
 */
@Service
public class Handler {



    @Autowired
    ElasticService elasticService;

    public ArrayList<ImpressionDto> execute(ArrayList<ImpressionDto> impressions){
        for(ImpressionDto impression : impressions){
            impression.session= java.util.UUID.randomUUID().toString();
            elasticService.save(impression);
        }
        return impressions;
    }
}
