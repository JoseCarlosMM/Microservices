package com.example;

import com.example.Exceptions.CampaignNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {
    public ArrayList execute(Integer campaign, ArrayList<Integer> campaigns) throws CustomException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {
            openConnection();
            int idPublisher;
            ResultSet rs = executeQuery("SELECT ID_publisher FROM adsconfiguration.Campaign_Publisher WHERE ID_campaign = " + campaign + ";");
            if(rs.next()){
                idPublisher = rs.getInt("ID_publisher");
            }
            else {
                throw new CampaignNotFoundException();
            }

            ResultSet rs2 = executeQuery("SELECT ID_campaign FROM adsconfiguration.Targeting WHERE Zip_Code = " + zip_code + " ;");
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