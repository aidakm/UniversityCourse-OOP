import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Класс отвечает ТОЛЬКО за работу с файлами (читает, сортирует, пишет).
// Формат строки в файле:
//   LECTURE,name,code,credits,maxStudents,lectureHall,weeklyHours
//   LAB,name,code,credits,maxStudents,labEquipment,numberOfLabs
//   ONLINE,name,code,credits,maxStudents,platform,hasRecordedVideos(true/false)
public class CourseFileManager {

    // ---------- Компараторы для сортировки (как в прошлом задании) ----------
    public static final Comparator<Course> BY_NAME =
            Comparator.comparing(Course::getCourseName);
    public static final Comparator<Course> BY_FEE =
            Comparator.comparingDouble(Course::calculateTuitionFee);
    public static final Comparator<Course> BY_CREDITS =
            Comparator.comparingInt(Course::getCredits);

    // ---------- ЧТЕНИЕ ----------
    // throws IOException: если файла нет или он не читается - исключение уйдёт вызывающему (Main)
    public Map<String, Course> loadCourses(String filename) throws IOException {
        Map<String, Course> courses = new LinkedHashMap<>(); // ключ - код курса

        // try-with-resources: BufferedReader закроется автоматически
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // пустые строки пропускаем
                }
                // try-catch ВНУТРИ цикла: одна плохая строка не должна ломать чтение остальных
                try {
                    Course course = parseCourse(line, lineNumber);
                    if (courses.containsKey(course.getCourseCode())) {
                        throw new InvalidCourseDataException(lineNumber,
                                "duplicate course code " + course.getCourseCode());
                    }
                    courses.put(course.getCourseCode(), course);
                } catch (NumberFormatException e) {
                    System.out.println("Error (line " + lineNumber + "): malformed number -> " + e.getMessage());
                } catch (InvalidCourseDataException e) {
                    System.out.println("Error (" + e.getMessage() + ")");
                }
            }
        }
        return courses;
    }

    // Превращает одну строку файла в объект нужного подкласса (полиморфизм!)
    // throws InvalidCourseDataException - наше исключение; NumberFormatException unchecked, объявлять не надо
    private Course parseCourse(String line, int lineNumber) throws InvalidCourseDataException {
        String[] p = line.split(",");
        if (p.length != 7) {
            throw new InvalidCourseDataException(lineNumber, "expected 7 fields, found " + p.length);
        }

        String type = p[0].trim().toUpperCase();
        String name = p[1].trim();
        String code = p[2].trim();
        int credits = Integer.parseInt(p[3].trim());      // может выбросить NumberFormatException
        int maxStudents = Integer.parseInt(p[4].trim());  // может выбросить NumberFormatException

        // throw: выбрасываем исключение сами, если данные не подходят по смыслу
        if (credits < 1 || credits > 10) {
            throw new InvalidCourseDataException(lineNumber, "credits must be between 1 and 10, got " + credits);
        }
        if (maxStudents <= 0) {
            throw new InvalidCourseDataException(lineNumber, "max students must be > 0, got " + maxStudents);
        }

        switch (type) {
            case "LECTURE":
                return new LectureCourse(name, code, credits, maxStudents,
                        p[5].trim(), Integer.parseInt(p[6].trim()));
            case "LAB":
                return new LabCourse(name, code, credits, maxStudents,
                        p[5].trim(), Integer.parseInt(p[6].trim()));
            case "ONLINE":
                String flag = p[6].trim().toLowerCase();
                if (!flag.equals("true") && !flag.equals("false")) {
                    throw new InvalidCourseDataException(lineNumber,
                            "last field must be true or false, got '" + p[6].trim() + "'");
                }
                return new OnlineCourse(name, code, credits, maxStudents,
                        p[5].trim(), Boolean.parseBoolean(flag));
            default:
                throw new InvalidCourseDataException(lineNumber, "unknown course type '" + p[0].trim() + "'");
        }
    }

    // ---------- СОРТИРОВКА ----------
    public List<Course> sortCourses(Map<String, Course> courses, Comparator<Course> comparator) {
        List<Course> list = new ArrayList<>(courses.values()); // Map нельзя сортировать - берём values в список
        list.sort(comparator);
        return list;
    }

    // ---------- ЗАПИСЬ (BufferedWriter) ----------
    public void saveCourses(List<Course> courses, String filename) throws IOException {
        // false = перезаписать файл (true дописывал бы в конец)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (Course c : courses) {
                writer.write(toFileLine(c));
                writer.newLine();
            }
        }
    }

    // Обратная операция к parseCourse: объект -> строка
    private String toFileLine(Course c) {
        String common = c.getCourseName() + "," + c.getCourseCode() + ","
                + c.getCredits() + "," + c.getMaxStudents();
        if (c instanceof LectureCourse) {
            LectureCourse l = (LectureCourse) c;
            return "LECTURE," + common + "," + l.getLectureHall() + "," + l.getWeeklyHours();
        } else if (c instanceof LabCourse) {
            LabCourse l = (LabCourse) c;
            return "LAB," + common + "," + l.getLabEquipment() + "," + l.getNumberOfLabs();
        } else if (c instanceof OnlineCourse) {
            OnlineCourse o = (OnlineCourse) c;
            return "ONLINE," + common + "," + o.getPlatform() + "," + o.isHasRecordedVideos();
        }
        return "UNKNOWN," + common + ",-,-";
    }

    // ---------- ОТЧЁТ (современный NIO: Files) ----------
    public void saveReport(List<Course> courses, String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("=== COURSE REPORT ===");
        double total = 0;
        for (Course c : courses) {
            lines.add(String.format("%-15s %-8s %-10s credits=%d fee=$%.2f",
                    c.getCourseName(), c.getCourseCode(), c.getCourseType(),
                    c.getCredits(), c.calculateTuitionFee()));
            total += c.calculateTuitionFee();
        }
        lines.add("Total courses: " + courses.size() + ", total fee: $" + String.format("%.2f", total));
        Files.write(Path.of(filename), lines); // одна строка - и файл записан
    }
}
