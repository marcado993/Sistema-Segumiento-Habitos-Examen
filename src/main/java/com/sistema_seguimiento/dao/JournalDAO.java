package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.JournalEntry;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO para gestionar entradas de diario (Journal)
 * (Refactorizado con Patrón DAO Consolidado)
 * Implementa IJournalDAO para facilitar el testing con mocks.
 * @version 2.0 - Refactor 4
 */
public class JournalDAO extends BaseDAO implements IJournalDAO {

    public JournalDAO() {
        super();
    }

    public JournalDAO(EntityManagerFactory emf) {
        super(emf);
    }


    /**
     * 🟢 FASE VERDE
     * Almacena una nueva entrada de diario usando el helper 'executeWithTransaction'.
     */
    @Override
    public JournalEntry storeJournalEntry(JournalEntry entry) {
        executeWithTransaction(
                em -> em.persist(entry),
                "Error al guardar JournalEntry"
        );
        return entry; // Devuelve la entidad (ahora con ID)
    }

    /**
     * 🟢 FASE VERDE
     * Obtiene todas las entradas de diario de un usuario usando el helper 'executeQuery'.
     */
    @Override
    public List<JournalEntry> getJournalEntriesByUser(Integer userId) {
        return executeQuery(em -> {
            TypedQuery<JournalEntry> query = em.createQuery(
                    "SELECT j FROM JournalEntry j WHERE j.userId = :userId ORDER BY j.createdAt DESC",
                    JournalEntry.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        });
    }
}