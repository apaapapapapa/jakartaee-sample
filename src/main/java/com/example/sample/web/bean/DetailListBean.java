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
import com.example.sample.service.DetailService;

@Named
@ViewScoped
public class DetailListBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private DetailService detailService;

    @Getter
    @Setter
    private transient List<DetailRowView> rows;

    @Getter
    @Setter
    private DetailSubmitForm form;

    /**
     * Default constructor.
     */
    @Inject
    public DetailListBean(final DetailService detailService) {
        this.detailService = detailService;
        this.form = new DetailSubmitForm();
    }

    @PostConstruct
    public void init() {
        String userId = form.getLoginUserId();
        if (userId == null || userId.isBlank()) {
            userId = "user1";
            form.setLoginUserId(userId);
        }
        reloadRows(userId);
    }

    private void reloadRows(final String userId) {
        rows = detailService.getListForLoginUser(userId);
        final List<Long> idsOnScreen = rows.stream()
                .map(DetailRowView::getDetailId)
                .toList();
        form.getSelected().keySet().retainAll(idsOnScreen);
        idsOnScreen.forEach(detailId -> form.getSelected().putIfAbsent(detailId, false));
    }

    public void submit() {
        try {
            final String userId = form.getLoginUserId();
            final List<Long> selectedIds = form.getSelectedIds();
            detailService.apply(selectedIds, userId);
            addInfo("申請が完了しました。");
            reloadRows(userId);
            form.clearSelections();
        } catch (final BusinessException ex) {
            addWarn(ex.getMessage());
        } catch (final Exception ex) {
            addError("エラーが発生しました。管理者に連絡してください。");
        }
    }

    private void addInfo(final String msg) {
        FacesContext
                .getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void addWarn(final String msg) {
        FacesContext
                .getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null));
    }

    private void addError(final String msg) {
        FacesContext
                .getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
