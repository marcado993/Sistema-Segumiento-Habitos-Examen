package com.sistema_seguimiento.services;

import com.sistema_seguimiento.dao.IJournalDAO;
import com.sistema_seguimiento.model.JournalEntry;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de Journal - Lógica de negocio (Refactorización 1)
 * 
 * Encapsula la lógica de negocio para gestión de entradas de diario:
 * - Validación de contenido (T5): No guardar entradas vacías
 * - Asociación con fecha actual (T2)
 * - Delegación al DAO para persistencia
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - TDD Fase Roja
 */
public class JournalService implements IJournalService {
    
    private IJournalDAO journalDAO;
    
    /**
     * Constructor con inyección de dependencias
     * 
     * @param journalDAO DAO para persistencia de entradas
     */
    public JournalService(IJournalDAO journalDAO) {
        this.journalDAO = journalDAO;
    }
    
    /**
     * 🔴 FASE ROJA - Guarda una nueva entrada de diario (T2, T5)
     * 
     * Validaciones (T5):
     * - Si el contenido es null o vacío, retorna null (no guarda)
     * - No genera errores ni alertas, simplemente no registra
     * 
     * Proceso (T2):
     * - Valida que el contenido no esté vacío
     * - Asocia la entrada con la fecha actual (LocalDateTime.now())
     * - Delega al DAO para persistencia
     * 
     * @param userId ID del usuario
     * @param content Contenido de la entrada
     * @return Entrada guardada con ID asignado, o null si validación falla
     */
    @Override
    public JournalEntry saveJournalEntry(Integer userId, String content) {
        // 🔴 STUB: Implementación mínima que retorna null
        // El test debe fallar porque no guarda nada
        return null;
    }
    
    /**
     * 🔴 FASE ROJA - Obtiene entradas de diario del usuario (T8)
     * 
     * @param userId ID del usuario
     * @return Lista de entradas ordenadas por fecha (DESC)
     */
    @Override
    public List<JournalEntry> getJournalEntriesByUser(Integer userId) {
        // 🔴 STUB: Delega al DAO sin lógica adicional
        return journalDAO.getJournalEntriesByUser(userId);
    }
}
