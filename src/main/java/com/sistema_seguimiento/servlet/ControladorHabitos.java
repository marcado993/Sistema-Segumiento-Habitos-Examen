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

/**
 * Controlador de Hábitos según el diagrama de clases
 * Maneja las operaciones relacionadas con hábitos
 */
@WebServlet("/controlador-habitos")
public class ControladorHabitos extends HttpServlet {

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
            // Ver todos los hábitos del usuario (vista de seguimiento general)
            System.out.println("📊 Cargando vista de seguimiento para usuario: " + usuarioId);
            List<Habito> habitos = habitoServicio.listarHabitosUsuario(usuarioId);
            System.out.println("📋 Hábitos encontrados: " + habitos.size());
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
        String notas = request.getParameter("notas");  // ✅ CORREGIDO: Usar "notas" en vez de "observacion"
        
        System.out.println("📝 Procesando registro de hábito:");
        System.out.println("   - Hábito ID: " + habitoIdStr);
        System.out.println("   - Fecha: " + fechaStr);
        System.out.println("   - Estado: " + estadoStr);
        
        if (habitoIdStr != null && !habitoIdStr.isEmpty()) {
            try {
                Integer habitoId = Integer.parseInt(habitoIdStr);
                LocalDate fecha = fechaStr != null ? LocalDate.parse(fechaStr) : LocalDate.now();
                
                Habito habito = habitoServicio.registrarCumplimiento(habitoId, fecha, notas);  // ✅ CORREGIDO
                
                if (habito != null) {
                    System.out.println("✅ Registro exitoso del hábito ID: " + habitoId);
                    
                    // Agregar puntos al usuario según el estado del hábito
                    pointsService.addPointsToUser(usuarioId, estadoStr);
                    
                    response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId + "&success=true");
                } else {
                    System.out.println("❌ Error al registrar hábito");
                    response.sendRedirect("controlador-habitos?action=registrar&usuarioId=" + usuarioId + "&error=save");
                }
            } catch (Exception e) {
                System.err.println("❌ Error al procesar registro: " + e.getMessage());
                e.printStackTrace();
                response.sendRedirect("controlador-habitos?action=registrar&usuarioId=" + usuarioId + "&error=exception");
            }
        } else {
            System.out.println("❌ No se proporcionó habitoId");
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
                // ✅ Parsear veces_realizado
                Integer vecesRealizado = 1; // Default
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
                registro.setNotas(notas);  // ✅ Cambio: setNotas en lugar de setObservacion
                registro.setVecesRealizado(vecesRealizado);  // ✅ NUEVO: Usar el valor ingresado por el usuario
                
                // ✅ NUEVO: Establecer estado de ánimo
                if (estadoAnimo != null && !estadoAnimo.isEmpty()) {
                    registro.setEstadoAnimo(estadoAnimo);
                } else {
                    registro.setEstadoAnimo("neutral");  // Default
                }
                
                // ✅ Determinar completado (Boolean) basado en si cumplió la meta
                Integer metaDiaria = obtenerMetaDiariaOPredeterminada(habito);
                if ("CUMPLIDO".equals(estado) || vecesRealizado >= metaDiaria) {
                    registro.setCompletado(true);  // ✅ Boolean true
                } else {
                    registro.setCompletado(false);  // ✅ Boolean false
                }
                
                System.out.println("📝 Registro creado:");
                System.out.println("   - Hábito: " + habito.getNombre());
                System.out.println("   - Fecha: " + fecha);
                System.out.println("   - Veces realizado: " + vecesRealizado);
                System.out.println("   - Meta diaria: " + metaDiaria);
                System.out.println("   - Completado: " + registro.getCompletado());
                System.out.println("   - Estado de ánimo: " + estadoAnimo);  // ✅ NUEVO log
                
                // Guardar el registro
                RegistroHabito registroGuardado = habitoServicio.getHabitoDAO().saveRegistro(registro);
                
                if (registroGuardado != null) {
                    // ✅ AGREGAR PUNTOS AL USUARIO según el estado
                    System.out.println("🎮 Agregando puntos al usuario " + usuarioId + " por estado: " + estado);
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
                String objetivoIdStr = request.getParameter("objetivoId");
                String fechaInicioStr = request.getParameter("fechaInicio");
                
                System.out.println("🎯 Creando hábito con objetivo:");
                System.out.println("   - Nombre: " + nombre);
                System.out.println("   - Descripción: " + descripcion);
                System.out.println("   - Frecuencia: " + frecuenciaStr);
                System.out.println("   - Meta Diaria: " + metaDiariaStr);
                System.out.println("   - Fecha Inicio: " + fechaInicioStr);
                System.out.println("   - Usuario ID: " + usuarioId);
                
                try {
                    Habito habito = new Habito();
                    habito.setNombre(nombre);
                    habito.setDescripcion(descripcion);
                    habito.setUsuarioId(usuarioId);
                    habito.setActivo(true);
                    
                    // ✅ Estado de ánimo con valor por defecto (no se pide en el formulario)
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
                        System.out.println("✅ Hábito guardado exitosamente con ID: " + habitoGuardado.getId());
                        request.getSession().setAttribute("mensaje", "✅ Hábito '" + nombre + "' creado exitosamente");
                        response.sendRedirect(request.getContextPath() + "/controlador-objetivos?action=listar");
                    } else {
                        System.err.println("❌ Error: habitoGuardado es null o no tiene ID");
                        request.getSession().setAttribute("error", "❌ Error al guardar el hábito. Intenta nuevamente.");
                        response.sendRedirect(request.getContextPath() + "/planificar");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Excepción al crear hábito: " + e.getMessage());
                    e.printStackTrace();
                    request.getSession().setAttribute("error", "❌ Error: " + e.getMessage());
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
                            request.getSession().setAttribute("mensaje", "✅ Hábito actualizado exitosamente");
                            response.sendRedirect("controlador-habitos?action=view&usuarioId=" + usuarioId);
                        } else {
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
     * Mostrar formulario para registrar hábito diario
     */
    private void mostrarFormularioRegistro(HttpServletRequest request, HttpServletResponse response, Integer usuarioId) throws ServletException, IOException {
        System.out.println("📝 Mostrando formulario de registro de hábitos para usuario: " + usuarioId);
        
        // Obtener todos los hábitos del usuario para que pueda seleccionar cuál registrar
        List<Habito> habitos = habitoServicio.listarHabitosUsuario(usuarioId);
        System.out.println("📋 Hábitos encontrados para registro: " + habitos.size());
        
        request.setAttribute("habitos", habitos);
        request.getRequestDispatcher("/WEB-INF/views/registroHabito.jsp").forward(request, response);
    }
}
