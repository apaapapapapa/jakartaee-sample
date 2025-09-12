package com.example.sample.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import com.example.sample.dto.DetailRowView;
import com.example.sample.exception.BusinessException;
import com.example.sample.model.Detail;
import com.example.sample.model.Status;
import com.example.sample.repository.DetailRepository;

@ApplicationScoped
public class DetailServiceImpl implements DetailService {

    private DetailRepository detailRepository;

    @Inject
    public DetailServiceImpl(final DetailRepository detailRepository){
        this.detailRepository = detailRepository;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DetailRowView> getListForLoginUser(final String userId) {
        return detailRepository.findByUserId(userId).stream()
                .map(detail -> new DetailRowView(detail.getDetailId(), detail.getTitle(), toLabel(detail.getStatus())))
                .toList();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void apply(final List<Long> selectedDetailIds, final String userId) {
        if (selectedDetailIds == null || selectedDetailIds.isEmpty()) {
            throw new BusinessException("申請する明細を1件以上選択してください。");
        }
        final List<Detail> targets = detailRepository.findByIdsForUser(selectedDetailIds, userId);
        if (targets.isEmpty()) {
            throw new BusinessException("申請対象の明細が見つかりません。");
        }
        // すべて DRAFT のものだけ申請可能
        for (final Detail detail : targets) {
            if (detail.getStatus() != Status.DRAFT) {
                throw new BusinessException("申請できない状態の明細が含まれています（ID: " + detail.getDetailId() + "）。");
            }
        }
        // 申請へ状態遷移（悲観ロックで競合対策）
        for (final Detail detail : targets) {
            detailRepository.markRequestedWithLock(detail);
        }
    }

    private String toLabel(final Status status) {
        return switch (status) {
            case DRAFT -> "下書き";
            case REQUESTED -> "申請中";
            case APPROVED -> "承認済み";
        };
    }
}
