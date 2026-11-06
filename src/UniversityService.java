import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

// Сервисный слой: все коллекции JCF живут здесь и меняются синхронно
public class UniversityService {
    private static final String COURSES_FILE = "courses.txt";
    private static final String USERS_FILE = "users.txt";

    // ---- Block A: реестры ----
    private final List<Course> courses = new ArrayList<>();      // список курсов
    private final Set<User> users = new LinkedHashSet<>();       // Set: дубликаты отсекает equals/hashCode

    // ---- Block B: индексы ----
    private final Map<String, Course> courseByCode = new HashMap<>();            // первичный ключ, O(1)
    private final Map<String, User> userByEmail = new HashMap<>();               // первичный ключ, O(1)
    private final Map<String, List<Course>> coursesByType = new TreeMap<>();     // категория: тип курса
    private final Map<String, List<User>> usersByRole = new TreeMap<>();         // категория: роль
    private final Map<String, Set<Student>> studentsByCourse = new HashMap<>();  // N:M курс <-> студенты

    // ---- Block D: очередь заявок по приоритету ----
    private final Queue<EnrollmentRequest> requests = new PriorityQueue<>();

    private final Department department = new Department("Science and Technology"); // агрегация
    private final CourseFileManager courseFiles = new CourseFileManager();
    private final UserFileManager userFiles = new UserFileManager();
    private final ReportGenerator reports = new ReportGenerator();

    private static String key(String s) {
        return s.trim().toUpperCase();
    }

    // =============== ЗАГРУЗКА И СОХРАНЕНИЕ ===============
    public void loadData() {
        try {
            for (Course c : courseFiles.loadCourses(COURSES_FILE).values()) {
                addCourse(c);
            }
        } catch (IOException e) {
            System.out.println("Could not read " + COURSES_FILE + ": " + e.getMessage());
        }
        try {
            for (User u : userFiles.loadUsers(USERS_FILE)) {
                addUser(u);
            }
        } catch (IOException e) {
            System.out.println("Could not read " + USERS_FILE + ": " + e.getMessage());
        }
        System.out.println("Loaded courses: " + courses.size() + ", users: " + users.size());
    }

