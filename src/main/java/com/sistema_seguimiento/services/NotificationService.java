package com.sistema_seguimiento.services;

/**
 * Servicio de Notificaciones (TDD Green Phase)
 * Maneja el envio de notificaciones recordatorias a los usuarios
 * Implementacion minima con logging en consola
 * TODO: Integrar sistema de notificaciones real (email, push, SMS)
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - Fase Verde TDD
 */
public class NotificationService {
    
    /**
     * Envía una notificación recordatoria para registrar el estado de ánimo
     * 
     * @param userId ID del usuario al que se enviará la notificación
     */
    public void sendMoodReminderNotification(Integer userId) {
        // 🟢 Implementación mínima para pasar el test
        // En producción real, esto enviaría una notificación real
        System.out.println("🔔 Notificación enviada al usuario " + userId + ": Recuerda registrar tu estado de ánimo hoy");
    }
}
