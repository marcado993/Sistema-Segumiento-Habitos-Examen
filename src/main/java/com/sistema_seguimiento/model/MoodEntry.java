package com.sistema_seguimiento.model;

import java.time.LocalDate;

/**
 * Modelo MoodEntry - Entrada de estado de animo del usuario (TDD Green Phase)
 * Representa una entrada de estado de animo diaria del usuario con validacion de fecha
 * @author Marco Antonio Castro - Nov 2025
 * 
 * Funcionalidad clave:
 * - isFromToday(): Determina si la entrada corresponde a la fecha actual
 * 
 * @author Sistema Seguimiento Habitos
 * @version 1.0 - Fase Verde TDD
 */
public class MoodEntry {
    
    private Integer userId;
    private LocalDate date;
    private String mood;
    private String notes;
    
    // Constructor
    public MoodEntry(Integer userId, LocalDate date, String mood, String notes) {
        this.userId = userId;
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
    
    // Getters
    public Integer getUserId() {
        return userId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getMood() {
        return mood;
    }
    
    public String getNotes() {
        return notes;
    }
    
    // Setters
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public void setMood(String mood) {
        this.mood = mood;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
