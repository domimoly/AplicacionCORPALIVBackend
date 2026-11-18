package cl.corpaliv.contenidos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.corpaliv.contenidos.model.Categoria;
import cl.corpaliv.contenidos.model.HistoriaImpacto;
import cl.corpaliv.contenidos.model.IndicadorImpacto;
import cl.corpaliv.contenidos.model.Programa;
import cl.corpaliv.contenidos.model.ProductoServicio;
import cl.corpaliv.contenidos.service.CategoriaService;
import cl.corpaliv.contenidos.service.HistoriaImpactoService;
import cl.corpaliv.contenidos.service.IndicadorImpactoService;
import cl.corpaliv.contenidos.service.ProductoServicioService;
import cl.corpaliv.contenidos.service.ProgramaService;

// DATOS DE EJEMPLO: se cargan automáticamente al iniciar el microservicio, pero SOLO si la base de datos está vacía
// (así no se duplican cada vez que se reinicia).
// EXPLICAR: CommandLineRunner es una interfaz de Spring: su método run() se ejecuta una vez, justo después de arrancar.
// Los textos están basados en la información pública de CORPALIV y Alba Lab; las historias y cifras son FICTICIAS
// (el caso prohíbe usar datos personales reales). Se usa el servicio (no el repositorio) para que se apliquen las mismas reglas.
@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaService categoriaService;
    private final ProgramaService programaService;
    private final HistoriaImpactoService historiaService;
    private final IndicadorImpactoService indicadorService;
    private final ProductoServicioService productoService;

    public DataLoader(CategoriaService categoriaService, ProgramaService programaService,
                      HistoriaImpactoService historiaService, IndicadorImpactoService indicadorService,
                      ProductoServicioService productoService) {
        this.categoriaService = categoriaService;
        this.programaService = programaService;
        this.historiaService = historiaService;
        this.indicadorService = indicadorService;
        this.productoService = productoService;
    }

    @Override
    public void run(String... args) {
        // Si ya hay categorías, la base de datos ya tiene datos: no se hace nada
        if (!categoriaService.listar().isEmpty()) {
            return;
        }

        // ---------- Categorías ----------
        crearCategoria("Educación", "Programas de educación", "PROGRAMA");
        crearCategoria("Formación laboral", "Programas de formación para el trabajo", "PROGRAMA");
        crearCategoria("Inclusión laboral", "Programas que conectan con empresas", "PROGRAMA");
        crearCategoria("Servicios a empresas", "Servicios de Alba Lab para organizaciones", "PROGRAMA");
        Categoria bienestar = crearCategoria("Bienestar", "Productos para el bienestar", "PRODUCTO");
        Categoria otros = crearCategoria("Otros", "Otros productos del catálogo", "PRODUCTO");
        Categoria servicios = crearCategoria("Servicios", "Servicios para empresas y organizaciones", "SERVICIO");

        // ---------- Programas ----------
        crearPrograma("Escuela Jan Van Dijk", "Educación para niñas, niños y jóvenes",
                "Escuela especial gratuita para estudiantes con discapacidad múltiple y/o sordoceguera. "
                        + "Niveles desde Medio Menor hasta 6° básico. Equipo de psicología, terapia ocupacional y fonoaudiología. "
                        + "Trabajo cercano con las familias. Autorizada por el Ministerio de Educación.",
                "Educación", "PUBLICADO");
        crearPrograma("Taller Sociolaboral", "Alba Lab · formación para jóvenes y adultos",
                "Espacio para explorar intereses y desarrollar habilidades para el trabajo. "
                        + "Dirigido a jóvenes y adultos con discapacidad múltiple.",
                "Formación laboral", "PUBLICADO");
        crearPrograma("Intermediación Laboral", "Alba Lab · del taller al primer empleo",
                "Acompaña el paso al trabajo y conecta a las personas con empresas. Incluye análisis del puesto de trabajo.",
                "Inclusión laboral", "PUBLICADO");
        crearPrograma("Servicios para empresas", "Capacitación, accesibilidad y más",
                "Capacitaciones en inclusión y discapacidad, experiencias de concientización, análisis de puesto de trabajo, "
                        + "políticas de inclusión laboral, acompañamiento de inserción laboral y evaluación de accesibilidad universal.",
                "Servicios a empresas", "PUBLICADO");
        crearPrograma("Ley de Inclusión Laboral", "Ley 21.015 y donaciones de empresas",
                "Las empresas con 100 o más trabajadores deben contar con al menos un 1% de personas con discapacidad. "
                        + "Si no pueden cumplirlo, existe la alternativa de donar a una fundación habilitada.",
                "Inclusión laboral", "PUBLICADO");
        // Un BORRADOR a propósito: aparece en GET /programas/admin pero NO en GET /programas (para probar la diferencia)
        crearPrograma("Programa piloto de voluntariado", "Borrador en preparación",
                "Programa en preparación. Todavía no se muestra en la app.",
                "Inclusión laboral", "BORRADOR");

        // ---------- Indicadores de impacto (cifras de EJEMPLO) ----------
        crearIndicador("Años de trayectoria", 32.0, "años");
        crearIndicador("Personas en formación", 40.0, "personas");
        crearIndicador("Empresas aliadas", 8.0, "empresas");
        crearIndicador("Productos elaborados", 250.0, "unidades");

        // ---------- Historias de impacto (FICTICIAS y anonimizadas) ----------
        crearHistoria("Un primer día de trabajo", "Un joven del Taller Sociolaboral",
                "Después de meses de práctica en el taller, un joven comenzó a trabajar medio día en una empresa aliada. "
                        + "Hoy realiza sus tareas con apoyo y su equipo celebra cada logro. Historia anonimizada y de ejemplo.");
        crearHistoria("Aprender jugando y comunicando", "Una niña de la Escuela Jan Van Dijk",
                "Con apoyos de comunicación y rutinas pensadas para ella, una niña aprendió a expresar lo que quiere y a "
                        + "compartir con sus compañeros. Su familia participa en cada etapa. Historia anonimizada y de ejemplo.");
        crearHistoria("Una alianza que abre puertas", "Una empresa que se sumó a la red",
                "Una empresa conoció el trabajo de CORPALIV, hizo un análisis de puestos junto al equipo y abrió una "
                        + "oportunidad laboral inclusiva. Historia anonimizada y de ejemplo.");

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

    private void crearPrograma(String nombre, String resumen, String descripcion, String categoria, String estado) {
        Programa programa = new Programa();
        programa.setNombre(nombre);
        programa.setResumen(resumen);
        programa.setDescripcion(descripcion);
        programa.setCategoria(categoria);
        programa.setEstadoPublicacion(estado);
        programaService.guardar(programa);
    }

    private void crearIndicador(String nombre, double valor, String unidad) {
        IndicadorImpacto indicador = new IndicadorImpacto();
        indicador.setNombre(nombre);
        indicador.setValor(valor);
        indicador.setUnidad(unidad);
        indicador.setPeriodo("2026");
        indicador.setFuente("Datos de ejemplo");
        indicador.setPublicado(true);
        indicadorService.guardar(indicador);
    }

    private void crearHistoria(String titulo, String resumen, String contenido) {
        HistoriaImpacto historia = new HistoriaImpacto();
        historia.setTitulo(titulo);
        historia.setResumen(resumen);
        historia.setContenido(contenido);
        historia.setAutorizada(true);
        historia.setAnonimizada(true);
        historia.setEstadoPublicacion("PUBLICADO");
        historiaService.guardar(historia);
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
