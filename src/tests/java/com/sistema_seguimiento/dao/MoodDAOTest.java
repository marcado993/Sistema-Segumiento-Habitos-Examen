package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.MoodEntry;
import com.sistema_seguimiento.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
        em = emf.createEntityManager();
        moodDAO = new MoodDAO();

        em.getTransaction().begin();

        em.createQuery("DELETE FROM MoodEntry").executeUpdate();
        em.createQuery("DELETE FROM Usuario").executeUpdate();

        usuarioPrueba = new Usuario("Test User", "test@user.com", "123");
        em.persist(usuarioPrueba);

    }

    @After
    public void tearDown() {
        // Hacemos commit o rollback de el test
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
        em.close();
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
        moodDAO.storeMoodRecord(em, entry);
        // Then (Entonces)
        MoodEntry guardado = em.find(MoodEntry.class, entry.getId());

        assertNotNull("La entrada no debe ser nula, debió guardarse", guardado);
        assertEquals("El estado de ánimo debe ser 'FELIZ'", "FELIZ", guardado.getMood());
        assertEquals("El ID de usuario debe coincidir", usuarioPrueba.getId(), guardado.getUsuario().getId());

    }
}