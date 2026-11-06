import java.util.ArrayList;

public class Student extends User {
    private String studentId;
    private ArrayList<Course> enrolledCourses = new ArrayList<>(); // агрегация

    public Student(String name, String email, String studentId) {
        super(name, email);
        this.studentId = (studentId == null || studentId.trim().isEmpty()) ? "N/A" : studentId;
    }

    public String getStudentId() { return studentId; }
    public ArrayList<Course> getEnrolledCourses() { return enrolledCourses; }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public void showPermissions() {
        System.out.println(getName() + " can: enroll in courses, drop courses, view own courses.");
    }

    public boolean enroll(Course course) {
        if (course == null) {
            System.out.println("Error: course cannot be null.");
            return false;
        }
        if (enrolledCourses.contains(course)) {
            System.out.println(getName() + " is already enrolled in " + course.getCourseName() + ".");
            return false;
        }
        if (course.getEnrolledStudents() >= course.getMaxStudents()) {
            System.out.println(getName() + " cannot enroll: " + course.getCourseName() + " is full.");
            return false;
        }
        course.addStudent();
        enrolledCourses.add(course);
        System.out.println(getName() + " enrolled in " + course.getCourseName() + ".");
        return true;
    }

    public boolean drop(Course course) {
        if (enrolledCourses.remove(course)) {
            course.setEnrolledStudents(course.getEnrolledStudents() - 1);
            System.out.println(getName() + " dropped " + course.getCourseName() + ".");
            return true;
        }
        System.out.println(getName() + " is not enrolled in that course.");
        return false;
    }



    public void viewMyCourses() {
        System.out.println(getName() + "'s courses:");
        if (enrolledCourses.isEmpty()) {
            System.out.println("  (none)");
        }
        for (Course c : enrolledCourses) {
            System.out.println("  - " + c.getCourseName() + " (" + c.getCourseType() + ")");
        }
    }

    public double calculateTotalFee() {
        double total = 0;
        for (Course c : enrolledCourses) {
            total += c.calculateTuitionFee();
        }
        return total;
    }
}