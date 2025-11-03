package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.Pet;
import com.sistema_seguimiento.model.PetType;
import com.sistema_seguimiento.model.UserPet;
import com.sistema_seguimiento.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO específico para gestionar UserPets con lógica de negocio adicional
 * Complementa el PetDAO existente con operaciones específicas para las mascotas de usuarios
 */
public class UserPetDAO {

    /**
     * Obtiene la mascota activa del usuario
     */
    public Optional<UserPet> findActiveUserPet(Integer usuarioId) {
        if (usuarioId == null) return Optional.empty();
        
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<UserPet> query = em.createQuery(
                "SELECT up FROM UserPet up " +
                "JOIN FETCH up.pet " +
                "WHERE up.usuario.id = :usuarioId AND up.active = true " +
                "ORDER BY up.unlockedAt DESC",
                UserPet.class);
            query.setParameter("usuarioId", usuarioId);
            query.setMaxResults(1);
            
            List<UserPet> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            System.err.println("Error al buscar mascota activa: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todas las mascotas del usuario (activas e inactivas)
     */
    public List<UserPet> findAllUserPets(Integer usuarioId) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<UserPet> query = em.createQuery(
                "SELECT up FROM UserPet up " +
                "JOIN FETCH up.pet " +
                "WHERE up.usuario.id = :usuarioId " +
                "ORDER BY up.unlockedAt DESC",
                UserPet.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta cuántos hábitos ha completado el usuario
     * @param usuarioId ID del usuario
     * @return Total de hábitos cumplidos
     */
    public long countCompletedHabits(Integer usuarioId) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(rh) FROM RegistroHabito rh " +
                "JOIN rh.habito h " +
                "WHERE h.usuarioId = :usuarioId AND rh.completado = true",
                Long.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getSingleResult();
        } catch (Exception e) {
            System.err.println("Error al contar hábitos completados: " + e.getMessage());
            return 0L;
        } finally {
            em.close();
        }
    }

    /**
     * Crea una nueva mascota para el usuario y desactiva la anterior
     */
    public UserPet createUserPet(Integer usuarioId, PetType petType) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();

            // Buscar usuario
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
            }

            // Desactivar mascota actual si existe
            TypedQuery<UserPet> query = em.createQuery(
                "SELECT up FROM UserPet up WHERE up.usuario.id = :usuarioId AND up.active = true",
                UserPet.class);
            query.setParameter("usuarioId", usuarioId);
            List<UserPet> activePets = query.getResultList();
            
            for (UserPet up : activePets) {
                up.setActive(false);
                em.merge(up);
            }

            // Buscar o crear Pet template
            Pet pet = findOrCreatePetByType(em, petType);

            // Crear nueva UserPet
            UserPet newUserPet = new UserPet(usuario, pet, petType);
            newUserPet.setActive(true);
            newUserPet.setProgress(0);
            em.persist(newUserPet);

            tx.commit();
            
            System.out.println("✅ Mascota " + petType + " creada para usuario " + usuarioId);
            return newUserPet;
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error al crear UserPet: " + e.getMessage());
            throw new RuntimeException("Error al crear mascota de usuario", e);
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza el estado de la mascota activa del usuario
     */
    public boolean updatePetState(Integer usuarioId, PetType newState) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();

            TypedQuery<UserPet> query = em.createQuery(
                "SELECT up FROM UserPet up WHERE up.usuario.id = :usuarioId AND up.active = true",
                UserPet.class);
            query.setParameter("usuarioId", usuarioId);
            query.setMaxResults(1);
            
            List<UserPet> results = query.getResultList();
            if (results.isEmpty()) {
                tx.rollback();
                return false;
            }

            UserPet userPet = results.get(0);
            userPet.setState(newState);
            em.merge(userPet);

            tx.commit();
            
            System.out.println("✅ Estado de mascota actualizado a " + newState + " para usuario " + usuarioId);
            return true;
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error al actualizar estado de mascota: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si el usuario ya tiene una mascota de cierto tipo
     */
    public boolean hasPetType(Integer usuarioId, PetType petType) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(up) FROM UserPet up " +
                "WHERE up.usuario.id = :usuarioId AND up.state = :petType",
                Long.class);
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("petType", petType);
            
            Long count = query.getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Busca o crea un Pet template por tipo dentro de una transacción
     */
    private Pet findOrCreatePetByType(EntityManager em, PetType petType) {
        TypedQuery<Pet> query = em.createQuery(
            "SELECT p FROM Pet p WHERE p.type = :type",
            Pet.class);
        query.setParameter("type", petType);
        query.setMaxResults(1);
        
        List<Pet> results = query.getResultList();
        
        if (!results.isEmpty()) {
            return results.get(0);
        }
        
        // Crear nuevo Pet si no existe
        Pet newPet = new Pet(petType, getPetName(petType), getPetDescription(petType));
        em.persist(newPet);
        return newPet;
    }

    /**
     * Obtiene el nombre de la mascota según su tipo
     */
    private String getPetName(PetType type) {
        return switch (type) {
            case HUEVO -> "Huevo Misterioso";
            case BEBE -> "Bebé Virtual";
            case ADULTO -> "Compañero Adulto";
            case LEGENDARIO -> "Mascota Legendaria";
        };
    }

    /**
     * Obtiene la descripción de la mascota según su tipo
     */
    private String getPetDescription(PetType type) {
        return switch (type) {
            case HUEVO -> "Un huevo lleno de potencial. ¿Qué habrá dentro?";
            case BEBE -> "Una adorable criatura que necesita tu cuidado.";
            case ADULTO -> "Un compañero fuerte y leal que te acompaña en tu viaje.";
            case LEGENDARIO -> "¡Una mascota legendaria! El resultado de tu dedicación absoluta.";
        };
    }

    /**
     * Guarda o actualiza un UserPet
     */
    public UserPet save(UserPet userPet) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            if (userPet.getId() == null) {
                em.persist(userPet);
            } else {
                userPet = em.merge(userPet);
            }
            
            tx.commit();
            return userPet;
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al guardar UserPet", e);
        } finally {
            em.close();
        }
    }
}

