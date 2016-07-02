package matching;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import matching.Exceptions.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
@RestController
public class MatchingController {

    @Autowired
    private EurekaClient discoveryClient;
    private RestTemplate restTemplate;
    private static String MATCHING_SERVICE="MATCHING-SERVICE";

    @RequestMapping(value = "/search",method = RequestMethod.GET, produces = "application/json")
    public ArrayList matching(
    ) throws CategoryNotFoundException {
        restTemplate = new RestTemplate();
        String urlMatchingService = getUrl(MATCHING_SERVICE);
        ArrayList<Integer> listCampaignsMatching = restTemplate.getForObject(urlMatchingService+"/matching?category="+5,ArrayList.class);
        return listCampaignsMatching;
    }

    public String getUrl(String serviceName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(serviceName, false);
        return instance.getHomePageUrl();
    }

}
