import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

// DEPENDENCY: uses Course and Department only as method parameters (no fields)
public class ReportGenerator {

    public void printCourseReport(Course course) {
        System.out.println("=== COURSE REPORT ===");
        System.out.println("Name: " + course.getCourseName() + " [" + course.getCourseType() + "]");
        System.out.println("Fee: $" + course.calculateTuitionFee());
        System.out.println("Enrolled: " + course.getEnrolledStudents() + "/" + course.getMaxStudents());
    }

    public void printDepartmentReport(Department department) {
        System.out.println("=== DEPARTMENT REPORT: " + department.getName() + " ===");
        double total = 0;
        for (Course c : department.getCourses()) {
            System.out.println("- " + c.getCourseName() + " (" + c.getCourseType() + "): $" + c.calculateTuitionFee());
            total += c.calculateTuitionFee();
        }
        System.out.println("Courses: " + department.getCoursesCount() + ", total fee: $" + total);
    }

    // Аналитика: агрегированная статистика по любой коллекции курсов
    public void printSummary(Collection<Course> courses) {
        System.out.println("=== SUMMARY REPORT ===");
        if (courses.isEmpty()) {
            System.out.println("No courses to analyze.");
            return;
        }
        double total = 0;
        Course mostExpensive = null;
        Map<String, Integer> countByType = new TreeMap<>();
        for (Course c : courses) {
            total += c.calculateTuitionFee();
            if (mostExpensive == null || c.calculateTuitionFee() > mostExpensive.calculateTuitionFee()) {
                mostExpensive = c;
            }
            countByType.merge(c.getCourseType(), 1, Integer::sum);
        }
        System.out.println("Courses: " + courses.size());
        System.out.println("Total fee: $" + total);
        System.out.println("Average fee: $" + total / courses.size());
        System.out.println("Most expensive: " + mostExpensive.getCourseName() + " ($" + mostExpensive.calculateTuitionFee() + ")");
        for (Map.Entry<String, Integer> e : countByType.entrySet()) {
            System.out.println("  " + e.getKey() + ": " + e.getValue());
        }
    }
}