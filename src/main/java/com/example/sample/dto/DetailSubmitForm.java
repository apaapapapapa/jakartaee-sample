package com.example.sample.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.sample.model.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailSubmitForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loginUserId;

    private Status filterStatus;

    private Long approverId;

    private Long reviewerId;

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
