public class Administrator extends User {
    private String department;

    public Administrator(String name, String email, String department) {
        super(name, email);
        this.department = (department == null || department.trim().isEmpty()) ? "General" : department;
    }

    public String getDepartment() { return department; }

    @Override
    public String getRole() { return "Administrator"; }

    @Override
    public void showPermissions() {
        System.out.println(getName() + " can: add/remove courses, assign instructors, change course capacity.");
    }

    public void addCourseToDepartment(Department dept, Course course) {
        System.out.println("[Admin " + getName() + "] adding course...");
        dept.addCourse(course);
    }

    public void removeCourseFromDepartment(Department dept, Course course) {
        System.out.println("[Admin " + getName() + "] removing course...");
        dept.removeCourse(course);
    }

    public void assignInstructor(Course course, Instructor instructor) {
        course.setInstructor(instructor);
        System.out.println("[Admin " + getName() + "] " + instructor.getName()
                + " assigned to " + course.getCourseName() + ".");
    }

    public void changeMaxStudents(Course course, int newMax) {
        System.out.println("[Admin " + getName() + "] changing capacity of " + course.getCourseName());
        course.setMaxStudents(newMax);
    }
}
