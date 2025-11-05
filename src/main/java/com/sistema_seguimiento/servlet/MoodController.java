package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.dao.MoodDAO;
import com.sistema_seguimiento.model.MoodEntry;
import com.sistema_seguimiento.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;

/**
 * MoodController - Controlador para el registro de estado de ánimo
 * 
 * Historia de Usuario: Resumen de estado de ánimo diario
 * 
 * Escenarios implementados:
 * - Escenario 1: Guardar estado de ánimo con confirmación
 * - Escenario 2: Notificación si no hay registro del día
 * - Escenario 3: Permitir cambiar estado del mismo día
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0
 */
@WebServlet("/mood-tracker")
public class MoodController extends HttpServlet {

    private MoodDAO moodDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // Inicializar DAO (producción)
        this.moodDAO = new MoodDAO();
    }

    // Setter para inyección en pruebas
    public void setMoodDAO(MoodDAO moodDAO) {
        this.moodDAO = moodDAO;
    }

    /**
     * Maneja GET - Muestra la vista del mood tracker
     * 
     * Escenario 2: Si es final del día y no hay registro, muestra notificación
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        // Validar sesión
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Integer userId = usuario.getId();
        
        try {
            // Verificar si ya existe un registro de hoy
            MoodEntry todayMood = moodDAO.getTodayMoodEntry(userId);
            
            if (todayMood != null) {
                // Ya existe registro de hoy - permitir edición (Escenario 3)
                req.setAttribute("existingMood", todayMood);
                req.setAttribute("canEdit", true);
            } else {
                // No hay registro - sugerir crear uno (Escenario 2)
                req.setAttribute("showReminder", true);
            }
            
            // Redirigir a la vista
            req.getRequestDispatcher("/WEB-INF/views/moodTracker.jsp").forward(req, resp);
            
        } catch (Exception e) {
            System.err.println("❌ [MOOD CONTROLLER] Error al cargar mood tracker: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error al cargar el registro de estado de ánimo.");
            req.getRequestDispatcher("/WEB-INF/views/moodTracker.jsp").forward(req, resp);
        }
    }

    /**
     * Maneja POST - Guarda o actualiza el estado de ánimo
     * 
     * Escenario 1: Guardar estado de ánimo con confirmación
     * Escenario 3: Actualizar estado del mismo día
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        // Validar sesión
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Integer userId = usuario.getId();
        
        // Extraer parámetros
        String action = req.getParameter("action");
        String moodValue = req.getParameter("mood");
        String notes = req.getParameter("notes");
        
        // Validar que se haya seleccionado un mood
        if (moodValue == null || moodValue.trim().isEmpty()) {
            MoodEntry todayMood = moodDAO.getTodayMoodEntry(userId);
            if (todayMood != null) {
                req.setAttribute("existingMood", todayMood);
                req.setAttribute("canEdit", true);
            }
            req.setAttribute("warningMessage", "⚠️ Por favor, selecciona un estado de ánimo antes de guardar.");
            req.getRequestDispatcher("/WEB-INF/views/moodTracker.jsp").forward(req, resp);
            return;
        }
        
        try {
            if ("save".equals(action)) {
                // Verificar si ya existe un registro de hoy
                MoodEntry existingMood = moodDAO.getTodayMoodEntry(userId);
                
                if (existingMood != null) {
                    // Escenario 3: Actualizar registro existente del mismo día
                    updateMoodSelection(existingMood, moodValue, notes, session, resp);
                } else {
                    // Escenario 1: Guardar nuevo registro
                    saveMoodSelection(userId, moodValue, notes, session, resp);
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ [MOOD CONTROLLER] Error al guardar mood: " + e.getMessage());
            e.printStackTrace();
            
            MoodEntry todayMood = moodDAO.getTodayMoodEntry(userId);
            if (todayMood != null) {
                req.setAttribute("existingMood", todayMood);
                req.setAttribute("canEdit", true);
            }
            req.setAttribute("errorMessage", "Ocurrió un error al guardar tu estado de ánimo. Por favor, intenta nuevamente.");
            req.getRequestDispatcher("/WEB-INF/views/moodTracker.jsp").forward(req, resp);
        }
    }

    /**
     * Escenario 1: Guarda la selección de mood del usuario
     * 
     * @param userId ID del usuario
     * @param moodValue Emoji seleccionado
     * @param notes Notas opcionales
     * @param session Sesión HTTP
     * @param resp Response para redirección
     */
    private void saveMoodSelection(Integer userId, String moodValue, String notes, 
                                    HttpSession session, HttpServletResponse resp) throws IOException {
        System.out.println("💾 [MOOD CONTROLLER] Guardando nuevo estado de ánimo...");
        
        // Crear nueva entrada de mood
        MoodEntry newMood = new MoodEntry();
        newMood.setUserId(userId);
        newMood.setDate(LocalDate.now());
        newMood.setMood(moodValue);
        newMood.setNotes(notes);
        
        // Guardar en BD
        MoodEntry savedMood = moodDAO.storeMoodRecord(newMood);
        
        System.out.println("✅ [MOOD CONTROLLER] Estado de ánimo guardado con ID: " + savedMood.getId());
        
        // Escenario 1: Mensaje de confirmación
        String moodEmoji = getMoodEmoji(moodValue);
        session.setAttribute("successMessage", 
            "✅ ¡Perfecto! Tu estado de ánimo " + moodEmoji + " ha sido registrado para hoy.");
        
        // Redirigir (POST-REDIRECT-GET pattern)
        resp.sendRedirect("mood-tracker");
    }

    /**
     * Escenario 3: Actualiza la selección de mood del usuario (solo mismo día)
     * 
     * @param existingMood Entrada existente
     * @param moodValue Nuevo valor de mood
     * @param notes Nuevas notas
     * @param session Sesión HTTP
     * @param resp Response para redirección
     */
    private void updateMoodSelection(MoodEntry existingMood, String moodValue, String notes,
                                      HttpSession session, HttpServletResponse resp) throws IOException {
        System.out.println("🔄 [MOOD CONTROLLER] Actualizando estado de ánimo existente...");
        
        // Verificar que sea del mismo día (Escenario 3)
        if (!existingMood.isFromToday()) {
            System.err.println("❌ [MOOD CONTROLLER] Intento de actualizar mood de día anterior");
            session.setAttribute("errorMessage", 
                "❌ No puedes modificar el estado de ánimo de días anteriores.");
            resp.sendRedirect("mood-tracker");
            return;
        }
        
        // Actualizar valores
        existingMood.setMood(moodValue);
        existingMood.setNotes(notes);
        
        // Actualizar en BD
        MoodEntry updatedMood = moodDAO.update(existingMood);
        
        System.out.println("✅ [MOOD CONTROLLER] Estado de ánimo actualizado con ID: " + updatedMood.getId());
        
        // Escenario 3: Mensaje de confirmación de actualización
        String moodEmoji = getMoodEmoji(moodValue);
        session.setAttribute("successMessage", 
            "✅ Tu estado de ánimo ha sido actualizado a " + moodEmoji + " correctamente.");
        
        // Redirigir (POST-REDIRECT-GET pattern)
        resp.sendRedirect("mood-tracker");
    }

    /**
     * Convierte el valor del mood en emoji para mensajes
     */
    private String getMoodEmoji(String moodValue) {
        switch (moodValue) {
            case "very-sad": return "😢 (Muy Triste)";
            case "sad": return "😔 (Triste)";
            case "neutral": return "😐 (Indiferente)";
            case "happy": return "😊 (Feliz)";
            case "very-happy": return "😄 (Muy Feliz)";
            default: return moodValue;
        }
    }
}
