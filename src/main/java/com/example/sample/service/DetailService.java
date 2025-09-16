package com.example.sample.service;

import java.util.List;

import com.example.sample.dto.DetailRowView;
import com.example.sample.model.Status;

public interface DetailService {

    List<DetailRowView> getListForLoginUser(String userId, Status status);
    
    void apply(List<Long> selectedDetailIds, String userId);
}