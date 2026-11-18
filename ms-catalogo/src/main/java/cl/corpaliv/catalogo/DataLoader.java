package cl.corpaliv.catalogo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.corpaliv.catalogo.model.Categoria;
import cl.corpaliv.catalogo.model.ProductoServicio;
import cl.corpaliv.catalogo.service.CategoriaService;
import cl.corpaliv.catalogo.service.ProductoServicioService;

// DATOS DE EJEMPLO: se cargan automáticamente al iniciar el microservicio, pero SOLO si la base de datos está vacía
// (así no se duplican cada vez que se reinicia).
// EXPLICAR: CommandLineRunner es una interfaz de Spring: su método run() se ejecuta una vez, justo después de arrancar.
// Los textos están basados en la información pública de Alba Lab; los precios son REFERENCIALES de ejemplo.
// Se usa el servicio (no el repositorio) para que se apliquen las mismas reglas que en la API.
@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaService categoriaService;
    private final ProductoServicioService productoService;

    public DataLoader(CategoriaService categoriaService, ProductoServicioService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @Override
    public void run(String... args) {
        // Si ya hay categorías, la base de datos ya tiene datos: no se hace nada
        if (!categoriaService.listar().isEmpty()) {
            return;
        }

        // ---------- Categorías (tipo PRODUCTO o SERVICIO) ----------
        Categoria bienestar = crearCategoria("Bienestar", "Productos para el bienestar", "PRODUCTO");
        Categoria otros = crearCategoria("Otros", "Otros productos del catálogo", "PRODUCTO");
        Categoria servicios = crearCategoria("Servicios", "Servicios para empresas y organizaciones", "SERVICIO");

        // ---------- Productos y servicios (precios REFERENCIALES de ejemplo) ----------
        crearProducto(bienestar, "Difusor de aroma 100 ml", "Ambienta tu espacio con aromas suaves, hecho a mano.", "$7.500");
        crearProducto(bienestar, "Difusor de aroma 250 ml", "Formato grande para hogares y oficinas.", "$12.000");
        crearProducto(bienestar, "Difusor de varilla", "Aroma continuo y decoración en un solo producto.", "$17.000");
        crearProducto(bienestar, "Guatero de semillas grande", "Calor natural para relajar, relleno con semillas.", "$11.990");
        crearProducto(otros, "Farol de luz", "Luz cálida para dar un toque especial a cualquier rincón.", "$11.900");
        crearProducto(otros, "Bolsa Alba Lab", "Bolsa reutilizable con el sello de Alba Lab.", "$4.000");
        crearProducto(servicios, "Capacitación en inclusión", "Charla o taller para equipos de empresas y organizaciones.", "A cotizar");
        crearProducto(servicios, "Experiencia de concientización", "Actividad práctica para comprender la discapacidad múltiple.", "A cotizar");
        crearProducto(servicios, "Evaluación de accesibilidad", "Revisión de espacios y procesos para que sean accesibles.", "A cotizar");
    }

    // ---- Métodos auxiliares: cada uno arma un objeto con setters y lo guarda usando el servicio ----

    private Categoria crearCategoria(String nombre, String descripcion, String tipo) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setTipo(tipo);
        return categoriaService.guardar(categoria);
    }

    private void crearProducto(Categoria categoria, String nombre, String descripcion, String precio) {
        ProductoServicio producto = new ProductoServicio();
        producto.setCategoriaId(categoria.getId());
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecioReferencial(precio);
        producto.setModalidadConsulta("Escribir desde la app");
        producto.setDisponible(true);
        producto.setPublicado(true);
        productoService.guardar(producto);
    }
}
