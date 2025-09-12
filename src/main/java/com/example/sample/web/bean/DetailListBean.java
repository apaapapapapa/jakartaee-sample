package com.example.sample.web.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import com.example.sample.dto.DetailRowView;
import com.example.sample.dto.SubmitForm;
import com.example.sample.exception.BusinessException;
import com.example.sample.service.DetailService;

@Named
@ViewScoped
public class DetailListBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private DetailService detailService;

    @Inject
    public DetailListBean(DetailService detailService){
        this.detailService = detailService;
    }

    @Getter
    private List<DetailRowView> rows;

    @Setter
    @Getter
    private SubmitForm form = new SubmitForm();

    @PostConstruct
    public void init() {
        final String userId = loginUserId();
        rows = detailService.getListForLoginUser(userId);
    }

    public void submit() {
        try {
            final String userId = loginUserId();
            detailService.apply(form.getSelectedIds(), userId);
            addInfo("申請が完了しました。");
            // 再読み込み
            rows = detailService.getListForLoginUser(userId);
            form.clear();
        } catch (BusinessException ex) {
            addWarn(ex.getMessage());
        } catch (Exception ex) {
            addError("エラーが発生しました。管理者に連絡してください。");
        }
    }

    private String loginUserId() {
        return "user1"; 
    }

    private void addInfo(final String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }
    private void addWarn(final String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null));
    }
    private void addError(final String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

}
