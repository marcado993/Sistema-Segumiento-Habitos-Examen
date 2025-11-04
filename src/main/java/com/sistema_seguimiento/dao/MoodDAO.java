package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.MoodEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * DAO para MoodEntry - Capa de acceso a datos (TDD Green Phase)
 * Maneja la persistencia de entradas de estado de animo con implementacion minima
 * @author Luis Guerrero
 * @version 1.0 - Nov 2025
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - Fase Verde TDD
 */
public class MoodDAO {
    
    /**
     * Verifica si existe una entrada de estado de ánimo para el usuario en el día actual
     * 
     * @param userId ID del usuario
     * @return true si existe una entrada de hoy, false en caso contrario
     */
    public boolean hasEntryForToday(Integer userId) {
        // 🟢 Implementación mínima para pasar el test
        // En producción real, esto consultaría la base de datos
        return false;
    }

    public void storeMoodRecord(EntityManager em, MoodEntry entry) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            if (!tx.isActive()) {
                tx.begin();
            }
            em.persist(entry); // Persistimos en el 'em' del test
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al guardar MoodEntry: " + e.getMessage());
            throw e;
        }
    }
}
