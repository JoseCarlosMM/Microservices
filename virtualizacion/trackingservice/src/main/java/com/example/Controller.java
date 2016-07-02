package com.example;

import com.example.base.KinesisConnectorExecutor;
import com.example.base.KinesisMessageModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by josec on 7/2/2016.
 */

@RestController
public class Controller {

    private static final String CONFIG_FILE = "RedshiftBasicSample.properties";

    @RequestMapping(value = "/tracking",method = RequestMethod.GET)
    public void kinesis(
    ) throws ParseException {
        String currentDate = getDate();

        String content ="{\n" +
                "\t\"session\": \"te111\",\n" +
                "\t\"campaign_id\": 533,\n" +
                "\t\"bid\": 10,\n" +
                "\t\"comission\": 5,\n" +
                "\t\"publisher_id\": 1,\n" +
                "\t\"advertiser_id\": 1,\n" +
                "\t\"query_id\": \"lds\",\n" +
                "\t\"ad_id\": 2,\n" +
                "\t\"ad_url\": \"url pruebas\",\n" +
                "\t\"click_url\": \"click loadb\",\n" +
                "\t\"headline\": \"head\",\n" +
                "\t\"description\": \"desc\",\n" +
                "\t\"created_at\":\""+ currentDate +"\""+
                "}";
        KinesisConnectorExecutor<KinesisMessageModel, byte[]> redshiftExecutor = new RedshiftBasicExecutor(CONFIG_FILE,content);
        redshiftExecutor.executeWorker = false;
        redshiftExecutor.run();


    }

    private String getDate() throws ParseException {
        SimpleDateFormat FORMATTER;
        Date date =new Date();
        FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return FORMATTER.format(date);
    }

}
