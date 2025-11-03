package com.sistema_seguimiento.servlet;

import com.sistema_seguimiento.dao.UserPetDAO;
import com.sistema_seguimiento.model.PetType;
import com.sistema_seguimiento.model.UserPet;
import com.sistema_seguimiento.model.Usuario;
import com.sistema_seguimiento.services.PetUnlockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las mascotas virtuales del usuario
 * Maneja la visualización, desbloqueo y evolución de mascotas
 */
@WebServlet(name = "ControladorMascotas", urlPatterns = {"/mascotas-virtuales"})
public class ControladorMascotas extends HttpServlet {

    private UserPetDAO userPetDAO;
    private PetUnlockService petUnlockService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userPetDAO = new UserPetDAO();
        this.petUnlockService = new PetUnlockService();
        System.out.println("✅ ControladorMascotas inicializado");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Integer usuarioId = usuario.getId();

        String action = request.getParameter("action");

        if ("check-unlock".equals(action)) {
            // Verificar si se debe desbloquear una nueva mascota
            verificarYDesbloquearMascota(request, response, usuarioId);
        } else {
            // Mostrar vista de mascotas
            mostrarMascotas(request, response, usuarioId);
        }
    }

    /**
     * Muestra la vista de mascotas del usuario
     */
    private void mostrarMascotas(HttpServletRequest request, HttpServletResponse response, Integer usuarioId)
            throws ServletException, IOException {

        try {
            System.out.println("🔍 Mostrando mascotas para usuario: " + usuarioId);
            
            // Obtener mascota activa
            Optional<UserPet> activePetOpt = userPetDAO.findActiveUserPet(usuarioId);
            System.out.println("✅ Mascota activa: " + (activePetOpt.isPresent() ? "Sí" : "No"));
            
            // Obtener todas las mascotas del usuario
            List<UserPet> allPets = userPetDAO.findAllUserPets(usuarioId);
            System.out.println("✅ Total mascotas: " + allPets.size());
            
            // Contar hábitos completados
            long habitosCompletados = userPetDAO.countCompletedHabits(usuarioId);
            System.out.println("✅ Hábitos completados: " + habitosCompletados);
            
            // Calcular progreso hacia la siguiente evolución
            int progresoActual = (int) habitosCompletados;
            int proximoHito = calcularProximoHito(progresoActual);
            int progresoParaProximoHito = proximoHito - progresoActual;
            
            // Determinar qué mascota debería tener según sus hábitos
            PetType tipoEsperado = petUnlockService.checkEvolution(usuarioId.longValue(), progresoActual);
            System.out.println("✅ Tipo esperado: " + tipoEsperado);

            request.setAttribute("activePet", activePetOpt.orElse(null));
            request.setAttribute("allPets", allPets);
            request.setAttribute("habitosCompletados", habitosCompletados);
            request.setAttribute("proximoHito", proximoHito);
            request.setAttribute("progresoParaProximoHito", progresoParaProximoHito);
            request.setAttribute("tipoEsperado", tipoEsperado);
            request.setAttribute("tieneActivePet", activePetOpt.isPresent());

            // Verificar si el usuario debería tener una mascota pero no la tiene
            if (tipoEsperado != null && activePetOpt.isEmpty()) {
                request.setAttribute("deberiaDesbloquear", true);
                request.setAttribute("tipoPorDesbloquear", tipoEsperado);
            }

            System.out.println("✅ Redirigiendo a JSP...");
            request.getRequestDispatcher("/WEB-INF/views/mascotasVirtuales.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en mostrarMascotas: " + e.getMessage());
            e.printStackTrace();
            
            // Configurar valores por defecto para evitar error 500
            request.setAttribute("activePet", null);
            request.setAttribute("allPets", java.util.Collections.emptyList());
            request.setAttribute("habitosCompletados", 0L);
            request.setAttribute("proximoHito", 10);
            request.setAttribute("progresoParaProximoHito", 10);
            request.setAttribute("tipoEsperado", null);
            request.setAttribute("tieneActivePet", false);
            request.setAttribute("error", "Error al cargar mascotas: " + e.getMessage());
            
            request.getRequestDispatcher("/WEB-INF/views/mascotasVirtuales.jsp").forward(request, response);
        }
    }

    /**
     * Verifica y desbloquea una mascota si corresponde
     */
    private void verificarYDesbloquearMascota(HttpServletRequest request, HttpServletResponse response, Integer usuarioId)
            throws IOException {

        // Contar hábitos completados
        long habitosCompletados = userPetDAO.countCompletedHabits(usuarioId);

        // Verificar si se debe desbloquear una mascota
        PetType tipoADesbloquear = petUnlockService.checkEvolution(usuarioId.longValue(), (int) habitosCompletados);

        if (tipoADesbloquear != null) {
            // Verificar si ya tiene esa mascota
            Optional<UserPet> activePet = userPetDAO.findActiveUserPet(usuarioId);

            if (activePet.isEmpty() || activePet.get().getState() != tipoADesbloquear) {
                // Desbloquear nueva mascota
                try {
                    userPetDAO.createUserPet(usuarioId, tipoADesbloquear);
                    System.out.println("🎉 Mascota " + tipoADesbloquear + " desbloqueada para usuario " + usuarioId);
                    response.sendRedirect(request.getContextPath() + "/mascotas-virtuales?unlocked=" + tipoADesbloquear);
                    return;
                } catch (Exception e) {
                    System.err.println("Error al desbloquear mascota: " + e.getMessage());
                }
            }
        }

        // Si no se desbloqueó nada, redirigir a la vista normal
        response.sendRedirect(request.getContextPath() + "/mascotas-virtuales");
    }

    /**
     * Calcula el próximo hito de desbloqueo
     */
    private int calcularProximoHito(int habitosActuales) {
        if (habitosActuales < 10) {
            return 10; // HUEVO
        } else if (habitosActuales < 50) {
            return 50; // BEBE
        } else if (habitosActuales < 100) {
            return 100; // ADULTO (futuro)
        } else {
            return 200; // LEGENDARIO (futuro)
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Integer usuarioId = usuario.getId();

        String action = request.getParameter("action");

        if ("unlock".equals(action)) {
            // Forzar desbloqueo (para testing o casos especiales)
            String petTypeStr = request.getParameter("petType");
            if (petTypeStr != null) {
                try {
                    PetType petType = PetType.valueOf(petTypeStr.toUpperCase());
                    userPetDAO.createUserPet(usuarioId, petType);
                    response.sendRedirect(request.getContextPath() + "/mascotas-virtuales?success=unlocked");
                    return;
                } catch (Exception e) {
                    System.err.println("Error al desbloquear mascota: " + e.getMessage());
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/mascotas-virtuales");
    }

    /**
     * Método público para ser llamado desde otros servlets
     * Verifica y desbloquea mascotas automáticamente
     */
    public static void checkAndUnlockPet(Integer usuarioId) {
        UserPetDAO dao = new UserPetDAO();
        PetUnlockService service = new PetUnlockService();

        long habitosCompletados = dao.countCompletedHabits(usuarioId);
        PetType tipoADesbloquear = service.checkEvolution(usuarioId.longValue(), (int) habitosCompletados);

        if (tipoADesbloquear != null) {
            Optional<UserPet> activePet = dao.findActiveUserPet(usuarioId);

            // Si no tiene mascota activa o la activa es diferente, desbloquear
            if (activePet.isEmpty() || activePet.get().getState() != tipoADesbloquear) {
                try {
                    dao.createUserPet(usuarioId, tipoADesbloquear);
                    System.out.println("🎉 Mascota " + tipoADesbloquear + " desbloqueada automáticamente para usuario " + usuarioId);
                } catch (Exception e) {
                    System.err.println("Error al desbloquear mascota automáticamente: " + e.getMessage());
                }
            }
        }
    }
}

