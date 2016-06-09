package com.example;


import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by josec on 6/5/2016.
 */
@RestController
public class ExclusionController {
    @RequestMapping(value = "/exclusion",method = RequestMethod.POST, produces = "application/json", consumes="application/json")
    public ArrayList targeting(
            @RequestParam(value = "campaign", required = true) Integer campaign,
            @RequestBody ArrayList<Integer> campaigns
    ) throws BaseHandler.CustomException {
        Handler handler = new Handler();
        return handler.execute(campaign,campaigns);
    }
}