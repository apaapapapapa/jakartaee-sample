package com.example.sample.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.sample.model.Detail;
import com.example.sample.model.Status;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.github.database.rider.junit5.util.EntityManagerProvider;

import jakarta.persistence.EntityManager;

@ExtendWith(DBUnitExtension.class)
class DetailRepositoryTest {

    static final EntityManagerProvider emProvider = EntityManagerProvider.instance("jakartaee-sample-test");

    public ConnectionHolder connectionHolder = emProvider::connection;

    private DetailRepository target;

    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = emProvider.getEm();
        target = new DetailRepository(em);
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.clear();
    }

    @Test
    @DataSet(value = "datasets/detail.yml", cleanBefore = true, transactional = true)
    void findByUserIdReturnsDescOrderedList() {

        List<?> result = target.findByUserId("user1");

        assertEquals(2, result.size(), "user1 の件数は 2 件のはず");

        // Entity のプロパティ名（detailId or id）に揺れがある場合に備えて反射で取得
        long firstId = readId(result.get(0));
        long secondId = readId(result.get(1));
        assertEquals(103L, firstId, "降順の先頭は detailId=103");
        assertEquals(101L, secondId, "2件目は detailId=101");
    }

    @Test
    @DataSet(value = "datasets/detail_lock.yml", cleanBefore = true, transactional = true)
    void testLockAndMarkRequested() {

        target.lockAndMarkRequested(201L, "user1");

        em.clear();
        Detail reloaded = em.find(Detail.class, 201L);
        assertNotNull(reloaded);
        assertEquals(Status.REQUESTED, reloaded.getStatus());
    }

    private static long readId(Object entity) {
        try {
            try {
                var m = entity.getClass().getMethod("getDetailId");
                return ((Number) m.invoke(entity)).longValue();
            } catch (NoSuchMethodException ignore) {
                var m = entity.getClass().getMethod("getId");
                return ((Number) m.invoke(entity)).longValue();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Entity の ID アクセサ(getDetailId/getId)が見つかりません。", e);
        }
        
    }
    
}
