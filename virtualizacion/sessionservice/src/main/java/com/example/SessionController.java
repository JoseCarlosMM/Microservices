package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by josec on 6/10/2016.
 */
@RestController
public class SessionController {
    @Autowired
    Handler handler;

    @RequestMapping(value = "/session",method = RequestMethod.POST, produces = "application/json", consumes="application/json")
    public ArrayList getSession(
            @RequestBody ArrayList<ImpressionDto> impressions
    )  {
        return handler.execute(impressions);
    }
}