package com.example;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by josec on 6/10/2016.
 */
@Document(
        indexName = "impression",
        type = "impression",
        shards = 1,
        replicas = 0,
        refreshInterval = "-1"
)
public class ImpressionDto {
    public Integer campaignId;
    public Double bid;
    public Double comission;
    @Id
    public  String session;
    public Integer publisherId;
    public Integer advertiserId;
    public Integer idAd;
    public String urlAd;
    public String clickUrl;
}
