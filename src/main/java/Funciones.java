import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Funciones {

    // MÉTODO 1: Listado Estructurado (Punto 4 del enunciado)
    public static void listarAlumnos(MongoCollection<Document> colAlumnos) {
        System.out.println("\n--- LISTADO DE ALUMNOS ---");
        for (Document doc : colAlumnos.find()) {
            System.out.println("ALUMNO: " + doc.getString("nombre"));
            System.out.println("   Edad: " + doc.getInteger("edad"));
            System.out.println("   Becas: " + (doc.getBoolean("becado") ? "Sí" : "No"));
            System.out.println("   Asignaturas: " + doc.getList("asignaturas", String.class));
            System.out.println("--------------------------");
        }
    }

    // MÉTODO 2: Búsqueda Filtrada (Punto 3 del enunciado)
    public static void buscarAlumno(MongoCollection<Document> colAlumnos, Scanner sc) {
        System.out.print("Introduce el nombre a buscar: ");
        String nombre = sc.nextLine();
        Document encontrado = colAlumnos.find(Filters.eq("nombre", nombre)).first();

        if (encontrado != null) {

            System.out.println("====================================");
            System.out.println(" ID UNICO:    " + encontrado.getObjectId("_id"));
            System.out.println(" NOMBRE:      " + encontrado.getString("nombre"));
            System.out.println(" EDAD:        " + encontrado.getInteger("edad") + " años");
            System.out.println(" ¿ES BECADO?: " + (encontrado.getBoolean("becado") ? "SÍ" : "NO"));

            // Para la lista de asignaturas
            List<String> materias = encontrado.getList("asignaturas", String.class);
            System.out.println(" ASIGNATURAS: " + String.join(", ", materias));
            System.out.println("====================================");
        } else {
            System.out.println("No se encontró ningún alumno con ese nombre.");
        }
    }

    // MÉTODO 3: Información combinada (Relación lógica)
    public static void listarRelacion(MongoCollection<Document> colAlumnos, MongoCollection<Document> colMatriculas) {
        System.out.println("\n--- MATRÍCULAS Y SUS ALUMNOS ---");
        for (Document mat : colMatriculas.find()) {
            ObjectId idAlu = mat.getObjectId("alumno_id");
            Document alu = colAlumnos.find(Filters.eq("_id", idAlu)).first();

            System.out.println("CURSO: " + mat.getString("curso"));
            System.out.println("   Precio: " + mat.getDouble("precio") + "€");
            if (alu != null) {
                System.out.println("   Estudiante: " + alu.getString("nombre"));
            }
            System.out.println("..........................");
        }
    }

    //MÉTODO 4: Actualizar campos
    public static void editarAlumno(MongoCollection<Document> colAlumnos, Scanner sc) {
        System.out.println("\n--- EDITAR ALUMNO ---");
        System.out.print("Introduce el nombre actual del alumno: ");
        String nombreBuscar = sc.nextLine();

        // Buscamos si existe
        Document alumno = colAlumnos.find(Filters.eq("nombre", nombreBuscar)).first();

        if (alumno != null) {
            boolean seguirEditando = true;

            do {
                System.out.println("\nModificando a: " + nombreBuscar);
                System.out.println("1. Nombre");
                System.out.println("2. Edad");
                System.out.println("3. Estado de Beca");
                System.out.println("4. Asignaturas");
                System.out.println("5. Finalizar edición");
                System.out.print("Elige qué quieres cambiar: ");

                int opcion = sc.nextInt();
                sc.nextLine();

                Document cambio = new Document();

                switch (opcion) {
                    case 1:
                        System.out.print("Nuevo nombre: ");
                        String nuevoNombre = sc.nextLine();
                        cambio.append("nombre", nuevoNombre);
                        colAlumnos.updateOne(Filters.eq("nombre", nombreBuscar), new Document("$set", cambio));
                        nombreBuscar = nuevoNombre;
                        System.out.println(">>> Nombre actualizado.");
                        break;
                    case 2:
                        System.out.print("Nueva edad: ");
                        cambio.append("edad", sc.nextInt());
                        sc.nextLine();
                        colAlumnos.updateOne(Filters.eq("nombre", nombreBuscar), new Document("$set", cambio));
                        System.out.println(">>> Edad actualizada.");
                        break;
                    case 3:
                        System.out.print("¿Está becado? (true/false): ");
                        cambio.append("becado", sc.nextBoolean());
                        sc.nextLine();
                        colAlumnos.updateOne(Filters.eq("nombre", nombreBuscar), new Document("$set", cambio));
                        System.out.println(">>> Beca actualizada.");
                        break;
                    case 4:
                        System.out.print("Nuevas asignaturas (separadas por comas): ");
                        String listaInput = sc.nextLine();
                        java.util.List<String> nuevaLista = java.util.Arrays.asList(listaInput.split(","));
                        cambio.append("asignaturas", nuevaLista);
                        colAlumnos.updateOne(Filters.eq("nombre", nombreBuscar), new Document("$set", cambio));
                        System.out.println(">>> Asignaturas actualizadas.");
                        break;
                    case 5:
                        seguirEditando = false;
                        System.out.println(">>> Saliendo del modo edición...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }

            } while (seguirEditando);

        } else {
            System.out.println(">>> No se encontró al alumno.");
        }
    }

    public static void eliminarAlumno(MongoCollection<Document> colAlumnos, Scanner sc) {
        System.out.print("Nombre del alumno a borrar: ");
        String nombre = sc.nextLine();

        colAlumnos.deleteOne(Filters.eq("nombre", nombre));

        System.out.println(">>> Operación finalizada. Volviendo al menú...");
    }


    public static void resetearYcargarDatos(MongoCollection<Document> colAlumnos, MongoCollection<Document> colMatriculas) {
        // 1. ELIMINAR DATOS EXISTENTES (Limpieza)
        colAlumnos.deleteMany(new Document());
        colMatriculas.deleteMany(new Document());
        System.out.println(">>> Base de datos limpiada (registros anteriores borrados).");

        // 2. CREAR DATOS DE PRUEBA

        // Alumno 1
        Document alu1 = new Document("nombre", "Ana Garcia")
                .append("edad", 21)
                .append("becado", true)
                .append("asignaturas", Arrays.asList("Java", "Sistemas", "Redes"));
        colAlumnos.insertOne(alu1);

        // Alumno 2
        Document alu2 = new Document("nombre", "Marcos Lopez")
                .append("edad", 24)
                .append("becado", false)
                .append("asignaturas", Arrays.asList("Bases de Datos", "Python"));
        colAlumnos.insertOne(alu2);

        // 3. CREAR MATRÍCULAS RELACIONADAS
        // Usamos los IDs que MongoDB generó automáticamente al insertar los alumnos

        Document mat1 = new Document("curso", "Desarrollo de Aplicaciones Multiplataforma")
                .append("precio", 450.0)
                .append("alumno_id", alu1.getObjectId("_id")); // Relación con Ana

        Document mat2 = new Document("curso", "Curso de Especialización en IA")
                .append("precio", 299.99)
                .append("alumno_id", alu2.getObjectId("_id")); // Relación con Marcos

        colMatriculas.insertMany(Arrays.asList(mat1, mat2));

        System.out.println(">>> Nuevos datos de prueba cargados correctamente.");
    }
}