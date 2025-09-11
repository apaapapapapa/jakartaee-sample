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
    public DetailServiceImpl(DetailRepository detailRepository){
        this.detailRepository = detailRepository;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DetailRowView> getListForLoginUser(String userId) {
        return detailRepository.findByUserId(userId).stream()
                .map(d -> new DetailRowView(d.getId(), d.getTitle(), toLabel(d.getStatus())))
                .toList();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void apply(List<Long> selectedDetailIds, String userId) {
        if (selectedDetailIds == null || selectedDetailIds.isEmpty()) {
            throw new BusinessException("申請する明細を1件以上選択してください。");
        }
        var targets = detailRepository.findByIdsForUser(selectedDetailIds, userId);
        if (targets.isEmpty()) {
            throw new BusinessException("申請対象の明細が見つかりません。");
        }
        // すべて DRAFT のものだけ申請可能
        for (Detail d : targets) {
            if (d.getStatus() != Status.DRAFT) {
                throw new BusinessException("申請できない状態の明細が含まれています（ID: " + d.getId() + "）。");
            }
        }
        // 申請へ状態遷移（悲観ロックで競合対策）
        for (Detail d : targets) {
            detailRepository.markRequestedWithLock(d);
        }
    }

    private String toLabel(Status s) {
        return switch (s) {
            case DRAFT -> "下書き";
            case REQUESTED -> "申請中";
            case APPROVED -> "承認済み";
        };
    }
}