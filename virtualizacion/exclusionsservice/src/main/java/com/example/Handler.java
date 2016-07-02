package com.example;

import com.amazonaws.util.json.Jackson;
import com.example.Exceptions.CampaignNotFoundException;
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

    public ArrayList execute(Integer campaign, ArrayList<Integer> campaigns) throws CustomException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        ArrayList<Integer> exclusions = new ArrayList<Integer>();
        try
        {
            String stKey1= "SELECT ID_publisher FROM adsconfiguration.Campaign_Publisher WHERE ID_campaign = " + campaign + ";";

            Jedis jedis = new Jedis(REDIS_URL);
            String cache1 =jedis.get(stKey1);

            int idPublisher;

            if(cache1!=null){
                System.out.println("cache1 used");
                idPublisher = Integer.valueOf(cache1);
            } else {
                System.out.println("cache1 NOT used");
                openConnection();
                ResultSet rs2 = executeQuery("SELECT ID_publisher FROM adsconfiguration.Campaign_Publisher WHERE ID_campaign = " + campaign + ";");
                if(rs2.next()){
                    idPublisher = rs2.getInt("ID_publisher");
                    jedis.set(stKey1,String.valueOf(idPublisher));
                    jedis.expire(stKey1,300);
                }
                else {
                    idPublisher = -1;
                    jedis.set(stKey1,String.valueOf(idPublisher));
                    jedis.expire(stKey1,300);
                }
                closeConnection();
            }

            if (idPublisher == -1){
                throw new CampaignNotFoundException();
            }

            String stKey2 = "SELECT ID_campaign FROM adsconfiguration.Campaign_Advertiser ad JOIN " +
                    " adsconfiguration.Exclusion ex ON ex.ID_advertiser = ad.ID_advertiser WHERE ID_publisher = " + idPublisher + " ;";
            String cache2 = jedis.get(stKey2);
            if(cache2!=null){
                System.out.println("cache2 used");
                exclusions = Jackson.fromJsonString(cache2,ArrayList.class);
            } else {
                System.out.println("cache2 NOT used");
                openConnection();
                ResultSet rs = executeQuery("SELECT ID_campaign FROM adsconfiguration.Campaign_Advertiser ad JOIN " +
                        " adsconfiguration.Exclusion ex ON ex.ID_advertiser = ad.ID_advertiser WHERE ID_publisher = " + idPublisher + " ;");
                exclusions = new ArrayList<>();
                while (rs.next()) {
                    exclusions.add(rs.getInt("ID_campaign"));
                }
                closeConnection();
                jedis.set(stKey2,Jackson.toJsonString(exclusions));
                jedis.expire(stKey2,300);
            }

            for(Integer i : campaigns){
                if(!exclusions.contains(i))
                    list.add(i);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }
}