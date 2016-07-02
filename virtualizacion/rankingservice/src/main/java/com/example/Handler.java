package com.example;

import com.amazonaws.util.json.Jackson;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {
    private String REDIS_URL ="localhost";
    //private String REDIS_URL ="redisads.qkoiz3.0001.use1.cache.amazonaws.com.com";


    public ArrayList execute(Integer limit, ArrayList<Integer> campaigns) throws CustomException {

        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {

            Jedis jedis = new Jedis(REDIS_URL);

            ArrayList<Integer> listCampaigns = new ArrayList<>();
            String stKey1 = "SELECT c.ID_campaign FROM adsconfiguration.Campaign_Advertiser c" +
                    " JOIN adsconfiguration.Campaign_Ads a ON c.ID_campaign = a.ID_campaign GROUP BY c.ID_campaign ORDER BY Bid DESC;";
            String cache1 = jedis.get(stKey1);
            if(cache1!=null){
                System.out.println("cache used");
                listCampaigns = Jackson.fromJsonString(cache1,ArrayList.class);
            } else {
                System.out.println("cache NOT used");
                openConnection();
                ResultSet rs = executeQuery("SELECT c.ID_campaign FROM adsconfiguration.Campaign_Advertiser c" +
                        " JOIN adsconfiguration.Campaign_Ads a ON c.ID_campaign = a.ID_campaign GROUP BY c.ID_campaign ORDER BY Bid DESC;");
                while (rs.next()) {
                    listCampaigns.add(rs.getInt("ID_campaign"));
                }
                closeConnection();
                jedis.set(stKey1,Jackson.toJsonString(listCampaigns));
                jedis.expire(stKey1,300);
            }
           /* ResultSet rs2 = executeQuery("SELECT c.ID_campaign FROM adsconfiguration.Campaign_Advertiser c" +
                    " JOIN adsconfiguration.Campaign_Ads a ON c.ID_campaign = a.ID_campaign GROUP BY c.ID_campaign ORDER BY Bid DESC;");*/

            int contLimit=0;
            for(Integer campaign : listCampaigns){
                if(contLimit>=limit){
                    break;
                }
                if(campaigns.contains(campaign))
                {
                    list.add(campaign);
                    contLimit++;
                }
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }
}