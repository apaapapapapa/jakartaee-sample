package com.example.sample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.github.database.rider.junit5.api.DBRider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ExtendWith(DBUnitExtension.class)
@DBRider
class DetailRepositoryJpaTest {

  static EntityManagerFactory emf;
  static EntityManager em;

  private static ConnectionHolder connectionHolder;

  @BeforeAll
  static void setUp() {
    emf = Persistence.createEntityManagerFactory("jakartaee-sample-test");
    em = emf.createEntityManager();
    connectionHolder = () -> em.unwrap(java.sql.Connection.class);
  }

  @AfterAll
  static void tearDown() {
    if (em != null) em.close();
    if (emf != null) emf.close();
  }

  @Test
  void testSomething() {
    em.getTransaction().begin();
    // ... JPAで検証 ...
    em.getTransaction().commit();
  }
}

