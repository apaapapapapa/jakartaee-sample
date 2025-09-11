package com.example.sample.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubmitForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "申請する明細を選択してください。")
    private List<Long> selectedIds = new ArrayList<>();

    public void clear() {
        selectedIds.clear();
    }
}