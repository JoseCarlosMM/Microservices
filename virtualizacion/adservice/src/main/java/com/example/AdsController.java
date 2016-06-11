package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by josec on 6/5/2016.
 */
@RestController
public class AdsController {
    @Autowired
    Handler handler;

    @RequestMapping(value = "/ads",method = RequestMethod.POST, produces = "application/json", consumes="application/json")
    public ArrayList getAds(
            @RequestBody ArrayList<Integer> campaigns,
            @RequestParam Integer campaignPublisher
    ) throws BaseHandler.CustomException {
        return handler.execute(campaigns,campaignPublisher);
    }
}