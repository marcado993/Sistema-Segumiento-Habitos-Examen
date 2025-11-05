package com.sistema_seguimiento.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseDAO {
    protected final EntityManagerFactory emf;

    public BaseDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public BaseDAO() {
        this.emf = EntityManagerUtil.getEntityManagerFactory();
    }

    /**
     * Ejecuta una operación con transacción (sin retorno).
     * Encapsula el patrón de begin/commit/rollback/close para evitar duplicación.
     *
     * @param operation La operación a ejecutar con el EntityManager
     * @param errorMessage El mensaje de error en caso de excepción
     */
    protected void executeWithTransaction(Consumer<EntityManager> operation, String errorMessage) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            operation.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Log y relanzar la excepción para que el servicio la maneje
            System.err.println(errorMessage + ": " + e.getMessage());
            throw new RuntimeException(errorMessage, e);
        } finally {
            em.close();
        }
    }

    /**
     * Ejecuta una consulta (sin transacción) con retorno.
     * Encapsula el patrón de try-finally-close para consultas.
     *
     * @param <T> El tipo de retorno de la consulta
     * @param query La función que ejecuta la consulta
     * @return El resultado de la consulta
     */
    protected <T> T executeQuery(Function<EntityManager, T> query) {
        EntityManager em = emf.createEntityManager();
        try {
            return query.apply(em);
        } catch (Exception e) {
            System.err.println("Error ejecutando consulta: " + e.getMessage());
            // Para consultas que devuelven listas, es mejor devolver vacío que fallar
            if (e instanceof jakarta.persistence.NoResultException) {
                return (T) Collections.emptyList();
            }
            throw new RuntimeException("Error en consulta de DAO", e);
        } finally {
            em.close();
        }
    }
}
