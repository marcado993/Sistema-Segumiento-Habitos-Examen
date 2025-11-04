package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.dao.JournalDAO;
import com.sistema_seguimiento.model.JournalEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JournalControllerTest {

    private static EntityManagerFactory emf;
    private static JournalDAO realDao;
    private static JournalController controller;

    @BeforeAll
    static void setupAll() {
        emf = Persistence.createEntityManagerFactory("test-persistence-unit");
        realDao = new JournalDAO(emf);
        controller = new JournalController();
        controller.setJournalDAO(realDao);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void saveJournalEntry_extractsParams_and_persists_with_DAO_real() {
        // Arrange
        Integer userId = 15;
        String content = "Entrada TDD sin mocks";

        // Act: usar la lógica del controlador
        JournalEntry built = controller.saveJournalEntry(userId, content);
        assertNotNull(built.getCreatedAt(), "La entrada debe tener createdAt asignado");

        // Assert (rojo esperado): verificar que se guardó en BD
        EntityManager em = emf.createEntityManager();
        try {
            List<JournalEntry> results = em.createQuery(
                "SELECT j FROM JournalEntry j WHERE j.userId = :uid", JournalEntry.class)
                .setParameter("uid", userId)
                .getResultList();

            assertEquals(1, results.size(), "Se esperaba exactamente 1 entrada persistida para el usuario");
            JournalEntry fromDb = results.get(0);
            assertNotNull(fromDb.getId(), "Se esperaba ID asignado por la BD");
            assertEquals(content, fromDb.getContent());
        } finally {
            em.close();
        }
    }
}
