import com.mongodb.client.*;
import org.bson.Document;
import java.util.Scanner;

public class App {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> colAlumnos;
    private static MongoCollection<Document> colMatriculas;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        mongoClient = MongoClients.create("mongodb+srv://admin:Usuario.25@mongodb.7yapws5.mongodb.net/");
        database = mongoClient.getDatabase("EscuelaDB");
        colAlumnos = database.getCollection("alumnos");
        colMatriculas = database.getCollection("matriculas");

        Funciones.resetearYcargarDatos(colAlumnos, colMatriculas);

        mostrarMenu();

        mongoClient.close();
    }

    public static void mostrarMenu() {
        int opcion = 0;
        do {
            System.out.println("\n===== GESTIÓN MONGODB ESCUELA =====");
            System.out.println("1. Listar todos los Alumnos");
            System.out.println("2. Buscar Alumno por Nombre");
            System.out.println("3. Ver Matrículas Detalladas (Relación)");
            System.out.println("4. Actualizar Alumno");
            System.out.println("5. Eliminar Alumno");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1: Funciones.listarAlumnos(colAlumnos); break;
                case 2: Funciones.buscarAlumno(colAlumnos, sc); break;
                case 3: Funciones.listarRelacion(colAlumnos, colMatriculas); break;
                case 4: Funciones.editarAlumno(colAlumnos, sc); break;
                case 5: Funciones.eliminarAlumno(colAlumnos, sc); break;
                case 6: System.out.println("¡Adiós!"); break;
                default: System.out.println("Opción no válida.");
            }
        } while (opcion != 5);
    }
}