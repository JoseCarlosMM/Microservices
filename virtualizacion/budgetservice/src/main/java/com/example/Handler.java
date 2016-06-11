package com.example;

import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/5/2016.
 */
public class Handler extends BaseHandler {
    public void execute( Integer idCampaign, Double Bid) throws CustomException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Jedis jedis = new Jedis("localhost");
        String stBudget =jedis.get(String.valueOf(idCampaign));

        if(stBudget==null){
            return;
        }

        Double budget= Double.valueOf(stBudget);
        budget -= Bid;
        jedis.set(String.valueOf(idCampaign),String.valueOf(budget));

        if (budget>0){
            return;
        }

        try
        {
            openConnection();

            executeUpdate("update adsconfiguration.Campaign_Advertiser set status = 0 WHERE ID_campaign ="+idCampaign+" ;");

            closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}