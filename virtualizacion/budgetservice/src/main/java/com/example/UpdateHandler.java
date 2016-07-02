package com.example;

/**
 * Created by josec on 6/11/2016.
 */

import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UpdateHandler extends BaseHandler {
    public void execute()  {
        try
        {
            Jedis jedis = new Jedis("localhost");
            openConnection();
            ResultSet rs2 = executeQuery("SELECT ID_campaign, Budget FROM adsconfiguration.Campaign_Advertiser WHERE status = 1;");
            while (rs2.next()) {

                Campaign campaign = new Campaign();

                campaign.id = rs2.getInt("ID_campaign");
                campaign.budget = rs2.getDouble("Budget");

                if(jedis.get(String.valueOf(campaign.id))==null){
                    jedis.set(String.valueOf(campaign.id),String.valueOf(campaign.budget));
                }
            }
            //Campaigns paused
            ResultSet rs = executeQuery("SELECT ID_campaign, Budget FROM adsconfiguration.Campaign_Advertiser WHERE status = 0;");
            while (rs.next()) {

                Campaign campaign = new Campaign();

                campaign.id = rs.getInt("ID_campaign");
                campaign.budget = rs.getDouble("Budget");

                if(jedis.get(String.valueOf(campaign.id))!=null){
                    jedis.del(String.valueOf(campaign.id));
                }
            }
            closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}