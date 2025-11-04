package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.dao.JournalDAO;
import com.sistema_seguimiento.model.JournalEntry;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/journal")
public class JournalController extends HttpServlet {

    private JournalDAO journalDAO;

    // Setter para inyección en pruebas
    public void setJournalDAO(JournalDAO journalDAO) {
        this.journalDAO = journalDAO;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Fase ROJA (TDD): implementación vacía a propósito
    }

    /**
     * Fase ROJA (TDD): lógica de guardado aún sin persistencia real.
     * Debe construir la entrada y delegar en DAO, pero aquí solo la construimos para provocar "rojo" natural.
     */
    public JournalEntry saveJournalEntry(Integer userId, String content) {
        return new JournalEntry(userId, content, LocalDateTime.now());
    }
}
