package com.example;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {
    public ArrayList execute( ArrayList<ImpressionDto> impressionDtos, Integer campaignPublisher) {
        ArrayList<Integer> campaigns = new ArrayList<>();
        for (ImpressionDto dto : impressionDtos){
            campaigns.add(dto.campaignId);
        }

        ArrayList<ImpressionDto> list = new ArrayList<ImpressionDto>();
        try
        {
            openConnection();
            Double comission = null;
            Integer idPublisher = null;
            ResultSet rs = executeQuery("SELECT Comission, ID_publisher FROM adsconfiguration.Campaign_Publisher WHERE ID_campaign = " + campaignPublisher + ";");
            if(rs.next()){
                comission =rs.getDouble("Comission");
                idPublisher = rs.getInt("ID_publisher");
            }
            ResultSet rs2 = executeQuery("SELECT ID_campaign, Bid, ID_advertiser FROM adsconfiguration.Campaign_Advertiser WHERE Budget > 0 ;");
            while (rs2.next()) {
                if(campaigns.contains(rs2.getInt("ID_campaign")))
                {
                    ImpressionDto impressionDto = impressionDtos.get(campaigns.indexOf(rs2.getInt("ID_campaign")));
                    impressionDto.comission = comission;
                    impressionDto.campaignId = rs2.getInt("ID_campaign");
                    impressionDto.publisherId = idPublisher;
                    impressionDto.bid = rs2.getDouble("Bid");
                    impressionDto.advertiserId = rs2.getInt("ID_advertiser");
                    list.add(impressionDto);
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
