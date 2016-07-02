package com.example;

import com.amazonaws.util.json.Jackson;
import com.example.exceptions.ZipCodeNotfound;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {

    //private String REDIS_URL ="localhost";
    private String REDIS_URL ="redisads.qkoiz3.0001.use1.cache.amazonaws.com.com";

    public ArrayList execute(Integer zip_code, ArrayList<Integer> campaigns) throws CustomException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {
            String stKey1= "SELECT ID_Zipcode FROM adsconfiguration.Zip_Code WHERE ID_Zipcode = " + zip_code + ";";
            String stKey2 = "SELECT ID_campaign FROM adsconfiguration.Targeting WHERE Zip_Code = " + zip_code + " ;";
            Jedis jedis = new Jedis(REDIS_URL);
            String cache1 =jedis.get(stKey1);
            String cache2 = jedis.get(stKey2);
            boolean exists;
            if(cache1!=null){
                System.out.println("cache1 used");
                exists = cache1.equalsIgnoreCase("true");
            } else {
                System.out.println("cache1 NOT used");
                openConnection();
                ResultSet rs2 = executeQuery("SELECT ID_Zipcode FROM adsconfiguration.Zip_Code WHERE ID_Zipcode = " + zip_code + ";");
                exists = rs2.next();
                closeConnection();
                if(exists){
                    jedis.set(stKey1,"true");
                } else {
                    jedis.set(stKey1,"false");
                }

                jedis.expire(stKey1,300);
            }

            if(!exists){
                throw new ZipCodeNotfound();
            }

            ArrayList<Integer> listCache;
            if(cache2!=null){
                System.out.println("cache2 used");
                listCache = Jackson.fromJsonString(cache2,ArrayList.class);
            } else {
                System.out.println("cache2 NOT used");
                openConnection();
                ResultSet rs = executeQuery("SELECT ID_campaign FROM adsconfiguration.Targeting WHERE Zip_Code = " + zip_code + " ;");
                listCache = new ArrayList<>();
                while (rs.next()) {
                    listCache.add(rs.getInt("ID_campaign"));
                }
                closeConnection();
                jedis.set(stKey2,Jackson.toJsonString(listCache));
                jedis.expire(stKey2,300);
            }

            for(Integer number: listCache){
                if(campaigns.contains(number))
                    list.add(number);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }
}