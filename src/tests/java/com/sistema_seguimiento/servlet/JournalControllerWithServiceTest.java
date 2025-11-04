package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.services.IJournalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * 🔴 FASE ROJA - Test Mock 1/2 para JournalController (HU01 - T2, T5, T8)
 * 
 * HU01: Sistema de Journal (Historial de Entradas)
 * 
 * Tareas relacionadas:
 * - T2: Implementar saveJournalEntry() con validación y fecha actual
 * - T5: Validación que impide guardar entradas vacías sin errores
 * - T8: Enviar lista de entradas al frontend
 * 
 * OBJETIVO DEL TEST:
 * Verificar que JournalController.doPost() interactúa correctamente con JournalService:
 * - Llama a journalService.saveJournalEntry() EXACTAMENTE 1 vez
 * - Solo cuando la validación pasa (contenido NO vacío)
 * - No llama al servicio cuando el contenido es vacío o null
 * 
 * ESTE TEST DEBE FALLAR porque:
 * - JournalController.doPost() NO está implementado
 * - No hay inyección de JournalService
 * - No hay validación de contenido vacío
 * 
 * TDD: Mock 1/2 - FASE ROJA
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - Fase Roja TDD
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🔴 FASE ROJA - Mock 1/2: JournalController + JournalService")
class JournalControllerWithServiceTest {
    
    @Mock
    private IJournalService journalService;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    private JournalController controller;
    
    @BeforeEach
    void setUp() {
        System.out.println("=".repeat(80));
        System.out.println("� [SETUP FASE VERDE] Inicializando JournalController con JournalService mockeado");
        controller = new JournalController();
        
        // Configurar comportamiento básico de mocks
        when(request.getSession()).thenReturn(session);
        
        System.out.println("✓ Mocks configurados: HttpServletRequest, HttpServletResponse, HttpSession");
        System.out.println("✓ JournalService mockeado listo para inyección");
        System.out.println("=".repeat(80));
    }
    
    /**
     * 🔴 TEST FASE ROJA - Mock 1/2
     * 
     * DEBE FALLAR porque:
     * 1. JournalController NO tiene setter para JournalService
     * 2. doPost() NO está implementado
     * 3. NO hay validación de contenido vacío
     * 4. NO hay llamada a journalService.saveJournalEntry()
     * 
     * ESCENARIO:
     * - Usuario autenticado (ID=1) en sesión
     * - Envía POST con contenido válido (no vacío)
     * - El controlador debe validar y llamar al servicio
     * 
     * EXPECTATIVAS:
     * - journalService.saveJournalEntry() debe ser llamado EXACTAMENTE 1 vez
     * - Con parámetros correctos: userId=1, content="Entrada de prueba"
     */
    @Test
    @DisplayName("🔴 ROJO: doPost con contenido válido debe llamar a journalService.saveJournalEntry() 1 vez")
    void testDoPost_ContenidoValido_DebeLlamarServiceUnaVez() throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔴 [TEST FASE ROJA INICIADO - Mock 1/2]");
        System.out.println("Test: doPost() debe llamar a JournalService cuando contenido es válido");
        System.out.println("=".repeat(80));
        
        // ==================== ARRANGE ====================
        System.out.println("\n📋 [ARRANGE] Preparando datos de prueba...");
        
        Integer userId = 1;
        String content = "Entrada de prueba válida para el diario";
        
        // Configurar request mock
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("content")).thenReturn(content);
        when(session.getAttribute("userId")).thenReturn(userId); // Cambio: usar "userId" directamente
        
        System.out.println("✓ Usuario en sesión: ID=" + userId);
        System.out.println("✓ Parámetros del request:");
        System.out.println("  - action: save");
        System.out.println("  - content: " + content);
        
        // Inyectar el servicio mockeado
        System.out.println("\n💉 Inyectando JournalService mock...");
        controller.setJournalService(journalService);
        
        // ==================== ACT ====================
        System.out.println("\n⚡ [ACT] Ejecutando controller.doPost(request, response)...");
        
        controller.doPost(request, response);
        
        System.out.println("✓ doPost() ejecutado");
        
        // ==================== ASSERT ====================
        System.out.println("\n✅ [ASSERT] Verificando que journalService.saveJournalEntry() fue llamado...");
        
        // Verificar que el servicio fue llamado EXACTAMENTE 1 vez
        verify(journalService, times(1)).saveJournalEntry(userId, content);
        
        System.out.println("✅ ASSERT PASSED: journalService.saveJournalEntry() llamado 1 vez con:");
        System.out.println("   - userId: " + userId);
        System.out.println("   - content: " + content);
        
        // ==================== RESULTADO ====================
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [TEST FASE VERDE COMPLETADO]");
        System.out.println("RESULTADO: ✅ TEST PASA");
        System.out.println("=".repeat(80) + "\n");
    }
    
    /**
     * � TEST ADICIONAL - Validación T5: Contenido vacío
     * 
     * ESCENARIO:
     * - Usuario envía POST con contenido vacío o null
     * - El controlador NO debe llamar al servicio (T5)
     * 
     * EXPECTATIVA:
     * - journalService.saveJournalEntry() NO debe ser llamado
     */
    @Test
    @DisplayName("� VERDE: doPost con contenido vacío NO debe llamar a journalService (T5)")
    void testDoPost_ContenidoVacio_NoDebeLlamarService() throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [TEST FASE VERDE INICIADO - Validación T5]");
        System.out.println("Test: doPost() NO debe llamar a JournalService con contenido vacío");
        System.out.println("=".repeat(80));
        
        // ARRANGE
        Integer userId = 1;
        String contentVacio = "   "; // Espacios en blanco (inválido)
        
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("content")).thenReturn(contentVacio);
        when(session.getAttribute("userId")).thenReturn(userId); // Cambio: usar "userId" directamente
        
        System.out.println("📋 Contenido vacío/inválido: '" + contentVacio + "'");
        
        // Inyectar el servicio mockeado
        controller.setJournalService(journalService);
        
        // ACT
        controller.doPost(request, response);
        
        // ASSERT
        System.out.println("\n✅ [ASSERT] Verificando que el servicio NO fue llamado...");
        verify(journalService, never()).saveJournalEntry(any(), any());
        
        System.out.println("✅ ASSERT PASSED: journalService.saveJournalEntry() NO fue llamado");
        System.out.println("   Validación T5 correcta: contenido vacío rechazado sin errores");
        
        System.out.println("\n� RESULTADO: ✅ TEST PASA");
        System.out.println("=".repeat(80) + "\n");
    }
}
