public class LectureCourse extends Course implements Schedulable, Assessable {
    private static final double RATE_PER_CREDIT = 100.0;

    private String lectureHall;
    private int weeklyHours;
    private String schedule = "Not scheduled";

    public LectureCourse(String courseName, String courseCode, int credits, int maxStudents,
                         String lectureHall, int weeklyHours) {
        super(courseName, courseCode, credits, maxStudents);
        System.out.println("[super() called] LectureCourse initialized: " + courseName);
        setLectureHall(lectureHall);
        setWeeklyHours(weeklyHours);
    }

    public String getLectureHall() { return lectureHall; }
    public int getWeeklyHours() { return weeklyHours; }

    public void setLectureHall(String lectureHall) {
        if (lectureHall == null || lectureHall.trim().isEmpty()) {
            System.out.println("Error: lecture hall cannot be empty.");
            this.lectureHall = "TBA";
        } else {
            this.lectureHall = lectureHall;
        }
    }

    public void setWeeklyHours(int weeklyHours) {
        if (weeklyHours <= 0) {
            System.out.println("Error: weekly hours must be positive.");
            if (this.weeklyHours <= 0) this.weeklyHours = 1;
        } else {
            this.weeklyHours = weeklyHours;
        }
    }

    // Abstract methods from Course
    @Override
    public double calculateTuitionFee() {
        return getCredits() * RATE_PER_CREDIT;
    }

    @Override
    public String getCourseType() {
        return "Lecture";
    }

    // Interface Schedulable
    @Override
    public void setSchedule(String day, String time) {
        this.schedule = day + " " + time + " in " + lectureHall;
    }

    @Override
    public String getSchedule() {
        return schedule;
    }

    // Interface Assessable
    @Override
    public void conductAssessment() {
        System.out.println("Written exam for " + getCourseName() + " is being conducted.");
    }

    @Override
    public int getAssessmentWeight() {
        return 60;
    }

    @Override
    public void displayInfo() {
        System.out.println("--- Lecture Course ---");
        super.displayInfo();
        System.out.println("Lecture hall: " + lectureHall);
        System.out.println("Weekly hours: " + weeklyHours);
        System.out.println("Schedule: " + schedule);
    }

    // Unique methods
    public void scheduleExam(String date) {
        System.out.println("Exam for " + getCourseName() + " scheduled on " + date + " in hall " + lectureHall + ".");
    }

    public int calculateWorkload() {
        int total = weeklyHours * 15;
        System.out.println("Total workload of " + getCourseName() + ": " + total + " hours per semester.");
        return total;
    }

    public void giveLecture(String topic) {
        System.out.println("Lecture on '" + topic + "' is being given in " + lectureHall + ".");
    }
}