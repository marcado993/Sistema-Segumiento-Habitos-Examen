package com.sistema_seguimiento.dao;

import com.sistema_seguimiento.model.RegistroProgreso;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class RegistroProgresoDAO extends BaseDAO{
    

    /**
     * Guarda un nuevo registro de progreso
     */
    public void guardar(RegistroProgreso registro) {
        executeWithTransaction(
            em -> em.persist(registro),
            "Error al guardar registro de progreso"
        );
    }
    
    /**
     * Actualiza un registro de progreso existente
     */
    public void actualizar(RegistroProgreso registro) {
        executeWithTransaction(
            em -> em.merge(registro),
            "Error al actualizar registro de progreso"
        );
    }
    
    /**
     * Obtiene todos los registros de progreso de un hábito
     */
    public List<RegistroProgreso> obtenerPorHabito(Integer habitoId) {
        return executeQuery(em -> {
            TypedQuery<RegistroProgreso> query = em.createQuery(
                "SELECT r FROM RegistroProgreso r WHERE r.habito.id = :habitoId ORDER BY r.fecha DESC",
                RegistroProgreso.class
            );
            query.setParameter("habitoId", habitoId);
            return query.getResultList();
        });
    }
    
    /**
     * Obtiene un registro de progreso por ID
     */
    public RegistroProgreso obtenerPorId(Integer id) {
        return executeQuery(em -> em.find(RegistroProgreso.class, id));
    }
    
    /**
     * Obtiene todos los registros de progreso
     */
    public List<RegistroProgreso> obtenerTodos() {
        return executeQuery(em -> {
            TypedQuery<RegistroProgreso> query = em.createQuery(
                "SELECT r FROM RegistroProgreso r ORDER BY r.fecha DESC",
                RegistroProgreso.class
            );
            return query.getResultList();
        });
    }
    
    /**
     * Elimina un registro de progreso
     */
    public void eliminar(Long id) {
        executeWithTransaction(
            em -> {
                RegistroProgreso registro = em.find(RegistroProgreso.class, id);
                if (registro != null) {
                    em.remove(registro);
                }
            },
            "Error al eliminar registro de progreso"
        );
    }
    
    /**
     * Cuenta los registros cumplidos de un hábito
     */
    public long contarCumplidos(Integer habitoId) {
        return executeQuery(em -> {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM RegistroProgreso r WHERE r.habito.id = :habitoId AND r.cumplido = true",
                Long.class
            );
            query.setParameter("habitoId", habitoId);
            return query.getSingleResult();
        });
    }
}
