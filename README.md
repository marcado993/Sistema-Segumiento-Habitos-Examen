# Sistema de Seguimiento de Hábitos - GR05_1BT3_622_25B

## 📋 Descripción del Proyecto

Plataforma web diseñada para fomentar la creación de hábitos positivos y combatir la procrastinación. Permite a los usuarios definir hábitos en distintos ámbitos (personal, académico, deportivo, etc.), asignar tareas asociadas y registrar su cumplimiento. El sistema ofrece visualización del progreso para incentivar la constancia y la disciplina.

## 🎯 Características Principales

- **Gestión de Hábitos**: Registro y seguimiento de hábitos diarios en múltiples categorías
- **Sistema de Puntos**: Recompensas por cumplimiento de objetivos
- **Registro de Estado de Ánimo**: Tracking diario del mood del usuario con notificaciones
- **Mascotas Virtuales**: Sistema de gamificación para motivación adicional
- **Frases Motivacionales**: Inspiración diaria para mantener la constancia
- **Planificación de Objetivos**: Establecimiento y seguimiento de metas personales

## 🛠️ Tecnologías Utilizadas

- **Backend**: Java 21, Jakarta EE (Servlets, JPA/Hibernate)
- **Base de Datos**: PostgreSQL (Supabase Cloud)
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven
- **CI/CD**: Jenkins
- **Logging**: java.util.logging.Logger
- **Control de Versiones**: Git

## 📦 Estructura del Proyecto

```
sistema-seguimiento/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/sistema_seguimiento/
│   │   │       ├── config/         # Configuración JPA
│   │   │       ├── dao/            # Data Access Objects
│   │   │       ├── filter/         # Filtros de autenticación
│   │   │       ├── model/          # Entidades del dominio
│   │   │       ├── services/       # Lógica de negocio
│   │   │       └── servlet/        # Controladores web
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── views/          # JSP views
│   │       │   └── web.xml
│   │       └── *.jsp               # Páginas públicas
│   └── tests/
│       └── java/
│           └── com/sistema_seguimiento/
│               ├── dao/            # Tests de integración
│               ├── model/          # Tests unitarios
│               ├── services/       # Tests con mocks
│               └── servlet/        # Tests de controladores
├── pom.xml
├── Dockerfile
├── Jenkinsfile
└── README.md
```

## 🧪 Metodología de Desarrollo: Test-Driven Development (TDD)

Este proyecto implementa TDD siguiendo el ciclo **Red-Green-Refactor**:

1. **🔴 Red**: Escribir tests que fallan primero
2. **🟢 Green**: Implementar código mínimo para pasar los tests
3. **🔵 Refactor**: Mejorar el código manteniendo los tests verdes

### Cobertura de Tests

El proyecto incluye múltiples niveles de testing:

- **Tests Unitarios**: `MoodEntry`, `MoodService` (validación de lógica)
- **Tests Parametrizados**: Validación de fechas con múltiples casos
- **Tests con Mocks**: Servicios con dependencias inyectadas (Mockito)
- **Tests de Integración**: DAOs con base de datos real

**Total**: 34 tests ✅ (0 failures, 0 skipped)

### Ejemplo: Feature MoodEntry

```java
// 1. RED: Test que falla
@Test
void should_ReturnTrue_When_MoodEntryIsFromToday() {
    MoodEntry entry = new MoodEntry(1, 5, LocalDate.now(), "Feliz");
    assertTrue(entry.isFromToday());
}

// 2. GREEN: Implementación mínima
public boolean isFromToday() {
    return this.fecha.equals(LocalDate.now());
}

// 3. REFACTOR: Código limpio con JavaDoc
/**
 * Verifica si la entrada de ánimo corresponde a la fecha actual.
 * @return true si la fecha de la entrada es hoy, false en caso contrario
 */
public boolean isFromToday() {
    return this.fecha.equals(LocalDate.now());
}
```

## 🚀 Instalación y Configuración

### Prerrequisitos

- **Java JDK**: 21 o superior
- **Maven**: 3.8+
- **PostgreSQL**: 12+
- **Servidor Jakarta EE**: TomEE 9.x, WildFly 27+, o similar

### Compilar el Proyecto

```bash
mvn clean compile
```

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo tests unitarios
mvn test -Dtest=*Test

