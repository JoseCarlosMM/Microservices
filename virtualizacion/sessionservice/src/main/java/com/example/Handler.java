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
            //impression.clickUrl = urlClickService+"/click?id="+impression.session;
            impression.clickUrl = "http://clicklb-470241018.us-east-1.elb.amazonaws.com:80/click?id="+impression.session;
            elasticService.save(impression);
        }
        return impressions;
    }


}
