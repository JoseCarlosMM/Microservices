package com.example;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
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
    private EurekaClient discoveryClient;

    private static String CLICK_SERVICE="CLICK-SERVICE";

    @Autowired
    ElasticService elasticService;

    public ArrayList<ImpressionDto> execute(ArrayList<ImpressionDto> impressions){
        for(ImpressionDto impression : impressions){
            impression.session= java.util.UUID.randomUUID().toString();
            String urlClickService = getUrl(CLICK_SERVICE);
            impression.clickUrl = urlClickService+"/click?session="+impression.session;
            elasticService.save(impression);
        }
        return impressions;
    }


    public String getUrl(String serviceName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(serviceName, false);
        return instance.getHomePageUrl();
    }
}
