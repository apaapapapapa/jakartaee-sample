package com.example.sample.repository;

import java.util.List;

import com.example.sample.model.Detail;
import com.example.sample.model.Status;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class DetailRepository {

    @PersistenceContext
    EntityManager em;

    public List<Detail> findByUserId(String userId) {
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.ownerUserId = :uid ORDER BY d.id DESC",
                Detail.class
        ).setParameter("uid", userId).getResultList();
    }

    public List<Detail> findByIdsForUser(List<Long> ids, String userId) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.id IN :ids AND d.ownerUserId = :uid",
                Detail.class
        ).setParameter("ids", ids)
         .setParameter("uid", userId)
         .getResultList();
    }

    public void markRequestedWithLock(Detail d) {
        // 悲観ロック
        em.lock(d, LockModeType.PESSIMISTIC_WRITE);
        d.setStatus(Status.REQUESTED);
        em.merge(d);
    }
}
