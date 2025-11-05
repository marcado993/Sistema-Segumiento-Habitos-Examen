package com.sistema_seguimiento.services;

import com.sistema_seguimiento.dao.MoodDAO;
import com.sistema_seguimiento.model.MoodEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Parametrizado para MoodService (TDD Red Phase)
 * @author Luis Guerrero
 * @version 1.0 - Nov 2025
 * 
 * Regla de negocio: Solo se permiten actualizaciones en entradas del día actual.
 */
@DisplayName("Tests Parametrizados: MoodService.isUpdateAllowed()")
class MoodServiceParameterizedTest {
    
    private static final Logger logger = Logger.getLogger(MoodServiceParameterizedTest.class.getName());
    private MoodService moodService;
    private MoodDAO moodDAO;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        logger.info("[SETUP] Inicializando MoodService para pruebas parametrizadas");
//        Inyecta los Mocks directamente en el constructor
        moodService = new MoodService(moodDAO, notificationService);
        logger.info("[SETUP] Configuración completada");
    }
    
    /**
     * Proveedor de datos para pruebas de isUpdateAllowed()
     * 
     * @return Stream de Arguments con (MoodEntry, expectedResult, description)
     */
    private static Stream<Arguments> provideEntriesForUpdateAllowed() {
        LocalDate today = LocalDate.now();
        
        return Stream.of(
            Arguments.of(
                new MoodEntry(1, today, "feliz", "Entrada de hoy"),
                true,
                "Entrada fecha actual"
            ),
            
            Arguments.of(
                new MoodEntry(1, today.minusDays(1), "triste", "Entrada de ayer"),
                false,
                "Entrada fecha pasada (-1 dia)"
            ),
            
            Arguments.of(
                new MoodEntry(1, today.plusDays(1), "esperanzado", "Entrada del futuro"),
                false,
                "Entrada fecha futura (+1 dia)"
            ),
            
            Arguments.of(
                new MoodEntry(1, today.minusDays(7), "neutral", "Entrada de hace 7 dias"),
                false,
                "Entrada fecha pasada (-7 dias)"
            ),
            
            Arguments.of(
                new MoodEntry(1, today.minusDays(30), "cansado", "Entrada de hace 30 dias"),
                false,
                "Entrada fecha pasada (-30 dias)"
            )
        );
    }
    
    @ParameterizedTest(name = "Test {index}: {2} esperado={1}")
    @MethodSource("provideEntriesForUpdateAllowed")
    @DisplayName("Validacion de actualizacion permitida segun fecha de entrada")
    void testIsUpdateAllowed_WithDifferentDates_ShouldReturnCorrectResult(
            MoodEntry entry, 
            boolean expectedResult,
            String description) {
        
        logger.info(String.format("[TEST] Ejecutando: %s | Fecha: %s | Esperado: %s", 
            description, entry.getDate(), expectedResult));
        
        // ACT: Verificar si se permite actualizar
        boolean result = moodService.isUpdateAllowed(entry);
        
        logger.info(String.format("[RESULT] Obtenido: %s | Test: %s", 
            result, result == expectedResult ? "PASSED" : "FAILED"));
        
        // ASSERT: Validar resultado esperado
        assertEquals(expectedResult, result, 
            String.format("Fallo en validacion para: %s (fecha=%s)", description, entry.getDate()));
    }
    
    @ParameterizedTest(name = "Test {index}: fecha={0}, esperado={1}")
    @MethodSource("provideDatesForUpdateAllowed")
    @DisplayName("Validacion directa con fechas especificas")
    void testIsUpdateAllowed_WithDifferentDatesDirectly_ShouldReturnCorrectResult(
            LocalDate date,
            boolean expectedResult) {
        
        logger.info(String.format("[TEST] Validando fecha directa: %s | Esperado: %s", date, expectedResult));
        
        // ARRANGE: Crear entrada con la fecha especificada
        MoodEntry entry = new MoodEntry(1, date, "test", "Test parametrizado directo");
        
        // ACT: Ejecutar validacion
        boolean result = moodService.isUpdateAllowed(entry);
        
        logger.info(String.format("[RESULT] Fecha: %s | Obtenido: %s | Esperado: %s | Status: %s", 
            date, result, expectedResult, result == expectedResult ? "OK" : "ERROR"));
        
        // ASSERT: Validar resultado
        assertEquals(expectedResult, result,
            String.format("Error en validacion: fecha=%s deberia retornar %b pero retorno %b", 
                date, expectedResult, result));
    }
    
    /**
     * Proveedor de fechas para validacion directa
     * 
     * @return Stream de Arguments con (fecha, resultadoEsperado)
     */
    private static Stream<Arguments> provideDatesForUpdateAllowed() {
        LocalDate today = LocalDate.now();
        
        return Stream.of(
            Arguments.of(today, true),
            Arguments.of(today.minusDays(1), false),
            Arguments.of(today.plusDays(1), false),
            Arguments.of(today.minusDays(2), false),
            Arguments.of(today.plusDays(5), false)
        );
    }
}
