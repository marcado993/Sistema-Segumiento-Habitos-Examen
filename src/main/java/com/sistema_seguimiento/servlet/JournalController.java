package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.model.JournalEntry;
import com.sistema_seguimiento.model.Usuario;
import com.sistema_seguimiento.services.IJournalService;
import com.sistema_seguimiento.services.JournalService;
import com.sistema_seguimiento.dao.IJournalDAO;
import com.sistema_seguimiento.dao.JournalDAO;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/journal")
public class JournalController extends HttpServlet {

    // SRP: El controlador solo delega al servicio
    private IJournalService journalService;
    private EntityManagerFactory emf; // ciclo de vida del servlet

    // Setter para inyección del servicio en pruebas
    public void setJournalService(IJournalService journalService) {
        this.journalService = journalService;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        // Wiring por defecto (producción): EMF -> DAO -> Service
        if (this.journalService == null) {
            this.emf = Persistence.createEntityManagerFactory("sistema-seguimiento-pu");
            IJournalDAO dao = new JournalDAO(emf);
            this.journalService = new JournalService(dao);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String content = req.getParameter("content");
        Integer userId = getUsuarioIdFromSession(req.getSession());

        if (journalService == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JournalService no configurado");
            return;
        }

        if ("save".equalsIgnoreCase(action)) {
            journalService.saveJournalEntry(userId, content); // T2/T5 en el servicio
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no soportada");
        }
    }

    /** Delegación al servicio (SRP) */
    public JournalEntry saveJournalEntry(Integer userId, String content) {
        if (journalService == null) {
            throw new IllegalStateException("JournalService no ha sido inicializado");
        }
        return journalService.saveJournalEntry(userId, content);
    }

    /** Delegación al servicio (SRP) */
    public List<JournalEntry> getJournalEntriesByUser(Integer userId) {
        if (journalService == null) {
            throw new IllegalStateException("JournalService no ha sido inicializado");
        }
        List<JournalEntry> entries = journalService.getJournalEntriesByUser(userId);
        // Logging opcional (sin lógica de negocio)
        if (entries == null || entries.isEmpty()) {
            System.out.println("📭 No hay entradas de diario para este usuario");
        } else {
            System.out.println("📚 Entradas de diario obtenidas: " + entries.size());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < entries.size(); i++) {
                JournalEntry entry = entries.get(i);
                System.out.println("#" + (i+1) + " ID=" + entry.getId() + ", user=" + entry.getUserId() + ", fecha=" + (entry.getCreatedAt()!=null? entry.getCreatedAt().format(formatter):"-") );
            }
        }
        return entries;
    }

    /** Obtiene el userId desde la sesión, tolerando ambos enfoques */
    private Integer getUsuarioIdFromSession(HttpSession session) {
        if (session == null) return null;
        Object u = session.getAttribute("usuario");
        if (u instanceof Usuario usuario && usuario.getId() != null) {
            return usuario.getId();
        }
        Object uid = session.getAttribute("userId");
        return (uid instanceof Integer) ? (Integer) uid : null;
    }
}
