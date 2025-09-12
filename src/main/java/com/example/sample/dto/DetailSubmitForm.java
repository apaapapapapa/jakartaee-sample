package com.example.sample.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailSubmitForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loginUserId;

    @NotNull(message = "承認者IDを入力してください")
    @Positive(message = "承認者IDは1以上の数値で入力してください")
    private Long approverId;

    @NotNull(message = "検閲者IDを入力してください")
    @Positive(message = "検閲者IDは1以上の数値で入力してください")
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
