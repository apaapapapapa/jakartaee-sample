package com.example.sample.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaResources {

    @PersistenceContext
    private EntityManager containerManagedEm;

    @Produces
    public EntityManager produceEntityManager() {
        return containerManagedEm;
    }
}
