import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    public static void main(String[] args) {
        GenerateInfoFiles generator = new GenerateInfoFiles();

        int productsCount = 10;
        int salesmenCount = 5;

        try {
            generator.createProductsFile(productsCount);
            generator.createSalesManInfoFile(salesmenCount);
            generator.createSalesMenFile(salesmenCount, "ventas_vendedor", 10000000);

            System.out.println("Archivos generados exitosamente.");
        } catch (Exception e) {
            System.out.println("Ocurrió un error al generar los archivos:");
            e.printStackTrace();
        }
    }

    /**
     * Crea archivo con información de productos.
     * Formato: IDProducto;NombreProducto;Precio
     */
    public void createProductsFile(int productsCount) throws IOException {
        String fileName = "productos.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= productsCount; i++) {
                String id = "P" + i;
                String nombre = "Producto" + i;
                double precio = 10 + new Random().nextDouble() * 90; // entre 10 y 100
                writer.write(id + ";" + nombre + ";" + String.format("%.2f", precio));
                writer.newLine();
            }
        }
        System.out.println("Archivo generado: " + fileName);
    }

    /**
     * Crea archivo con información de vendedores.
     * Formato: TipoDocumento;NumeroDocumento;Nombre;Apellido
     */
    public void createSalesManInfoFile(int salesmanCount) throws IOException {
        String fileName = "salesmenInfo.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= salesmanCount; i++) {
                String tipoDoc = "CC";
                long numDoc = 10000000 + i;
                String nombre = "Vendedor" + i;
                String apellido = "Apellido" + i;
                writer.write(tipoDoc + ";" + numDoc + ";" + nombre + ";" + apellido);
                writer.newLine();
            }
        }
        System.out.println("Archivo generado: " + fileName);
    }

    /**
     * Crea archivos de ventas para cada vendedor.
     * Cada archivo incluye ventas de hasta 3 productos.
     * Formato: IDProducto;Cantidad
     */
    public void createSalesMenFile(int randomSalesCount, String baseFileName, long idBase) throws IOException {
        Random rand = new Random();

        for (int i = 1; i <= randomSalesCount; i++) {
            String fileName = baseFileName + i + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                // Encabezado con datos del vendedor
                String tipoDoc = "CC";
                long docNumber = idBase + i;
                writer.write(tipoDoc + ";" + docNumber);
                writer.newLine();

                // 3 productos vendidos (puedes ajustar esto)
                for (int j = 1; j <= 3; j++) {
                    int prodId = rand.nextInt(10) + 1; // P1 a P10
                    int qty = rand.nextInt(10) + 1;    // cantidad entre 1 y 10
                    writer.write("P" + prodId + ";" + qty);
                    writer.newLine();
                }
            }
            System.out.println("Archivo generado: " + fileName);
        }
    }
}
