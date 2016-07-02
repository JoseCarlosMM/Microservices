package com.example;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by josec on 6/10/2016.
 */
@Service
public class ElasticService implements ServiceInterface{

    private static String URL_ELASTIC="http://search-elasticads-f674wqvucunkk2f6leotld7way.us-east-1.es.amazonaws.com";

    public ImpressionDto save(ImpressionDto post) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ImpressionDto> entity = new HttpEntity<>(post, headers);
        restTemplate.put(URL_ELASTIC+"/impressions/ads/"+post.session,entity,String.class);
        return post;
    }

    @Override
    public List<ImpressionDto> findBySession(String session) {
        RestTemplate restTemplate = new RestTemplate();
        List<ImpressionDto> impressions = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String response = restTemplate.getForObject(URL_ELASTIC+"/impressions/ads/"+session, String.class);
        ImpressionDto impressionDto= null;
        try {
            JSONObject object = new JSONObject(response);
            impressionDto = Jackson.fromJsonString(object.getJSONObject("_source").toString(),ImpressionDto.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        impressions.add(impressionDto);
        return impressions;
    }
}
