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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test de integracion para MoodService usando Mocks (TDD Red Phase)
 * @author Luis Guerrero y Jhair Zambrano
 * @version 1.0 - Nov 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests MoodService con Mocks")
public class MoodServiceTest {
    
    private static final Logger logger = Logger.getLogger(MoodServiceTest.class.getName());
    
    @Mock
    private MoodDAO moodDAO;
    
    @Mock
    private NotificationService notificationService;
    
    private MoodService moodService;
    
    @BeforeEach
    void setUp() {
        logger.info("[SETUP] Inicializando MoodService con dependencias mockeadas");

//        Inyecta los Mocks directamente en el constructor
        moodService = new MoodService(moodDAO, notificationService);
        logger.info("[SETUP] Configuración completada");
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
    @DisplayName("Debe lanzar excepcion cuando userId es null")
    void testCheckDailyMoodRecord_WhenUserIdIsNull_ShouldThrowException() {
        // ARRANGE: Preparar caso de userId invalido
        Integer userId = null;
        logger.info("[TEST] Validando que se lance excepcion con userId null");
        
        // ACT & ASSERT: Ejecutar esperando IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> moodService.checkDailyMoodRecord(userId));
        
        // ASSERT: Verificar mensaje de excepcion
        assertEquals("User ID cannot be null", exception.getMessage());
        logger.info("[TEST] PASSED - Excepcion lanzada correctamente: " + exception.getMessage());
        
        // ASSERT: Verificar que no se invocaron dependencias
        verify(moodDAO, never()).hasEntryForToday(any());
        verify(notificationService, never()).sendMoodReminderNotification(any());
        logger.info("[TEST] PASSED - userId null manejado correctamente sin llamadas a dependencias");
    }

    @Test
    @DisplayName("No debe actualizar una entrada de ayer")
    public void given_EntryDeAyer_when_updateMoodSelection_then_UpdateFails() {
        System.out.println("--- Ejecutando Test 6/12 (Lógica de Actualización - Falla) ---");

        // Given (Dado)
        MoodEntry entryDeAyer = new MoodEntry();
        entryDeAyer.setDate(LocalDate.now().minusDays(1)); // Fecha de ayer
        entryDeAyer.setMood("FELIZ");

        // When (Cuando)
        boolean resultado = moodService.updateMoodSelection(entryDeAyer, "TRISTE");

        // Then (Entonces)
        assertFalse(resultado,"No se debe permitir actualizar una entrada de ayer");
        assertEquals("El estado de ánimo no debió cambiar", "FELIZ", entryDeAyer.getMood());
    }

    @Test
    @DisplayName("Debe actualizar una entrada de hoy")
    public void given_EntryDeHoy_when_updateMoodSelection_then_UpdateSucceeds() {
        System.out.println("--- Ejecutando Test 6/12 (Lógica de Actualización - Éxito) ---");

        // Given (Dado)
        MoodEntry entryDeHoy = new MoodEntry();
        entryDeHoy.setDate(LocalDate.now());
        entryDeHoy.setMood("FELIZ");

        // When (Cuando)
        boolean resultado = moodService.updateMoodSelection(entryDeHoy, "TRISTE"); // <-- Fallará aquí

        // Then (Entonces)
        assertTrue(resultado, "Se debe permitir actualizar una entrada de hoy");
        assertEquals("El estado de ánimo debió cambiar", "TRISTE", entryDeHoy.getMood());
    }

}
