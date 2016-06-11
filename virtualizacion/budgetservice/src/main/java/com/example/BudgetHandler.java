package com.example;

import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by josec on 6/11/2016.
 */
public class BudgetHandler extends BaseHandler {
    public void execute()  {
        try
        {
            Jedis jedis = new Jedis("localhost");
            openConnection();
            executeUpdate("update adsconfiguration.Campaign_Advertiser set status =1 WHERE status = 0 AND Budget > 0;");
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