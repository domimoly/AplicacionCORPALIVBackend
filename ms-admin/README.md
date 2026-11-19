# ms-admin

Microservicio de administración de CORPALIV: **login de gestores (JWT), usuarios y roles**. Puerto **9093** · Base de datos MySQL `bd_admin`.
Swagger (documentación interactiva): http://localhost:9093/doc/swagger-ui.html

Es el **único** microservicio que *crea* (firma) tokens JWT. Los demás (`ms-contenidos`, `ms-catalogo`, `ms-vinculacion`) solo los *validan* con la misma clave secreta.

## Arquitectura (por capas)
```
cl.corpaliv.admin
├── model/        Entidades (tablas): Rol, Usuario
├── dto/          Datos que entran y salen de la API: LoginRequest, LoginResponse, UsuarioRequest, UsuarioResponse
├── repository/   Acceso a la base de datos (Spring Data JPA)
├── service/      AuthService (login), JwtService (crea el token), UsuarioService, RolService
├── controller/   Endpoints REST
├── exception/    Manejo de errores (404, 400, 401, 500) en JSON
├── config/       Seguridad (JWT, BCrypt) y Swagger
└── DataLoader    Roles y usuarios de ejemplo (solo si la BD está vacía)
```

## Endpoints
`🔓` = público · `🔒 ADMIN` = requiere token de un usuario con rol ADMIN (`Authorization: Bearer <token>`)

| Método y ruta | Acceso | Qué hace |
|---|---|---|
| `POST /auth/login` | 🔓 | Recibe `{correo, clave}`. Devuelve el token JWT o `401` |
| `GET /usuarios` | 🔒 ADMIN | Lista los usuarios (sin clave) |
| `GET /usuarios/{id}` | 🔒 ADMIN | Un usuario |
| `POST /usuarios` | 🔒 ADMIN | Crea un usuario (`201`) |
| `PUT /usuarios/{id}` | 🔒 ADMIN | Modifica (si no envías clave, se mantiene la actual) |
| `DELETE /usuarios/{id}` | 🔒 ADMIN | Elimina (`204`) |
| `GET /roles` | 🔒 ADMIN | Lista los roles (GESTOR, ADMIN) |

Códigos: `200` ok · `201` creado · `204` eliminado · `400` datos inválidos · `401` login incorrecto o falta token · `403` token sin permiso (no es ADMIN) · `404` no existe.

## Usuarios de ejemplo (FICTICIOS, solo para probar)
| Correo | Clave | Rol |
|---|---|---|
| `gestor@ejemplo.cl` | `Gestor2026` | GESTOR (el mismo gestor de demostración de la app) |
| `admin@ejemplo.cl` | `Admin2026` | ADMIN |

## Cómo obtener un token y usarlo
1. Login (PowerShell, usa `curl.exe` para no confundirlo con el alias de PowerShell):
```
curl.exe -X POST http://localhost:9093/auth/login -H "Content-Type: application/json" -d "{\"correo\":\"gestor@ejemplo.cl\",\"clave\":\"Gestor2026\"}"
```
Responde algo así: `{"token":"eyJ...","tipo":"Bearer","expiraEnSegundos":28800,"nombre":"...","correo":"...","rol":"GESTOR"}`

2. Usar el token en un endpoint protegido de otro microservicio, por ejemplo crear un programa en `ms-contenidos`:
```
curl.exe http://localhost:9091/programas/admin -H "Authorization: Bearer <pega aquí el token>"
```
3. En **Swagger** (de cualquier microservicio): botón **Authorize** → pega el token (sin escribir `Bearer`).

## Reglas de negocio
- Las claves se guardan con **hash BCrypt** (nunca en texto) y la API **nunca devuelve** la clave ni el hash.
- El login responde siempre `Correo o clave incorrectos` (si el correo no existe, si la clave es mala o si el usuario está desactivado), para no dar pistas.
- El correo es único (se guarda en minúsculas y sin espacios). El rol debe existir.
- El token dura 8 horas (`jwt.expiracion-horas`) y lleva: `sub` (correo), `rol`, `nombre`.
- Solo un ADMIN puede administrar usuarios y roles; un GESTOR puede hacer login y gestionar contenidos y solicitudes.

## Seguridad
La clave con la que se firman los tokens es `jwt.secret` (en `application.properties`, o la variable de entorno `SECRET`).
Debe ser **idéntica en todos los microservicios**. Las claves y correos de ejemplo son solo para desarrollo.
