package com.sistema_seguimiento.services;

import com.sistema_seguimiento.dao.MoodDAO;
import com.sistema_seguimiento.model.MoodEntry;

import java.time.LocalDate;

/**
 * Servicio de Estado de Animo - Logica de negocio (TDD Green Phase)
 * @author Luis Guerrero y Jhair Zambrano
 * @version 1.0 - Nov 2025
 * 
 * Funcionalidades:
 * - checkDailyMoodRecord(): Verifica si el usuario registro su estado hoy
 * - isUpdateAllowed(): Determina si se permite actualizar una entrada (solo del dia actual)
 * 
 * Reglas de negocio:
 * - Enviar notificacion si usuario no ha registrado estado de animo del dia
 * - Solo permitir actualizaciones en entradas del dia actual
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - Fase Verde TDD
 */
public class MoodService {
    
    private MoodDAO moodDAO;
    private NotificationService notificationService;
    
    /**
     * Verifica si el usuario ha registrado su estado de ánimo hoy
     * Si no lo ha hecho, envía una notificación recordatoria
     * 
     * @param userId ID del usuario a verificar
     */
    public void checkDailyMoodRecord(Integer userId) {
        // ====== REFACTORIZACIÓN: EXTRACT METHOD ======
        
        // ===== CÓDIGO ANTES (sin Extract Method) =====
        // if (userId == null) {
        //     return;
        // }
        // boolean hasEntry = moodDAO.hasEntryForToday(userId);
        // if (!hasEntry) {
        //     notificationService.sendMoodReminderNotification(userId);
        // }
        // ==============================================
        
        // ===== CÓDIGO DESPUÉS (con Extract Method) =====
        validateUserId(userId);  // ← EXTRACT METHOD aplicado aquí
        
        boolean hasEntry = moodDAO.hasEntryForToday(userId);
        
        if (!hasEntry) {
            notificationService.sendMoodReminderNotification(userId);
        }
        // ================================================
    }
    
    /**
     * Valida que el ID de usuario no sea null
     * MÉTODO EXTRAÍDO mediante técnica Extract Method
     * 
     * @param userId ID a validar
     * @throws IllegalArgumentException si userId es null
     */
    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
    
    /**
     * Determina si se permite actualizar una entrada de estado de ánimo
     * Solo se permite actualizar entradas del día actual
     * 
     * @param entry Entrada de estado de ánimo a verificar
     * @return true si se permite actualizar, false en caso contrario
     */
    public boolean isUpdateAllowed(MoodEntry entry) {
        // 🟢 Validar entrada null
        if (entry == null || entry.getDate() == null) {
            return false;
        }
        
        // 🟢 Solo se permite actualizar si la entrada es de hoy
        return entry.isFromToday();
    }
    
    // Setters para inyección de dependencias (necesario para tests)
    public void setMoodDAO(MoodDAO moodDAO) {
        this.moodDAO = moodDAO;
    }
    
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    /**
     * Lógica de T7 H02: Permite actualizar el estado de ánimo
     * solo si la entrada es del día actual
     * @param entry el registro de ánimo a modificar
     * @param nuevoEstadoAnimo el estado (ej. "TRISTE").
     * @return true si la actualización fue permitida, false si no.
     */
    public boolean updateMoodSelection(MoodEntry entry, String nuevoEstadoAnimo) {
        if (entry.getDate().equals(LocalDate.now())){
            entry.setMood(nuevoEstadoAnimo);
            return true;
        }else {
            return false;
        }
    }
}
