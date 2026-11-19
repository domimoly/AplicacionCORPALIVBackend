# CORPALIV Backend

Microservicios en **Spring Boot** (Java 17, Maven) para la app móvil de CORPALIV / Alba Lab.
Proyecto DSY1105 · Desarrollo de Aplicaciones Móviles · Duoc UC.

| Microservicio | Puerto | Base de datos | Qué hace | Estado |
|---|---|---|---|---|
| `ms-contenidos` | 9091 | `bd_contenidos` | Programas, historias e indicadores de impacto | ✅ listo |
| `ms-vinculacion` | 9092 | `bd_vinculacion` | Solicitudes de colaboración, tipos de colaboración e historial de estados | pendiente |
| `ms-admin` | 9093 | `bd_admin` | Login de gestores (JWT), usuarios y roles | ✅ listo |
| `ms-catalogo` | 9094 | `bd_catalogo` | Productos, servicios y categorías | ✅ listo |

## Requisitos
- JDK 17 (o superior) y la variable `JAVA_HOME` apuntando a él
- MySQL corriendo en `localhost:3306` (usuario `root`, sin clave; si el tuyo es distinto, cambia `application.properties` de cada servicio)
- VS Code con *Extension Pack for Java* y *Spring Boot Extension Pack*

Cada carpeta es un proyecto Maven independiente (incluye `mvnw`, no hace falta instalar Maven).
Las bases de datos se crean solas al iniciar.

## Orden recomendado para probar
1. Inicia `ms-admin` y haz login (ver `ms-admin/README.md`) para obtener un token.
2. Inicia `ms-contenidos` y `ms-catalogo`: sus `GET` públicos funcionan sin token; crear, modificar, eliminar y las listas `/admin` piden el token.
3. En Swagger usa el botón **Authorize** y pega el token.

## Cómo ejecutar un microservicio
```
cd ms-contenidos
.\mvnw.cmd spring-boot:run        (Windows)
./mvnw spring-boot:run            (Linux / Mac)
```
Para correr las pruebas unitarias: `.\mvnw.cmd test`. Para generar el .jar: `.\mvnw.cmd clean package`.

Cada microservicio tiene su propio `README.md` con los endpoints y cómo probarlos (por ejemplo `ms-contenidos/README.md` y `ms-catalogo/README.md`).
