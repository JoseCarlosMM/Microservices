package com.example;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

/**
 * Created by josec on 6/11/2016.
 */
@RestController
public class Controller {

    @Autowired
    private EurekaClient discoveryClient;

    private static String SESSION_SERVICE="SESSION-SERVICE";

    @RequestMapping(value = "/click",method = RequestMethod.GET, produces = "application/json")
    public Response getSession(
                @RequestParam String session
    )  {

        RestTemplate restTemplate = new RestTemplate();
        String urlSessionService = getUrl(SESSION_SERVICE);

        ImpressionDto impression = restTemplate.getForObject(urlSessionService+"/impressions?id="+session,ImpressionDto.class);
        Response response = new Response();
        response.adUrl = impression.urlAd;
        return response;
    }

    public String getUrl(String serviceName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(serviceName, false);
        return instance.getHomePageUrl();
    }
}