package com.example;

import com.amazonaws.util.json.Jackson;
import com.eureka2.shading.codehaus.jackson.map.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private String REDIS_URL ="localhost";
    //private String REDIS_URL ="redisads.qkoiz3.0001.use1.cache.amazonaws.com.com";

    Integer publisherCampaign;

    Random rand;

    String query_id;
    public AdsDto execute(ArrayList<Integer> campaigns, Integer publisherCampaign) throws CustomException {
        ArrayList<Ad> listAds = new ArrayList<Ad>();
        rand = new Random();
        ArrayList<ImpressionDto> impressions = new ArrayList<ImpressionDto>();
        Integer idCampaign=-1;
        this.publisherCampaign= publisherCampaign;
        query_id = java.util.UUID.randomUUID().toString();
        try
        {
            String stKey = "SELECT c.ID_campaign, Headline, Description, Url, a.ID_ad  FROM adsconfiguration.Campaign_Ads c JOIN " +
                    " adsconfiguration.Ads a ON a.ID_ad = c.ID_ad JOIN adsconfiguration.Campaign_Advertiser ca ON c.ID_campaign = ca.ID_campaign ORDER BY Bid DESC";
            Jedis jedis = new Jedis(REDIS_URL);
            String cache =jedis.get(stKey);


            ArrayList<HashMap<String,Object>> list;
            if (cache!=null){
                System.out.println("cache used");
                list = Jackson.fromJsonString(cache, ArrayList.class);
            } else {
                System.out.println("cache NOT used");
                openConnection();
                ResultSet rs = executeQuery("SELECT c.ID_campaign, Headline, Description, Url, a.ID_ad  FROM adsconfiguration.Campaign_Ads c JOIN " +
                        " adsconfiguration.Ads a ON a.ID_ad = c.ID_ad JOIN adsconfiguration.Campaign_Advertiser ca ON c.ID_campaign = ca.ID_campaign ORDER BY Bid DESC");
                list = getHashmap(rs,new String[]{"ID_campaign","Headline","Description","Url","ID_ad"});
                closeConnection();
                jedis.set(stKey, Jackson.toJsonString(list));
                jedis.expire(stKey,300);
            }

            ArrayList<Ad> tempAds= new ArrayList<>();
            boolean firstTime=true;
            for (HashMap result: list) {
                if(campaigns.contains(result.get("ID_campaign")))
                {
                    if(result.get("ID_campaign")!=idCampaign )
                    {
                        //do the random
                        if(!firstTime) {
                            if(tempAds.size()>0){
                              Ad randomAd =tempAds.get(getRandomNumber(tempAds.size()));
                              listAds.add(randomAd);
                              impressions.add(getImpression(randomAd));
                            }
                        }else{
                            firstTime=false;
                        }

                        idCampaign = (Integer) result.get("ID_campaign");
                        tempAds = new ArrayList<>();
                    }

                    Ad ad= new Ad();
                    ad.description=(String)result.get("Description");
                    ad.headline=(String) result.get("Headline");
                    ad.url=(String) result.get("Url");
                    ad.id = (Integer)result.get("ID_ad");
                    ad.idCampaign = (Integer)result.get("ID_campaign");
                    tempAds.add(ad);

                }
            }
            if(tempAds.size()>0) {
                Ad randomAd = tempAds.get(getRandomNumber(tempAds.size()));
                listAds.add(randomAd);
                impressions.add(getImpression(randomAd));
            }


        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        ArrayList<ImpressionDto> responsePricingService = callPricingService(impressions);
        ArrayList<ImpressionDto> responseSessionService = callSessionService(responsePricingService);

        AdsDto dtoResponse = new AdsDto();
        dtoResponse.header = new AdsDto.Header();
        dtoResponse.header.query_id = query_id;
        dtoResponse.body =new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for(Object object: responseSessionService){
            ImpressionDto impressionDto = mapper.convertValue(object,ImpressionDto.class);
            AdsDto.Body body = new AdsDto.Body();
            body.impression_id = impressionDto.session;
            body.click_url = impressionDto.clickUrl;
            body.headline = impressionDto.headline;
            body.description = impressionDto.description;
            dtoResponse.body.add(body);
        }
        return dtoResponse;
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
        impressionDto.headline = ad.headline;
        impressionDto.description = ad.description;
        impressionDto.query_id = query_id;
        return impressionDto;
    }

    public String getUrl(String serviceName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(serviceName, false);
        return instance.getHomePageUrl();
    }

    public ArrayList<HashMap<String,Object>> getHashmap(ResultSet rs, String[] fields) throws SQLException {

        ArrayList<HashMap<String,Object>> list = new ArrayList<>();
        while (rs.next()){
            HashMap<String,Object> hashMap = new HashMap<>();
            for (String st : fields){
                hashMap.put(st,rs.getObject(st));
            }
            list.add(hashMap);
        }
        return list;
    }

}