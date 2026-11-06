public class OnlineCourse extends Course implements Schedulable, Assessable, Shareable {
    private static final double RATE_PER_CREDIT = 80.0;
    private static final double VIDEO_ACCESS_FEE = 50.0;

    private String platform;
    private boolean hasRecordedVideos;
    private String schedule = "Not scheduled";
    private int sharedMaterialsCount = 0;

    public OnlineCourse(String courseName, String courseCode, int credits, int maxStudents,
                        String platform, boolean hasRecordedVideos) {
        super(courseName, courseCode, credits, maxStudents);
        System.out.println("[super() called] OnlineCourse initialized: " + courseName);
        setPlatform(platform);
        this.hasRecordedVideos = hasRecordedVideos;
    }

    public String getPlatform() { return platform; }
    public boolean isHasRecordedVideos() { return hasRecordedVideos; }

    public void setPlatform(String platform) {
        if (platform == null || platform.trim().isEmpty()) {
            System.out.println("Error: platform cannot be empty.");
            this.platform = "Unknown";
        } else {
            this.platform = platform;
        }
    }

    public void setHasRecordedVideos(boolean hasRecordedVideos) {

        this.hasRecordedVideos = hasRecordedVideos;
    }

    // Abstract methods from Course
    @Override
    public double calculateTuitionFee() {
        double fee = getCredits() * RATE_PER_CREDIT;
        if (hasRecordedVideos) {
            fee += VIDEO_ACCESS_FEE;
        }
        return fee;
    }

    @Override
    public String getCourseType() {
        return "Online";
    }

    // Interface Schedulable
    @Override
    public void setSchedule(String day, String time) {
        this.schedule = day + " " + time + " on " + platform;
    }

    @Override
    public String getSchedule() {
        return schedule;
    }

    // Interface Assessable
    @Override
    public void conductAssessment() {
        System.out.println("Online quiz for " + getCourseName() + " is open on " + platform + ".");
    }

    @Override
    public int getAssessmentWeight() {
        return 50;
    }

    // Interface Shareable
    @Override
    public void shareMaterial(String material) {
        sharedMaterialsCount++;
        System.out.println("Material '" + material + "' shared on " + platform + ".");
    }

    @Override
    public int getSharedMaterialsCount() {
        return sharedMaterialsCount;
    }

    @Override
    public void displayInfo() {
        System.out.println("--- Online Course ---");
        super.displayInfo();
        System.out.println("Platform: " + platform);
        System.out.println("Recorded videos: " + (hasRecordedVideos ? "Yes" : "No"));
        System.out.println("Schedule: " + schedule);
    }

    // Unique methods
    public void uploadMaterial(String material) {
        System.out.println("Material '" + material + "' uploaded to " + platform + ".");
    }

    public void sendLink() {
        System.out.println("Link to " + getCourseName() + " on " + platform + " sent to all students.");
    }

    public void startLiveSession() {
        System.out.println("Live session of " + getCourseName() + " started on " + platform + ".");
    }
}