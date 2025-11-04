package com.sistema_seguimiento.services;

import com.sistema_seguimiento.model.JournalEntry;
import java.util.List;

/**
 * Interfaz para el Servicio de Journal
 * Capa de lógica de negocio entre el Controlador y el DAO
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - TDD
 */
public interface IJournalService {
    
    /**
     * Guarda una nueva entrada de diario
     * 
     * @param userId ID del usuario
     * @param content Contenido de la entrada
     * @return Entrada guardada con ID asignado, o null si el contenido es vacío
     */
    JournalEntry saveJournalEntry(Integer userId, String content);
    
    /**
     * Obtiene todas las entradas de diario de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de entradas ordenadas por fecha (DESC)
     */
    List<JournalEntry> getJournalEntriesByUser(Integer userId);
}
