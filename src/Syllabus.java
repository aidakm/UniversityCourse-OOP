import java.util.ArrayList;
import java.util.Objects;

// Part of Course (COMPOSITION): created by Course and destroyed together with it
public class Syllabus {
    private String title;
    private ArrayList<String> topics;

    public Syllabus(String title) {
        this.title = title;
        this.topics = new ArrayList<>();
    }

    public String getTitle() { return title; }

    public void addTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            System.out.println("Error: topic cannot be empty.");
        } else {
            topics.add(topic);
        }
    }

    public int getTopicsCount() { return topics.size(); }

    public void display() {
        System.out.println("Syllabus: " + title);
        if (topics.isEmpty()) {
            System.out.println("  (no topics yet)");
        }
        for (int i = 0; i < topics.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + topics.get(i));
        }
    }

    // Бизнес-ключ: title
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Syllabus)) return false;
        return Objects.equals(title, ((Syllabus) o).title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
