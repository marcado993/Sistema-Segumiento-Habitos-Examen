package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.dao.IJournalDAO;
import com.sistema_seguimiento.model.JournalEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * � FASE VERDE - Test con Mock para JournalController (HU01 - T8)
 * 
 * HU01: Sistema de Journal (Historial de Entradas)
 * T8: Configurar el JournalController para enviar al frontend la lista de entradas 
 *     obtenidas y renderizarla en la vista de historial.
 * 
 * OBJETIVO DEL TEST:
 * Verificar que el servicio/controlador llama al DAO y devuelve la lista de entradas correcta.
 * 
 * ✅ FASE VERDE COMPLETADA:
 * - JournalController TIENE el método getJournalEntriesByUser()
 * - Llama al DAO correctamente
 * - Devuelve la lista de entradas esperada
 * 
 * TDD: TESTS PASANDO - FASE VERDE
 * 
 * @author Sistema Seguimiento Habitos
 * @version 2.0 - Fase Verde TDD
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("� FASE VERDE - Tests JournalController con Mocks")
class JournalControllerTest {
    
    @Mock
    private IJournalDAO journalDAO;
    
    private JournalController controller;
    
    @BeforeEach
    void setUp() {
        System.out.println("=".repeat(80));
        System.out.println("� [SETUP FASE VERDE] Inicializando JournalController con JournalDAO mockeado");
        controller = new JournalController();
        controller.setJournalDAO(journalDAO);
        System.out.println("✓ JournalController creado con DAO mock inyectado");
        System.out.println("=".repeat(80));
    }
    
    /**
     * � TEST FASE VERDE - getJournalEntriesByUser()
     * 
     * ✅ PASA CORRECTAMENTE porque:
     * 1. JournalController TIENE el método getJournalEntriesByUser()
     * 2. El método LLAMA al DAO correctamente
     * 3. El método DEVUELVE la lista correcta
     * 
     * ESCENARIO:
     * - Usuario con ID 1 tiene 3 entradas en su diario
     * - El controlador debe llamar a journalDAO.getJournalEntriesByUser(userId)
     * - El controlador debe devolver la lista completa de entradas
     * 
     * EXPECTATIVAS:
     * - journalDAO.getJournalEntriesByUser() debe ser llamado EXACTAMENTE 1 vez
     * - El resultado debe ser una lista NO vacía
     * - El resultado debe contener las 3 entradas esperadas
     * - Las entradas deben tener los datos correctos
     */
    @Test
    @DisplayName("� VERDE: getJournalEntriesByUser() debe llamar al DAO y devolver lista de entradas")
    void testGetJournalEntriesByUser_DebeRetornarListaDeEntradas() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [TEST FASE VERDE INICIADO]");
        System.out.println("Test: getJournalEntriesByUser() debe llamar al DAO y devolver lista correcta");
        System.out.println("=".repeat(80));
        
        // ==================== ARRANGE ====================
        System.out.println("\n📋 [ARRANGE] Preparando datos de prueba...");
        
        Integer userId = 1;
        LocalDateTime now = LocalDateTime.now();
        
        // Crear entradas de prueba (simulando lo que devolvería la BD)
        JournalEntry entry1 = new JournalEntry(userId, "Primera entrada del diario", now.minusDays(2));
        entry1.setId(1);
        
        JournalEntry entry2 = new JournalEntry(userId, "Segunda entrada del diario", now.minusDays(1));
        entry2.setId(2);
        
        JournalEntry entry3 = new JournalEntry(userId, "Tercera entrada del diario (más reciente)", now);
        entry3.setId(3);
        
        List<JournalEntry> mockEntries = Arrays.asList(entry3, entry2, entry1); // Orden DESC por fecha
        
        System.out.println("✓ Usuario ID: " + userId);
        System.out.println("✓ Entradas esperadas: " + mockEntries.size());
        System.out.println("  - Entry 1 (ID=" + entry1.getId() + "): " + 
            entry1.getContent().substring(0, Math.min(30, entry1.getContent().length())) + "...");
        System.out.println("  - Entry 2 (ID=" + entry2.getId() + "): " + 
            entry2.getContent().substring(0, Math.min(30, entry2.getContent().length())) + "...");
        System.out.println("  - Entry 3 (ID=" + entry3.getId() + "): " + 
            entry3.getContent().substring(0, Math.min(30, entry3.getContent().length())) + "...");
        
        // Configurar el mock del DAO
        when(journalDAO.getJournalEntriesByUser(userId)).thenReturn(mockEntries);
        System.out.println("✓ Mock de JournalDAO configurado para devolver " + mockEntries.size() + " entradas");
        
