public abstract class Course implements Comparable<Course> {
    private String courseName;
    private String courseCode;
    private int credits;
    private int maxStudents;
    private int enrolledStudents;

    // COMPOSITION: Syllabus is created inside Course and cannot exist without it
    private final Syllabus syllabus;

    // AGGREGATION: Instructor is created outside and only referenced here
    private Instructor instructor;

    // Default constructor
    public Course() {
        this.courseName = "Unknown";
        this.courseCode = "N/A";
        this.credits = 1;
        this.maxStudents = 30;
        this.enrolledStudents = 0;
        this.syllabus = new Syllabus("Unknown");
    }

    // Parameterized constructor
    public Course(String courseName, String courseCode, int credits, int maxStudents) {
        setCourseName(courseName);
        setCourseCode(courseCode);
        setCredits(credits);
        setMaxStudents(maxStudents);
        this.enrolledStudents = 0;
        this.syllabus = new Syllabus(this.courseName);
    }

    // Abstract methods: every subclass MUST implement them
    public abstract double calculateTuitionFee();
    public abstract String getCourseType();

    // Getters
    public String getCourseName() { return courseName; }
    public String getCourseCode() { return courseCode; }
    public int getCredits() { return credits; }
    public int getMaxStudents() { return maxStudents; }
    public int getEnrolledStudents() { return enrolledStudents; }
    public Instructor getInstructor() { return instructor; }

    // Setters with validation
    public void setCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            System.out.println("Error: course name cannot be empty.");
            this.courseName = "Unknown";
        } else {
            this.courseName = courseName;
        }
    }

    public void setCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            System.out.println("Error: course code cannot be empty.");
            this.courseCode = "N/A";
        } else {
            this.courseCode = courseCode;
        }
    }

    public void setCredits(int credits) {
        if (credits < 1 || credits > 10) {
            System.out.println("Error: credits must be between 1 and 10. Value not changed.");
            if (this.credits < 1) this.credits = 1;
        } else {
            this.credits = credits;
        }
    }

    public void setMaxStudents(int maxStudents) {
        if (maxStudents <= 0) {
            System.out.println("Error: max students must be greater than 0. Value not changed.");
            if (this.maxStudents <= 0) this.maxStudents = 30;
        } else {
            this.maxStudents = maxStudents;
        }
    }

    public void setEnrolledStudents(int enrolledStudents) {
        if (enrolledStudents < 0 || enrolledStudents > maxStudents) {
            System.out.println("Error: enrolled students must be between 0 and " + maxStudents + ". Value not changed.");
        } else {
            this.enrolledStudents = enrolledStudents;
        }
    }

    // Aggregation: the instructor is assigned from outside
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    // Composition: the course manages its own syllabus
    public void addSyllabusTopic(String topic) {
        syllabus.addTopic(topic);
    }

    public void printSyllabus() {
        syllabus.display();
    }

    public int getSyllabusTopicsCount() {
        return syllabus.getTopicsCount();
    }

    // Regular (non-abstract) methods
    public void addStudent() {
        if (enrolledStudents < maxStudents) {
            enrolledStudents++;
            System.out.println("Student added to " + courseName + ". Enrolled: " + enrolledStudents + "/" + maxStudents);
        } else {
            System.out.println("Course " + courseName + " is full.");
        }
    }

    public void displayInfo() {
        System.out.println("Course: " + courseName + " (" + courseCode + ")");
        System.out.println("Type: " + getCourseType());
        System.out.println("Credits: " + credits);
        System.out.println("Students: " + enrolledStudents + "/" + maxStudents);
        System.out.println("Tuition fee: $" + calculateTuitionFee());
        System.out.println("Instructor: " + (instructor != null ? instructor.getName() : "Not assigned"));
        System.out.println("Syllabus topics: " + syllabus.getTopicsCount());
    }
    // ---------- Контракты (Block A и C) ----------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        return courseCode.equalsIgnoreCase(((Course) o).courseCode);
    }

    @Override
    public int hashCode() {
        return courseCode.toLowerCase().hashCode();
    }

    @Override
    public int compareTo(Course other) {
        return courseCode.compareToIgnoreCase(other.courseCode); // естественный порядок: по коду
    }
}