package query;

import com.amazonaws.util.IOUtils;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.Jackson;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import query.Exceptions.CampaignNotFoundException;
import query.Exceptions.CategoryNotFoundException;
import query.Exceptions.NoAdsException;
import query.Exceptions.ZipCodeNotfound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by josec on 6/4/2016.
 */
@RestController
public class QueryController {

    @Autowired
    private EurekaClient discoveryClient;

    private RestTemplate restTemplate;

    private static String MATCHING_SERVICE="MATCHING-SERVICE";
    private static String TARGETING_SERVICE="TARGETING-SERVICE";
    private static String RANKING_SERVICE="RANKING-SERVICE";
    private static String ADS_SERVICE="ADS-SERVICE";
    private static String EXCLUSION_SERVICE="EXCLUSION-SERVICE";

    @RequestMapping(value = "/query",method = RequestMethod.GET, produces = "application/json")
    public AdsDto query (
            @RequestParam(value = "category", required = true) Integer category,
            @RequestParam(value = "campaign", required = true) Integer campaign,
            @RequestParam(value = "maximum", required = false) Integer maximum,
            @RequestParam(value = "zip_code", required = true) Integer zip_code
    )
    {
        if(maximum==null)
            maximum=10;

        String urlMatchingService = getUrl(MATCHING_SERVICE);
        String urlTargetingService = getUrl(TARGETING_SERVICE);
        String urlRankingService = getUrl(RANKING_SERVICE);
        String urlAdsService = getUrl(ADS_SERVICE);
        String urlExclusionService = getUrl(EXCLUSION_SERVICE);

        ArrayList<Ad> listAds = new ArrayList();
        ArrayList<Integer> listCampaignsSubasta = new ArrayList<>();

        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        ArrayList<Integer> listCampaignsMatching = restTemplate.getForObject(urlMatchingService+"/matching?category="+category,ArrayList.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(Jackson.toJsonString(listCampaignsMatching), headers);
        ArrayList<Integer> listCampaignsTargeting = restTemplate.postForObject(urlTargetingService+"/targeting?zip_code="+zip_code,entity,ArrayList.class);

        entity = new HttpEntity<>(Jackson.toJsonString(listCampaignsMatching), headers);
        ArrayList<Integer> listCampaignsExclusionFiltered = restTemplate.postForObject(urlExclusionService+"/exclusion?campaign="+campaign,entity,ArrayList.class);

        for(Integer i: listCampaignsMatching){
            if(listCampaignsTargeting.contains(i) && listCampaignsExclusionFiltered.contains(i)){
                listCampaignsSubasta.add(i);
            }
        }

        entity = new HttpEntity<>(Jackson.toJsonString(listCampaignsSubasta), headers);
        listCampaignsSubasta = restTemplate.postForObject(urlRankingService+"/ranking?limit="+maximum,entity,ArrayList.class);

        entity = new HttpEntity<>(Jackson.toJsonString(listCampaignsSubasta), headers);
        AdsDto finalDto = restTemplate.postForObject(urlAdsService+"/ads?campaignPublisher="+campaign,entity,AdsDto.class);

        if (finalDto.body == null || finalDto.body.isEmpty())
            throw  new NoAdsException();

        return finalDto;
    }

    public String getUrl(String serviceName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(serviceName, false);
        return instance.getHomePageUrl();
    }

    public class CustomResponseErrorHandler implements ResponseErrorHandler {

        private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

        public boolean hasError(ClientHttpResponse response) throws IOException {
            return errorHandler.hasError(response);
        }

        public void handleError(ClientHttpResponse response) throws IOException {
            String errorString = IOUtils.toString(response.getBody());
            JSONObject json;
            try {
                json = new JSONObject(errorString);
                CategoryNotFoundException exception = new CategoryNotFoundException();
                if(json.getString("message").equalsIgnoreCase("Category not found"))
                {
                    throw new CategoryNotFoundException();
                }
                if(json.getString("message").equalsIgnoreCase("Zip code not found"))
                {
                    throw new ZipCodeNotfound();
                }
                if(json.getString("message").equalsIgnoreCase("Campaign not found"))
                {
                    throw new CampaignNotFoundException();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
