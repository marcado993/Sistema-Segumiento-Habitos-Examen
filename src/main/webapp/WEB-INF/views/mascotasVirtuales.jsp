<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sistema_seguimiento.model.UserPet" %>
<%@ page import="com.sistema_seguimiento.model.PetType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    UserPet activePet = (UserPet) request.getAttribute("activePet");
    List<UserPet> allPets = (List<UserPet>) request.getAttribute("allPets");
    Long habitosCompletados = (Long) request.getAttribute("habitosCompletados");
    Integer proximoHito = (Integer) request.getAttribute("proximoHito");
    Integer progresoParaProximoHito = (Integer) request.getAttribute("progresoParaProximoHito");
    PetType tipoEsperado = (PetType) request.getAttribute("tipoEsperado");
    Boolean deberiaDesbloquear = (Boolean) request.getAttribute("deberiaDesbloquear");
    String unlockedParam = request.getParameter("unlocked");
    
    String nombreUsuario = (String) session.getAttribute("nombre");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // ✅ Valores por defecto para evitar errores
    if (habitosCompletados == null) habitosCompletados = 0L;
    if (proximoHito == null) proximoHito = 10;
    if (progresoParaProximoHito == null) progresoParaProximoHito = 10;
    if (allPets == null) allPets = java.util.Collections.emptyList();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Mascotas Virtuales - Sistema de Seguimiento</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #E9F7EF 0%, #FFE5E5 100%);
            min-height: 100vh;
            padding: 20px;
            color: #555;
        }
        .container { max-width: 1200px; margin: 0 auto; }
        
        /* Header */
        .header {
            background: white;
            padding: 2rem;
            border-radius: 20px;
            margin-bottom: 2rem;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .header h1 {
            font-size: 36px;
            font-weight: 700;
            color: #555;
            margin-bottom: 10px;
        }
        .header p {
            font-size: 16px;
            color: #888;
            font-weight: 500;
        }
        
        /* Notificación de desbloqueo */
        .unlock-notification {
            background: linear-gradient(135deg, #FFD6A5, #A8E6CF);
            padding: 1.5rem;
            border-radius: 16px;
            margin-bottom: 2rem;
            text-align: center;
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.02); }
        }
        .unlock-notification h2 {
            font-size: 28px;
            color: #555;
            margin-bottom: 10px;
        }
        .unlock-notification p {
            font-size: 16px;
            color: #666;
        }
        
        /* Pet Display Card */
        .pet-display {
            background: white;
            padding: 3rem;
            border-radius: 24px;
            margin-bottom: 2rem;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .pet-icon {
            font-size: 120px;
            margin-bottom: 1rem;
            animation: float 3s ease-in-out infinite;
        }
        @keyframes float {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }
        .pet-name {
            font-size: 28px;
            font-weight: 700;
            color: #555;
            margin-bottom: 0.5rem;
        }
        .pet-description {
            font-size: 16px;
            color: #888;
            margin-bottom: 1rem;
        }
        .pet-state-badge {
            display: inline-block;
            padding: 0.5rem 1.5rem;
            background: linear-gradient(135deg, #A8E6CF, #FFD6A5);
            color: #555;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
        }
        
        /* Empty State */
        .empty-state {
            padding: 4rem 2rem;
        }
        .empty-state-icon {
            font-size: 100px;
            margin-bottom: 1.5rem;
            opacity: 0.6;
        }
        .empty-state-title {
            font-size: 24px;
            font-weight: 600;
            color: #666;
            margin-bottom: 1rem;
        }
        .empty-state-text {
            font-size: 16px;
            color: #888;
            margin-bottom: 2rem;
            line-height: 1.6;
        }
        
        /* Stats Card */
        .stats-card {
            background: white;
            padding: 2rem;
            border-radius: 20px;
            margin-bottom: 2rem;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }
        .stats-card h2 {
            font-size: 24px;
            font-weight: 600;
            color: #555;
            margin-bottom: 1.5rem;
        }
        .stat-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 12px;
            margin-bottom: 1rem;
        }
        .stat-label {
            font-size: 16px;
            color: #666;
            font-weight: 500;
        }
        .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #555;
        }
        
        /* Progress Container */
        .progress-container {
            margin-top: 1.5rem;
        }
        .progress-label {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
            font-size: 14px;
            color: #666;
        }
        .progress-bar-bg {
            height: 12px;
            background: #e0e0e0;
            border-radius: 10px;
            overflow: hidden;
        }
        .progress-bar-fill {
            height: 100%;
            background: linear-gradient(90deg, #A8E6CF, #FFD6A5);
            border-radius: 10px;
            transition: width 0.5s ease;
        }
        
        /* Evolution Timeline */
        .evolution-timeline {
            background: white;
            padding: 2rem;
            border-radius: 20px;
            margin-bottom: 2rem;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }
        .evolution-timeline h2 {
            font-size: 24px;
            font-weight: 600;
            color: #555;
            margin-bottom: 2rem;
            text-align: center;
        }
        .timeline {
            display: flex;
            justify-content: space-around;
            align-items: center;
            position: relative;
            padding: 2rem 0;
        }
        .timeline::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 10%;
            right: 10%;
            height: 4px;
            background: linear-gradient(90deg, #A8E6CF, #FFD6A5);
            transform: translateY(-50%);
            z-index: 0;
        }
        .milestone {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 0.5rem;
            position: relative;
            z-index: 1;
        }
        .milestone-icon {
            font-size: 60px;
            width: 100px;
            height: 100px;
            background: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            border: 4px solid #e0e0e0;
            transition: all 0.3s;
        }
        .milestone.unlocked .milestone-icon {
            border-color: #A8E6CF;
            box-shadow: 0 8px 20px rgba(168, 230, 207, 0.4);
        }
        .milestone-label {
            font-size: 16px;
            font-weight: 600;
            color: #666;
        }
        .milestone.unlocked .milestone-label {
            color: #555;
        }
        .milestone-requirement {
            font-size: 12px;
            color: #888;
        }
        
        /* Buttons */
        .btn-container {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }
        .btn {
            padding: 14px 32px;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
            font-family: 'Poppins', sans-serif;
        }
        .btn-primary {
            background: linear-gradient(135deg, #A8E6CF, #FFD6A5);
            color: #555;
        }
        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(168, 230, 207, 0.4);
        }
        .btn-secondary {
            background: #f5f5f5;
            color: #555;
        }
        .btn-secondary:hover {
            background: #e0e0e0;
            transform: translateY(-2px);
        }
        
        @media (max-width: 768px) {
            .timeline { flex-wrap: wrap; gap: 2rem; }
            .timeline::before { display: none; }
            .collection-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🐣 Mascotas Virtuales</h1>
            <p>Cuida tu mascota completando hábitos</p>
        </div>
        
        <% if (unlockedParam != null) { %>
        <div class="unlock-notification">
            <h2>🎉 ¡Felicitaciones <%= nombreUsuario %>!</h2>
            <p>Has desbloqueado una nueva mascota: <strong><%= unlockedParam %></strong></p>
        </div>
        <% } %>
        
        <% if (deberiaDesbloquear != null && deberiaDesbloquear) { %>
        <div class="unlock-notification">
            <h2>🎁 ¡Tienes una mascota esperando!</h2>
            <p>Has alcanzado <%= habitosCompletados %> hábitos completados. ¡Reclama tu nueva mascota!</p>
            <a href="${pageContext.request.contextPath}/mascotas-virtuales?action=check-unlock" class="btn btn-primary" style="margin-top: 1rem;">Reclamar Mascota</a>
        </div>
        <% } %>
        
        <% if (activePet != null) { %>
        <div class="pet-display">
            <div class="pet-icon"><%= getPetEmoji(activePet.getState()) %></div>
            <div class="pet-name"><%= activePet.getPet().getName() %></div>
            <div class="pet-description"><%= activePet.getPet().getDescription() %></div>
            <div class="pet-state-badge"><%= formatPetState(activePet.getState()) %></div>
            <p style="margin-top: 1rem; color: #888; font-size: 14px;">
                Desbloqueado el <%= activePet.getUnlockedAt().format(formatter) %>
            </p>
        </div>
        <% } else { %>
        <div class="pet-display empty-state">
            <div class="empty-state-icon">🥚</div>
            <div class="empty-state-title">Aún no tienes una mascota</div>
            <div class="empty-state-text">
                Completa <strong>10 hábitos</strong> para desbloquear tu primera mascota.<br>
                Actualmente llevas: <strong><%= habitosCompletados %> hábitos</strong>
            </div>
            <% if (habitosCompletados >= 10) { %>
            <a href="${pageContext.request.contextPath}/mascotas-virtuales?action=check-unlock" class="btn btn-primary">Desbloquear Mi Mascota</a>
            <% } else { %>
            <a href="${pageContext.request.contextPath}/controlador-habitos?action=registrar" class="btn btn-primary">Registrar Hábitos</a>
            <% } %>
        </div>
        <% } %>
        
        <div class="stats-card">
            <h2>📊 Estadísticas</h2>
            <div class="stat-item">
                <span class="stat-label">Hábitos Completados</span>
                <span class="stat-value"><%= habitosCompletados %></span>
            </div>
            <div class="stat-item">
                <span class="stat-label">Mascotas Desbloqueadas</span>
                <span class="stat-value"><%= allPets != null ? allPets.size() : 0 %></span>
            </div>
            
            <div class="progress-container">
                <div class="progress-label">
                    <span>Progreso hacia la próxima mascota</span>
                    <span><strong><%= habitosCompletados %></strong> / <%= proximoHito %></span>
                </div>
                <div class="progress-bar-bg">
                    <% 
                        int progress = (int) ((habitosCompletados * 100.0) / proximoHito);
                        if (progress > 100) progress = 100;
                    %>
                    <div class="progress-bar-fill" style="width: <%= progress %>%;"></div>
                </div>
                <p style="margin-top: 10px; font-size: 14px; color: #888;">
                    Te faltan <strong><%= progresoParaProximoHito > 0 ? progresoParaProximoHito : 0 %> hábitos</strong> para desbloquear la siguiente mascota
                </p>
            </div>
        </div>
        
        <div class="evolution-timeline">
            <h2>🌟 Línea de Evolución</h2>
            <div class="timeline">
                <div class="milestone <%= habitosCompletados >= 10 ? "unlocked" : "" %>">
                    <div class="milestone-icon">🥚</div>
                    <div class="milestone-label">Huevo</div>
                    <div class="milestone-requirement">10 hábitos</div>
                </div>
                <div class="milestone <%= habitosCompletados >= 50 ? "unlocked" : "" %>">
                    <div class="milestone-icon">🐣</div>
                    <div class="milestone-label">Bebé</div>
                    <div class="milestone-requirement">50 hábitos</div>
                </div>
                <div class="milestone <%= habitosCompletados >= 100 ? "unlocked" : "" %>">
                    <div class="milestone-icon">🦋</div>
                    <div class="milestone-label">Adulto</div>
                    <div class="milestone-requirement">100 hábitos</div>
                </div>
                <div class="milestone <%= habitosCompletados >= 200 ? "unlocked" : "" %>">
                    <div class="milestone-icon">🐉</div>
                    <div class="milestone-label">Legendario</div>
                    <div class="milestone-requirement">200 hábitos</div>
                </div>
            </div>
        </div>
        
        <div class="btn-container">
            <a href="${pageContext.request.contextPath}/controlador-habitos?action=registrar" class="btn btn-primary">
                📝 Registrar Hábitos
            </a>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-secondary">
                🏠 Volver al Dashboard
            </a>
        </div>
    </div>
</body>
</html>

<%!
    // Métodos auxiliares para el JSP
    private String getPetEmoji(PetType type) {
        if (type == null) return "🥚";
        switch (type) {
            case HUEVO: return "🥚";
            case BEBE: return "🐣";
            case ADULTO: return "🦋";
            case LEGENDARIO: return "🐉";
            default: return "🥚";
        }
    }
    
    private String formatPetState(PetType type) {
        if (type == null) return "Fase: Desconocida";
        switch (type) {
            case HUEVO: return "Fase: Huevo";
            case BEBE: return "Fase: Bebé";
            case ADULTO: return "Fase: Adulto";
            case LEGENDARIO: return "Fase: Legendario";
            default: return "Fase: Desconocida";
        }
    }
%>

