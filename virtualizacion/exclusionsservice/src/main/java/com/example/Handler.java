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
        ArrayList<Integer> exclusions = new ArrayList<Integer>();
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

            ResultSet rs2 = executeQuery("SELECT ID_campaign FROM adsconfiguration.Campaign_Advertiser ad JOIN " +
                    " adsconfiguration.Exclusion ex ON ex.ID_advertiser = ad.ID_advertiser WHERE ID_publisher = " + idPublisher + " ;");

            while (rs2.next()) {
                exclusions.add(rs2.getInt("ID_campaign"));
            }

            for(Integer i : campaigns){
                if(!exclusions.contains(i))
                    list.add(i);
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