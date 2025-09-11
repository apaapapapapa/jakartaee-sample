package com.example.sample.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailRowView {

    private Long id;

    private String title;

    private String status;

    private boolean selected;

    public DetailRowView(Long id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

}