import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// Демонстрация работы с файлами + обработка исключений.
// В Main достаточно добавить одну строку:  FileIoDemo.run();
public class FileIoDemo {

    public static void run() {
        System.out.println("\n===== 7. FILE I/O AND EXCEPTION HANDLING =====");
        CourseFileManager manager = new CourseFileManager();

        // --- Сценарий 1: всё хорошо ---
        System.out.println("\n--- Scenario 1: valid file ---");
        try {
            Map<String, Course> loaded = manager.loadCourses("courses.txt");
            System.out.println("Loaded courses: " + loaded.size());

            List<Course> sorted = manager.sortCourses(loaded, CourseFileManager.BY_FEE);
            manager.saveCourses(sorted, "sorted_courses.txt");
            manager.saveReport(sorted, "report.txt");

            System.out.println("Sorted by fee and saved to sorted_courses.txt");
            for (Course c : sorted) {
                System.out.println("  " + c.getCourseName() + " -> $" + c.calculateTuitionFee());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found -> " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error while reading/writing file: " + e.getMessage());
        } finally {
            System.out.println("[finally] File operations (scenario 1) finished.");
        }

        // --- Сценарий 2: файла не существует ---
        System.out.println("\n--- Scenario 2: missing file ---");
        try {
            manager.loadCourses("no_such_file.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found -> " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error while reading file: " + e.getMessage());
        } finally {
            System.out.println("[finally] File operations (scenario 2) finished.");
        }

        // --- Сценарий 3: в файле есть плохие строки ---
        System.out.println("\n--- Scenario 3: file with malformed lines ---");
        try {
            Map<String, Course> loaded = manager.loadCourses("courses_bad.txt");
            System.out.println("Loaded only valid courses: " + loaded.size());
            List<Course> sorted = manager.sortCourses(loaded, CourseFileManager.BY_NAME);
            manager.saveCourses(sorted, "sorted_bad.txt");
        } catch (IOException e) {
            System.out.println("Error while reading/writing file: " + e.getMessage());
        } finally {
            System.out.println("[finally] File operations (scenario 3) finished.");
        }
    }
}