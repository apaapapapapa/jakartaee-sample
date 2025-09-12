package com.example.sample.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailRowView {

    private Long detailId;

    private String title;

    private String status;

    private boolean selected;

public DetailRowView(final Long detailId, final String title, final String status) {
    this.detailId = detailId;
    this.title = title;
    this.status = status;
}

}
