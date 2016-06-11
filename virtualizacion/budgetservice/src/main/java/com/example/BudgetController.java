package com.example;


import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;

/**
 * Created by josec on 6/5/2016.
 */
@RestController
public class BudgetController {
    @RequestMapping(value = "/budget",method = RequestMethod.GET, produces = "application/json")
    public void targeting(
            @RequestParam Integer idCampaign,
            @RequestParam Double Bid
    ) throws BaseHandler.CustomException {
        Handler handler = new Handler();
        handler.execute(idCampaign,Bid);
    }
}