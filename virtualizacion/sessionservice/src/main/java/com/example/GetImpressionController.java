package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by josec on 6/10/2016.
 */
@RestController
public class GetImpressionController {
    @Autowired
    GetImpressionHandler handler;

    @RequestMapping(value = "/impressions",method = RequestMethod.GET, produces = "application/json")
    public ImpressionDto getSession(
            @RequestParam String id
    )  {
        return handler.execute(id);
    }
}