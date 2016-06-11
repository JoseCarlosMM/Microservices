package com.example;

import java.util.List;

/**
 * Created by josec on 6/10/2016.
 */
public interface ServiceInterface {
    ImpressionDto save(ImpressionDto impressionDto);
    List<ImpressionDto> findBySession(String session);
}
