package com.sistema_seguimiento.services;

/**
 * Servicio de Notificaciones (TDD Green Phase)
 * Maneja el envio de notificaciones recordatorias con logging
 * @author Luis Guerrero
 * @version 1.0 - Nov 2025
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
    /**
     * Genera el texto para el recordatorio de estado de ánimo.
     *
     * @param userName el nombre del usuario a notificar
     * @return el mensaje de recordatorio formateado.
     */
    public String generateReminderMessage(String userName) {
        return "!Hola " + userName + ", no olvides registrar tu ánimo de hoy!";
    }
}
