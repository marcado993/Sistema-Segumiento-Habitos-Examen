package com.sistema_seguimiento.services;

import com.sistema_seguimiento.dao.MoodDAO;
import com.sistema_seguimiento.model.MoodEntry;

/**
 * Servicio de Estado de Animo - Logica de negocio
 * 
 * NUEVO: Implementado en Fase Verde TDD
 * Maneja la logica de negocio relacionada con los registros de estado de animo
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
        // 🟢 Validar userId null
        if (userId == null) {
            return;
        }
        
        // 🟢 Verificar si existe registro de hoy
        boolean hasEntry = moodDAO.hasEntryForToday(userId);
        
        // 🟢 Si NO hay registro, enviar notificación
        if (!hasEntry) {
            notificationService.sendMoodReminderNotification(userId);
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
}