        // ==================== ACT ====================
        System.out.println("\n⚡ [ACT] Ejecutando controller.getJournalEntriesByUser(" + userId + ")...");
        
        List<JournalEntry> result = controller.getJournalEntriesByUser(userId);
        
        System.out.println("✓ Método ejecutado");
        System.out.println("  Resultado: " + (result != null ? result.size() + " entradas" : "NULL"));
        
        // ==================== ASSERT ====================
        System.out.println("\n✅ [ASSERT] Verificando resultados...");
        
        // 1. Verificar que el DAO fue llamado exactamente 1 vez
        System.out.println("\n[ASSERT 1/4] Verificando que journalDAO.getJournalEntriesByUser() fue llamado 1 vez...");
        verify(journalDAO, times(1)).getJournalEntriesByUser(userId);
        System.out.println("✅ ASSERT 1 PASSED: DAO llamado correctamente");
        
        // 2. Verificar que el resultado NO es null
        System.out.println("\n[ASSERT 2/4] Verificando que el resultado NO es null...");
        assertNotNull(result, "El resultado NO debe ser null");
        System.out.println("✅ ASSERT 2 PASSED: Resultado no es null");
        
        // 3. Verificar que el resultado contiene 3 entradas
        System.out.println("\n[ASSERT 3/4] Verificando que el resultado contiene 3 entradas...");
        assertEquals(3, result.size(), "Debe devolver las 3 entradas del usuario");
        System.out.println("✅ ASSERT 3 PASSED: Contiene 3 entradas");
        
        // 4. Verificar que las entradas son las correctas (orden y contenido)
        System.out.println("\n[ASSERT 4/4] Verificando que las entradas son las correctas...");
        assertEquals(entry3.getId(), result.get(0).getId(), "Primera entrada debe ser la más reciente (ID=3)");
        assertEquals(entry2.getId(), result.get(1).getId(), "Segunda entrada debe ser ID=2");
        assertEquals(entry1.getId(), result.get(2).getId(), "Tercera entrada debe ser la más antigua (ID=1)");
        
        assertEquals(entry3.getContent(), result.get(0).getContent());
        assertEquals(entry2.getContent(), result.get(1).getContent());
        assertEquals(entry1.getContent(), result.get(2).getContent());
        System.out.println("✅ ASSERT 4 PASSED: Las entradas son correctas en orden DESC");
        
        // ==================== RESULTADO ====================
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [TEST FASE VERDE COMPLETADO]");
        System.out.println("RESULTADO: ✅ TEST PASÓ EXITOSAMENTE");
        System.out.println("RAZÓN: JournalController tiene getJournalEntriesByUser() y funciona correctamente");
        System.out.println("=".repeat(80) + "\n");
    }
    
    /**
     * � TEST ADICIONAL FASE VERDE - Lista vacía
     * 
     * ✅ PASA CORRECTAMENTE porque el método maneja listas vacías
     * 
     * ESCENARIO:
     * - Usuario sin entradas en su diario
     * - El controlador debe devolver una lista vacía (no null)
     */
    @Test
    @DisplayName("� VERDE: getJournalEntriesByUser() con usuario sin entradas debe devolver lista vacía")
    void testGetJournalEntriesByUser_UsuarioSinEntradas_DebeRetornarListaVacia() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("� [TEST FASE VERDE INICIADO - CASO LÍMITE]");
        System.out.println("Test: Usuario sin entradas debe devolver lista vacía");
        System.out.println("=".repeat(80));
        
        // ARRANGE
        Integer userId = 999;
        List<JournalEntry> emptyList = Arrays.asList();
        when(journalDAO.getJournalEntriesByUser(userId)).thenReturn(emptyList);
        System.out.println("📋 Usuario ID: " + userId + " (sin entradas)");
        
        // ACT
        List<JournalEntry> result = controller.getJournalEntriesByUser(userId);
        
        // ASSERT
        System.out.println("\n✅ [ASSERT] Verificando...");
        verify(journalDAO, times(1)).getJournalEntriesByUser(userId);
        assertNotNull(result, "El resultado NO debe ser null incluso sin entradas");
        assertTrue(result.isEmpty(), "La lista debe estar vacía");
        
        System.out.println("✅ Test completado - Lista vacía devuelta correctamente");
        System.out.println("� RESULTADO: ✅ TEST PASÓ EXITOSAMENTE");
        System.out.println("=".repeat(80) + "\n");
    }
}
