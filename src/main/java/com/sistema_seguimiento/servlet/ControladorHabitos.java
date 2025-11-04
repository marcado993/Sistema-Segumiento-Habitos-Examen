package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.dao.HabitoDAO;
import com.sistema_seguimiento.model.Habito;
import com.sistema_seguimiento.model.RegistroHabito;
import com.sistema_seguimiento.model.Usuario;
import com.sistema_seguimiento.services.HabitoServicio;
import com.sistema_seguimiento.services.PointsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controlador de Habitos - Maneja operaciones CRUD y registro de cumplimiento
 * 
 * MODIFICADO: Reemplazados System.out.println con emojis por java.util.logging.Logger
 * Se implemento logging estructurado con prefijos:
 * - [SEGUIMIENTO] - Vista de seguimiento
 * - [REGISTRO] - Procesamiento de registros
 * - [CUMPLIMIENTO] - Registro de cumplimiento con detalles
 * - [PUNTOS] - Asignacion de puntos
 * - [CREAR] - Creacion de habitos
 * - [ACTUALIZAR] - Actualizacion de habitos
 * - [FORMULARIO] - Carga de formularios
 * 
 * Mejora: Codigo mas profesional, mantenible y siguiendo best practices de Java
 * 
 * Responsabilidades:
 * - Gestion de habitos del usuario (crear, listar, editar, eliminar)
 * - Registro de cumplimiento diario
 * - Calculo y asignacion de puntos
 * - Vista de seguimiento y estadisticas
 * 
 * @author Sistema Seguimiento Habitos
 * @version 2.0 - Logging Profesional
 */