# Con cobertura
mvn test jacoco:report
```

### Generar WAR

```bash
mvn clean package
```

El archivo WAR se generará en: `target/sistema-seguimiento.war`

## 🔧 Configuración de Base de Datos

### PostgreSQL en Supabase

1. Crear una cuenta en [Supabase](https://supabase.com)
2. Crear un nuevo proyecto
3. Obtener las credenciales de conexión

### Configurar persistence.xml

Editar `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:postgresql://[HOST]:[PORT]/[DATABASE]"/>
<property name="jakarta.persistence.jdbc.user" value="[USER]"/>
<property name="jakarta.persistence.jdbc.password" value="[PASSWORD]"/>
```

### Ejecutar Scripts SQL

```bash
psql -h [HOST] -U [USER] -d [DATABASE] -f SCRIPTS_SQL_SOLO_TABLAS.sql
```

## 📊 CI/CD con Jenkins

### Configuración Jenkins Freestyle Project

**1. Source Code Management**
- Repository URL: `https://github.com/marcado993/Sistema-Segumiento-Habitos-Examen.git`
- Branch Specifier: `*/main`

**2. Build Triggers**
- Poll SCM: `H/5 * * * *` (cada 5 minutos)

**3. Build Steps**
- Invoke top-level Maven targets
- Goals: `clean test package`

**4. Post-build Actions**
- Publish JUnit test result report
- Test report XMLs: `**/target/surefire-reports/*.xml`

### Pipeline Automático

1. Commit → Push a GitHub
2. Jenkins detecta cambios (polling)
3. Ejecuta: `mvn clean test package`
4. Genera reportes de tests
5. Notifica resultado del build

## 📝 Convenciones de Código

### Logging Profesional

❌ **Evitar:**
```java
System.out.println("Usuario registrado");
```

✅ **Usar:**
```java
private static final Logger logger = Logger.getLogger(MiClase.class.getName());
logger.info("[REGISTRO] Usuario registrado exitosamente: " + username);
```

### Prefijos Estructurados

- `[REGISTRO]`: Operaciones de registro/login
- `[PUNTOS]`: Sistema de puntos
- `[CUMPLIMIENTO]`: Registro de cumplimiento de hábitos
- `[ERROR]`: Errores críticos
- `[INFO]`: Información general

### JavaDoc

```java
/**
 * Verifica si el usuario tiene permitido actualizar su estado de ánimo.
 * 
 * @param entry la entrada de ánimo a validar
 * @return true si la actualización está permitida, false en caso contrario
 * @throws IllegalArgumentException si entry es null
 */
public boolean isUpdateAllowed(MoodEntry entry) {
    // Implementación
}
```

### Nombres de Tests

Seguir patrón: `should_ExpectedBehavior_When_Condition`

```java
@Test
void should_ReturnFalse_When_MoodEntryIsFromYesterday()

@Test
void should_SendNotification_When_NoMoodEntryToday()
```

## 🐛 Resolución de Problemas

### Build Failures

```bash
# Limpiar cache de Maven
mvn clean

# Forzar actualización de dependencias
mvn clean install -U

# Skip tests temporalmente
mvn clean package -DskipTests
```

### Tests Fallando

```bash
# Ver detalles de tests
mvn test -X

# Ejecutar test específico
mvn test -Dtest=MoodEntryTest
```

### Base de Datos

```bash
# Verificar conexión
java -cp target/classes com.sistema_seguimiento.VerificarBaseDatos
```

## 📈 Roadmap

- [x] Implementación TDD de MoodEntry
- [x] Sistema de puntos
- [x] Mascotas virtuales
- [x] Refactorización con logging profesional
- [x] Configuración Jenkins
- [ ] Dashboard de estadísticas
- [ ] API REST
- [ ] Aplicación móvil

## 👥 Equipo de Desarrollo

**Grupo 05 - 1BT3_622_25B**

- **Desarrollador Principal**: Marco Antonio Castro Domínguez
- **Metodología**: Test-Driven Development (TDD)
- **Universidad**: [Tu Universidad]
- **Asignatura**: Ingeniería de Software

## 📅 Historial de Versiones

- **v1.0.0** (Nov 2025) - Implementación inicial con TDD
  - Sistema de hábitos
  - MoodEntry feature
  - Sistema de puntos
  - Integración con Jenkins

## 📄 Licencia

Proyecto educativo - Examen de Ingeniería de Software

## 🔗 Enlaces Útiles

- [Jakarta EE Specification](https://jakarta.ee/specifications/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Maven Documentation](https://maven.apache.org/guides/)

---

⭐ **Nota**: Este proyecto fue desarrollado siguiendo las mejores prácticas de desarrollo de software, incluyendo TDD, clean code, logging profesional, y principios SOLID.

🚀 **Status del Build**: ![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
🧪 **Tests**: ![Tests](https://img.shields.io/badge/tests-34%20passed-success)
