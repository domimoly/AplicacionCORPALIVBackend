# ms-catalogo

Microservicio del catálogo de CORPALIV / Alba Lab: **productos, servicios y categorías**. Puerto **9094** · Base de datos MySQL `bd_catalogo`.
Swagger (documentación interactiva): http://localhost:9094/doc/swagger-ui.html

## Arquitectura (por capas)
```
cl.corpaliv.catalogo
├── model/        Entidades (tablas): Categoria, ProductoServicio
├── repository/   Acceso a la base de datos (Spring Data JPA)
├── service/      Lógica y reglas de negocio
├── controller/   Endpoints REST
├── exception/    Manejo de errores (404, 400, 500) en JSON
├── config/       Seguridad (JWT) y Swagger
└── DataLoader    Datos de ejemplo (solo si la BD está vacía)
```

## Endpoints
`🔓` = público (lo usa cualquier visitante de la app) · `🔒` = requiere token JWT de un gestor (`Authorization: Bearer <token>`)

| Recurso | GET lista 🔓 | GET /admin 🔒 | GET /{id} 🔓 | GET /{id}/exists 🔓 | POST 🔒 | PUT /{id} 🔒 | DELETE /{id} 🔒 |
|---|---|---|---|---|---|---|---|
| `/productos` | ✅ publicados | ✅ todos | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/categorias` | ✅ | – | ✅ | – | ✅ | ✅ | ✅ |

Códigos: `200` ok · `201` creado · `204` eliminado · `400` datos inválidos · `401` falta token · `404` no existe.

## Reglas de negocio
- Los visitantes solo ven productos **publicados** (`publicado = true`). Los no publicados solo se ven con token en `/productos/admin`.
- Un **producto** debe pertenecer a una **categoría existente** (si no, `400` "La categoría indicada no existe").
- No hay pagos en línea: la persona consulta y CORPALIV la contacta (campo `modalidadConsulta`). Los precios son referenciales.
- Todos los datos de ejemplo son **ficticios**.

## Relación con otros microservicios
Los programas, historias e indicadores de impacto están en **ms-contenidos** (puerto 9091). Cada microservicio tiene su propia base de datos.
`GET /productos/{id}/exists` lo usa ms-vinculacion para comprobar que un producto existe (por ejemplo, en una solicitud de tipo COMPRA).

## Probar rápido (sin token)
```
curl http://localhost:9094/productos            ->  9 productos publicados
curl http://localhost:9094/categorias           ->  3 categorías
curl http://localhost:9094/productos/admin      ->  401 (pide token)
curl http://localhost:9094/productos/1/exists   ->  true
```
Ejemplo de creación (con token que entregará ms-admin):
```
curl -X POST http://localhost:9094/productos -H "Authorization: Bearer <token>" -H "Content-Type: application/json" ^
  -d "{\"categoriaId\":1,\"nombre\":\"Producto nuevo\",\"publicado\":true}"
```

## Seguridad
Para obtener un token: `POST http://localhost:9093/auth/login` (ver `ms-admin/README.md`). En Swagger usa el botón **Authorize**.
Los tokens JWT se firman en `ms-admin` y este servicio solo los **valida** con la misma clave (`jwt.secret` en `application.properties`,
o la variable de entorno `SECRET`). La clave debe ser idéntica en todos los microservicios.
