package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by josec on 6/10/2016.
 */
@Service
public class ElasticService implements ServiceInterface{
    @Autowired
    ElasticRepo repo;

    public ImpressionDto save(ImpressionDto post) {
        repo.save(post);
        return post;
    }

    public ImpressionDto findOne(String id) {
        return repo.findOne(id);
    }

    @Override
    public List<ImpressionDto> findBySession(String session) {
        return repo.findBySession(session);
    }
}
