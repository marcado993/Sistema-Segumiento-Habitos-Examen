package com.sistema_seguimiento.services;

import com.sistema_seguimiento.dao.JournalDAO;
import com.sistema_seguimiento.model.JournalEntry;
import com.sistema_seguimiento.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JournalServiceTest {

    private static EntityManagerFactory emf;
    private static JournalService service;

    @BeforeAll
    static void setupAll() {
        emf = Persistence.createEntityManagerFactory("test-persistence-unit");
        JournalDAO dao = new JournalDAO(emf);
        service = new JournalService(dao);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void saveJournalEntry_persistsEntry_via_DAO_real() {
        // Arrange: Crear usuario primero
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Usuario usuario = null;
        try {
            tx.begin();
            usuario = new Usuario();
            usuario.setNombre("Test User");
            usuario.setCorreo("testservice@example.com");
            usuario.setContrasena("password123");
            usuario.setPuntos(0);
            em.persist(usuario);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
        
        Integer userId = usuario.getId();
        String content = "Entrada desde servicio (T2 HU01)";

        // Act
        JournalEntry saved = service.saveJournalEntry(userId, content);

        // Assert básicos
        assertNotNull(saved, "El servicio debe retornar la entrada guardada");
        assertNotNull(saved.getId(), "Se esperaba ID asignado por la BD");
        assertEquals(userId, saved.getUserId());
        assertEquals(content, saved.getContent());
        assertNotNull(saved.getCreatedAt(), "Debe asignarse createdAt");

        // Verificar realmente en BD (nueva sesión)
        em = emf.createEntityManager();
        try {
            JournalEntry fromDb = em.find(JournalEntry.class, saved.getId());
            assertNotNull(fromDb, "La entrada debe existir en la BD");
            assertEquals(userId, fromDb.getUserId());
            assertEquals(content, fromDb.getContent());
        } finally {
            em.close();
        }
    }
}

