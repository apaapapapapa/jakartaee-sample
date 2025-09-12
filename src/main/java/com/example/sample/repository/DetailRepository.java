package com.example.sample.repository;

import java.util.List;

import com.example.sample.model.Detail;
import com.example.sample.model.Status;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor
public class DetailRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public List<Detail> findByUserId(final String userId) {
        return entityManager.createQuery(
                "SELECT d FROM Detail d WHERE d.ownerUserId = :uid ORDER BY d.id DESC",
                Detail.class
        ).setParameter("uid", userId).getResultList();
    }

    public List<Detail> findByIdsForUser(final List<Long> ids, final String userId) {
        final List<Detail> result;
        if (ids == null || ids.isEmpty()) {
            result = List.of();
        } else {
            result = entityManager.createQuery(
                    "SELECT d FROM Detail d WHERE d.id IN :ids AND d.ownerUserId = :uid",
                    Detail.class
            ).setParameter("ids", ids)
             .setParameter("uid", userId)
             .getResultList();
        }
        return result;
    }

    public void markRequestedWithLock(final Detail detail) {
        entityManager.lock(detail, LockModeType.PESSIMISTIC_WRITE);
        detail.setStatus(Status.REQUESTED);
        entityManager.merge(detail);
    }
}
