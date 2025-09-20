import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    // Clase auxiliar para Vendedor
    static class Vendedor {
        String tipoDoc;
        String numDoc;
        String nombres;
        String apellidos;
        double totalVentas = 0;

        public String getNombreCompleto() {
            return nombres + " " + apellidos;
        }
    }

    // Clase auxiliar para Producto
    static class Producto {
        String id;
        String nombre;
        double precio;
        int cantidadVendida = 0;

        public double getTotalVendido() {
            return cantidadVendida * precio;
        }
    }

    public static void main(String[] args) {
        try {
            // 📌 Rutas de los archivos (ajustadas a tu proyecto)
            String carpetaDatos = "./"; // raíz del proyecto
            String archivoVendedores = carpetaDatos + "salesmenInfo.txt";
            String archivoProductos = carpetaDatos + "productos.txt";

            // Cargar vendedores y productos
            Map<String, Vendedor> vendedores = cargarVendedores(archivoVendedores);
            Map<String, Producto> productos = cargarProductos(archivoProductos);

            // Procesar archivos de ventas (todos los que empiezan por "ventas_")
            Files.list(Paths.get(carpetaDatos))
                    .filter(path -> path.getFileName().toString().startsWith("ventas_"))
                    .forEach(path -> procesarVentas(path.toFile(), vendedores, productos));

            // Generar reportes
            generarReporteVendedores(vendedores, carpetaDatos + "ReporteVendedores.csv");
            generarReporteProductos(productos, carpetaDatos + "ReporteProductos.csv");

            System.out.println("✅ Reportes generados exitosamente.");

        } catch (Exception e) {
            System.err.println("❌ Error al ejecutar el programa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 📌 Cargar archivo de vendedores
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
            }s
        }
        return vendedores;
    }

    // 📌 Cargar archivo de productos
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

    // 📌 Procesar ventas de un archivo
    private static void procesarVentas(File archivo, Map<String, Vendedor> vendedores, Map<String, Producto> productos) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String vendedorLinea = br.readLine(); // primera línea = vendedor
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

    // 📌 Generar reporte de vendedores
    private static void generarReporteVendedores(Map<String, Vendedor> vendedores, String salida) throws IOException {
        List<Vendedor> lista = new ArrayList<>(vendedores.values());
        lista.sort((a, b) -> Double.compare(b.totalVentas, a.totalVentas));

        try (PrintWriter pw = new PrintWriter(new FileWriter(salida))) {
            for (Vendedor v : lista) {
                pw.println(v.getNombreCompleto() + ";" + v.totalVentas);
            }
        }
    }

    // 📌 Generar reporte de productos
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
