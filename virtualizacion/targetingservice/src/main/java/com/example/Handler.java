package com.example;

import com.example.exceptions.ZipCodeNotfound;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {
    public ArrayList execute(Integer zip_code, ArrayList<Integer> campaigns) throws CustomException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {
            openConnection();

            ResultSet rs = executeQuery("SELECT ID_Zipcode FROM adsconfiguration.Zip_Code WHERE ID_Zipcode = " + zip_code + ";");
            if(!rs.next()){
                throw new ZipCodeNotfound();
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