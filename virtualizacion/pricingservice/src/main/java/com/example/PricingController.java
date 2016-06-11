package com.example;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by josec on 6/10/2016.
 */
@RestController
public class PricingController {
    @RequestMapping(value = "/pricing",method = RequestMethod.POST, produces = "application/json",consumes="application/json")
    public ArrayList matching(
            @RequestBody ArrayList<ImpressionDto> impressions,
            @RequestParam Integer campaignPublisher
    ) {
        Handler handler = new Handler();
        return handler.execute(impressions,campaignPublisher);
    }
}
