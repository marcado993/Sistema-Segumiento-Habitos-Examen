package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.MoodEntry;
import com.sistema_seguimiento.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.junit.*;
import static org.junit.Assert.*;

import java.time.LocalDate;

public class MoodDAOTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private MoodDAO moodDAO;
    private Usuario usuarioPrueba;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("test-persistence-unit");
    }

    @Before
    public void setUp() {
        moodDAO = new MoodDAO(emf);

        EntityManager emSetup = emf.createEntityManager();
        EntityTransaction tx = emSetup.getTransaction();
        try {
            tx.begin();
            emSetup.createQuery("DELETE FROM MoodEntry").executeUpdate();
            emSetup.createQuery("DELETE FROM Usuario").executeUpdate();

            usuarioPrueba = new Usuario("Test User", "test@user.com", "123");
            emSetup.persist(usuarioPrueba);
            tx.commit();
        } catch (Exception e) {
            if(tx.isActive()) tx.rollback();
            throw e; // Lanza el error si el setup falla
        } finally {
            emSetup.close();
        }

        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            em.close();
        }
    }

    @AfterClass
    public static void tearDownClass() {
        emf.close();
    }

    @Test
    public void givenNuevoMoodEntry_whenStoreMoodRecord_thenEsPersistido() {
        System.out.println("--- Ejecutando Test 5/12 (MoodDAO CRUD) ---");
        // Given (Dado)
        MoodEntry entry = new MoodEntry();
        entry.setUsuario(usuarioPrueba);
        entry.setMood("FELIZ");
        entry.setDate(LocalDate.now());

        // When (Cuando)
        moodDAO.save(entry);
        // Then (Entonces)
        MoodEntry guardado = em.find(MoodEntry.class, entry.getId());

        assertNotNull("La entrada no debe ser nula, debió guardarse", guardado);
        assertEquals("El estado de ánimo debe ser 'FELIZ'", "FELIZ", guardado.getMood());
        assertEquals("El ID de usuario debe coincidir", usuarioPrueba.getId(), guardado.getUsuario().getId());

    }
}