package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/5/2016.
 */
public class Handler extends BaseHandler {
    public ArrayList execute( ArrayList<Integer> campaigns) throws CustomException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {
            openConnection();

            ResultSet rs2 = executeQuery("SELECT ID_campaign FROM adsconfiguration.Campaign_Advertiser WHERE Budget > 0 ;");
            while (rs2.next()) {
                if(campaigns.contains(rs2.getInt("ID_campaign")))
                    list.add(rs2.getInt("ID_campaign"));
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