    public void saveAll() {
        try {
            courseFiles.saveCourses(courses, COURSES_FILE);
            userFiles.saveUsers(users, USERS_FILE);
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    // =============== CRUD: КУРСЫ ===============
    public boolean addCourse(Course c) {
        if (c == null || courseByCode.containsKey(key(c.getCourseCode()))) {
            return false; // пустой объект или такой код уже есть
        }
        courses.add(c);
        courseByCode.put(key(c.getCourseCode()), c);
        coursesByType.computeIfAbsent(c.getCourseType(), t -> new ArrayList<>()).add(c);
        department.addCourse(c);
        return true;
    }

    // Удаление синхронно чистит ВСЕ связанные структуры
    public boolean removeCourse(String code) {
        Course c = findCourse(code);
        if (c == null) {
            return false;
        }
        courseByCode.remove(key(code));
        courses.remove(c);

        List<Course> group = coursesByType.get(c.getCourseType());
        if (group != null) {
            group.remove(c);
            if (group.isEmpty()) {
                coursesByType.remove(c.getCourseType());
            }
        }
        department.removeCourse(c);

        Set<Student> enrolled = studentsByCourse.remove(key(code));
        if (enrolled != null) {
            for (Student s : enrolled) {
                s.getEnrolledCourses().remove(c); // у студентов курс тоже пропадает
            }
        }
        requests.removeIf(r -> r.getCourse().equals(c)); // и заявки на него
        return true;
    }

    public boolean updateCapacity(Course c, int newMax) {
        if (c == null) {
            return false;
        }
        if (newMax < c.getEnrolledStudents()) {
            System.out.println("Error: " + c.getEnrolledStudents() + " students already enrolled.");
            return false;
        }
        c.setMaxStudents(newMax);
        return true;
    }

    // =============== CRUD: ПОЛЬЗОВАТЕЛИ ===============
    public boolean addUser(User u) {
        if (u == null || !users.add(u)) { // Set вернёт false, если такой email уже есть
            return false;
        }
        userByEmail.put(u.getEmail().toLowerCase(), u);
        usersByRole.computeIfAbsent(u.getRole(), r -> new ArrayList<>()).add(u);
        return true;
    }

    // =============== ПОИСК И ФИЛЬТРАЦИЯ ===============
    public Course findCourse(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return courseByCode.get(key(code)); // O(1)
    }

    public User findUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userByEmail.get(email.trim().toLowerCase()); // O(1)
    }

    public List<Course> getCoursesByType(String type) {
        return new ArrayList<>(coursesByType.getOrDefault(type, Collections.emptyList()));
    }

    public int countByRole(String role) {
        return usersByRole.getOrDefault(role, Collections.emptyList()).size();
    }

    // =============== СОРТИРОВКА (Block C) ===============
    // cmp == null -> естественный порядок (Comparable), иначе переданный Comparator
    public List<Course> getSortedCourses(Comparator<Course> cmp) {
        List<Course> sorted = new ArrayList<>(courses);
        if (cmp == null) {
            Collections.sort(sorted);
        } else {
            sorted.sort(cmp);
        }
        return sorted;
    }

    // =============== ОЧЕРЕДЬ ЗАЯВОК (Block D) ===============
    public boolean submitRequest(Student s, Course c, int priority) {
        if (s == null || c == null) {
            return false;
        }
        if (s.getEnrolledCourses().contains(c)) {
            System.out.println("You are already enrolled in " + c.getCourseName() + ".");
            return false;
        }
        for (EnrollmentRequest r : requests) {
            if (r.getStudent().equals(s) && r.getCourse().equals(c)) {
                System.out.println("You already have a pending request for " + c.getCourseName() + ".");
                return false;
            }
        }
        requests.offer(new EnrollmentRequest(s, c, priority)); // кладём в очередь
        return true;
    }

    public boolean processNextRequest() {
        EnrollmentRequest r = requests.poll(); // достаёт и удаляет заявку с наивысшим приоритетом
        if (r == null) {
            System.out.println("Queue is empty - nothing to process.");
            return false;
        }
        System.out.println("Processing " + r);
        boolean ok = r.getStudent().enroll(r.getCourse());
        if (ok) {
            studentsByCourse.computeIfAbsent(key(r.getCourse().getCourseCode()), k -> new LinkedHashSet<>())
                    .add(r.getStudent());
        }
        return ok;
    }

    public void showQueue() {
        if (requests.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        // Итератор PriorityQueue НЕ отсортирован, поэтому копируем и сортируем только для показа
        List<EnrollmentRequest> sorted = new ArrayList<>(requests);
        Collections.sort(sorted);
        for (EnrollmentRequest r : sorted) {
            System.out.println("  " + r);
        }
    }

    public boolean dropCourse(Student s, Course c) {
        if (c == null || !s.drop(c)) {
            return false;
        }
        Set<Student> set = studentsByCourse.get(key(c.getCourseCode()));
        if (set != null) {
            set.remove(s);
        }
        return true;
    }

    // =============== АНАЛИТИКА ===============
    public void printAnalytics() {
        reports.printSummary(courses);
        System.out.println("Users by role:");
        for (Map.Entry<String, List<User>> e : usersByRole.entrySet()) {
            System.out.println("  " + e.getKey() + ": " + e.getValue().size());
        }
        System.out.println("Students per course:");
        for (Course c : courses) {
            Set<Student> set = studentsByCourse.get(key(c.getCourseCode()));
            System.out.println("  " + c.getCourseCode() + ": " + (set == null ? 0 : set.size()));
        }
        System.out.println("Pending requests: " + requests.size());
    }
}