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
    
    /**
     * 🟢 FASE VERDE - Valida que una entrada de texto no esté vacía (T5 HU01)
     * 
     * Validaciones:
     * - Rechaza null (retorna false)
     * - Rechaza string vacío "" (retorna false)
     * - Rechaza string con solo espacios " " (retorna false)
     * - Acepta texto válido "Resumen válido" (retorna true)
     * 
     * @param texto Texto a validar
     * @return true si el texto es válido (no vacío), false si es inválido
     */
    boolean validarEntrada(String texto);
}
