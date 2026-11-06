public class LabCourse extends Course implements Assessable {
    private static final double RATE_PER_CREDIT = 100.0;
    private static final double FEE_PER_LAB = 20.0;

    private String labEquipment;
    private int numberOfLabs;

    public LabCourse(String courseName, String courseCode, int credits, int maxStudents,
                     String labEquipment, int numberOfLabs) {
        super(courseName, courseCode, credits, maxStudents);
        System.out.println("[super() called] LabCourse initialized: " + courseName);
        setLabEquipment(labEquipment);
        setNumberOfLabs(numberOfLabs);
    }

    public String getLabEquipment() { return labEquipment; }
    public int getNumberOfLabs() { return numberOfLabs; }

    public void setLabEquipment(String labEquipment) {
        if (labEquipment == null || labEquipment.trim().isEmpty()) {
            System.out.println("Error: lab equipment cannot be empty.");
            this.labEquipment = "None";
        } else {
            this.labEquipment = labEquipment;
        }
    }

    public void setNumberOfLabs(int numberOfLabs) {
        if (numberOfLabs <= 0) {
            System.out.println("Error: number of labs must be positive.");
            if (this.numberOfLabs <= 0) this.numberOfLabs = 1;
        } else {
            this.numberOfLabs = numberOfLabs;
        }
    }

    // Abstract methods from Course
    @Override
    public double calculateTuitionFee() {
        return getCredits() * RATE_PER_CREDIT + numberOfLabs * FEE_PER_LAB;
    }

    @Override
    public String getCourseType() {
        return "Laboratory";
    }

    // Interface Assessable
    @Override
    public void conductAssessment() {
        System.out.println("Lab reports for " + getCourseName() + " are being checked.");
    }

    @Override
    public int getAssessmentWeight() {
        return 40;
    }

    @Override
    public void displayInfo() {
        System.out.println("--- Lab Course ---");
        super.displayInfo();
        System.out.println("Equipment: " + labEquipment);
        System.out.println("Number of labs: " + numberOfLabs);
    }

    // Unique methods
    public void conductLab(int labNumber) {
        if (labNumber < 1 || labNumber > numberOfLabs) {
            System.out.println("Error: lab #" + labNumber + " does not exist.");
        } else {
            System.out.println("Conducting lab #" + labNumber + " of " + numberOfLabs + " using " + labEquipment + ".");
        }
    }

    public void checkSafety() {
        System.out.println("Safety check for " + getCourseName() + " completed. Equipment (" + labEquipment + ") is OK.");
    }

    public void orderEquipment(String item) {
        System.out.println("Ordered new equipment for lab: " + item);
    }
}
