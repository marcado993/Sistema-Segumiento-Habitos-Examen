package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.dao.IJournalDAO;
import com.sistema_seguimiento.model.JournalEntry;
import com.sistema_seguimiento.services.IJournalService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/journal")
public class JournalController extends HttpServlet {

    private IJournalDAO journalDAO;
    private IJournalService journalService;

    // Setter para inyección en pruebas
    public void setJournalDAO(IJournalDAO journalDAO) {
        this.journalDAO = journalDAO;
    }
    
    // Setter para inyección del servicio en pruebas
    public void setJournalService(IJournalService journalService) {
        this.journalService = journalService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 🟢 FASE VERDE - Implementación para Mock 1/2 (T2, T5, T8)
        
        // Extraer parámetros del request
        String action = req.getParameter("action");
        String content = req.getParameter("content");
        
        // Obtener userId de la sesión
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        // Validación T5: contenido no debe estar vacío
        if (content == null || content.trim().isEmpty()) {
            System.out.println("⚠️ [JOURNAL CONTROLLER] Contenido vacío o inválido - No se guarda entrada");
            // No llamar al servicio si el contenido es inválido
            return;
        }
        
        // Si la acción es "save" y hay un servicio configurado, guardar la entrada
        if ("save".equals(action) && journalService != null) {
            System.out.println("💾 [JOURNAL CONTROLLER] Guardando entrada de diario...");
            journalService.saveJournalEntry(userId, content);
            System.out.println("✅ [JOURNAL CONTROLLER] Entrada guardada exitosamente");
        }
    }

    /**
     * Fase ROJA (TDD): lógica de guardado aún sin persistencia real.
     * Debe construir la entrada y delegar en DAO, pero aquí solo la construimos para provocar "rojo" natural.
     */
    public JournalEntry saveJournalEntry(Integer userId, String content) {
        return new JournalEntry(userId, content, LocalDateTime.now());
    }
    
    /**
     * 🟢 FASE VERDE - Obtener lista de entradas de diario por usuario (HU01 - T8)
     * 
     * Implementación mínima para pasar el test:
     * - Delega al DAO para obtener las entradas del usuario
     * - Retorna la lista obtenida (ordenada DESC por fecha en el DAO)
     * - Muestra en consola la lista de entradas con formato detallado
     * 
     * @param userId ID del usuario
     * @return Lista de entradas del diario del usuario (ordenada DESC por fecha)
     */
    public List<JournalEntry> getJournalEntriesByUser(Integer userId) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🟢 [JOURNAL CONTROLLER] Obteniendo entradas de diario para usuario ID: " + userId);
        System.out.println("=".repeat(80));
        
        // Validar que el DAO no sea null (importante para IntelliJ IDEA)
        if (journalDAO == null) {
            System.err.println("❌ ERROR: JournalDAO es null. Debe inyectarse antes de usar.");
            throw new IllegalStateException("JournalDAO no ha sido inicializado");
        }
        
        // Delegar al DAO para obtener las entradas
        List<JournalEntry> entries = journalDAO.getJournalEntriesByUser(userId);
        
        // Logging detallado de las entradas obtenidas
        if (entries == null || entries.isEmpty()) {
            System.out.println("📭 No hay entradas de diario para este usuario");
            System.out.println("   Total de entradas: 0");
        } else {
            System.out.println("📚 Entradas de diario obtenidas: " + entries.size());
            System.out.println("-".repeat(80));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int index = 1;
            
            for (JournalEntry entry : entries) {
                System.out.println(String.format("📝 Entrada #%d:", index));
                System.out.println("   ID: " + entry.getId());
                System.out.println("   Usuario ID: " + entry.getUserId());
                
                // Manejo seguro del contenido (null safe)
                String content = entry.getContent();
                if (content != null && content.length() > 60) {
                    System.out.println("   Contenido: " + content.substring(0, 60) + "...");
                } else {
                    System.out.println("   Contenido: " + (content != null ? content : "[vacío]"));
                }
                
                // Manejo seguro de la fecha (null safe)
                if (entry.getCreatedAt() != null) {
                    System.out.println("   Fecha: " + entry.getCreatedAt().format(formatter));
                } else {
                    System.out.println("   Fecha: [no disponible]");
                }
                
                if (index < entries.size()) {
                    System.out.println("   " + "-".repeat(76));
                }
                index++;
            }
        }
        
        System.out.println("=".repeat(80));
        System.out.println("✅ [JOURNAL CONTROLLER] Entradas obtenidas exitosamente");
        System.out.println("=".repeat(80) + "\n");
        
        return entries;
    }
}
