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
        // Validación (T5)
        if (!validarEntrada(content)) {
            return null;
        }
        // Construcción de la entidad (T2)
        JournalEntry entry = new JournalEntry(userId, content, LocalDateTime.now());
        // Delegación al DAO (persistencia)
        return journalDAO.storeJournalEntry(entry);
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
    
    /**
     * 🟢 FASE VERDE - Valida que una entrada no esté vacía (T5 HU01)
     * 
     * Implementación de validación parametrizada:
     * - null → false (inválido)
     * - "" → false (inválido)
     * - " " → false (inválido, solo espacios)
     * - "Resumen válido" → true (válido)
     * 
     * @param texto Texto a validar
     * @return true si el texto es válido (no vacío y no solo espacios), false en caso contrario
     */
    @Override
    public boolean validarEntrada(String texto) {
        // Validación 1: null es inválido
        if (texto == null) {
            return false;
        }
        
        // Validación 2: vacío o solo espacios es inválido
        if (texto.trim().isEmpty()) {
            return false;
        }
        
        // Validación 3: si pasa las validaciones, es válido
        return true;
    }
}
