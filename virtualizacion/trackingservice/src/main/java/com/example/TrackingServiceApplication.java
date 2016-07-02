package com.example;

import com.example.base.KinesisConnectorExecutor;
import com.example.base.KinesisMessageModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
public class TrackingServiceApplication {
	private static final String CONFIG_FILE = "RedshiftBasicSample.properties";

	public static void main(String[] args) throws ParseException {

		SpringApplication.run(TrackingServiceApplication.class, args);

		try {
			Class.forName("com.amazon.redshift.jdbc41.Driver");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Could not load redshift driver");
		}
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
		redshiftExecutor.executeWorker = true;
		redshiftExecutor.run();

	}

	private static String getDate() throws ParseException {
		SimpleDateFormat FORMATTER;
		Date date =new Date();
		FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return FORMATTER.format(date);
	}
}
