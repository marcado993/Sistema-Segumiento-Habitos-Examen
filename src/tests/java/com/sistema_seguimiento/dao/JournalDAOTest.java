package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.JournalEntry;
import com.sistema_seguimiento.model.Usuario;
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
        // Arrange: Crear usuario primero
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Usuario usuario = null;
        try {
            tx.begin();
            usuario = new Usuario();
            usuario.setNombre("Test User");
            usuario.setCorreo("test@example.com");
            usuario.setContrasena("password123");
            usuario.setPuntos(0);
            em.persist(usuario);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
        
        JournalEntry entry = new JournalEntry(usuario.getId(), "Mi primer apunte", LocalDateTime.now());

        // Act
        JournalEntry persisted = journalDAO.storeJournalEntry(entry);

        // Assert (ID asignado)
        assertNotNull(persisted.getId(), "Se esperaba que el ID fuera asignado tras persistir");

        // Verificar que realmente quedó en BD abriendo un EM nuevo
        em = emf.createEntityManager();
        try {
            JournalEntry fromDb = em.find(JournalEntry.class, persisted.getId());
            assertNotNull(fromDb, "La entidad debería existir en la BD");
            assertEquals("Mi primer apunte", fromDb.getContent());
            assertEquals(usuario.getId(), fromDb.getUserId());
            assertNotNull(fromDb.getCreatedAt());
        } finally {
            em.close();
        }
    }

    @Test
    @Order(2)
    void getJournalEnztriesByUser_returnsEntriesSortedByNewestFirst() {
        // Arrange: Crear usuarios primero
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Usuario usuario1 = null;
        Usuario usuario2 = null;
        try {
            tx.begin();
            
            // Crear usuario 1
            usuario1 = new Usuario();
            usuario1.setNombre("Test User 1");
            usuario1.setCorreo("test1@example.com");
            usuario1.setContrasena("password123");
            usuario1.setPuntos(0);
            em.persist(usuario1);
            
            // Crear usuario 2
            usuario2 = new Usuario();
            usuario2.setNombre("Test User 2");
            usuario2.setCorreo("test2@example.com");
            usuario2.setContrasena("password123");
            usuario2.setPuntos(0);
            em.persist(usuario2);
            
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
        
        // Ahora crear entradas
        em = emf.createEntityManager();
        tx = em.getTransaction();
        try {
            tx.begin();
            LocalDateTime now = LocalDateTime.now();

            // Usuario 1 (3 entradas)
            JournalEntry e1 = new JournalEntry(usuario1.getId(), "Entrada antigua", now.minusDays(2));
            JournalEntry e2 = new JournalEntry(usuario1.getId(), "Entrada intermedia", now.minusDays(1));
            JournalEntry e3 = new JournalEntry(usuario1.getId(), "Entrada reciente", now);
            
            // Asociar usuario
            Usuario u1 = em.find(Usuario.class, usuario1.getId());
            e1.setUsuario(u1);
            e2.setUsuario(u1);
            e3.setUsuario(u1);
            
            em.persist(e1);
            em.persist(e2);
            em.persist(e3);

            // Otro usuario (para validar filtro)
            JournalEntry other = new JournalEntry(usuario2.getId(), "Otra entrada", now.minusDays(3));
            Usuario u2 = em.find(Usuario.class, usuario2.getId());
            other.setUsuario(u2);
            em.persist(other);
            
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }

        // Act
        List<JournalEntry> results = journalDAO.getJournalEntriesByUser(usuario1.getId());

        // Assert: tamaño y orden (más reciente primero)
        assertEquals(3, results.size(), "Se esperaban 3 entradas para el usuario 1");
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
