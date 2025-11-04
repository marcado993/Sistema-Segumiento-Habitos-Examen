package com.sistema_seguimiento.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * � TEST PARAMETRIZADO 1/2 - Validación de entradas (T5 HU01)
 * 
 * Clase bajo prueba: JournalService
 * Método a probar: validarEntrada(String texto)
 * 
 * PROPÓSITO:
 * - Probar la validación de entradas vacías usando diferentes casos
 * - Verificar que rechaza: null, "", " " (espacios)
 * - Verificar que acepta: "Resumen válido"
 * 
 * VENTAJAS DE @ParameterizedTest:
 * - Evita duplicación de código (un test para múltiples casos)
 * - Fácil agregar nuevos casos de prueba
 * - Reporte claro de qué casos pasan/fallan
 * 
 * HISTORIA DE USUARIO:
 * HU01 - T5: Validación de entradas de diario
 * "Como usuario, quiero que el sistema valide que mi entrada no esté vacía
 *  para evitar guardar contenido inválido"
 */
@DisplayName("� FASE VERDE - Prueba Parametrizada: JournalService.validarEntrada() (T5)")
public class JournalServiceParametrizedTest {

    private JournalService journalService;

    @BeforeEach
    void setUp() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [SETUP FASE VERDE] Inicializando JournalService para test parametrizado");
        System.out.println("=".repeat(80));
        
        // Crear instancia del servicio (sin DAO porque solo probamos validación)
        journalService = new JournalService(null);
        
        System.out.println("✓ JournalService inicializado (DAO=null, solo validación)");
        System.out.println("✓ Test parametrizado configurado con @CsvSource");
        System.out.println("=".repeat(80));
    }

    /**
     * � TEST PARAMETRIZADO - Validación T5
     * 
     * CASOS DE PRUEBA:
     * 1. null → false (no guardar)
     * 2. "" (vacío) → false (no guardar)
     * 3. " " (solo espacios) → false (no guardar)
     * 4. "Resumen válido" → true (guardar)
     * 
     * @param input Texto a validar
     * @param expected Resultado esperado (true=válido, false=inválido)
     */
    @ParameterizedTest(name = "� Test {index}: validarEntrada(\"{0}\") debe retornar {1}")
    @CsvSource(value = {
        "null, false",           // Caso 1: null no debe guardarse
        "'', false",             // Caso 2: string vacío no debe guardarse
        "' ', false",            // Caso 3: solo espacios no debe guardarse
        "'Resumen válido', true" // Caso 4: contenido válido debe guardarse
    }, nullValues = {"null"})
    @DisplayName("� VERDE: Validar entrada con diferentes inputs (T5)")
    void testValidarEntrada_ConDiferentesInputs_DebeValidarCorrectamente(String input, boolean expected) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [TEST PARAMETRIZADO INICIADO]");
        System.out.println("Input: \"" + input + "\" | Expected: " + expected);
        System.out.println("=".repeat(80));
        
        // ==================== ARRANGE ====================
        System.out.println("\n📋 [ARRANGE] Preparando caso de prueba...");
        System.out.println("  - Input recibido: " + (input == null ? "null" : "\"" + input + "\""));
        System.out.println("  - Resultado esperado: " + (expected ? "VÁLIDO (guardar)" : "INVÁLIDO (no guardar)"));
        
        // ==================== ACT ====================
        System.out.println("\n⚡ [ACT] Ejecutando journalService.validarEntrada(input)...");
        
        boolean resultado = journalService.validarEntrada(input);
        
        System.out.println("✓ Método ejecutado");
        System.out.println("  - Resultado obtenido: " + resultado);
        
        // ==================== ASSERT ====================
        System.out.println("\n✅ [ASSERT] Verificando que el resultado sea el esperado...");
        
        assertEquals(expected, resultado, 
            "Validación incorrecta para input: " + (input == null ? "null" : "\"" + input + "\""));
        
        System.out.println("✅ ASSERT PASSED:");
        System.out.println("  - Input: " + (input == null ? "null" : "\"" + input + "\""));
        System.out.println("  - Expected: " + expected);
        System.out.println("  - Actual: " + resultado);
        System.out.println("  - Estado: " + (expected == resultado ? "✓ CORRECTO" : "✗ INCORRECTO"));
        
        // ==================== RESULTADO ====================
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [TEST PARAMETRIZADO COMPLETADO]");
        System.out.println("RESULTADO: ✅ TEST PASA");
        System.out.println("=".repeat(80) + "\n");
    }
}
