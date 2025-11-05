<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Verificar si el usuario está logueado
    HttpSession currentSession = request.getSession(false);
    if (currentSession == null || currentSession.getAttribute("usuario") == null) {
        // Si no está logueado, redirigir al login
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // Si está logueado, obtener información del usuario
    String nombreUsuario = (String) currentSession.getAttribute("nombre");
    String correoUsuario = (String) currentSession.getAttribute("correo");
    
    // Obtener puntos del usuario
    com.sistema_seguimiento.model.Usuario usuario = (com.sistema_seguimiento.model.Usuario) currentSession.getAttribute("usuario");
    int puntosUsuario = (usuario != null && usuario.getPuntos() != null) ? usuario.getPuntos() : 0;
    
    // Verificar si debe mostrar la frase diaria
    Boolean mostrarFrase = (Boolean) currentSession.getAttribute("mostrarFraseDiaria");
    String fraseDiaria = (String) currentSession.getAttribute("fraseDiaria");
    
    // Limpiar el atributo para que no se muestre de nuevo en la misma sesión
    if (mostrarFrase != null && mostrarFrase) {
        currentSession.removeAttribute("mostrarFraseDiaria");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Sistema de Seguimiento</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Poppins', sans-serif; background: #E9F7EF; min-height: 100vh; padding: 20px; color: #555; }
        .container { max-width: 1200px; margin: 0 auto; }
        .header { background: white; padding: 2rem; border-radius: 16px; margin-bottom: 2rem; box-shadow: 0 4px 12px rgba(0,0,0,0.05); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; }
        .header-left h1 { font-family: 'Poppins', sans-serif; color: #555; font-size: 32px; font-weight: 700; margin-bottom: 5px; }
        .header-left .subtitle { font-family: 'Poppins', sans-serif; color: #888; font-size: 18px; font-weight: 500; }
        .user-info { background: linear-gradient(135deg, #A8E6CF, #FFD6A5); color: #555; padding: 15px 20px; border-radius: 12px; display: flex; align-items: center; gap: 15px; }
        .user-avatar { width: 50px; height: 50px; background: rgba(255,255,255,0.6); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; }
        .user-details .welcome { font-size: 12px; opacity: 0.8; }
        .user-details .username { font-size: 16px; font-weight: 600; margin-top: 2px; }
        .user-details .email { font-size: 11px; opacity: 0.7; margin-top: 2px; }
        .user-points { background: rgba(255,255,255,0.8); padding: 8px 16px; border-radius: 10px; display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 18px; }
        .user-points .points-icon { font-size: 24px; }
        .btn-logout { background: rgba(255,255,255,0.6); color: #555; padding: 8px 16px; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.3s; text-decoration: none; display: inline-block; }
        .btn-logout:hover { background: white; transform: translateY(-2px); box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; margin-bottom: 2rem; }
        .dashboard-card { background: white; padding: 2rem; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); transition: all 0.3s; text-decoration: none; color: #555; display: block; position: relative; overflow: hidden; }
        .dashboard-card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #A8E6CF, #FFD6A5); transform: scaleX(0); transition: transform 0.3s; }
        .dashboard-card:hover::before { transform: scaleX(1); }
        .dashboard-card:hover { transform: translateY(-5px); box-shadow: 0 8px 24px rgba(0,0,0,0.12); }
        .card-icon { width: 60px; height: 60px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 32px; margin-bottom: 1rem; }
        .card-objetivo .card-icon { background: #A8E6CF; }
        .card-planificar .card-icon { background: #FFD6A5; }
        .card-habito .card-icon { background: #F3E8FF; }
        .card-seguimiento .card-icon { background: #FFB6B9; }
        .card-mascotas .card-icon { background: #FFE5B4; }
        .card-title { font-family: 'Poppins', sans-serif; font-size: 20px; font-weight: 600; margin-bottom: 8px; color: #555; }
        .card-description { font-size: 14px; color: #888; line-height: 1.6; }
        .section-title { font-family: 'Poppins', sans-serif; font-size: 24px; font-weight: 600; color: #555; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 10px; }
        .section-divider { height: 3px; background: linear-gradient(90deg, #A8E6CF, transparent); margin-bottom: 1.5rem; border-radius: 2px; }
        
        /* Estilos para la ventana emergente de frase diaria */
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 1000; animation: fadeIn 0.3s ease; }
        .modal-overlay.show { display: flex; align-items: center; justify-content: center; }
        .modal-content { background: white; padding: 3rem 2.5rem; border-radius: 20px; max-width: 500px; width: 90%; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3); position: relative; animation: slideIn 0.4s ease; }
        .modal-header { text-align: center; margin-bottom: 1.5rem; }
        .modal-icon { font-size: 60px; margin-bottom: 10px; }
        .modal-title { font-family: 'Poppins', sans-serif; font-size: 24px; font-weight: 700; color: #555; margin-bottom: 5px; }
        .modal-subtitle { font-family: 'Poppins', sans-serif; font-size: 16px; color: #888; font-weight: 500; }
        .modal-quote { font-family: 'Poppins', sans-serif; font-size: 20px; color: #555; line-height: 1.6; text-align: center; margin: 2rem 0; padding: 1.5rem; background: linear-gradient(135deg, #A8E6CF20, #FFD6A520); border-radius: 12px; border-left: 4px solid #A8E6CF; font-weight: 500; font-style: italic; }
        .modal-footer { text-align: center; }
        .btn-close-modal { background: linear-gradient(135deg, #A8E6CF, #FFD6A5); color: #555; padding: 12px 30px; border: none; border-radius: 12px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.3s; }
        .btn-close-modal:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2); }
        
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
        @keyframes slideIn { from { transform: translateY(-50px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
        
        @media (max-width: 768px) {
            .header { flex-direction: column; text-align: center; }
            .user-info { flex-direction: column; }
            .dashboard-grid { grid-template-columns: 1fr; }
            .modal-content { padding: 2rem 1.5rem; }
            .modal-quote { font-size: 18px; }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="header-left">
                <h1>🎯 Dashboard - Sistema de Seguimiento</h1>
                <p class="subtitle">Alcanza tus metas, un hábito a la vez</p>
            </div>
            <div class="user-info">
                <div class="user-avatar">👤</div>
                <div class="user-details">
                    <div class="welcome">Bienvenido/a</div>
                    <div class="username"><%= nombreUsuario != null ? nombreUsuario : "Usuario" %></div>
                    <div class="email"><%= correoUsuario %></div>
                </div>
                <div class="user-points">
                    <span class="points-icon">🎮</span>
                    <span><%= puntosUsuario %> pts</span>
                </div>
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">🚪 Salir</a>
            </div>
        </div>
        
        <div class="section-title">🎯 Gestión de Objetivos</div>
        <div class="section-divider"></div>
        
        <div class="dashboard-grid">
            <a href="${pageContext.request.contextPath}/controlador-objetivos?action=nuevo" class="dashboard-card card-objetivo">
                <div class="card-icon">📋</div>
                <div class="card-title">Establecer Objetivo</div>
                <div class="card-description">Define tu meta general y establece fechas límite para alcanzarla. Este es el primer paso hacia el éxito.</div>
            </a>
            
            <a href="${pageContext.request.contextPath}/planificar" class="dashboard-card card-planificar">
                <div class="card-icon">🎯</div>
                <div class="card-title">Planificar Objetivo</div>
                <div class="card-description">Asocia hábitos específicos a tus objetivos para crear un plan de acción efectivo y medible.</div>
            </a>
        </div>
        
        <div class="section-title" style="margin-top: 2rem;">📝 Gestión de Hábitos</div>
        <div class="section-divider"></div>
        
        <div class="dashboard-grid">
            <a href="${pageContext.request.contextPath}/controlador-habitos?action=registrar" class="dashboard-card card-habito">
                <div class="card-icon">✅</div>
                <div class="card-title">Registrar Hábito Diario</div>
                <div class="card-description">Marca tus hábitos cumplidos del día. La consistencia es la clave del éxito en la formación de hábitos.</div>
            </a>
            
            <a href="${pageContext.request.contextPath}/controlador-habitos?action=view" class="dashboard-card card-seguimiento">
                <div class="card-icon">📊</div>
                <div class="card-title">Vista de Seguimiento</div>
                <div class="card-description">Monitorea tu progreso con estadísticas detalladas. Visualiza rachas, porcentajes de éxito y más.</div>
            </a>
        </div>
        
        <div class="section-title" style="margin-top: 2rem;">🎮 Gamificación</div>
        <div class="section-divider"></div>
        
        <div class="dashboard-grid">
            <a href="${pageContext.request.contextPath}/mascotas-virtuales" class="dashboard-card card-mascotas">
                <div class="card-icon">🐣</div>
                <div class="card-title">Mascotas Virtuales</div>
                <div class="card-description">Cuida y evoluciona tu mascota virtual completando hábitos. Desbloquea nuevas etapas y alcanza la mascota legendaria.</div>
            </a>
            
            <a href="${pageContext.request.contextPath}/frases-motivacion" class="dashboard-card card-objetivo">
                <div class="card-icon">✨</div>
                <div class="card-title">Frases Motivacionales</div>
                <div class="card-description">Descubre frases inspiradoras que te ayudarán a mantenerte enfocado en tus metas. Cambia de frase cuando lo necesites.</div>
            </a>
        </div>
        
        <div class="section-title" style="margin-top: 2rem;">📖 Reflexión Personal</div>
        <div class="section-divider"></div>
        
        <div class="dashboard-grid">
            <a href="${pageContext.request.contextPath}/journal" class="dashboard-card card-planificar">
                <div class="card-icon">📝</div>
                <div class="card-title">Diario Personal</div>
                <div class="card-description">Reflexiona sobre tu día, expresa tus emociones y logros. Un espacio personal para el autoconocimiento y crecimiento.</div>
            </a>
            
            <a href="${pageContext.request.contextPath}/mood-tracker" class="dashboard-card card-objetivos">
                <div class="card-icon">😊</div>
                <div class="card-title">Estado de Ánimo</div>
                <div class="card-description">Registra cómo te sientes cada día. Hacer un seguimiento de tu estado emocional te ayuda a identificar patrones y mejorar tu bienestar.</div>
            </a>
        </div>
        
        <div style="text-align: center; margin-top: 3rem; padding: 2rem; background: white; border-radius: 16px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);">
            <p style="font-family: 'Poppins', sans-serif; font-size: 18px; color: #555; margin-bottom: 10px; font-weight: 500; font-style: italic;">"El éxito es la suma de pequeños esfuerzos repetidos día tras día"</p>
            <p style="font-family: 'Poppins', sans-serif; font-size: 12px; color: #888;">Sistema de Seguimiento de Hábitos y Objetivos © 2025</p>
        </div>
    </div>
    
    <!-- Modal de frase diaria -->
    <% if (mostrarFrase != null && mostrarFrase && fraseDiaria != null) { %>
    <div id="quoteModal" class="modal-overlay show">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-icon">✨</div>
                <div class="modal-title">¡Bienvenido de nuevo!</div>
                <div class="modal-subtitle">Tu frase inspiradora del día</div>
            </div>
            <div class="modal-quote">
                "<%= fraseDiaria %>"
            </div>
            <div class="modal-footer">
                <button class="btn-close-modal" onclick="cerrarModal()">¡Empecemos! 🚀</button>
            </div>
        </div>
    </div>
    <% } %>
    
    <script>
        function cerrarModal() {
            const modal = document.getElementById('quoteModal');
            if (modal) {
                modal.classList.remove('show');
                setTimeout(() => {
                    modal.style.display = 'none';
                }, 300);
            }
        }
        
        // Cerrar modal al hacer clic fuera de él
        document.addEventListener('click', function(event) {
            const modal = document.getElementById('quoteModal');
            if (modal && event.target === modal) {
                cerrarModal();
            }
        });
        
        // Cerrar modal con la tecla Escape
        document.addEventListener('keydown', function(event) {
            if (event.key === 'Escape') {
                cerrarModal();
            }
        });
    </script>
</body>
</html>
