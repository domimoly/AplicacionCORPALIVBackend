# ms-contenidos

Microservicio de contenidos institucionales de CORPALIV: programas, historias e indicadores de impacto. Puerto **9091** · Base de datos MySQL `bd_contenidos`.
Swagger (documentación interactiva): http://localhost:9091/doc/swagger-ui.html

## Arquitectura (por capas)
```
cl.corpaliv.contenidos
├── model/        Entidades (tablas): Programa, HistoriaImpacto, IndicadorImpacto
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
| `/programas` | ✅ publicados | ✅ todos | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/historias` | ✅ publicadas | ✅ todas | ✅ | – | ✅ | ✅ | ✅ |
| `/indicadores` | ✅ publicados | ✅ todos | ✅ | – | ✅ | ✅ | ✅ |

Códigos: `200` ok · `201` creado · `204` eliminado · `400` datos inválidos · `401` falta token · `404` no existe.

## Reglas de negocio
- Los visitantes solo ven contenido **PUBLICADO** (`estadoPublicacion` o `publicado`). Los borradores solo se ven con token en `/admin`.
- Una **historia** solo se puede publicar si está **autorizada y anonimizada** (si no, `400`).
- Todos los datos de ejemplo son **ficticios** (no hay datos personales ni fotos de beneficiarios).

## Probar rápido (sin token)
```
curl http://localhost:9091/programas            ->  5 programas publicados
curl http://localhost:9091/programas/admin      ->  401 (pide token)
curl http://localhost:9091/programas/1/exists   ->  true
```
Ejemplo de creación (con token que entregará ms-admin):
```
curl -X POST http://localhost:9091/programas -H "Authorization: Bearer <token>" -H "Content-Type: application/json" ^
  -d "{\"nombre\":\"Nuevo programa\",\"resumen\":\"Resumen\",\"estadoPublicacion\":\"BORRADOR\"}"
```

## Relación con otros microservicios
Los productos, servicios y categorías están en **ms-catalogo** (puerto 9094), no aquí. Cada microservicio tiene su propia base de datos.
`GET /programas/{id}/exists` lo usa ms-vinculacion para comprobar que un programa existe antes de aceptar una solicitud.

## Seguridad
Los tokens JWT se firman en `ms-admin` y este servicio solo los **valida** con la misma clave (`jwt.secret` en `application.properties`,
o la variable de entorno `SECRET`). La clave debe ser idéntica en los tres microservicios.