@WebServlet("/controlador-habitos")
public class ControladorHabitos extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ControladorHabitos.class.getName());
    private final HabitoServicio habitoServicio = new HabitoServicio();
    private PointsService pointsService = new PointsService();

    @Override
    public void init() throws ServletException {
        super.init();
        habitoServicio.setHabitoDAO(new HabitoDAO());
    }
    
    /**
     * Setters para inyección de dependencias (necesario para tests con mocks)
     */
    public void setHabitoDAO(HabitoDAO habitoDAO) {
        habitoServicio.setHabitoDAO(habitoDAO);
    }
    
    public void setPointsService(PointsService pointsService) {
        this.pointsService = pointsService;
    }
    
    /**
     * Obtener el usuarioId de la sesión
     */
    private Integer getUsuarioIdFromSession(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return (usuario != null) ? usuario.getId() : null;
    }
    
    /**
     * Registrar cumplimiento de un hábito
     * Basado en el diagrama de secuencia
     */
    public Habito registrarCumplimiento(Integer habitoId, LocalDate fecha, String observacion) {
        return habitoServicio.registrarCumplimiento(habitoId, fecha, observacion);
    }
    
    /**
     * Obtener registros de un hábito en un rango de fechas
     */
    public List<RegistroHabito> obtenerRegistros(Integer habitoId, LocalDate fechaInicio, LocalDate fechaFin) {
        return habitoServicio.obtenerRegistros(habitoId, fechaInicio, fechaFin);
    }
    
    /**
     * Obtener todos los registros de hoy del usuario
     */
    public List<RegistroHabito> obtenerRegistrosDeHoy(Integer usuarioId) {
        return habitoServicio.obtenerRegistrosDeHoy(usuarioId);
    }
    
    /**
     * Buscar hábito por ID
     */
    public Habito buscarHabito(Integer habitoId) {
        return habitoServicio.buscarHabito(habitoId);
    }
    
    /**
     * Crear ficha de racha actual
     */
    public int crearFichaRacha(Habito habito, String estado, boolean cumplido) {
        return habitoServicio.crearFichaRacha(habito, estado, cumplido);
    }
    
    /**
     * Crear nuevo registro de hábito
     */
    public RegistroHabito crearNuevoRegistro(Habito habito, LocalDate fecha, String observacion) {
        return habitoServicio.crearNuevoRegistro(habito, fecha, observacion);
    }
    
    /**
     * Obtener lista de hábitos del usuario
     */
    public List<Habito> listarHabitosUsuario(Integer usuarioId) {
        return habitoServicio.listarHabitosUsuario(usuarioId);
    }
    
    /**
     * Guardar o actualizar hábito
     */
    public Habito guardarHabito(Habito habito) {
        return habitoServicio.guardarHabito(habito);
    }
    
    /**
     * Eliminar hábito (soft delete)
     */
    public boolean eliminarHabito(Integer habitoId) {
        return habitoServicio.eliminarHabito(habitoId);
    }
    
    /**
     * Obtener estadísticas del usuario
     */
    public Long obtenerHabitosCompletadosHoy(Integer usuarioId) {
        return habitoServicio.obtenerHabitosCompletadosHoy(usuarioId);
    }
    
    /**
     * Obtener porcentaje de completado de la semana
     */
    public Double obtenerPorcentajeSemana(Integer usuarioId) {
        return habitoServicio.obtenerPorcentajeSemana(usuarioId);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Obtener usuarioId de la sesión
        HttpSession session = request.getSession(false);
        Integer usuarioId = getUsuarioIdFromSession(session);
        
        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            procesarAccion(request, response, action, usuarioId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("controlador-habitos?action=list&error=exception");
        }
    }

    /**
     * Procesa la acción solicitada por el usuario para las peticiones GET.
     * Actúa como un despachador que, basándose en el parámetro ´action´,
     * delega la ejecución a la lógica correpondiente para listar, ver o eliminar hábitos.
     * @param request El objeto HttpServletRequest que contiene la solicitud del cliente
     * @param response El objeto HttpServletResponse para envíar la respuesta.
     * @param action La acción específica a realizar (ej. "list", "view", "delete").
     * @param usuarioId El ID del usuario para filtrar los datos correspondientes.
     * @throws ServletException si ocurre un error específico del servlet.
     * @throws IOException si ocurre un error de entrada/salida.
     */
    private void procesarAccion(HttpServletRequest request, HttpServletResponse response, String action, Integer usuarioId) throws ServletException, IOException {
        switch (action) {
            case "list":
                listarHabitos(request, response, usuarioId);
                break;
            case "registrar":
                mostrarFormularioRegistro(request, response, usuarioId);
                break;
            case "view":
                verSeguimiento(request, response, usuarioId);
                break;
            case "delete":
            case "eliminar":
                eliminarHabito(request, response, usuarioId);
                break;
            case "editar":
                editarHabito(request, response, usuarioId);
                break;
            default:// Por defecto, listar hábitos
                response.sendRedirect("controlador-habitos?action=list&usuarioId=" + usuarioId);
                break;
        }
    }
    
    /**
     * Cargar hábito para editar
     */
    private void editarHabito(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws ServletException, IOException {
        String habitoIdStr = request.getParameter("habitoId");
        if (habitoIdStr != null) {
            Integer habitoId = Integer.parseInt(habitoIdStr);
            Habito habito = habitoServicio.buscarHabito(habitoId);
            
            if (habito != null && habito.getUsuarioId().equals(usuarioId)) {
                request.setAttribute("habito", habito);
                request.setAttribute("modoEdicion", true);
                request.getRequestDispatcher("/WEB-INF/views/registroHabito.jsp").forward(request, response);
            } else {
                response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId + "&error=notfound");
            }
        } else {
            response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId);
        }
    }

    /**
     * Elimina un hábito específico del usuario.
     * Gestionar la petición para eliminar un hábito
     * Obtieen el ID del hábito desde los parámetros de la solicitud,
     * invoca a la lógica de negocio para su eliminación y redirige al usuario
     * a la lista de hábitos con un mensaje de éxito o error.
     * @param request El objeto HttpServletRequest que contiene la solicitud.
     * @param response El objetp HttpServletResponse para envíar la respuesta de redirección.
     * @param usuarioId El identificador del usuario propietario del hábito.
     * @throws IOException si ocurre un error durante la redirección.
     */
    private void eliminarHabito(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws IOException {
        String habitoIdStr = request.getParameter("habitoId");
        if (habitoIdStr != null) {
            Integer habitoId = Integer.parseInt(habitoIdStr);
            boolean success = habitoServicio.eliminarHabito(habitoId);

            if (success) {
                response.sendRedirect("controlador-habitos?action=list&usuarioId=" + usuarioId + "&deleted=true");
            } else {
                response.sendRedirect("controlador-habitos?action=list&usuarioId=" + usuarioId + "&error=delete");
            }
        }
    }

    /**
     * Muestra la vista de seguimiento de hábitos.
     * Si se proporciona un ID de hábito, muestra la vista detallada de ese hábito específico
     * incluyendo su historial de registros. Si no, muestra la vista general de todos los
     * hábitos del usuario.
     * @param request El objeto HttpServletRequests, donde se almacenarán los datos para la vista.
     * @param response El objeto HttpServletResponse para hacer el forward a la vista JSP.
     * @param usuarioId El identificador del usuario cuyos hábitos se van a mostrar.
     * @throws ServletException Por si ocurre un error durante el forward.
     * @throws IOException Por si ocurre un error de entrada/salida.
     */
    private void verSeguimiento(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws ServletException, IOException {
        String habitoIdStr = request.getParameter("habitoId");

        if (habitoIdStr != null && !habitoIdStr.isEmpty()) {
            // Ver detalle de un hábito específico
            Integer habitoId = Integer.parseInt(habitoIdStr);
            Habito habito = habitoServicio.buscarHabito(habitoId);

            if (habito != null) {
                LocalDate hoy = LocalDate.now();
                LocalDate hace30Dias = hoy.minusDays(30);
                List<RegistroHabito> registros = habitoServicio.obtenerRegistros(habitoId, hace30Dias, hoy);

                request.setAttribute("habito", habito);
                request.setAttribute("registros", registros);
                request.setAttribute("racha", habito.calcularRachaActual());
                request.getRequestDispatcher("/WEB-INF/views/vistaSeguimiento.jsp").forward(request, response);

            } else {
                response.sendRedirect("controlador-habitos?action=list&error=notfound");
            }
        } else {
            // Ver todos los habitos del usuario (vista de seguimiento general)
            logger.info(String.format("[SEGUIMIENTO] Cargando vista para usuario ID: %d", usuarioId));
            List<Habito> habitos = habitoServicio.listarHabitosUsuario(usuarioId);
            logger.info(String.format("[SEGUIMIENTO] Habitos encontrados: %d", habitos.size()));
            request.setAttribute("habitos", habitos);
            request.getRequestDispatcher("/WEB-INF/views/vistaSeguimiento.jsp").forward(request, response);
        }
    }

    /**
     * Procesa el formulario de registro de hábito (POST)
     */
    private void procesarRegistroHabito(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws IOException {
        String habitoIdStr = request.getParameter("habitoId");
        String fechaStr = request.getParameter("fecha");
        String estadoStr = request.getParameter("estado");
        String notas = request.getParameter("notas");
        
        logger.info(String.format("[REGISTRO] Procesando habito ID=%s, fecha=%s, estado=%s", 
            habitoIdStr, fechaStr, estadoStr));
        
        if (habitoIdStr != null && !habitoIdStr.isEmpty()) {
            try {
                Integer habitoId = Integer.parseInt(habitoIdStr);
                LocalDate fecha = fechaStr != null ? LocalDate.parse(fechaStr) : LocalDate.now();
                
                Habito habito = habitoServicio.registrarCumplimiento(habitoId, fecha, notas);
                
                if (habito != null) {
                    logger.info(String.format("[REGISTRO] Exitoso - Habito ID: %d", habitoId));
                    
                    // Agregar puntos al usuario segun el estado del habito
                    pointsService.addPointsToUser(usuarioId, estadoStr);
                    
                    response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId + "&success=true");
                } else {
                    logger.warning(String.format("[REGISTRO] Error al registrar habito ID: %d", habitoId));
                    response.sendRedirect("controlador-habitos?action=registrar&usuarioId=" + usuarioId + "&error=save");
                }
            } catch (Exception e) {
                logger.severe(String.format("[REGISTRO] Excepcion: %s", e.getMessage()));
                response.sendRedirect("controlador-habitos?action=registrar&usuarioId=" + usuarioId + "&error=exception");
            }
        } else {
            logger.warning("[REGISTRO] HabitoId no proporcionado");
            response.sendRedirect("controlador-habitos?action=registrar&usuarioId=" + usuarioId + "&error=missing");
        }
    }    /**
     * Obtiene y muestra la lista de todos los hábitos activos de un usuario.
     * Prepara los datos necesarios y los reenvía a la vista JSP encargada de
     * renderizar la lista de hábitos para sus registros.
     * @param request El objeto HttpServletRequest, donde se guardará la lista de hábitos.
     * @param response El objeto HttpServletResponse para hacer el forward a la vista.
     * @param usuarioId El Identificador del usuario cuyos hábitos se listarán.
     * @throws ServletException si ocurre un error durante el forward.
     * @throws IOException si ocurre un error de entrada/salida.
     */
    private void listarHabitos(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws ServletException, IOException {
        List<Habito> habitos = habitoServicio.listarHabitosUsuario(usuarioId);
        request.setAttribute("habitos", habitos);
        request.getRequestDispatcher("/WEB-INF/views/registroHabito.jsp").forward(request, response);
    }


    private Integer obtenerMetaDiariaOPredeterminada(Habito habito) {
        return habito.getMetaDiaria() != null ? habito.getMetaDiaria() : 1;
    }

/**
     * Procesa la solicitud para registrar el cumplimiento de un hábito con estado específico.
     * Obtiene los detalles del registro desde los parámetros de la solicitud, crea el registro
     * con el estado correspondiente (CUMPLIDO, NO_CUMPLIDO, PARCIAL) y redirige al usuario a la
     * lista de hábitos con un mensaje de éxito o error.
     * @param request El objeto HttpServletRequest que contiene los detalles del registro.
     * @param response El objeto HttpServletResponse para la redirección.
     * @param usuarioId El ID del usuario que realiza el registro.
     * @throws IOException si ocurre un error durante la redirección.
     */
    private void procesarRegistroCumplimiento(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws IOException {
        String habitoIdStr = request.getParameter("habitoId");
        String notas = request.getParameter("notas");  // ✅ CORREGIDO: Usar "notas" en vez de "observacion"
        String vecesRealizadoStr = request.getParameter("vecesRealizado");  // ✅ NUEVO
        String estadoAnimo = request.getParameter("estadoAnimo");  // ✅ NUEVO: Estado de ánimo
        String fechaStr = request.getParameter("fecha");
        String estado = request.getParameter("estado"); // CUMPLIDO, NO_CUMPLIDO, PARCIAL
        
        LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty()) 
            ? LocalDate.parse(fechaStr) 
            : LocalDate.now();
        
        if (habitoIdStr != null) {
            Integer habitoId = Integer.parseInt(habitoIdStr);
            Habito habito = habitoServicio.buscarHabito(habitoId);
            
            if (habito != null) {
                // Parsear veces realizado
                Integer vecesRealizado = 1;
                if (vecesRealizadoStr != null && !vecesRealizadoStr.isEmpty()) {
                    try {
                        vecesRealizado = Integer.parseInt(vecesRealizadoStr);
                    } catch (NumberFormatException e) {
                        vecesRealizado = 1;
                    }
                }
                
                // Crear el registro con el estado correspondiente
                RegistroHabito registro = new RegistroHabito();
                registro.setHabito(habito);
                registro.setFecha(fecha);
                registro.setNotas(notas);
                registro.setVecesRealizado(vecesRealizado);
                
                // Establecer estado de animo
                if (estadoAnimo != null && !estadoAnimo.isEmpty()) {
                    registro.setEstadoAnimo(estadoAnimo);
                } else {
                    registro.setEstadoAnimo("neutral");
                }
                
                // Determinar completado basado en si cumplio la meta
                Integer metaDiaria = obtenerMetaDiariaOPredeterminada(habito);
                if ("CUMPLIDO".equals(estado) || vecesRealizado >= metaDiaria) {
                    registro.setCompletado(true);
                } else {
                    registro.setCompletado(false);
                }
                
                logger.info(String.format("[CUMPLIMIENTO] Habito: %s | Fecha: %s | Veces: %d | Meta: %d | Completado: %b | Estado animo: %s", 
                    habito.getNombre(), fecha, vecesRealizado, metaDiaria, registro.getCompletado(), estadoAnimo));
                
                // Guardar el registro
                RegistroHabito registroGuardado = habitoServicio.getHabitoDAO().saveRegistro(registro);
                
                if (registroGuardado != null) {
                    // Agregar puntos al usuario segun el estado
                    logger.info(String.format("[PUNTOS] Agregando puntos a usuario %d por estado: %s", usuarioId, estado));
                    pointsService.addPointsToUser(usuarioId, estado);
                    
                    response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId + "&success=true");
                } else {
                    response.sendRedirect("controlador-habitos?action=list&usuarioId=" + usuarioId + "&error=register");
                }
            } else {
                response.sendRedirect("controlador-habitos?action=list&usuarioId=" + usuarioId + "&error=notfound");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Obtener usuarioId de la sesión
        HttpSession session = request.getSession(false);
        Integer usuarioId = getUsuarioIdFromSession(session);
        
        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            if ("registrar".equals(action)) {
                // Procesar registro de hábito cumplido
                procesarRegistroHabito(request, response, usuarioId);
                
            } else if ("crear-con-objetivo".equals(action)) {
                // Crear hábito asociado a un objetivo
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String frecuenciaStr = request.getParameter("frecuencia");
                String metaDiariaStr = request.getParameter("metaDiaria");
                String fechaInicioStr = request.getParameter("fechaInicio");
                
                logger.info(String.format("[CREAR] Habito con objetivo - Nombre: %s | Usuario: %d | Frecuencia: %s | Meta: %s", 
                    nombre, usuarioId, frecuenciaStr, metaDiariaStr));
                
                try {
                    Habito habito = new Habito();
                    habito.setNombre(nombre);
                    habito.setDescripcion(descripcion);
                    habito.setUsuarioId(usuarioId);
                    habito.setActivo(true);
                    habito.setEstadoAnimo("neutral");
                    
                    if (frecuenciaStr != null && !frecuenciaStr.isEmpty()) {
                        habito.setFrecuencia(Habito.FrecuenciaHabito.valueOf(frecuenciaStr.toUpperCase()));
                    } else {
                        habito.setFrecuencia(Habito.FrecuenciaHabito.DIARIA); // Default
                    }
                    
                    if (metaDiariaStr != null && !metaDiariaStr.isEmpty()) {
                        habito.setMetaDiaria(Integer.parseInt(metaDiariaStr));
                    } else {
                        habito.setMetaDiaria(1); // Default
                    }
                    
                    if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
                        habito.setFechaInicio(LocalDate.parse(fechaInicioStr));
                    } else {
                        habito.setFechaInicio(LocalDate.now()); // Default
                    }
                    
                    Habito habitoGuardado = habitoServicio.guardarHabito(habito);
                    
                    if (habitoGuardado != null && habitoGuardado.getId() != null) {
                        logger.info(String.format("[CREAR] Habito guardado exitosamente con ID: %d", habitoGuardado.getId()));
                        request.getSession().setAttribute("mensaje", "Habito '" + nombre + "' creado exitosamente");
                        response.sendRedirect(request.getContextPath() + "/controlador-objetivos?action=listar");
                    } else {
                        logger.warning("[CREAR] Error: habitoGuardado es null o no tiene ID");
                        request.getSession().setAttribute("error", "Error al guardar el habito. Intenta nuevamente.");
                        response.sendRedirect(request.getContextPath() + "/planificar");
                    }
                } catch (Exception e) {
                    logger.severe(String.format("[CREAR] Excepcion al crear habito: %s", e.getMessage()));
                    request.getSession().setAttribute("error", "Error: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/planificar");
                }
                
            } else if ("actualizar".equals(action)) {
                // Actualizar hábito existente
                String habitoIdStr = request.getParameter("habitoId");
                if (habitoIdStr != null) {
                    Integer habitoId = Integer.parseInt(habitoIdStr);
                    Habito habito = habitoServicio.buscarHabito(habitoId);
                    
                    if (habito != null && habito.getUsuarioId().equals(usuarioId)) {
                        // Actualizar campos
                        habito.setNombre(request.getParameter("nombre"));
                        habito.setDescripcion(request.getParameter("descripcion"));
                        
                        String frecuenciaStr = request.getParameter("frecuencia");
                        String metaDiariaStr = request.getParameter("metaDiaria");
                        String fechaInicioStr = request.getParameter("fechaInicio");
                        
                        if (frecuenciaStr != null && !frecuenciaStr.isEmpty()) {
                            habito.setFrecuencia(Habito.FrecuenciaHabito.valueOf(frecuenciaStr));
                        }
                        
                        if (metaDiariaStr != null && !metaDiariaStr.isEmpty()) {
                            habito.setMetaDiaria(Integer.parseInt(metaDiariaStr));
                        }
                        
                        if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
                            habito.setFechaInicio(LocalDate.parse(fechaInicioStr));
                        }
                        
                        Habito habitoGuardado = habitoServicio.guardarHabito(habito);
                        
                        if (habitoGuardado != null) {
                            logger.info(String.format("[ACTUALIZAR] Habito ID %d actualizado exitosamente", habitoId));
                            request.getSession().setAttribute("mensaje", "Habito actualizado exitosamente");
                            response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId);
                        } else {
                            logger.warning(String.format("[ACTUALIZAR] Error al actualizar habito ID: %d", habitoId));
                            response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId + "&error=save");
                        }
                    } else {
                        response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId + "&error=permission");
                    }
                }
                
            } else if ("create".equals(action) || "update".equals(action)) {
                // Crear o actualizar hábito
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String frecuenciaStr = request.getParameter("frecuencia");
                String metaDiariaStr = request.getParameter("metaDiaria");
                String habitoIdStr = request.getParameter("habitoId");
                
                Habito habito;
                if (habitoIdStr != null && !habitoIdStr.isEmpty()) {
                    // Actualizar hábito existente
                    Integer habitoId = Integer.parseInt(habitoIdStr);
                    habito = habitoServicio.buscarHabito(habitoId);
                    if (habito == null) {
                        response.sendRedirect("controlador-habitos?action=list&error=notfound");
                        return;
                    }
                } else {
                    // Crear nuevo hábito
                    habito = new Habito();
                    habito.setUsuarioId(usuarioId);
                }
                
                // Actualizar datos
                habito.setNombre(nombre);
                habito.setDescripcion(descripcion);
                
                if (frecuenciaStr != null && !frecuenciaStr.isEmpty()) {
                    habito.setFrecuencia(Habito.FrecuenciaHabito.valueOf(frecuenciaStr));
                }
                
                if (metaDiariaStr != null && !metaDiariaStr.isEmpty()) {
                    habito.setMetaDiaria(Integer.parseInt(metaDiariaStr));
                }
                
                Habito habitoGuardado = habitoServicio.guardarHabito(habito);
                
                if (habitoGuardado != null) {
                    response.sendRedirect("controlador-habitos?action=list&usuarioId=" + usuarioId + "&saved=true");
                } else {
                    response.sendRedirect("controlador-habitos?action=list&usuarioId=" + usuarioId + "&error=save");
                }
                
            } else if ("registrar".equals(action)) {
                procesarRegistroCumplimiento(request, response, usuarioId);
                
            } else {
                // Acción no reconocida, redirigir a GET
                doGet(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("controlador-habitos?action=list&error=exception");
        }
    }
    
    /**
     * Mostrar formulario para registrar habito diario
     */
    private void mostrarFormularioRegistro(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws ServletException, IOException {
        logger.info(String.format("[FORMULARIO] Mostrando registro de habitos para usuario ID: %d", usuarioId));
        
        // Obtener todos los habitos del usuario para que pueda seleccionar cual registrar
        List<Habito> habitos = habitoServicio.listarHabitosUsuario(usuarioId);
        logger.info(String.format("[FORMULARIO] Habitos disponibles para registro: %d", habitos.size()));
        
        request.setAttribute("habitos", habitos);
        request.getRequestDispatcher("/WEB-INF/views/registroHabito.jsp").forward(request, response);
    }
}
