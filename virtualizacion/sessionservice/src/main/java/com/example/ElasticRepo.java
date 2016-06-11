package com.example;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Created by josec on 6/10/2016.
 */
public interface ElasticRepo extends ElasticsearchRepository<ImpressionDto,String> {
    List<ImpressionDto> findBySession(String session);
}
