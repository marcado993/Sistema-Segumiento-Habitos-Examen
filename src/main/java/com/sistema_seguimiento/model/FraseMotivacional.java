package com.sistema_seguimiento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "frases_motivacionales")
public class FraseMotivacional {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;
    
    @Column(nullable = false)
    private Boolean activa = true;
    
    @Column(nullable = false)
    private Integer orden = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructores
    public FraseMotivacional() {
        this.createdAt = LocalDateTime.now();
        this.activa = true;
    }
    
    public FraseMotivacional(String texto, Integer orden) {
        this.texto = texto;
        this.orden = orden;
        this.activa = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTexto() {
        return texto;
    }
    
    public void setTexto(String texto) {
        this.texto = texto;
    }
    
    public Boolean getActiva() {
        return activa;
    }
    
    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
    
    public Integer getOrden() {
        return orden;
    }
    
    public void setOrden(Integer orden) {
        this.orden = orden;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "FraseMotivacional{" +
                "id=" + id +
                ", texto='" + texto + '\'' +
                ", activa=" + activa +
                ", orden=" + orden +
                '}';
    }
}

