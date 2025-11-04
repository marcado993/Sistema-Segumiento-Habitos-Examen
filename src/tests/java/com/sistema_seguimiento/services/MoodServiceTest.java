package com.sistema_seguimiento.services;

import com.sistema_seguimiento.dao.MoodDAO;
import com.sistema_seguimiento.model.MoodEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integracion para MoodService usando Mocks (TDD Red Phase)
 * @author Luis Guerrero
 * @version 1.0 - Nov 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests MoodService con Mocks")
class MoodServiceTest {
    
    private static final Logger logger = Logger.getLogger(MoodServiceTest.class.getName());
    
    @Mock
    private MoodDAO moodDAO;
    
    @Mock
    private NotificationService notificationService;
    
    private MoodService moodService;
    
    @BeforeEach
    void setUp() {
        logger.info("[SETUP] Inicializando MoodService con dependencias mockeadas");
        moodService = new MoodService();
        moodService.setMoodDAO(moodDAO);
        moodService.setNotificationService(notificationService);
        logger.info("[SETUP] Configuracion completada");
    }
    
    @Test
    @DisplayName("Debe enviar notificacion cuando usuario no tiene registro diario")
    void testCheckDailyMoodRecord_WhenNoEntryToday_ShouldSendNotification() {
        // ARRANGE: Configurar mock para simular ausencia de registro
        Integer userId = 1;
        when(moodDAO.hasEntryForToday(userId)).thenReturn(false);
        logger.info(String.format("[TEST] Usuario %d sin registro hoy - debe enviar notificacion", userId));
        
        // ACT: Ejecutar verificacion de registro diario
        moodService.checkDailyMoodRecord(userId);
        logger.info("[TEST] Verificacion ejecutada");
        
        // ASSERT: Validar que se envio notificacion exactamente una vez
        verify(notificationService, times(1)).sendMoodReminderNotification(userId);
        verify(moodDAO, times(1)).hasEntryForToday(userId);
        logger.info("[TEST] PASSED - Notificacion enviada correctamente");
    }
    
    @Test
    @DisplayName("NO debe enviar notificacion cuando usuario ya registro su estado")
    void testCheckDailyMoodRecord_WhenEntryExistsToday_ShouldNotSendNotification() {
        // ARRANGE: Configurar mock para simular registro existente
        Integer userId = 1;
        when(moodDAO.hasEntryForToday(userId)).thenReturn(true);
        logger.info(String.format("[TEST] Usuario %d con registro existente - NO debe enviar notificacion", userId));
        
        // ACT: Ejecutar verificacion de registro diario
        moodService.checkDailyMoodRecord(userId);
        logger.info("[TEST] Verificacion ejecutada");
        
        // ASSERT: Validar que NO se envio notificacion
        verify(notificationService, never()).sendMoodReminderNotification(userId);
        verify(moodDAO, times(1)).hasEntryForToday(userId);
        logger.info("[TEST] PASSED - Notificacion NO enviada como se esperaba");
    }
    
    @Test
    @DisplayName("Debe manejar userId null sin lanzar excepcion")
    void testCheckDailyMoodRecord_WhenUserIdIsNull_ShouldHandleGracefully() {
        // ARRANGE: Preparar caso de userId invalido
        Integer userId = null;
        logger.info("[TEST] Validando manejo de userId null");
        
        // ACT & ASSERT: Ejecutar sin esperar excepcion
        assertDoesNotThrow(() -> moodService.checkDailyMoodRecord(userId));
        logger.info("[TEST] Metodo ejecutado sin lanzar excepcion");
        
        // ASSERT: Verificar que no se invocaron dependencias
        verify(moodDAO, never()).hasEntryForToday(any());
        verify(notificationService, never()).sendMoodReminderNotification(any());
        logger.info("[TEST] PASSED - userId null manejado correctamente sin llamadas a dependencias");
    }
}
