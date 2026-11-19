<p align="center">
  <img src="https://raw.githubusercontent.com/domimoly/AplicacionCORPALIV/master/app/src/main/res/drawable/logo_corpaliv.png" width="320" alt="Logo de CORPALIV">
</p>

<h1 align="center">CORPALIV · Backend</h1>

<p align="center">
  Microservicios que dan servicio a la app móvil de inclusión social y laboral de CORPALIV.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java%2017-6A60AA?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-00A878?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-FFC30D?style=for-the-badge&logo=mysql&logoColor=black" alt="MySQL">
  <img src="https://img.shields.io/badge/JWT-FF5FA2?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/DSY1105-Desarrollo%20de%20Aplicaciones%20M%C3%B3viles-6A60AA?style=flat-square" alt="Asignatura">
  <img src="https://img.shields.io/badge/Duoc%20UC-Evaluaci%C3%B3n%20Parcial%204-FF5FA2?style=flat-square" alt="Evaluación">
  <img src="https://img.shields.io/badge/Datos-ficticios-00A878?style=flat-square" alt="Datos ficticios">
  <img src="https://img.shields.io/badge/Estado-MVP-FFC30D?style=flat-square&labelColor=555" alt="MVP">
</p>

---

## Índice

1. [Integrantes](#integrantes)
2. [¿De qué trata el proyecto?](#de-qué-trata-el-proyecto)
3. [Microservicios](#microservicios)
4. [Endpoints](#endpoints)
5. [Tecnologías usadas](#tecnologías-usadas)
6. [Requisitos](#requisitos)
7. [Cómo ejecutar el backend](#cómo-ejecutar-el-backend)
8. [Uso desde la app Android](#uso-desde-la-app-android)
9. [Pruebas unitarias](#pruebas-unitarias)

---

## Integrantes

| Integrante | Rol |
|---|---|
| **Javiera Faundez** | Desarrollo de la app y del backend |
| **Dominga Ruiz** | Desarrollo de la app y del backend |

## ¿De qué trata el proyecto?

**CORPALIV (Alba Lab)** es una organización sin fines de lucro que trabaja por la inclusión social y laboral de personas con discapacidad múltiple.

Este repositorio contiene el **backend** de su app móvil: cuatro microservicios en **Spring Boot**, cada uno con su propia base de datos MySQL. La app Android está en otro repositorio: [AplicacionCORPALIV](https://github.com/domimoly/AplicacionCORPALIV).

> [!NOTE]
> Es un MVP (una primera versión), no un sistema listo para producción. Todos los datos son **ficticios**, de ejemplo.

> [!TIP]
> Dividimos los servicios **por área de negocio, no por tabla**: por ejemplo, `ms-contenidos` agrupa programas, historias e indicadores, que son contenido que el equipo publica.

## Microservicios

| Servicio | Puerto | Base de datos | Qué hace |
|---|---|---|---|
| `ms-admin` | 9093 | `bd_admin` | Login, usuarios y roles. Es el único que firma los tokens JWT |
| `ms-contenidos` | 9091 | `bd_contenidos` | Programas, historias e indicadores de impacto |
| `ms-vinculacion` | 9092 | `bd_vinculacion` | Solicitudes de colaboración, tipos y seguimiento |
| `ms-catalogo` | 9094 | `bd_catalogo` | Categorías, productos y servicios |

Cada servicio valida el token por su cuenta. La documentación de cada uno está en `http://localhost:<puerto>/doc/swagger-ui.html`.

## Endpoints

Los que dicen **token** requieren el encabezado `Authorization: Bearer <token>`.

![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) consulta ·
![POST](https://img.shields.io/badge/POST-6A60AA?style=flat-square) crea ·
![PUT](https://img.shields.io/badge/PUT-FFC30D?style=flat-square&labelColor=555) edita ·
![PATCH](https://img.shields.io/badge/PATCH-FF8A3D?style=flat-square) cambia una parte ·
![DELETE](https://img.shields.io/badge/DELETE-D81B60?style=flat-square) elimina

### ms-admin · puerto 9093

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| ![POST](https://img.shields.io/badge/POST-6A60AA?style=flat-square) | `/auth/login` | público | Entrega el token (correo y clave) |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) ![POST](https://img.shields.io/badge/POST-6A60AA?style=flat-square) | `/usuarios` | token de ADMIN | Lista y crea usuarios |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) ![PUT](https://img.shields.io/badge/PUT-FFC30D?style=flat-square&labelColor=555) ![DELETE](https://img.shields.io/badge/DELETE-D81B60?style=flat-square) | `/usuarios/{id}` | token de ADMIN | Consulta, edita y elimina un usuario |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/roles` | token de ADMIN | Lista de roles |

### ms-contenidos · puerto 9091

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/programas`, `/programas/{id}` | público | Programas publicados |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/historias`, `/historias/{id}` | público | Historias de impacto |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/indicadores`, `/indicadores/{id}` | público | Indicadores de impacto |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/programas/{id}/exists` | público | Lo usa ms-vinculacion |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/programas/admin`, `/historias/admin`, `/indicadores/admin` | token | Lista completa, incluye borradores |
| ![POST](https://img.shields.io/badge/POST-6A60AA?style=flat-square) | `/programas`, `/historias`, `/indicadores` | token | Crear |
| ![PUT](https://img.shields.io/badge/PUT-FFC30D?style=flat-square&labelColor=555) ![DELETE](https://img.shields.io/badge/DELETE-D81B60?style=flat-square) | `/programas/{id}`, `/historias/{id}`, `/indicadores/{id}` | token | Editar y eliminar |

### ms-vinculacion · puerto 9092

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| ![POST](https://img.shields.io/badge/POST-6A60AA?style=flat-square) | `/solicitudes` | público | Crea una solicitud y devuelve su código de seguimiento |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/solicitudes/seguimiento/{codigo}` | público | Estado de una solicitud (sin datos personales) |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/tipos-colaboracion` | público | Tipos de colaboración |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/solicitudes` (filtro opcional `?estado=`), `/solicitudes/{id}`, `/solicitudes/{id}/historial` | token | Consulta para el gestor |
| ![PATCH](https://img.shields.io/badge/PATCH-FF8A3D?style=flat-square) | `/solicitudes/{id}/estado` | token | Cambia el estado y deja el historial |
| ![DELETE](https://img.shields.io/badge/DELETE-D81B60?style=flat-square) | `/solicitudes/{id}` | token | Elimina la solicitud y su historial |

### ms-catalogo · puerto 9094

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/productos`, `/productos/{id}`, `/categorias`, `/categorias/{id}` | público | Catálogo |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/productos/{id}/exists` | público | Lo usa ms-vinculacion |
| ![GET](https://img.shields.io/badge/GET-00A878?style=flat-square) | `/productos/admin` | token | Lista completa del catálogo |
| ![POST](https://img.shields.io/badge/POST-6A60AA?style=flat-square) | `/productos`, `/categorias` | token | Crear |
| ![PUT](https://img.shields.io/badge/PUT-FFC30D?style=flat-square&labelColor=555) ![DELETE](https://img.shields.io/badge/DELETE-D81B60?style=flat-square) | `/productos/{id}`, `/categorias/{id}` | token | Editar y eliminar |

## Tecnologías usadas

<p>
  <img src="https://img.shields.io/badge/Java%2017-6A60AA?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-00A878?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Maven-FF5FA2?style=flat-square&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/MySQL-FFC30D?style=flat-square&logo=mysql&logoColor=black" alt="MySQL">
  <img src="https://img.shields.io/badge/JWT-6A60AA?style=flat-square&logo=jsonwebtokens&logoColor=white" alt="JWT">
  <img src="https://img.shields.io/badge/Swagger-00A878?style=flat-square&logo=swagger&logoColor=white" alt="Swagger">
  <img src="https://img.shields.io/badge/JUnit%20%2B%20Mockito-FF5FA2?style=flat-square&logo=junit5&logoColor=white" alt="JUnit y Mockito">
</p>

## Requisitos

- **JDK 17** o superior y la variable `JAVA_HOME` apuntando a él (el proyecto está configurado para Java 17; también corre con Java 21).
- **MySQL** corriendo en `localhost:3306` (por ejemplo con XAMPP), usuario `root` sin clave. Si el tuyo es distinto, cambia el `application.properties` de cada servicio.
- **VS Code** con *Extension Pack for Java* y *Spring Boot Extension Pack*.

Cada carpeta es un proyecto Maven independiente (incluye `mvnw`, no hace falta instalar Maven). Las bases de datos y los datos de ejemplo se crean solos al iniciar.

## Cómo ejecutar el backend

1. Abrir una terminal en la carpeta de cada servicio y ejecutar:

```bash
.\mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run            # Linux / Mac
```

2. Orden recomendado: `ms-admin`, `ms-contenidos`, `ms-catalogo` y por último `ms-vinculacion` (este consulta a `ms-contenidos` y `ms-catalogo` para comprobar que un programa o producto existe).
3. Probar en Swagger (`http://localhost:<puerto>/doc/swagger-ui.html`): primero `POST /auth/login` en ms-admin, copiar el token y pegarlo en el botón **Authorize** de cada servicio (sin la palabra `Bearer`).

**Cuentas de prueba (ficticias, las crea `ms-admin` al iniciar):** `gestor@ejemplo.cl` / `Gestor2026` (rol GESTOR) y `admin@ejemplo.cl` / `Admin2026` (rol ADMIN).

> [!WARNING]
> Si cambias de versión del proyecto y un servicio falla al iniciar por columnas antiguas, borra su base de datos en phpMyAdmin (por ejemplo `bd_contenidos`) y vuelve a ejecutarlo: se crea de nuevo sola.

## Uso desde la app Android

- El **login** de los gestores se hace en `ms-admin` (`POST /auth/login`) y devuelve un token JWT. Solo `ms-admin` firma los tokens; cada servicio los valida por su cuenta.
- Las **solicitudes** y los mensajes de contacto se envían a `ms-vinculacion` (`POST /solicitudes`, público) y se consultan con su código en `GET /solicitudes/seguimiento/{codigo}`.
- Programas, historias e indicadores se leen de `ms-contenidos`, y productos y categorías de `ms-catalogo` (`GET` públicos).
- El emulador de Android llama al computador con la dirección `10.0.2.2` (por ejemplo `http://10.0.2.2:9092`).

## Pruebas unitarias

Para ejecutar las pruebas de un servicio, desde su carpeta:

```bash
.\mvnw.cmd test
```

Cada servicio tiene además su propio `README.md` con los endpoints y cómo probarlos.

---

<p align="center">
  <sub>Proyecto académico · DSY1105 · Duoc UC · Todos los datos son ficticios</sub>
</p>
