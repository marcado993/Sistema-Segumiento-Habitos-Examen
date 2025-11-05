package com.sistema_seguimiento.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Modelo MoodEntry - Entrada de estado de animo del usuario (TDD Green Phase)
 * Representa una entrada de estado de animo diaria del usuario con validacion de fecha
 * @author Luis Guerrero y Jhair Zambrano
 * @version 1.0 - Nov 2025
 * Funcionalidad clave:
 * - isFromToday(): Determina si la entrada corresponde a la fecha actual
 *
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - Fase Verde TDD
 */
@Entity
@Table(name = "mood_entry")
public class MoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Transient // @Transient significa: No guardar este campo en la BD
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Relación real con la entidad Usuario

    @Column(name = "fecha", nullable = false) // 🔧 Columna real en Supabase
    private LocalDate date;

    @Column(name = "estadoanimo", nullable = false) // 🔧 Columna real en Supabase
    private String mood;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public MoodEntry() {
        this.date = LocalDate.now();
    }

    public MoodEntry(Integer userId, LocalDate date, String mood, String notes) {
        this.userId = userId; // Sigue funcionando para la lógica
        this.date = date;
        this.mood = mood;
        this.notes = notes;
    }

    /**
     * 🟢 Verifica si esta entrada es de hoy
     * @return true si la fecha de la entrada es hoy, false en caso contrario
     */
    public boolean isFromToday() {
        if (this.date == null) {
            return false;
        }
        return this.date.isEqual(LocalDate.now());
    }
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Getter para 'userId' (no tiene setter, se maneja vía 'usuario')
    public Integer getUserId() {
        if (this.usuario != null) {
            return this.usuario.getId();
        }
        return this.userId; // Devuelve el temporal si 'usuario' no está cargado
    }

    // Setter para 'userId' (permite seteo temporal antes de asociar Usuario)
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}