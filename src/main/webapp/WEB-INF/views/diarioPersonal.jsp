<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sistema_seguimiento.model.JournalEntry" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Diario Personal - Sistema de Seguimiento</title>
    <meta charset="UTF-8">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&family=Inter:wght@400;500&family=Dancing+Script:wght@500&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        /* 🎨 EFECTO DE CARGA - FADE IN */
        body {
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: #FFF8E7;
            min-height: 100vh;
            padding: 20px;
            color: #555555;
            animation: fadeIn 0.6s ease-in;
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        /* 💫 LOADING SPINNER */
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 248, 231, 0.95);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.3s ease;
        }
        
        .loading-overlay.active {
            opacity: 1;
            pointer-events: all;
        }
        
        .spinner {
            width: 50px;
            height: 50px;
            border: 4px solid #FFE5B4;
            border-top: 4px solid #FFB84D;
            border-radius: 50%;
            animation: spin 0.8s linear infinite;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .loading-text {
            position: absolute;
            margin-top: 90px;
            color: #FFB84D;
            font-weight: 600;
            font-size: 14px;
        }
        
        .container {
            max-width: 900px;
            margin: 0 auto;
        }
        
        /* Breadcrumbs */
        .breadcrumb {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 2rem;
            padding: 1rem;
            background: white;
            border-radius: 12px;
            font-size: 14px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        }
        
        .breadcrumb-item {
            display: flex;
            align-items: center;
            gap: 5px;
            color: #888;
        }
        
        .breadcrumb-item.active {
            color: #FFB84D;
            font-weight: 600;
        }
        
        .breadcrumb-separator {
            color: #DDD;
        }
        
        /* Header del módulo */
        .module-header {
            background: white;
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2rem;
            text-align: center;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }
        
        .module-header h1 {
            font-family: 'Poppins', sans-serif;
            color: #555555;
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .module-header p {
            color: #888;
            font-size: 16px;
            line-height: 1.6;
        }
        
        /* Formulario de entrada */
        .entry-form {
            background: white;
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }
        
        .entry-form h2 {
            font-family: 'Poppins', sans-serif;
            color: #555555;
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .entry-form h2::before {
            content: "✍️";
            font-size: 24px;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #555555;
            font-weight: 600;
            font-size: 14px;
        }
        
        textarea {
            width: 100%;
            padding: 15px;
            border: 2px solid #FFE5B4;
            border-radius: 12px;
            font-size: 15px;
            font-family: 'Inter', sans-serif;
            transition: all 0.3s ease;
            background: #FFFBF0;
            resize: vertical;
            min-height: 150px;
            line-height: 1.6;
        }
        
        textarea:focus {
            outline: none;
            border-color: #FFB84D;
            box-shadow: 0 0 0 3px rgba(255, 184, 77, 0.2);
            background: white;
        }
        
        textarea::placeholder {
            color: #BBB;
            font-style: italic;
        }
        
        .char-counter {
            text-align: right;
            font-size: 12px;
            color: #888;
            margin-top: 5px;
        }
        
        .btn-group {
            display: flex;
            gap: 15px;
            margin-top: 1.5rem;
        }
        
        button {
            flex: 1;
            padding: 15px;
            border: none;
            border-radius: 16px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Inter', sans-serif;
        }
        
        .btn-primary {
            background: #FFD6A5;
            color: #555555;
        }
        
        .btn-primary:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255, 214, 165, 0.5);
            background: #FFC78A;
        }
        
        .btn-primary:disabled {
            background: #F0F0F0;
            color: #BBB;
            cursor: not-allowed;
        }
        
        .btn-secondary {
            background: #F3E8FF;
            color: #555555;
        }
        
        .btn-secondary:hover {
            background: #E8DAFF;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(243, 232, 255, 0.4);
        }
        
        /* Historial de entradas */
        .history-section {
            background: white;
            border-radius: 16px;
            padding: 2rem;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }
        
        .history-section h2 {
            font-family: 'Poppins', sans-serif;
            color: #555555;
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .history-section h2::before {
            content: "📖";
            font-size: 24px;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem 2rem;
            color: #888;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 1rem;
            opacity: 0.3;
        }
        
        .empty-state p {
            font-size: 16px;
            margin-bottom: 0.5rem;
        }
        
        .empty-state small {
            color: #BBB;
            font-size: 14px;
        }
        
        .journal-entries {
            display: flex;
            flex-direction: column;
            gap: 1.5rem;
        }
        
        .journal-entry {
            background: #FFFBF0;
            border-left: 4px solid #FFD6A5;
            border-radius: 12px;
            padding: 1.5rem;
            transition: all 0.3s ease;
            animation: slideIn 0.4s ease-out;
        }
        
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateX(-20px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }
        
        .journal-entry:hover {
            transform: translateX(5px);
            box-shadow: 0 4px 12px rgba(255, 214, 165, 0.3);
        }
        
        .entry-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
            padding-bottom: 0.75rem;
            border-bottom: 2px solid #FFE5B4;
        }
        
        .entry-date {
            font-weight: 600;
            color: #FFB84D;
            font-size: 15px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .entry-date::before {
            content: "📅";
            font-size: 18px;
        }
        
        .entry-time {
            font-size: 13px;
            color: #888;
        }
        
        .entry-content {
            color: #555555;
            font-size: 15px;
            line-height: 1.8;
            white-space: pre-wrap;
            word-wrap: break-word;
        }
        
        /* Mensajes de feedback */
        .message {
            padding: 1rem 1.5rem;
            border-radius: 12px;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 12px;
            animation: slideDown 0.4s ease-out;
        }
        
        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .message-success {
            background: #D4EDDA;
            border-left: 4px solid #28A745;
            color: #155724;
        }
        
        .message-success::before {
            content: "✅";
            font-size: 20px;
        }
        
        .message-error {
            background: #F8D7DA;
            border-left: 4px solid #DC3545;
            color: #721C24;
        }
        
        .message-error::before {
            content: "⚠️";
            font-size: 20px;
        }
        
        .message-warning {
            background: #FFF3CD;
            border-left: 4px solid #FFC107;
            color: #856404;
        }
        
        .message-warning::before {
            content: "⚡";
            font-size: 20px;
        }
        
        /* Responsive */
        @media (max-width: 768px) {
            body {
                padding: 15px;
            }
            
            .container {
                max-width: 100%;
            }
            
            .module-header h1 {
                font-size: 24px;
            }
            
            .btn-group {
                flex-direction: column;
            }
            
            button {
                width: 100%;
            }
            
            .entry-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 8px;
            }
        }
    </style>
</head>
<body>
    <!-- Loading Overlay -->
    <div class="loading-overlay" id="loadingOverlay">
        <div>
            <div class="spinner"></div>
            <div class="loading-text">Guardando tu reflexión...</div>
        </div>
    </div>

    <div class="container">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <span class="breadcrumb-item">🏠 Inicio</span>
            <span class="breadcrumb-separator">›</span>
            <span class="breadcrumb-item active">📝 Diario Personal</span>
        </div>

        <!-- Header del módulo -->
        <div class="module-header">
            <h1>Mi Diario Personal</h1>
            <p>Un espacio para reflexionar sobre tus emociones, logros y experiencias diarias</p>
        </div>

        <!-- Mensajes de feedback -->
        <% 
            String successMessage = (String) request.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("errorMessage");
            String warningMessage = (String) request.getAttribute("warningMessage");
            
            // También revisar mensajes en la sesión (para POST-REDIRECT-GET)
            HttpSession userSession = request.getSession(false);
            if (userSession != null) {
                if (successMessage == null) {
                    successMessage = (String) userSession.getAttribute("successMessage");
                    userSession.removeAttribute("successMessage");
                }
                if (errorMessage == null) {
                    errorMessage = (String) userSession.getAttribute("errorMessage");
                    userSession.removeAttribute("errorMessage");
                }
                if (warningMessage == null) {
                    warningMessage = (String) userSession.getAttribute("warningMessage");
                    userSession.removeAttribute("warningMessage");
                }
            }
            
            if (successMessage != null) { 
        %>
            <div class="message message-success">
                <span><%= successMessage %></span>
            </div>
        <% } %>
        
        <% if (errorMessage != null) { %>
            <div class="message message-error">
                <span><%= errorMessage %></span>
            </div>
        <% } %>
        
        <% if (warningMessage != null) { %>
            <div class="message message-warning">
                <span><%= warningMessage %></span>
            </div>
        <% } %>

        <!-- Formulario de nueva entrada -->
        <div class="entry-form">
            <h2>Escribe tu reflexión del día</h2>
            
            <form action="journal" method="post" id="journalForm">
                <input type="hidden" name="action" value="save">
                
                <div class="form-group">
                    <label for="content">
                        ¿Cómo fue tu día? ¿Qué lograste? ¿Cómo te sientes?
                    </label>
                    <textarea 
                        id="content" 
                        name="content" 
                        placeholder="Escribe aquí tus pensamientos, emociones y reflexiones del día. Recuerda que este es tu espacio personal para expresarte libremente..."
                        maxlength="2000"
                        required
                    ></textarea>
                    <div class="char-counter">
                        <span id="charCount">0</span> / 2000 caracteres
                    </div>
                </div>

                <div class="btn-group">
                    <button type="submit" class="btn-primary" id="saveBtn">
                        💾 Guardar Entrada
                    </button>
                    <button type="button" class="btn-secondary" onclick="window.location.href='<%= request.getContextPath() %>/index.jsp'">
                        🏠 Volver al Dashboard
                    </button>
                </div>
            </form>
        </div>

        <!-- Historial de entradas -->
        <div class="history-section">
            <h2>Historial de Entradas</h2>
            
            <%
                @SuppressWarnings("unchecked")
                List<JournalEntry> entries = (List<JournalEntry>) request.getAttribute("journalEntries");
                
                if (entries == null || entries.isEmpty()) {
            %>
                <div class="empty-state">
                    <div class="empty-state-icon">📔</div>
                    <p><strong>Aún no tienes entradas en tu diario</strong></p>
                    <small>Comienza a escribir tus reflexiones diarias y aparecerán aquí</small>
                </div>
            <%
                } else {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            %>
                <div class="journal-entries">
                    <% for (JournalEntry entry : entries) { %>
                        <div class="journal-entry">
                            <div class="entry-header">
                                <span class="entry-date">
                                    <%= entry.getCreatedAt().format(dateFormatter) %>
                                </span>
                                <span class="entry-time">
                                    🕐 <%= entry.getCreatedAt().format(timeFormatter) %>
                                </span>
                            </div>
                            <div class="entry-content">
                                <%= entry.getContent() %>
                            </div>
                        </div>
                    <% } %>
                </div>
            <%
                }
            %>
        </div>
    </div>

    <script>
        // Contador de caracteres
        const contentTextarea = document.getElementById('content');
        const charCount = document.getElementById('charCount');
        const saveBtn = document.getElementById('saveBtn');
        
        contentTextarea.addEventListener('input', function() {
            const count = this.value.length;
            charCount.textContent = count;
            
            // Validación en tiempo real (Escenario 2: No permitir guardar si está vacío)
            if (this.value.trim().length === 0) {
                saveBtn.disabled = true;
            } else {
                saveBtn.disabled = false;
            }
            
            // Cambiar color si se acerca al límite
            if (count > 1800) {
                charCount.style.color = '#DC3545';
                charCount.style.fontWeight = '600';
            } else {
                charCount.style.color = '#888';
                charCount.style.fontWeight = '400';
            }
        });
        
        // Validación antes de enviar (Escenario 2: No permitir contenido vacío)
        document.getElementById('journalForm').addEventListener('submit', function(e) {
            const content = contentTextarea.value.trim();
            
            if (content.length === 0) {
                e.preventDefault();
                alert('⚠️ Por favor, escribe algo antes de guardar tu entrada.');
                contentTextarea.focus();
                return false;
            }
            
            // Mostrar overlay de carga (Escenario 1: Feedback al guardar)
            document.getElementById('loadingOverlay').classList.add('active');
        });
        
        // Deshabilitar botón inicialmente si el textarea está vacío
        window.addEventListener('DOMContentLoaded', function() {
            if (contentTextarea.value.trim().length === 0) {
                saveBtn.disabled = true;
            }
            
            // Auto-focus en el textarea
            contentTextarea.focus();
        });
        
        // Guardar borrador en localStorage (opcional - prevenir pérdida de datos)
        contentTextarea.addEventListener('input', function() {
            localStorage.setItem('journalDraft', this.value);
        });
        
        // Recuperar borrador al cargar
        window.addEventListener('DOMContentLoaded', function() {
            const draft = localStorage.getItem('journalDraft');
            if (draft && contentTextarea.value.trim().length === 0) {
                if (confirm('Se encontró un borrador guardado. ¿Deseas recuperarlo?')) {
                    contentTextarea.value = draft;
                    charCount.textContent = draft.length;
                    saveBtn.disabled = false;
                }
            }
        });
        
        // Limpiar borrador al guardar exitosamente
        <% if (successMessage != null) { %>
            localStorage.removeItem('journalDraft');
        <% } %>
    </script>
</body>
</html>
