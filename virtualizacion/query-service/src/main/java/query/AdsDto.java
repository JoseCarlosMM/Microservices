package query;

import java.util.ArrayList;

/**
 * Created by josec on 6/11/2016.
 */
public class AdsDto {
    public Header header;
    public ArrayList<Body> body;

    public static class Header{
        public String query_id;
    }
    public static class Body{
        public String impression_id;
        public String headline;
        public String description;
        public String click_url;
    }
}

