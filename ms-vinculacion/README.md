# ms-vinculacion

Microservicio de vinculación de CORPALIV: **solicitudes de colaboración, estados e historial**. Puerto **9092** · Base de datos MySQL `bd_vinculacion`.
Swagger (documentación interactiva): http://localhost:9092/doc/swagger-ui.html

Es el microservicio que usa la parte de **Colaborar** de la app: una persona o empresa envía una solicitud, recibe un **código de seguimiento** y
puede consultar en qué estado va. El gestor de vinculación (con token de `ms-admin`) la revisa, cambia su estado y deja un mensaje.

## Arquitectura (por capas)
```
cl.corpaliv.vinculacion
├── model/        Entidades (tablas): SolicitudColaboracion, TipoColaboracion, HistorialSolicitud (+ EstadoSolicitud)
├── dto/          Datos que entran y salen de la API: SolicitudRequest, SolicitudResponse, SeguimientoResponse, CambioEstadoRequest, HistorialResponse
├── repository/   Acceso a la base de datos (Spring Data JPA)
├── service/      SolicitudService (reglas de negocio), GeneradorCodigo
├── client/       ContenidosClient y CatalogoClient: llaman por HTTP a ms-contenidos y ms-catalogo
├── controller/   Endpoints REST
├── exception/    Manejo de errores (404, 400, 503, 500) en JSON
├── config/       Seguridad (JWT) y Swagger
└── DataLoader    Tipos de colaboración y 3 solicitudes de ejemplo (solo si la BD está vacía)
```

## Endpoints
`🔓` = público (lo usa cualquier visitante de la app) · `🔒` = requiere token JWT de un gestor (`Authorization: Bearer <token>`)

| Método y ruta | Acceso | Qué hace |
|---|---|---|
| `POST /solicitudes` | 🔓 | Envía una solicitud. Responde `201` con el **código de seguimiento** y el estado |
| `GET /solicitudes/seguimiento/{codigo}` | 🔓 | Consulta el estado con el código (**sin datos personales**) |
| `GET /tipos-colaboracion` | 🔓 | Tipos para el formulario: DONACION, ALIANZA_RSE, VOLUNTARIADO, COMPRA, CAPACITACION, OTRO |
| `GET /solicitudes` | 🔒 | Lista completa (más nuevas primero). Filtro opcional: `?estado=RECIBIDA` |
| `GET /solicitudes/{id}` | 🔒 | Una solicitud completa |
| `GET /solicitudes/{id}/historial` | 🔒 | Cambios de estado: quién, cuándo y con qué mensaje |
| `PATCH /solicitudes/{id}/estado` | 🔒 | Cambia el estado: `{"estado":"EN_REVISION","observacion":"..."}` |
| `DELETE /solicitudes/{id}` | 🔒 | Elimina la solicitud y su historial (`204`) |

Códigos: `200` ok · `201` creada · `204` eliminada · `400` datos inválidos · `401` falta token · `404` no existe · `503` otro microservicio no responde.

### Ejemplo de solicitud (POST /solicitudes)
```json
{
  "nombre": "Empresa Ejemplo S.A.",
  "medioContacto": "contacto@empresa-ejemplo.cl",
  "tipo": "ALIANZA_RSE",
  "mensaje": "Queremos conocer cómo sumarnos.",
  "monto": null,
  "fechaContacto": "2026-12-01",
  "programaId": 3,
  "productoId": null,
  "prioritario": false,
  "aceptaPrivacidad": true
}
```
Obligatorios: `nombre`, `medioContacto`, `tipo` y `aceptaPrivacidad` en `true`. Los demás son opcionales.

## Estados
`RECIBIDA` → `EN_REVISION` → `CONTACTADO` → `CONCRETADO` o `DESCARTADO`. Son los mismos códigos de la app (`EstadoSolicitud.kt`).

## Reglas de negocio
- Una solicitud nueva siempre parte en **RECIBIDA** y recibe un código aleatorio tipo `COL-7KQ4M2XA` (imposible de adivinar contando 1, 2, 3...).
- El **tipo** debe existir. Si se indica `programaId`, debe existir en **ms-contenidos**; si se indica `productoId`, en **ms-catalogo**
  (este servicio los consulta por HTTP). Si el otro servicio está apagado, responde `503` con un mensaje claro.
- Solo existen los 5 estados válidos. Una solicitud **cerrada** (CONCRETADO o DESCARTADO) **no se puede modificar**.
- Cada cambio de estado queda en el **historial** con el correo del gestor (sale del token).
- La consulta pública **no devuelve** nombre, contacto ni mensaje: son datos personales y solo los ve un gestor con token.
- Todos los datos de ejemplo son **ficticios**.

## Probar rápido
Con el servicio encendido (y MySQL):
```
curl.exe http://localhost:9092/tipos-colaboracion
curl.exe http://localhost:9092/solicitudes                       ->  401 (pide token)
curl.exe -X POST http://localhost:9092/solicitudes -H "Content-Type: application/json" -d "{\"nombre\":\"Prueba\",\"medioContacto\":\"prueba@ejemplo.cl\",\"tipo\":\"OTRO\",\"aceptaPrivacidad\":true}"
```
La respuesta trae el `codigoSeguimiento`; luego: `curl.exe http://localhost:9092/solicitudes/seguimiento/COL-XXXXXXXX`

Para las rutas con token, primero haz login en `ms-admin` (ver `ms-admin/README.md`) y usa el botón **Authorize** de Swagger.
Para probar `programaId` y `productoId` deben estar encendidos `ms-contenidos` (9091) y `ms-catalogo` (9094).
Las direcciones de esos servicios están en `application.properties` (`servicios.contenidos.url` y `servicios.catalogo.url`).

## Seguridad
Los tokens JWT se firman en `ms-admin` y este servicio solo los **valida** con la misma clave (`jwt.secret`, o la variable de entorno `SECRET`).
Pendiente para una versión futura: limitar cuántas solicitudes puede enviar una misma persona (el envío es público).
