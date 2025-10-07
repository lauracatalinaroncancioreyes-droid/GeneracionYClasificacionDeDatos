import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Clase principal del proyecto "Generación y Clasificación de Datos".
 * <p>
 * Esta clase lee los archivos de productos, vendedores y ventas, procesa la información 
 * y genera reportes consolidados en formato CSV con:
 * <ul>
 *   <li>Ventas totales por vendedor</li>
 *   <li>Productos más vendidos</li>
 * </ul>
 * 
 * @author Catalina 
 * @version 3.0
 */
public class Main {

    /** Representa un vendedor con su información básica y total de ventas acumulado. */
    static class Vendedor {
        String tipoDoc;
        String numDoc;
        String nombres;
        String apellidos;
        double totalVentas = 0;

        /** @return nombre completo del vendedor */
        public String getNombreCompleto() {
            return nombres + " " + apellidos;
        }
    }

    /** Representa un producto con su información básica y total de unidades vendidas. */
    static class Producto {
        String id;
        String nombre;
        double precio;
        int cantidadVendida = 0;

        /** @return valor total vendido del producto */
        public double getTotalVendido() {
            return cantidadVendida * precio;
        }
    }

    /**
     * Método principal del programa.
     * <p>
     * Carga los archivos de entrada (productos, vendedores y ventas),
     * procesa los datos y genera los reportes solicitados.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        try {
            // 📂 Directorio base del proyecto
            String carpetaDatos = "./";
            String archivoVendedores = carpetaDatos + "salesmenInfo.txt";
            String archivoProductos = carpetaDatos + "productos.txt";

            // Cargar información de vendedores y productos
            Map<String, Vendedor> vendedores = cargarVendedores(archivoVendedores);
            Map<String, Producto> productos = cargarProductos(archivoProductos);

            // Procesar archivos de ventas (ventas_vendedorX.txt)
            Files.list(Paths.get(carpetaDatos))
                    .filter(path -> path.getFileName().toString().startsWith("ventas_"))
                    .forEach(path -> procesarVentas(path.toFile(), vendedores, productos));

            // Generar reportes de salida
            generarReporteVendedores(vendedores, carpetaDatos + "ReporteVendedores.csv");
            generarReporteProductos(productos, carpetaDatos + "ReporteProductos.csv");

            System.out.println("✅ Reportes generados exitosamente.");

        } catch (Exception e) {
            System.err.println("❌ Error al ejecutar el programa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga la información de los vendedores desde un archivo.
     *
     * @param ruta ruta del archivo de vendedores
     * @return mapa de vendedores indexado por tipo y número de documento
     * @throws IOException si ocurre un error al leer el archivo
     */
    private static Map<String, Vendedor> cargarVendedores(String ruta) throws IOException {
        Map<String, Vendedor> vendedores = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 4) {
                    Vendedor v = new Vendedor();
                    v.tipoDoc = partes[0];
                    v.numDoc = partes[1];
                    v.nombres = partes[2];
                    v.apellidos = partes[3];
                    vendedores.put(v.tipoDoc + ";" + v.numDoc, v);
                }
            }
        }
        return vendedores;
    }

    /**
     * Carga la información de los productos desde un archivo.
     *
     * @param ruta ruta del archivo de productos
     * @return mapa de productos indexado por ID de producto
     * @throws IOException si ocurre un error al leer el archivo
     */
    private static Map<String, Producto> cargarProductos(String ruta) throws IOException {
        Map<String, Producto> productos = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 3) {
                    Producto p = new Producto();
                    p.id = partes[0];
                    p.nombre = partes[1];
                    p.precio = Double.parseDouble(partes[2].replace(",", "."));
                    productos.put(p.id, p);
                }
            }
        }
        return productos;
    }

    /**
     * Procesa un archivo de ventas de un vendedor específico.
     * <p>
     * Cada archivo comienza con la información del vendedor, seguida de las líneas
     * con ID de producto y cantidad vendida.
     *
     * @param archivo    archivo de ventas
     * @param vendedores mapa de vendedores
     * @param productos  mapa de productos
     */
    private static void procesarVentas(File archivo, Map<String, Vendedor> vendedores, Map<String, Producto> productos) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String vendedorLinea = br.readLine(); // primera línea = datos del vendedor
            if (vendedorLinea == null) return;

            String[] datosVendedor = vendedorLinea.split(";");
            String keyVendedor = datosVendedor[0] + ";" + datosVendedor[1];
            Vendedor vendedor = vendedores.get(keyVendedor);

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 2) {
                    String idProducto = partes[0];
                    int cantidad = Integer.parseInt(partes[1]);

                    Producto producto = productos.get(idProducto);
                    if (producto != null) {
                        producto.cantidadVendida += cantidad;
                        double subtotal = cantidad * producto.precio;
                        if (vendedor != null) {
                            vendedor.totalVentas += subtotal;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error procesando archivo " + archivo.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Genera un reporte con la información de los vendedores y sus ventas totales.
     *
     * @param vendedores mapa de vendedores
     * @param salida     ruta del archivo CSV de salida
     * @throws IOException si ocurre un error al escribir el archivo
     */
    private static void generarReporteVendedores(Map<String, Vendedor> vendedores, String salida) throws IOException {
        List<Vendedor> lista = new ArrayList<>(vendedores.values());
        lista.sort((a, b) -> Double.compare(b.totalVentas, a.totalVentas));

        try (PrintWriter pw = new PrintWriter(new FileWriter(salida))) {
            for (Vendedor v : lista) {
                pw.println(v.getNombreCompleto() + ";" + v.totalVentas);
            }
        }
    }

    /**
     * Genera un reporte con los productos más vendidos.
     *
     * @param productos mapa de productos
     * @param salida    ruta del archivo CSV de salida
     * @throws IOException si ocurre un error al escribir el archivo
     */
    private static void generarReporteProductos(Map<String, Producto> productos, String salida) throws IOException {
        List<Producto> lista = new ArrayList<>(productos.values());
        lista.sort((a, b) -> Integer.compare(b.cantidadVendida, a.cantidadVendida));

        try (PrintWriter pw = new PrintWriter(new FileWriter(salida))) {
            for (Producto p : lista) {
                pw.println(p.nombre + ";" + p.cantidadVendida + ";" + p.precio);
            }
        }
    }
}
