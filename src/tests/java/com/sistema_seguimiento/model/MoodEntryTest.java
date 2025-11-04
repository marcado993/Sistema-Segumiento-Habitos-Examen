package com.sistema_seguimiento.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para modelo MoodEntry (TDD Red Phase)
 * @author Luis Guerrero
 * @version 1.0 - Nov 2025
 */
@DisplayName("Tests Modelo MoodEntry")
class MoodEntryTest {
    
    private static final Logger logger = Logger.getLogger(MoodEntryTest.class.getName());
    
    @Test
    @DisplayName("Debe retornar true cuando entrada corresponde a fecha actual")
    void testIsFromToday_WhenEntryIsToday_ShouldReturnTrue() {
        // ARRANGE: Crear entrada con fecha de hoy
        LocalDate today = LocalDate.now();
        MoodEntry entry = new MoodEntry(1, today, "feliz", "Me siento muy bien hoy");
        logger.info(String.format("[TEST] Validando entrada con fecha actual: %s", today));
        
        // ACT: Ejecutar validacion de fecha
        boolean result = entry.isFromToday();
        
        // ASSERT: Resultado debe ser true
        assertTrue(result, "Entrada con fecha actual debe retornar true");
        logger.info("[TEST] PASSED - Entrada de hoy validada correctamente");
    }
    
    @Test
    @DisplayName("Debe retornar false cuando entrada es de fecha pasada")
    void testIsFromToday_WhenEntryIsYesterday_ShouldReturnFalse() {
        // ARRANGE: Crear entrada con fecha de ayer
        LocalDate yesterday = LocalDate.now().minusDays(1);
        MoodEntry entry = new MoodEntry(1, yesterday, "neutral", "Dia normal");
        logger.info(String.format("[TEST] Validando entrada con fecha pasada: %s", yesterday));
        
        // ACT: Ejecutar validacion de fecha
        boolean result = entry.isFromToday();
        
        // ASSERT: Resultado debe ser false
        assertFalse(result, "Entrada de fecha pasada debe retornar false");
        logger.info("[TEST] PASSED - Entrada de ayer rechazada correctamente");
    }
    
    @Test
    @DisplayName("Debe retornar false cuando entrada es de fecha futura")
    void testIsFromToday_WhenEntryIsFuture_ShouldReturnFalse() {
        // ARRANGE: Crear entrada con fecha futura
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        MoodEntry entry = new MoodEntry(1, tomorrow, "esperanzado", "Espero que sea un buen dia");
        logger.info(String.format("[TEST] Validando entrada con fecha futura: %s", tomorrow));
        
        // ACT: Ejecutar validacion de fecha
        boolean result = entry.isFromToday();
        
        // ASSERT: Resultado debe ser false
        assertFalse(result, "Entrada de fecha futura debe retornar false");
        logger.info("[TEST] PASSED - Entrada futura rechazada correctamente");
    }
    
    @Test
    @DisplayName("Debe manejar entrada con fecha null retornando false")
    void testIsFromToday_WhenDateIsNull_ShouldReturnFalse() {
        // ARRANGE: Crear entrada con fecha null
        MoodEntry entry = new MoodEntry(1, null, "confundido", "Sin fecha");
        logger.info("[TEST] Validando entrada con fecha null");
        
        // ACT: Ejecutar validacion de fecha
        boolean result = entry.isFromToday();
        
        // ASSERT: Resultado debe ser false
        assertFalse(result, "Entrada con fecha null debe retornar false");
        logger.info("[TEST] PASSED - Fecha null manejada correctamente");
    }
}
