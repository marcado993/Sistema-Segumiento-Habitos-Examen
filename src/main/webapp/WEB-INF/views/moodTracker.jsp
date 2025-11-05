<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sistema_seguimiento.model.MoodEntry" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mood Tracker - Sistema de Seguimiento</title>
    <meta charset="UTF-8">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        /* 🎨 EFECTO DE CARGA - FADE IN */
        body {
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
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
        
        .container {
            max-width: 700px;
            margin: 0 auto;
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 40px;
        }
        
        .header {
            text-align: center;
            margin-bottom: 40px;
        }
        
        .header h1 {
            font-family: 'Poppins', sans-serif;
            font-size: 2.5em;
            color: #667eea;
            margin-bottom: 10px;
            font-weight: 700;
        }
        
        .header p {
            color: #888;
            font-size: 1.1em;
        }
        
        /* 📅 FECHA */
        .date-display {
            text-align: center;
            margin-bottom: 30px;
            padding: 15px;
            background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
            border-radius: 12px;
        }
        
        .date-display p {
            font-size: 1.2em;
            color: #555;
            font-weight: 500;
        }
        
        /* 😊 EMOJI SELECTOR */
        .mood-selector {
            margin-bottom: 30px;
        }
        
        .mood-selector h2 {
            font-size: 1.3em;
            color: #333;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .emoji-container {
            display: flex;
            justify-content: space-around;
            gap: 10px;
            flex-wrap: wrap;
        }
        
        .emoji-option {
            flex: 1;
            min-width: 100px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .emoji-option input[type="radio"] {
            display: none;
        }
        
        .emoji-label {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px 15px;
            border: 3px solid transparent;
            border-radius: 15px;
            background: #f9f9f9;
            transition: all 0.3s ease;
        }
        
        .emoji-label:hover {
            transform: scale(1.1);
            background: #f0f0f0;
        }
        
        .emoji-option input[type="radio"]:checked + .emoji-label {
            border-color: #667eea;
            background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
            transform: scale(1.15);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }
        
        .emoji-icon {
            font-size: 3em;
            margin-bottom: 10px;
        }
        
        .emoji-text {
            font-size: 0.9em;
            color: #666;
            font-weight: 500;
        }
        
        /* 📝 NOTAS */
        .notes-section {
            margin-bottom: 30px;
        }
        
        .notes-section label {
            display: block;
            font-size: 1.1em;
            color: #333;
            margin-bottom: 10px;
            font-weight: 500;
        }
        
        .notes-section textarea {
            width: 100%;
            min-height: 120px;
            padding: 15px;
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            font-family: 'Inter', sans-serif;
            font-size: 1em;
            resize: vertical;
            transition: border-color 0.3s ease;
        }
        
        .notes-section textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        /* 🎯 BOTONES */
        .button-group {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }
        
        .btn {
            flex: 1;
            padding: 15px 30px;
            font-size: 1.1em;
            font-weight: 600;
            border: none;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Poppins', sans-serif;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #e0e0e0;
            color: #555;
        }
        
        .btn-secondary:hover {
            background: #d0d0d0;
            transform: translateY(-2px);
        }
        
        /* 🔔 MENSAJES */
        .message {
            padding: 15px 20px;
            border-radius: 12px;
            margin-bottom: 20px;
            font-size: 1em;
            animation: slideDown 0.5s ease;
        }
        
        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .warning {
            background: #fff3cd;
            color: #856404;
            border: 1px solid #ffeeba;
        }
        
        .error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .reminder {
            background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
            color: #667eea;
            border: 2px solid #667eea;
            font-weight: 500;
        }
        
        /* 📊 ESTADO ACTUAL */
        .current-mood {
            background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
            padding: 20px;
            border-radius: 15px;
            margin-bottom: 30px;
            border: 2px solid #667eea;
        }
        
        .current-mood h3 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 1.2em;
        }
        
        .current-mood-display {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .current-mood-emoji {
            font-size: 3em;
        }
        
        .current-mood-text {
            flex: 1;
        }
        
        .current-mood-text p {
            margin: 5px 0;
            color: #555;
        }
        
        .current-mood-text .mood-name {
            font-size: 1.3em;
            font-weight: 600;
            color: #333;
        }
        
        /* 📱 RESPONSIVE */
        @media (max-width: 768px) {
            .container {
                padding: 25px;
            }
            
            .header h1 {
                font-size: 2em;
            }
            
            .emoji-container {
                flex-direction: column;
            }
            
            .emoji-option {
                min-width: 100%;
            }
            
            .button-group {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- 🎨 HEADER -->
        <div class="header">
            <h1>😊 Mood Tracker</h1>
            <p>¿Cómo te sientes hoy?</p>
        </div>
        
        <!-- 📅 FECHA ACTUAL -->
        <div class="date-display">
            <p>📅 <%= java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "ES"))) %></p>
        </div>
        
        <%
            // Obtener datos del request
            MoodEntry existingMood = (MoodEntry) request.getAttribute("existingMood");
            Boolean canEdit = (Boolean) request.getAttribute("canEdit");
            Boolean showReminder = (Boolean) request.getAttribute("showReminder");
            
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("errorMessage");
            String warningMessage = (String) request.getAttribute("warningMessage");
            
            // Limpiar mensajes de sesión después de mostrarlos
            if (successMessage != null) {
                session.removeAttribute("successMessage");
            }
        %>
        
        <!-- 🔔 MENSAJES -->
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
            <div class="message success">
                <%= successMessage %>
            </div>
        <% } %>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="message error">
                <%= errorMessage %>
            </div>
        <% } %>
        
        <% if (warningMessage != null && !warningMessage.isEmpty()) { %>
            <div class="message warning">
                <%= warningMessage %>
            </div>
        <% } %>
        
        <% if (showReminder != null && showReminder) { %>
            <div class="message reminder">
                📢 ¡Recuerda registrar tu estado de ánimo de hoy! Es importante hacer un seguimiento diario.
            </div>
        <% } %>
        
        <!-- 📊 MOSTRAR MOOD ACTUAL SI EXISTE -->
        <% if (existingMood != null) { %>
            <div class="current-mood">
                <h3>Tu estado de ánimo de hoy:</h3>
                <div class="current-mood-display">
                    <div class="current-mood-emoji">
                        <%= getMoodEmoji(existingMood.getMood()) %>
                    </div>
                    <div class="current-mood-text">
                        <p class="mood-name"><%= getMoodName(existingMood.getMood()) %></p>
                        <% if (existingMood.getNotes() != null && !existingMood.getNotes().trim().isEmpty()) { %>
                            <p><strong>Notas:</strong> <%= existingMood.getNotes() %></p>
                        <% } %>
                    </div>
                </div>
            </div>
        <% } %>
        
        <!-- 😊 FORMULARIO DE SELECCIÓN -->
        <form method="POST" action="mood-tracker" id="moodForm">
            <input type="hidden" name="action" value="save">
            
            <!-- EMOJI SELECTOR -->
            <div class="mood-selector">
                <h2>Selecciona tu estado de ánimo:</h2>
                <div class="emoji-container">
                    <!-- Muy Triste -->
                    <div class="emoji-option">
                        <input type="radio" name="mood" id="very-sad" value="very-sad" 
                               <%= (existingMood != null && "very-sad".equals(existingMood.getMood())) ? "checked" : "" %>>
                        <label for="very-sad" class="emoji-label">
                            <span class="emoji-icon">😢</span>
                            <span class="emoji-text">Muy Triste</span>
                        </label>
                    </div>
                    
                    <!-- Triste -->
                    <div class="emoji-option">
                        <input type="radio" name="mood" id="sad" value="sad"
                               <%= (existingMood != null && "sad".equals(existingMood.getMood())) ? "checked" : "" %>>
                        <label for="sad" class="emoji-label">
                            <span class="emoji-icon">😔</span>
                            <span class="emoji-text">Triste</span>
                        </label>
                    </div>
                    
                    <!-- Neutral -->
                    <div class="emoji-option">
                        <input type="radio" name="mood" id="neutral" value="neutral"
                               <%= (existingMood != null && "neutral".equals(existingMood.getMood())) ? "checked" : "" %>>
                        <label for="neutral" class="emoji-label">
                            <span class="emoji-icon">😐</span>
                            <span class="emoji-text">Indiferente</span>
                        </label>
                    </div>
                    
                    <!-- Feliz -->
                    <div class="emoji-option">
                        <input type="radio" name="mood" id="happy" value="happy"
                               <%= (existingMood != null && "happy".equals(existingMood.getMood())) ? "checked" : "" %>>
                        <label for="happy" class="emoji-label">
                            <span class="emoji-icon">😊</span>
                            <span class="emoji-text">Feliz</span>
                        </label>
                    </div>
                    
                    <!-- Muy Feliz -->
                    <div class="emoji-option">
                        <input type="radio" name="mood" id="very-happy" value="very-happy"
                               <%= (existingMood != null && "very-happy".equals(existingMood.getMood())) ? "checked" : "" %>>
                        <label for="very-happy" class="emoji-label">
                            <span class="emoji-icon">😄</span>
                            <span class="emoji-text">Muy Feliz</span>
                        </label>
                    </div>
                </div>
            </div>
            
            <!-- NOTAS OPCIONALES -->
            <div class="notes-section">
                <label for="notes">Notas adicionales (opcional):</label>
                <textarea name="notes" id="notes" placeholder="¿Qué hizo que te sintieras así hoy?"><%= (existingMood != null && existingMood.getNotes() != null) ? existingMood.getNotes() : "" %></textarea>
            </div>
            
            <!-- BOTONES -->
            <div class="button-group">
                <button type="submit" class="btn btn-primary">
                    <%= (existingMood != null) ? "💾 Actualizar Estado" : "💾 Guardar Estado" %>
                </button>
                <button type="button" class="btn btn-secondary" onclick="window.location.href='<%= request.getContextPath() %>/index.jsp'">
                    🏠 Volver al Dashboard
                </button>
            </div>
        </form>
    </div>
    
    <%!
        // Método auxiliar para obtener el emoji del mood
        private String getMoodEmoji(String mood) {
            switch (mood) {
                case "very-sad": return "😢";
                case "sad": return "😔";
                case "neutral": return "😐";
                case "happy": return "😊";
                case "very-happy": return "😄";
                default: return "😐";
            }
        }
        
        // Método auxiliar para obtener el nombre del mood
        private String getMoodName(String mood) {
            switch (mood) {
                case "very-sad": return "Muy Triste";
                case "sad": return "Triste";
                case "neutral": return "Indiferente";
                case "happy": return "Feliz";
                case "very-happy": return "Muy Feliz";
                default: return "No especificado";
            }
        }
    %>
    
    <script>
        // Validación del formulario
        document.getElementById('moodForm').addEventListener('submit', function(e) {
            const moodSelected = document.querySelector('input[name="mood"]:checked');
            if (!moodSelected) {
                e.preventDefault();
                alert('⚠️ Por favor, selecciona un estado de ánimo antes de guardar.');
            }
        });
    </script>
</body>
</html>
