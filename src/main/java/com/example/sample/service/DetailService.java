package com.example.sample.service;

import java.util.List;

import com.example.sample.dto.DetailRowView;

public interface DetailService {

    List<DetailRowView> getListForLoginUser(String userId);
    
    void apply(List<Long> selectedDetailIds, String userId);
}