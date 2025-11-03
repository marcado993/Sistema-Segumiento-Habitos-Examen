package com.sistema_seguimiento.model;

/**
 * Tipos de mascotas virtuales disponibles en el sistema
 * Representan las diferentes etapas de evolución de las mascotas
 */
public enum PetType {
    /**
     * Primera etapa - Se desbloquea con 10 hábitos completados
     */
    HUEVO,

    /**
     * Segunda etapa - Se desbloquea con 50 hábitos completados
     */
    BEBE,

    /**
     * Tercera etapa - Se desbloquea con 100 hábitos completados
     */
    ADULTO,

    /**
     * Cuarta etapa - Se desbloquea con 200 hábitos completados
     */
    LEGENDARIO
}

