package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {
    public ArrayList execute(Integer limit, ArrayList<Integer> campaigns) throws CustomException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {
            openConnection();

            ResultSet rs2 = executeQuery("SELECT c.ID_campaign FROM adsconfiguration.Campaign_Advertiser c" +
                    " JOIN adsconfiguration.Campaign_Ads a ON c.ID_campaign = a.ID_campaign GROUP BY c.ID_campaign ORDER BY Bid DESC;");
            int contLimit=0;
            while (rs2.next()) {
                if(contLimit>=limit){
                    break;
                }
                if(campaigns.contains(rs2.getInt("ID_campaign")))
                {
                    list.add(rs2.getInt("ID_campaign"));
                    contLimit++;
                }

            }

            closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }
}