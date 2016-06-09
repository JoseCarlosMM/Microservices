package com.example;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by josec on 6/5/2016.
 */
@RestController
public class RankingController {
    @RequestMapping(value = "/ranking",method = RequestMethod.POST, produces = "application/json", consumes="application/json")
    public ArrayList targeting(
            @RequestParam(value = "limit", required = true) Integer limit,
            @RequestBody ArrayList<Integer> campaigns
    ) throws BaseHandler.CustomException {
        Handler handler = new Handler();
        return handler.execute(limit,campaigns);
    }
}