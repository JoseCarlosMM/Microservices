package com.example;

import com.amazonaws.util.json.Jackson;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by josec on 6/4/2016.
 */
@Service
public class Handler extends BaseHandler {
    @Autowired
    private EurekaClient discoveryClient;

    private static String PRICING_SERVICE="PRICING-SERVICE";
    private static String SESSION_SERVICE="SESSION-SERVICE";
    Integer publisherCampaign;

    Random rand;

    public ArrayList execute(ArrayList<Integer> campaigns, Integer publisherCampaign) throws CustomException {
        ArrayList<Ad> listAds = new ArrayList<Ad>();
        rand = new Random();
        ArrayList<ImpressionDto> impressions = new ArrayList<ImpressionDto>();
        Integer idCampaign=-1;
        this.publisherCampaign= publisherCampaign;
        try
        {
            openConnection();

            ResultSet rs2 = executeQuery("SELECT c.ID_campaign, Headline, Description, Url, a.ID_ad  FROM adsconfiguration.Campaign_Ads c JOIN " +
                    " adsconfiguration.Ads a ON a.ID_ad = c.ID_ad JOIN adsconfiguration.Campaign_Advertiser ca ON c.ID_campaign = ca.ID_campaign ORDER BY Bid DESC");



            ArrayList<Ad> tempAds= new ArrayList<>();
            boolean firstTime=true;
            while (rs2.next()) {
                if(campaigns.contains(rs2.getInt("ID_campaign")))
                {
                    if(rs2.getInt("ID_campaign")!=idCampaign )
                    {
                        //do the random
                        if(!firstTime) {
                            Ad randomAd =tempAds.get(getRandomNumber(tempAds.size()));
                            listAds.add(randomAd);
                            impressions.add(getImpression(randomAd));
                        }else{
                            firstTime=false;
                        }

                        idCampaign = rs2.getInt("ID_campaign");
                        tempAds = new ArrayList<>();
                    }

                    Ad ad= new Ad();
                    ad.description=rs2.getString("Description");
                    ad.headline=rs2.getString("Headline");
                    ad.url=rs2.getString("Url");
                    ad.id = rs2.getInt("ID_ad");
                    ad.idCampaign = rs2.getInt("ID_campaign");
                    tempAds.add(ad);

                }
            }

            Ad randomAd =tempAds.get(getRandomNumber(tempAds.size()));
            listAds.add(randomAd);
            impressions.add(getImpression(randomAd));
            callPricingService(impressions);

            closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        ArrayList<ImpressionDto> responsePricingService = callPricingService(impressions);
        ArrayList<ImpressionDto> responseSessionService = callSessionService(responsePricingService);
        return responseSessionService;
    }

    private int getRandomNumber(int max){
        return rand.nextInt(max);
    }

    private ArrayList<ImpressionDto>  callPricingService(ArrayList<ImpressionDto> listImpressions){
        String url =  getUrl(PRICING_SERVICE);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(Jackson.toJsonString(listImpressions), headers);
        listImpressions = restTemplate.postForObject(url+"/pricing?campaignPublisher="+publisherCampaign,entity,ArrayList.class);
        return listImpressions;
    }

    private ArrayList<ImpressionDto>  callSessionService(ArrayList<ImpressionDto> listImpressions){
        String url =  getUrl(SESSION_SERVICE);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(Jackson.toJsonString(listImpressions), headers);
        listImpressions = restTemplate.postForObject(url+"/session",entity,ArrayList.class);
        return listImpressions;
    }
    private ImpressionDto getImpression(Ad ad){
        ImpressionDto impressionDto = new ImpressionDto();
        impressionDto.urlAd= ad.url;
        impressionDto.idAd = ad.id;
        impressionDto.campaignId = ad.idCampaign;
        return impressionDto;
    }

    public String getUrl(String serviceName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(serviceName, false);
        return instance.getHomePageUrl();
    }

}