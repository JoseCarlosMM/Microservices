package com.example;

import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by josec on 6/11/2016.
 */
public class BudgetHandler extends BaseHandler {
    private String REDIS_URL ="localhost";
    //private String REDIS_URL ="redisads.qkoiz3.0001.use1.cache.amazonaws.com.com";

    public void execute()  {
        try
        {
            Jedis jedis = new Jedis(REDIS_URL);
            openConnection();
            executeUpdate("update adsconfiguration.Campaign_Advertiser set active = 1 WHERE active = 0 AND status = 1 AND Budget > 0;");
            ResultSet rs2 = executeQuery("SELECT ID_campaign, Budget FROM adsconfiguration.Campaign_Advertiser;");
            while (rs2.next()) {

                Campaign campaign = new Campaign();

                campaign.id = rs2.getInt("ID_campaign");
                campaign.budget = rs2.getDouble("Budget");

                jedis.set(String.valueOf(campaign.id),String.valueOf(campaign.budget));
            }
            closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}