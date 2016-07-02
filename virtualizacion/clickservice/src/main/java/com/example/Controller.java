package com.example;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

/**
 * Created by josec on 6/11/2016.
 */
@RestController
public class Controller {

    @Autowired
    private EurekaClient discoveryClient;

    private static String SESSION_SERVICE="SESSION-SERVICE";
    private static String BUDGET_SERVICE="BUDGET-SERVICE";

    @RequestMapping(value = "/click",method = RequestMethod.GET, produces = "application/json")
    public ModelAndView getSession(
                @RequestParam String id
    )  {

        RestTemplate restTemplate = new RestTemplate();
        String urlSessionService = getUrl(SESSION_SERVICE);
        String urlBudgetService = getUrl(BUDGET_SERVICE);

        ImpressionDto impression = restTemplate.getForObject(urlSessionService+"/impressions?id="+id,ImpressionDto.class);
        Response response = new Response();
        response.adUrl = impression.urlAd;

        restTemplate.getForObject(urlBudgetService+"/budget?idCampaign="+impression.campaignId+"&Bid="+impression.bid,ImpressionDto.class);

        RedirectView rv = new RedirectView(impression.urlAd);
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setUrl("http://"+impression.urlAd);
        //rv.setUrl(impression.urlAd);
        ModelAndView mv = new ModelAndView(rv);
        return mv;
    }

    public String getUrl(String serviceName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(serviceName, false);
        return instance.getHomePageUrl();
    }
}