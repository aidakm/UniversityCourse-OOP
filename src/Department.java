import java.util.ArrayList;

// AGGREGATION: Department has Courses, but the courses exist independently of it
public class Department {
    private String name;
    private ArrayList<Course> courses;

    public Department(String name) {
        this.name = (name == null || name.trim().isEmpty()) ? "Unknown" : name;
        this.courses = new ArrayList<>();
    }

    public String getName() { return name; }

    public ArrayList<Course> getCourses() { return courses; }

    public void addCourse(Course course) {
        if (course == null) {
            System.out.println("Error: course cannot be null.");
        } else {
            courses.add(course);
            System.out.println(course.getCourseName() + " added to department " + name + ".");
        }
    }

    public boolean removeCourse(Course course) {
        boolean removed = courses.remove(course);
        if (removed) {
            System.out.println(course.getCourseName() + " removed from department " + name + ".");
        }
        return removed;
    }

    public int getCoursesCount() { return courses.size(); }

    // Бизнес-ключ: name
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        return name.equalsIgnoreCase(((Department) o).name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
