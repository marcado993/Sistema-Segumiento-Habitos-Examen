package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.FraseMotivacional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del DAO para frases motivacionales
 * Carga las frases desde la base de datos
 */
public class QuoteDAOImpl implements QuoteDAO {
    
    /**
     * Obtiene todas las frases activas desde la base de datos
     * @return Lista de textos de frases
     */
    @Override
    public List<String> getQuotes() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<FraseMotivacional> query = em.createQuery(
                "SELECT f FROM FraseMotivacional f WHERE f.activa = true ORDER BY f.orden ASC",
                FraseMotivacional.class
            );
            
            List<FraseMotivacional> frases = query.getResultList();
            
            // Si no hay frases en la BD, devolver frases por defecto
            if (frases.isEmpty()) {
                System.out.println("⚠️ No hay frases en la BD, usando frases por defecto");
                return getFrasesDefault();
            }
            
            System.out.println("✅ Cargadas " + frases.size() + " frases desde la BD");
            
            // Convertir entidades a textos
            return frases.stream()
                    .map(FraseMotivacional::getTexto)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            System.err.println("❌ Error al cargar frases de BD: " + e.getMessage());
            return getFrasesDefault();
        } finally {
            em.close();
        }
    }
    
    /**
     * Obtiene todas las entidades de frases motivacionales
     * @return Lista de FraseMotivacional
     */
    public List<FraseMotivacional> getAllFrases() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<FraseMotivacional> query = em.createQuery(
                "SELECT f FROM FraseMotivacional f WHERE f.activa = true ORDER BY f.orden ASC",
                FraseMotivacional.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al cargar frases: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    /**
     * Frases por defecto en caso de que la BD no esté disponible
     * @return Lista de frases hardcodeadas
     */
    private List<String> getFrasesDefault() {
        List<String> quotes = new ArrayList<>();
        
        quotes.add("El éxito es la suma de pequeños esfuerzos repetidos día tras día.");
        quotes.add("No cuentes los días, haz que los días cuenten.");
        quotes.add("La disciplina es el puente entre las metas y los logros.");
        quotes.add("Cada día es una nueva oportunidad para mejorar.");
        quotes.add("Los hábitos son la base del éxito a largo plazo.");
        quotes.add("La constancia es la clave del progreso.");
        quotes.add("Hoy es el día perfecto para comenzar.");
        quotes.add("Pequeños pasos cada día llevan a grandes cambios.");
        quotes.add("Tu futuro se crea con lo que haces hoy, no mañana.");
        quotes.add("La motivación te inicia, el hábito te mantiene.");
        quotes.add("Cree en ti mismo y todo será posible.");
        quotes.add("El único modo de hacer un gran trabajo es amar lo que haces.");
        quotes.add("No esperes el momento perfecto, toma el momento y hazlo perfecto.");
        quotes.add("El éxito no es el final, el fracaso no es fatal: es el coraje para continuar lo que cuenta.");
        quotes.add("Tu única limitación es la que te impones a ti mismo.");
        
        return quotes;
    }
}

