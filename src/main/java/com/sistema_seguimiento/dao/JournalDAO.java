package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.JournalEntry;
import jakarta.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.List;

/**
 * DAO para gestionar entradas de diario (Journal)
 * Desacoplado de EntityManagerUtil para permitir inyectar un EMF de pruebas.
 */
public class JournalDAO {

    private final EntityManagerFactory emf;

    public JournalDAO(EntityManagerFactory emf) {
        if (emf == null) throw new IllegalArgumentException("EntityManagerFactory no puede ser null");
        this.emf = emf;
    }

    /**
     * Fase ROJA (TDD): implementación mínima que NO persiste ni asigna ID.
     * El test debe fallar porque el ID seguirá siendo null y no se podrá encontrar en BD.
     */
    public JournalEntry storeJournalEntry(JournalEntry entry) {
        // stub: no persiste, solo devuelve la misma instancia
        return entry;
    }

    /**
     * Fase ROJA (TDD): stub que retorna lista vacía.
     * El test debe fallar porque se esperan entradas y orden por fecha desc.
     */
    public List<JournalEntry> getJournalEntriesByUser(Integer userId) {
        return Collections.emptyList();
    }
}
