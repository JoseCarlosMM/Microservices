/*
 * Copyright 2013-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.base;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This is the data model for the objects being sent through the Amazon Kinesis streams in the samples
 * 
 */
public class KinesisMessageModel implements Serializable {

    public Integer campaign_id;
    public Double bid;
    public Double comission;
    public  String session;
    public Integer publisher_id;
    public Integer advertiser_id;
    public String query_id;
    public Integer ad_id;
    public String ad_url;
    public String click_url;
    public String headline;
    public String description;
    public Date created_at;

}
