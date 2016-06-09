package com.example;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by josec on 6/5/2016.
 */
@RestController
public class TargetingController {
    @RequestMapping(value = "/targeting",method = RequestMethod.POST, produces = "application/json", consumes="application/json")
    public ArrayList targeting(
            @RequestParam(value = "zip_code", required = true) Integer zipcode,
            @RequestBody ArrayList<Integer> campaigns
    ) throws BaseHandler.CustomException {
        Handler handler = new Handler();
        return handler.execute(zipcode,campaigns);
    }
}