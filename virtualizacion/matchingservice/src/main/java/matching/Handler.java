package matching;

import com.amazonaws.util.json.Jackson;
import matching.Exceptions.CategoryNotFoundException;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
public class Handler extends BaseHandler {

    private String REDIS_URL ="localhost";
    //private String REDIS_URL ="redisads.qkoiz3.0001.use1.cache.amazonaws.com.com";

    public ArrayList execute(Integer category) throws CategoryNotFoundException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try
        {
            String stKey1= "SELECT * FROM icampaignadv WHERE Category = " + category + ";";
            String stKey2 = "SELECT campaign FROM icampaignadv WHERE Category = " + category +
                    " AND Status = 1 AND active = 1 AND Budget > 0;";
            Jedis jedis = new Jedis(REDIS_URL);
            String cache1 =jedis.get(stKey1);
            String cache2 = jedis.get(stKey2);
            boolean exists;
            if(cache1!=null){
                System.out.println("cache1 used");
                exists = cache1.equalsIgnoreCase("true");
            } else {
                System.out.println("cache1 NOT used");
                openConnection();
                ResultSet rs2 = executeQuery("SELECT * FROM icampaignadv WHERE Category = " + category + ";");
                exists = rs2.next();
                closeConnection();
                if(exists){
                    jedis.set(stKey1,"true");
                } else {
                    jedis.set(stKey1,"false");
                }

                jedis.expire(stKey1,300);
            }

            if(!exists){
                throw new CategoryNotFoundException();
            }


            if(cache2!=null){
                System.out.println("cache2 used");
                list = Jackson.fromJsonString(cache2,ArrayList.class);
            } else {
                System.out.println("cache2 NOT used");
                openConnection();
                ResultSet rs = executeQuery("SELECT campaign FROM icampaignadv WHERE Category = " + category +
                        " AND Status = 1 AND active = 1 AND Budget > 0;");
                list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rs.getInt("campaign"));
                }
                closeConnection();
                jedis.set(stKey2,Jackson.toJsonString(list));
                jedis.expire(stKey2,300);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }
}
