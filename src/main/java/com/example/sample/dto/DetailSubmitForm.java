package com.example.sample.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailSubmitForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "承認者を入力してください")
    private String approverName;

    @NotBlank(message = "検閲者を入力してください")
    private String reviewerName;

    private final Map<Long, Boolean> selected = new LinkedHashMap<>();

    public List<Long> getSelectedIds() {
        return selected.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }

    public void clearSelections() {
        selected.replaceAll((k, v) -> false);
    }

}
