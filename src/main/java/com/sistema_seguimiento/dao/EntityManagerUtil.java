package com.sistema_seguimiento.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtil {
    
    private static EntityManagerFactory entityManagerFactory;
    
    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("sistema-seguimiento-pu");
        } catch (Exception e) {
            System.err.println("Error al inicializar EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory no inicializado");
        }
        return entityManagerFactory.createEntityManager();
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory no inicializado");
        }
        return entityManagerFactory;
    }
}
