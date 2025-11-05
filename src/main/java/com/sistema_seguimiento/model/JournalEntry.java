package com.sistema_seguimiento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Transient // No guardar este campo en la BD
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario; // Relación real con Usuario

    @Column(name = "content", nullable = false, length = 4000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public JournalEntry() {}

    public JournalEntry(Integer userId, String content, LocalDateTime createdAt) {
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { 
        return usuario != null ? usuario.getId() : userId;
    }
    
    public void setUserId(Integer userId) { 
        this.userId = userId; 
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario;
        if (usuario != null) {
            this.userId = usuario.getId();
        }
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

