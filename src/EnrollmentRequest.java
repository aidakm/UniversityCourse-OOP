// Заявка студента на запись на курс. Лежит в PriorityQueue.
// Порядок: сначала по приоритету (1 = самый важный), при равенстве по времени подачи (FIFO).
public class EnrollmentRequest implements Comparable<EnrollmentRequest> {
    private static int counter = 0;

    private final int id;
    private final Student student;
    private final Course course;
    private final int priority;

    public EnrollmentRequest(Student student, Course course, int priority) {
        this.id = ++counter;
        this.student = student;
        this.course = course;
        this.priority = priority;
    }

    public int getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public int getPriority() { return priority; }

    @Override
    public int compareTo(EnrollmentRequest other) {
        int byPriority = Integer.compare(priority, other.priority);
        return byPriority != 0 ? byPriority : Integer.compare(id, other.id);
    }

    // Бизнес-ключ: id заявки
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrollmentRequest)) return false;
        return id == ((EnrollmentRequest) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Request #" + id + ": " + student.getName() + " -> " + course.getCourseCode()
                + " (priority " + priority + ")";
    }
}
