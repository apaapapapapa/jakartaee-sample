package com.example.sample.web.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import com.example.sample.dto.DetailRowView;
import com.example.sample.dto.DetailSubmitForm;
import com.example.sample.exception.BusinessException;
import com.example.sample.model.Status;
import com.example.sample.service.DetailService;

@Named
@ViewScoped
public class DetailListBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_USERID = "user1";

    private final DetailService detailService;
    private final FacesContext facesContext;

    @Getter
    @Setter
    private transient List<DetailRowView> rows;

    @Getter
    @Setter
    private DetailSubmitForm form = new DetailSubmitForm();

    private boolean initialized;

    @Inject
    public DetailListBean(final DetailService detailService, final FacesContext facesContext) {
        this.detailService = detailService;
        this.facesContext = facesContext;
    }

    @PostConstruct
    public void init() {
        String userId = form.getLoginUserId();
        if (userId == null || userId.isBlank()) {
            form.setLoginUserId(DEFAULT_USERID);
        }
        reloadRows();
    }

    public void onPreRenderView() {
        if (initialized) {
            return;
        }
        init();
        initialized = true;
    }

    public void onUserStatusFilterChange() {
        form.clearSelections();
        reloadRows();
    }

    public void onReload() {
        reloadRows();
        addMessage(FacesMessage.SEVERITY_INFO, "最新の一覧に更新しました。");
    }

    public Status[] getAllStatuses() {
        return Status.values();
    }

    public void submit() {
        try {
            final String userId = form.getLoginUserId();
            final List<Long> selectedIds = form.getSelectedIds();

            detailService.apply(selectedIds, userId);

            addMessage(FacesMessage.SEVERITY_INFO, "申請が完了しました。");
            form.clearSelections();
            reloadRows();
        } catch (final BusinessException ex) {
            addMessage(FacesMessage.SEVERITY_WARN, ex.getMessage());
        } catch (final Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "エラーが発生しました。管理者に連絡してください。");
        }
    }

    // ===== private helpers =====

    private void reloadRows() {
        final String userId = form.getLoginUserId();
        rows = detailService.getListForLoginUser(userId, form.getFilterStatus());
        syncSelectionsWithRows();
    }

    private void syncSelectionsWithRows() {
        final List<Long> idsOnScreen = rows.stream()
            .map(DetailRowView::getDetailId)
            .toList();
        form.getSelected().keySet().retainAll(idsOnScreen);
        idsOnScreen.forEach(id -> form.getSelected().putIfAbsent(id, false));
    }

    private void addMessage(final FacesMessage.Severity sev, final String msg) {
        facesContext.addMessage(null, new FacesMessage(sev, msg, null));
    }
}
