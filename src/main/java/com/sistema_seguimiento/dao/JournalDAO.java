package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.JournalEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

/**
 * DAO para gestionar entradas de diario (Journal)
 * Desacoplado de EntityManagerUtil para permitir inyectar un EMF de pruebas.
 * Implementa IJournalDAO para facilitar el testing con mocks.
 */
public class JournalDAO implements IJournalDAO {

    private final EntityManagerFactory emf;

    public JournalDAO(EntityManagerFactory emf) {
        if (emf == null) throw new IllegalArgumentException("EntityManagerFactory no puede ser null");
        this.emf = emf;
    }

    /**
     * Persiste una entrada de diario usando JPA y retorna la entidad con ID asignado
     */
    @Override
    public JournalEntry storeJournalEntry(JournalEntry entry) {
        if (entry == null) throw new IllegalArgumentException("JournalEntry no puede ser null");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entry);
            tx.commit();
            return entry;
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene las entradas de un usuario ordenadas de más reciente a más antigua (createdAt DESC)
     */
    @Override
    public List<JournalEntry> getJournalEntriesByUser(Integer userId) {
        if (userId == null) return Collections.emptyList();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<JournalEntry> query = em.createQuery(
                "SELECT j FROM JournalEntry j WHERE j.userId = :userId ORDER BY j.createdAt DESC",
                JournalEntry.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
