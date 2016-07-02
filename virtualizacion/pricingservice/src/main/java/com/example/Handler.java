package com.example;


import com.amazonaws.util.json.Jackson;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {

    private String REDIS_URL ="localhost";
    //private String REDIS_URL ="redisads.qkoiz3.0001.use1.cache.amazonaws.com.com";

    public ArrayList execute( ArrayList<ImpressionDto> impressionDtos, Integer campaignPublisher) {
        ArrayList<Integer> campaigns = new ArrayList<>();
        for (ImpressionDto dto : impressionDtos){
            campaigns.add(dto.campaignId);
        }

        ArrayList<ImpressionDto> list = new ArrayList<ImpressionDto>();

        try
        {

            Double comission = null;
            Integer idPublisher = null;

            String stKey1 = "SELECT Comission, ID_publisher FROM adsconfiguration.Campaign_Publisher WHERE ID_campaign = " + campaignPublisher + ";";
            String stKey2 = "SELECT ID_campaign, Bid, ID_advertiser FROM adsconfiguration.Campaign_Advertiser WHERE Budget > 0 ;";

            ArrayList<HashMap<String,Object>> listResult;
            ArrayList<HashMap<String,Object>> listResult2;

            Jedis jedis = new Jedis(REDIS_URL);
            String cache1 =jedis.get(stKey1);
            String cache2 =jedis.get(stKey2);

            if (cache1!=null){
                System.out.println("cache1 used");
                listResult = Jackson.fromJsonString(cache1, ArrayList.class);
            } else {
                System.out.println("cache1 NOT used");
                openConnection();
                ResultSet rs = executeQuery("SELECT Comission, ID_publisher FROM adsconfiguration.Campaign_Publisher WHERE ID_campaign = " + campaignPublisher + ";");
                listResult = getHashmap(rs,new String[]{"Comission","ID_publisher"});
                closeConnection();
                jedis.set(stKey1, Jackson.toJsonString(listResult));
                jedis.expire(stKey1,300);
            }

            if(listResult.size()>0){
                comission = (Double) listResult.get(0).get("Comission");
                idPublisher = (Integer) listResult.get(0).get("ID_publisher");
            }

            if (cache2!=null){
                System.out.println("cache2 used");
                listResult2 = Jackson.fromJsonString(cache2, ArrayList.class);
            } else {
                System.out.println("cache2 NOT used");
                openConnection();
                ResultSet rs = executeQuery("SELECT ID_campaign, Bid, ID_advertiser FROM adsconfiguration.Campaign_Advertiser WHERE Budget > 0 ;");
                listResult2 = getHashmap(rs,new String[]{"ID_campaign","Bid","ID_advertiser"});
                closeConnection();
                jedis.set(stKey2, Jackson.toJsonString(listResult2));
                jedis.expire(stKey2,300);
            }

            for (HashMap result : listResult2) {
                if(campaigns.contains(result.get("ID_campaign")))
                {
                    ImpressionDto impressionDto = impressionDtos.get(campaigns.indexOf(result.get("ID_campaign")));
                    impressionDto.comission = comission;
                    impressionDto.campaignId = (Integer) result.get("ID_campaign");
                    impressionDto.publisherId = idPublisher;
                    impressionDto.bid = (Double)  result.get("Bid");
                    impressionDto.advertiserId = (Integer) result.get("ID_advertiser");
                    list.add(impressionDto);
                }

            }


        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
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
