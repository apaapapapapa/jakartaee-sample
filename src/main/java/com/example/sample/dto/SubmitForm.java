package com.example.sample.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SubmitForm implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private final List<Long> selectedIds = new ArrayList<>();

    public List<Long> getSelectedIds() {
        return Collections.unmodifiableList(selectedIds);
    }

    public void setSelectedIds(Collection<Long> ids) {
        selectedIds.clear();
        if (ids != null) {
            selectedIds.addAll(ids);
        }
    }

    public void clear() {
        selectedIds.clear();
    }
}