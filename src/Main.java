import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
    static final String ADMIN_CODE = "ADMIN123"; // секретный код для регистрации администратора
    static final Scanner sc = new Scanner(System.in);
    static final UniversityService service = new UniversityService();

    public static void main(String[] args) {
        service.loadData();
        while (true) {
            System.out.println("\n1. Register   2. Login   0. Exit");
            String choice = ask("> ");
            if (choice.equals("0")) {
                break;
            } else if (choice.equals("1")) {
                register();
            } else if (choice.equals("2")) {
                User u = login();
                if (u instanceof Administrator) {
                    adminMenu((Administrator) u);
                } else if (u instanceof Student) {
                    studentMenu((Student) u);
                }
            }
        }
        service.saveAll();
        System.out.println("Goodbye!");
    }

    // ================= РЕГИСТРАЦИЯ И ВХОД =================
    static void register() {
        String role = ask("1) Student  2) Administrator: ");
        if (!role.equals("1") && !role.equals("2")) {
            System.out.println("Invalid choice.");
            return;
        }
        String name = ask("Name: ").replace(",", " ");
        String email = ask("Email: ");
        String pass = ask("Password: ").replace(",", " ");
        if (name.isEmpty() || pass.isEmpty() || !email.contains("@") || email.contains(",")
                || service.findUser(email) != null) {
            System.out.println("Error: empty field, bad email, or email already used.");
            return;
        }
        User u;
        if (role.equals("2")) {
            if (!ask("Admin code: ").equals(ADMIN_CODE)) {
                System.out.println("Wrong admin code.");
                return;
            }
            u = new Administrator(name, email, "Science and Technology");
        } else {
            u = new Student(name, email, "S" + (service.countByRole("Student") + 1));
        }
        u.setPassword(pass);
        if (service.addUser(u)) {
            service.saveAll();
            System.out.println("Registered as " + u.getRole() + ". Now you can log in.");
        }
    }

    static User login() {
        String email = ask("Email: ");
        String pass = ask("Password: ");
        User u = service.findUser(email);
        if (u == null || !u.getPassword().equals(pass)) {
            System.out.println("Wrong email or password.");
            return null;
        }
        System.out.println("Welcome, " + u.getName() + "! Role: " + u.getRole());
        u.showPermissions();
        return u;
    }

    // ================= МЕНЮ СТУДЕНТА =================
    static void studentMenu(Student s) {
        while (true) {
            System.out.println("\n--- STUDENT: 1 list courses | 2 find by code | 3 filter by type"
                    + " | 4 request enrollment | 5 drop | 6 my courses | 7 my fee | 0 logout");
            switch (ask("> ")) {
                case "0":
                    return;
                case "1":
                    listCourses();
                    break;
                case "2":
                    findByCode();
                    break;
                case "3":
                    filterByType();
                    break;
                case "4": {
                    Course c = askCourse();
                    if (c != null) {
                        int priority = number("Priority (1 = high, 3 = low): ", 1, 3);
                        if (service.submitRequest(s, c, priority)) {
                            System.out.println("Request sent. Wait until an administrator processes it.");
                        }
                    }
                    break;
                }
                case "5": {
                    Course c = askCourse();
                    if (c != null) {
                        service.dropCourse(s, c);
                    }
                    break;
                }
                case "6":
                    s.viewMyCourses();
                    break;
                case "7":
                    System.out.println("Total fee: $" + s.calculateTotalFee());
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    // ================= МЕНЮ АДМИНИСТРАТОРА =================
    static void adminMenu(Administrator admin) {
        while (true) {
            System.out.println("\n--- ADMIN: 1 list | 2 find | 3 filter | 4 add course | 5 remove course"
                    + " | 6 assign instructor | 7 capacity | 8 show queue | 9 process next | 10 analytics | 0 logout");
            switch (ask("> ")) {
                case "0":
                    return;
                case "1":
                    listCourses();
                    break;
                case "2":
                    findByCode();
                    break;
                case "3":
                    filterByType();
                    break;
                case "4": {
                    Course c = buildCourse();
                    if (c != null && service.addCourse(c)) {
                        System.out.println("Course added.");
                    }
                    break;
                }
                case "5":
                    System.out.println(service.removeCourse(ask("Course code: "))
                            ? "Course removed from all collections." : "Course not found.");
                    break;
                case "6": {
                    Course c = askCourse();
                    if (c != null) {
                        admin.assignInstructor(c, new Instructor(
                                ask("Instructor name: "), ask("Instructor email: "), ask("Specialization: ")));
                    }
                    break;
                }
                case "7": {
                    Course c = askCourse();
                    if (c != null) {
                        service.updateCapacity(c, number("New capacity (1-500): ", 1, 500));
                    }
                    break;
                }
                case "8":
                    service.showQueue();
                    break;
                case "9":
                    service.processNextRequest();
                    break;
                case "10":
                    service.printAnalytics();
                    break;
                default:
                    System.out.println("Unknown option.");
            }
            service.saveAll();
        }
    }

    // ================= ОБЩИЕ ДЕЙСТВИЯ =================
    static void listCourses() {
        System.out.println("Sort by: 1 code (natural) | 2 name | 3 fee | 4 credits");
        Comparator<Course> cmp = null;
        switch (ask("> ")) {
            case "2": cmp = CourseFileManager.BY_NAME; break;
            case "3": cmp = CourseFileManager.BY_FEE; break;
            case "4": cmp = CourseFileManager.BY_CREDITS; break;
            default: break; // естественный порядок
        }
        printCourses(service.getSortedCourses(cmp));
    }

    static void findByCode() {
        Course c = service.findCourse(ask("Course code: "));
        if (c == null) {
            System.out.println("Course not found.");
        } else {
            c.displayInfo();
        }
    }

    static void filterByType() {
        String[] types = {"Lecture", "Laboratory", "Online"};
        int n = number("Type: 1 Lecture | 2 Laboratory | 3 Online: ", 1, 3);
        printCourses(service.getCoursesByType(types[n - 1]));
    }

    static Course askCourse() {
        Course c = service.findCourse(ask("Course code: "));
        if (c == null) {
            System.out.println("Course not found.");
        }
        return c;
    }

    static void printCourses(List<Course> list) {
        if (list.isEmpty()) {
            System.out.println("  (no courses)");
            return;
        }
        for (Course c : list) {
            Instructor i = c.getInstructor();
            System.out.println(String.format("  %-8s %-16s %-10s $%-8.2f %d/%d  %s",
                    c.getCourseCode(), c.getCourseName(), c.getCourseType(), c.calculateTuitionFee(),
                    c.getEnrolledStudents(), c.getMaxStudents(), i == null ? "-" : i.getName()));
        }
    }

    static Course buildCourse() {
        String type = ask("Type: 1) Lecture  2) Lab  3) Online: ");
        String name = ask("Name: ").replace(",", " ");
        String code = ask("Code: ").replace(",", " ");
        if (!(type.equals("1") || type.equals("2") || type.equals("3")) || name.isEmpty() || code.isEmpty()) {
            System.out.println("Error: invalid type, or empty name/code.");
            return null;
        }
        if (service.findCourse(code) != null) {
            System.out.println("Error: course with this code already exists.");
            return null;
        }
        int credits = number("Credits (1-10): ", 1, 10);
        int max = number("Max students (1-500): ", 1, 500);
        if (type.equals("1")) {
            return new LectureCourse(name, code, credits, max,
                    ask("Lecture hall: ").replace(",", " "), number("Weekly hours (1-40): ", 1, 40));
        } else if (type.equals("2")) {
            return new LabCourse(name, code, credits, max,
                    ask("Equipment: ").replace(",", " "), number("Number of labs (1-30): ", 1, 30));
        }
        return new OnlineCourse(name, code, credits, max,
                ask("Platform: ").replace(",", " "), ask("Recorded videos (yes/no): ").equalsIgnoreCase("yes"));
    }

    static String ask(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // Просит число в диапазоне, пока не введут правильно (ловит NumberFormatException)
    static int number(String prompt, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(ask(prompt));
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}