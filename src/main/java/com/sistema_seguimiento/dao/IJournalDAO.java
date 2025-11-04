package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.JournalEntry;
import java.util.List;

/**
 * Interfaz para el DAO de Journal
 * Permite mockear fácilmente en tests unitarios
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - TDD
 */
public interface IJournalDAO {
    
    /**
     * Almacena una nueva entrada de diario
     * 
     * @param entry Entrada a almacenar
     * @return Entrada almacenada con ID asignado
     */
    JournalEntry storeJournalEntry(JournalEntry entry);
    
    /**
     * Obtiene todas las entradas de diario de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de entradas ordenadas por fecha (DESC)
     */
    List<JournalEntry> getJournalEntriesByUser(Integer userId);
}
