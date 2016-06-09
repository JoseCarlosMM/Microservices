package com.example.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by josec on 6/5/2016.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Campaign not found")
public class CampaignNotFoundException extends RuntimeException {
}

