package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.JournalEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JournalDAOTest {

    private static EntityManagerFactory emf;
    private static JournalDAO journalDAO;

    @BeforeAll
    static void setupAll() {
        emf = Persistence.createEntityManagerFactory("test-persistence-unit");
        journalDAO = new JournalDAO(emf);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    @Order(1)
    void storeJournalEntry_persistsEntityAndAssignsId() {
        // Arrange
        JournalEntry entry = new JournalEntry(1, "Mi primer apunte", LocalDateTime.now());

        // Act
        JournalEntry persisted = journalDAO.storeJournalEntry(entry);

        // Assert (ID asignado)
        assertNotNull(persisted.getId(), "Se esperaba que el ID fuera asignado tras persistir");

        // Verificar que realmente quedó en BD abriendo un EM nuevo
        EntityManager em = emf.createEntityManager();
        try {
            JournalEntry fromDb = em.find(JournalEntry.class, persisted.getId());
            assertNotNull(fromDb, "La entidad debería existir en la BD");
            assertEquals("Mi primer apunte", fromDb.getContent());
            assertEquals(1, fromDb.getUserId());
            assertNotNull(fromDb.getCreatedAt());
        } finally {
            em.close();
        }
    }

    @Test
    @Order(2)
    void getJournalEntriesByUser_returnsEntriesSortedByNewestFirst() {
        // Arrange: sembrar datos en H2 manualmente
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            LocalDateTime now = LocalDateTime.now();

            // Usuario 10 (3 entradas)
            JournalEntry e1 = new JournalEntry(10, "Entrada antigua", now.minusDays(2));
            JournalEntry e2 = new JournalEntry(10, "Entrada intermedia", now.minusDays(1));
            JournalEntry e3 = new JournalEntry(10, "Entrada reciente", now);
            em.persist(e1);
            em.persist(e2);
            em.persist(e3);

            // Otro usuario (para validar filtro)
            JournalEntry other = new JournalEntry(20, "Otra entrada", now.minusDays(3));
            em.persist(other);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }

        // Act
        List<JournalEntry> results = journalDAO.getJournalEntriesByUser(10);

        // Assert: tamaño y orden (más reciente primero)
        assertEquals(3, results.size(), "Se esperaban 3 entradas para el usuario 10");
        assertEquals("Entrada reciente", results.get(0).getContent());
        assertEquals("Entrada intermedia", results.get(1).getContent());
        assertEquals("Entrada antigua", results.get(2).getContent());
        assertTrue(
            results.get(0).getCreatedAt().isAfter(results.get(1).getCreatedAt()) &&
            results.get(1).getCreatedAt().isAfter(results.get(2).getCreatedAt()),
            "Las entradas deben estar ordenadas de más reciente a más antigua"
        );
    }
}
