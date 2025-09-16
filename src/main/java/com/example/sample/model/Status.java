package com.example.sample.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    DRAFT("下書き"),
    REQUESTED("申請中"),
    APPROVED("承認済み");

    private final String label;

}