# CORPALIV Backend

Microservicios en **Spring Boot** (Java 17, Maven) para la app móvil de CORPALIV / Alba Lab.
Proyecto DSY1105 · Desarrollo de Aplicaciones Móviles · Duoc UC.

| Microservicio | Puerto | Base de datos | Qué hace | Estado |
|---|---|---|---|---|
| `ms-contenidos` | 9091 | `bd_contenidos` | Programas, historias e indicadores de impacto, productos y categorías | ✅ listo |
| `ms-vinculacion` | 9092 | `bd_vinculacion` | Solicitudes de colaboración, tipos de colaboración e historial de estados | pendiente |
| `ms-admin` | 9093 | `bd_admin` | Login de gestores (JWT), usuarios y roles | pendiente |

## Requisitos
- JDK 17 (o superior) y la variable `JAVA_HOME` apuntando a él
- MySQL corriendo en `localhost:3306` (usuario `root`, sin clave; si el tuyo es distinto, cambia `application.properties` de cada servicio)
- VS Code con *Extension Pack for Java* y *Spring Boot Extension Pack*

Cada carpeta es un proyecto Maven independiente (incluye `mvnw`, no hace falta instalar Maven).
Las bases de datos se crean solas al iniciar.

## Cómo ejecutar un microservicio
```
cd ms-contenidos
.\mvnw.cmd spring-boot:run        (Windows)
./mvnw spring-boot:run            (Linux / Mac)
```
Para correr las pruebas unitarias: `.\mvnw.cmd test`. Para generar el .jar: `.\mvnw.cmd clean package`.

Ver `ms-contenidos/README.md` para los endpoints y cómo probarlos.