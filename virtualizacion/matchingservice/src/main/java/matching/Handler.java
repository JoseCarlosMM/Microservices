package matching;

import matching.Exceptions.CategoryNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {
    public ArrayList execute(Integer category) throws CategoryNotFoundException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {
            openConnection();
            ResultSet rs2 = executeQuery("SELECT ID_campaign FROM adsconfiguration.Campaign_Advertiser WHERE Category = " + category +
                    ";");

            if(!rs2.next()){
                throw new CategoryNotFoundException();
            }

            ResultSet rs = executeQuery("SELECT ID_campaign FROM adsconfiguration.Campaign_Advertiser WHERE Category = " + category +
                    " AND Status = 1 AND Budget > 0;");
            while (rs.next()) {
                list.add(rs.getInt("ID_campaign"));
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
