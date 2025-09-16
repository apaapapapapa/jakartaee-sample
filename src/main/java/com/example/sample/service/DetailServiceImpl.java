package com.example.sample.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.sample.dto.DetailRowView;
import com.example.sample.exception.BusinessException;
import com.example.sample.model.Detail;
import com.example.sample.model.Status;
import com.example.sample.repository.DetailRepository;

@ApplicationScoped
public class DetailServiceImpl implements DetailService {

    private final DetailRepository detailRepository;

    @Inject
    public DetailServiceImpl(final DetailRepository detailRepository){
        this.detailRepository = detailRepository;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DetailRowView> getListForLoginUser(final String userId, final Status status) {
        requireUserId(userId);
        return detailRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(d -> new DetailRowView(d.getDetailId(), d.getTitle(), d.getStatus().getLabel()))
                .toList();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void apply(final List<Long> selectedDetailIds, final String userId) {
        requireUserId(userId);
        if (selectedDetailIds == null || selectedDetailIds.isEmpty()) {
            throw new BusinessException("申請する明細を1件以上選択してください。");
        }

        final List<Detail> targets =
                detailRepository.findByDetailIdInAndOwnerUserId(selectedDetailIds, userId);

        final Set<Long> foundIds = targets.stream()
                .map(Detail::getDetailId)
                .collect(Collectors.toSet());
        final List<Long> missing = selectedDetailIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
        if (!missing.isEmpty()) {
            throw new BusinessException("申請対象の明細が見つからない、もしくは権限がありません: " + missing);
        }

        final List<Long> invalid = targets.stream()
                .filter(d -> d.getStatus() != Status.DRAFT)
                .map(Detail::getDetailId)
                .toList();
        if (!invalid.isEmpty()) {
            throw new BusinessException("申請できない状態の明細が含まれています（ID: " + invalid + "）。");
        }

        for (final Long id : selectedDetailIds) {
            detailRepository.lockAndMarkRequested(id, userId);
        }
    }

    private static void requireUserId(final String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException("ログインユーザーが特定できません。");
        }
    }
}