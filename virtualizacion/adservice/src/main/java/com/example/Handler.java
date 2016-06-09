package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {
    Random rand;
    public Handler(){
        rand = new Random();
    }
    public ArrayList execute(ArrayList<Integer> campaigns) throws CustomException {
        ArrayList<Ad> listAds = new ArrayList<Ad>();
        Integer idCampaign=-1;
        try
        {
            openConnection();

            ResultSet rs2 = executeQuery("SELECT c.ID_campaign, Headline, Description, Url  FROM adsconfiguration.Campaign_Ads c JOIN " +
                    " adsconfiguration.Ads a ON a.ID_ad = c.ID_ad JOIN adsconfiguration.Campaign_Advertiser ca ON c.ID_campaign = ca.ID_campaign ORDER BY Bid DESC");
            ArrayList<Ad> tempAds= new ArrayList<>();
            boolean firstTime=true;
            while (rs2.next()) {
                if(campaigns.contains(rs2.getInt("ID_campaign")))
                {
                    if(rs2.getInt("ID_campaign")!=idCampaign )
                    {
                        //do the random
                        if(!firstTime) {
                            listAds.add(tempAds.get(getRandomNumber(tempAds.size())));
                        }else{
                            firstTime=false;
                        }

                        idCampaign = rs2.getInt("ID_campaign");
                        tempAds = new ArrayList<>();
                    }

                    Ad ad= new Ad();
                    ad.description=rs2.getString("Description");
                    ad.headline=rs2.getString("Headline");
                    ad.url=rs2.getString("Url");
                    tempAds.add(ad);

                }
            }

            listAds.add(tempAds.get(getRandomNumber(tempAds.size())));

            closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return listAds;
    }

    private int getRandomNumber(int max){

        return rand.nextInt(max);
    }
}