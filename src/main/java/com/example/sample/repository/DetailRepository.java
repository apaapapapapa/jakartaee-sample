package com.example.sample.repository;

import java.util.List;

import com.example.sample.model.Detail;
import com.example.sample.model.Status;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor // Weldの制約で引数なしコンストラクタも必要
public class DetailRepository {

    private EntityManager em;

    @Inject
    public DetailRepository(EntityManager em) {
        this.em = em;
    }
    
    private static final String USER_ID_PARAM = "userId";

    public List<Detail> findByUserId(final String userId) {
        requireUserId(userId);
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.ownerUserId = :userId ORDER BY d.detailId DESC",
                Detail.class)
            .setParameter(USER_ID_PARAM, userId)
            .getResultList();
    }

    public List<Detail> findByUserIdAndStatus(final String userId, final Status status) {
        requireUserId(userId);
        if (status == null) {
            return findByUserId(userId);
        }
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.ownerUserId = :userId AND d.status = :status ORDER BY d.detailId DESC",
                Detail.class)
            .setParameter(USER_ID_PARAM, userId)
            .setParameter("status", status)
            .getResultList();
    }

    public List<Detail> findByDetailIdInAndOwnerUserId(final List<Long> ids, final String userId) {
        requireUserId(userId);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.detailId IN :ids AND d.ownerUserId = :userId",
                Detail.class)
            .setParameter("ids", ids)
            .setParameter(USER_ID_PARAM, userId)
            .getResultList();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void lockAndMarkRequested(final Long detailId, final String userId) {
        requireUserId(userId);
        final Detail managed = em.find(Detail.class, detailId, LockModeType.PESSIMISTIC_WRITE);
        if (managed == null || !userId.equals(managed.getOwnerUserId())) {
            throw new IllegalArgumentException("対象が存在しないか、ユーザー不一致: id=" + detailId);
        }
        managed.setStatus(Status.REQUESTED);
        em.flush();
    }

    private static void requireUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
    }
}
