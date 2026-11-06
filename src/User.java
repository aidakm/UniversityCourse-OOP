// Базовый абстрактный класс для всех ролей в системе
public abstract class User {
    private String name;
    private String email;
    private String password = "";

    public User(String name, String email) {
        setName(name);
        setEmail(email);
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Error: name cannot be empty.");
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

    public void setPassword(String password) { this.password = password; }

    // Каждая роль обязана реализовать
    public abstract String getRole();
    public abstract void showPermissions();

    public void displayInfo() {
        System.out.println("[" + getRole() + "] " + name + " (" + email + ")");
    }

    // Бизнес-ключ пользователя: email
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return email.equalsIgnoreCase(((User) o).email);
    }

    @Override
    public int hashCode() {
        return email.toLowerCase().hashCode();
    }
}