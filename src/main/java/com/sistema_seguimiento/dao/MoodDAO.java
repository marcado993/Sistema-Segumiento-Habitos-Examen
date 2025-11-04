package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.MoodEntry;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;

/**
 * DAO para MoodEntry - Capa de acceso a datos (TDD Green Phase)
 * Maneja la persistencia de entradas de estado de animo con implementacion minima
 * @author Luis Guerrero y Jhair Zambrano
 * @version 1.0 - Nov 2025
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - Fase Verde TDD
 */

public class MoodDAO extends BaseDAO {

    public MoodDAO() {
        super(); // Llama al constructor de BaseDAO
    }

    /**
     * Constructor que acepta un EntityManagerFactory para pruebas u otros propósitos.
     * @param emf El EntityManagerFactory a usar
     */
    public MoodDAO(EntityManagerFactory emf) {
        super(emf); // Llama al constructor de BaseDAO con el EMF de H2
    }

    /**
     * Verifica si existe una entrada de estado de ánimo para el usuario dado en la fecha actual.
     * @param userId ID del usuario
     * @return true si existe una entrada para hoy, false en caso contrario
     */
    public boolean hasEntryForToday(Integer userId) {
        return executeQuery(em -> {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(m) FROM MoodEntry m WHERE m.usuario.id = :userId AND m.date = :today",
                    Long.class
            );
            query.setParameter("userId", userId);
            query.setParameter("today", LocalDate.now());
            return query.getSingleResult() > 0;
        });
    }

    /**
     * Guarda una nueva entrada de estado de ánimo en la base de datos.
     * @param entry La entrada de estado de ánimo a guardar
     * @return La entrada guardada
     */
    public MoodEntry save(MoodEntry entry) {
        executeWithTransaction(
                em -> em.persist(entry),
                "Error al guardar MoodEntry"
        );
        return entry;
    }
}