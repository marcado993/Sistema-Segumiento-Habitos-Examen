package com.sistema_seguimiento.config;

import com.sistema_seguimiento.dao.EntityManagerUtil;
import com.sistema_seguimiento.dao.UsuarioDAOJPA;
import com.sistema_seguimiento.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

/**
 * Utilidad para verificar la estructura de la base de datos
 * y el funcionamiento del sistema de puntos
 */
public class VerificarBaseDatos {
    
    public static void main(String[] args) {
        System.out.println("🔍 VERIFICACIÓN DE BASE DE DATOS - SISTEMA DE PUNTOS");
        System.out.println("=".repeat(60));
        
        EntityManager em = null;
        try {
            // 1. Verificar conexión
            System.out.println("\n1️⃣ Verificando conexión a la base de datos...");
            em = EntityManagerUtil.getEntityManager();
            System.out.println("✅ Conexión establecida correctamente");
            
            // 2. Verificar estructura de tabla usuario
            System.out.println("\n2️⃣ Verificando estructura de tabla 'usuario'...");
            verificarEstructuraUsuario(em);
            
            // 3. Verificar usuarios existentes y sus puntos
            System.out.println("\n3️⃣ Verificando usuarios y puntos...");
            verificarUsuariosYPuntos(em);
            
            // 4. Probar agregar puntos
            System.out.println("\n4️⃣ Probando funcionalidad de agregar puntos...");
            probarAgregarPuntos();
            
            // 5. Verificar registros de hábitos
            System.out.println("\n5️⃣ Verificando registros de hábitos recientes...");
            verificarRegistrosHabitos(em);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ VERIFICACIÓN COMPLETADA CON ÉXITO");
            System.out.println("=".repeat(60));
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR DURANTE LA VERIFICACIÓN:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    private static void verificarEstructuraUsuario(EntityManager em) {
        try {
            String sql = "SELECT column_name, data_type, is_nullable, column_default " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = 'usuario' " +
                        "ORDER BY ordinal_position";
            
            Query query = em.createNativeQuery(sql);
            List<Object[]> columns = query.getResultList();
            
            System.out.println("\n   Columnas de la tabla 'usuario':");
            System.out.println("   " + "-".repeat(70));
            System.out.printf("   %-20s %-20s %-10s %-15s%n", "COLUMNA", "TIPO", "NULLABLE", "DEFAULT");
            System.out.println("   " + "-".repeat(70));
            
            boolean tienePuntos = false;
            for (Object[] col : columns) {
                String columnName = (String) col[0];
                String dataType = (String) col[1];
                String nullable = (String) col[2];
                String defaultValue = col[3] != null ? col[3].toString() : "null";
                
                System.out.printf("   %-20s %-20s %-10s %-15s%n", 
                    columnName, dataType, nullable, defaultValue);
                
                if ("puntos".equals(columnName)) {
                    tienePuntos = true;
                }
            }
            
            System.out.println("   " + "-".repeat(70));
            
            if (tienePuntos) {
                System.out.println("   ✅ La columna 'puntos' existe en la tabla");
            } else {
                System.out.println("   ⚠️ LA COLUMNA 'puntos' NO EXISTE - DEBES EJECUTAR EL SCRIPT DE REPARACIÓN");
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Error al verificar estructura: " + e.getMessage());
        }
    }
    
    private static void verificarUsuariosYPuntos(EntityManager em) {
        try {
            String sql = "SELECT id, nombre, correo, puntos, fecha_registro, activo " +
                        "FROM usuario ORDER BY id";
            
            Query query = em.createNativeQuery(sql);
            List<Object[]> usuarios = query.getResultList();
            
            if (usuarios.isEmpty()) {
                System.out.println("   ⚠️ No hay usuarios en la base de datos");
                return;
            }
            
            System.out.println("\n   Usuarios registrados:");
            System.out.println("   " + "-".repeat(80));
            System.out.printf("   %-5s %-20s %-30s %-10s %-8s%n", 
                "ID", "NOMBRE", "CORREO", "PUNTOS", "ACTIVO");
            System.out.println("   " + "-".repeat(80));
            
            int totalUsuarios = 0;
            int usuariosConPuntos = 0;
            int usuariosConPuntosNull = 0;
            
            for (Object[] usuario : usuarios) {
                Integer id = (Integer) usuario[0];
                String nombre = (String) usuario[1];
                String correo = (String) usuario[2];
                Integer puntos = usuario[3] != null ? (Integer) usuario[3] : null;
                Boolean activo = (Boolean) usuario[5];
                
                String puntosStr = puntos != null ? puntos.toString() : "NULL ⚠️";
                String activoStr = Boolean.TRUE.equals(activo) ? "Sí" : "No";
                
                System.out.printf("   %-5d %-20s %-30s %-10s %-8s%n", 
                    id, nombre, correo, puntosStr, activoStr);
                
                totalUsuarios++;
                if (puntos != null && puntos > 0) {
                    usuariosConPuntos++;
                }
                if (puntos == null) {
                    usuariosConPuntosNull++;
                }
            }
            
            System.out.println("   " + "-".repeat(80));
            System.out.println("   Total de usuarios: " + totalUsuarios);
            System.out.println("   Usuarios con puntos > 0: " + usuariosConPuntos);
            
            if (usuariosConPuntosNull > 0) {
                System.out.println("   ⚠️ Usuarios con puntos NULL: " + usuariosConPuntosNull);
                System.out.println("   → Ejecuta el script 'reparar-base-datos-puntos.sql'");
            } else {
                System.out.println("   ✅ Todos los usuarios tienen puntos definidos");
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Error al verificar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void probarAgregarPuntos() {
        try {
            UsuarioDAOJPA usuarioDAO = new UsuarioDAOJPA();
            
            // Buscar el primer usuario
            List<Usuario> usuarios = usuarioDAO.findAll();
            if (usuarios.isEmpty()) {
                System.out.println("   ⚠️ No hay usuarios para probar");
                return;
            }
            
            Usuario usuario = usuarios.get(0);
            Integer puntosAntes = usuario.getPuntos();
            
            System.out.println("   Usuario de prueba: " + usuario.getNombre());
            System.out.println("   Puntos antes: " + puntosAntes);
            
            // Intentar agregar 10 puntos
            boolean resultado = usuarioDAO.addPoints(usuario.getId(), 10);
            
            if (resultado) {
                // Recargar el usuario
                Usuario usuarioActualizado = usuarioDAO.findById(usuario.getId()).orElse(null);
                if (usuarioActualizado != null) {
                    Integer puntosDespues = usuarioActualizado.getPuntos();
                    System.out.println("   Puntos después: " + puntosDespues);
                    
                    if (puntosDespues == puntosAntes + 10) {
                        System.out.println("   ✅ Función addPoints() funciona correctamente");
                        
                        // Revertir los puntos agregados
                        usuarioDAO.addPoints(usuario.getId(), -10);
                        System.out.println("   ↩️ Puntos revertidos al valor original");
                    } else {
                        System.out.println("   ⚠️ Los puntos no se agregaron correctamente");
                    }
                }
            } else {
                System.out.println("   ❌ Error al agregar puntos");
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Error al probar agregar puntos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verificarRegistrosHabitos(EntityManager em) {
        try {
            String sql = "SELECT rh.id, rh.fecha, h.nombre AS habito, rh.completado, " +
                        "rh.veces_realizado, u.nombre AS usuario, u.puntos " +
                        "FROM registro_habito rh " +
                        "JOIN habito h ON rh.habito_id = h.id " +
                        "JOIN usuario u ON h.usuario_id = u.id " +
                        "ORDER BY rh.fecha DESC LIMIT 5";
            
            Query query = em.createNativeQuery(sql);
            List<Object[]> registros = query.getResultList();
            
            if (registros.isEmpty()) {
                System.out.println("   ℹ️ No hay registros de hábitos todavía");
                return;
            }
            
            System.out.println("\n   Últimos registros de hábitos:");
            System.out.println("   " + "-".repeat(90));
            System.out.printf("   %-5s %-12s %-20s %-12s %-8s %-15s %-10s%n",
                "ID", "FECHA", "HÁBITO", "COMPLETADO", "VECES", "USUARIO", "PUNTOS");
            System.out.println("   " + "-".repeat(90));
            
            for (Object[] registro : registros) {
                Integer id = (Integer) registro[0];
                String fecha = registro[1].toString();
                String habito = (String) registro[2];
                Boolean completado = (Boolean) registro[3];
                Integer veces = registro[4] != null ? (Integer) registro[4] : 0;
                String usuario = (String) registro[5];
                Integer puntos = registro[6] != null ? (Integer) registro[6] : 0;
                
                String completadoStr = Boolean.TRUE.equals(completado) ? "Sí ✅" : "No ❌";
                
                System.out.printf("   %-5d %-12s %-20s %-12s %-8d %-15s %-10d%n",
                    id, fecha, habito, completadoStr, veces, usuario, puntos);
            }
            
            System.out.println("   " + "-".repeat(90));
            
        } catch (Exception e) {
            System.err.println("   ℹ️ No se pudieron cargar registros de hábitos");
            System.err.println("   " + e.getMessage());
        }
    }
}
