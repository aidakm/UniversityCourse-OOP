// Exists independently of Course and Department (used in AGGREGATION)
public class Instructor {
    private String name;
    private String email;
    private String specialization;

    public Instructor() {
        this.name = "Unknown";
        this.email = "N/A";
        this.specialization = "General";
    }

    public Instructor(String name, String email, String specialization) {
        setName(name);
        setEmail(email);
        setSpecialization(specialization);
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSpecialization() { return specialization; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Error: instructor name cannot be empty.");
            this.name = "Unknown";
        } else {
            this.name = name;
        }
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            System.out.println("Error: invalid email.");
            this.email = "N/A";
        } else {
            this.email = email;
        }
    }

    public void setSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            System.out.println("Error: specialization cannot be empty.");
            this.specialization = "General";
        } else {
            this.specialization = specialization;
        }
    }

    public void displayInfo() {
        System.out.println("Instructor: " + name + " (" + email + "), " + specialization);
    }

    // Бизнес-ключ: email
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instructor)) return false;
        return email.equalsIgnoreCase(((Instructor) o).email);
    }

    @Override
    public int hashCode() {
        return email.toLowerCase().hashCode();
    }
}