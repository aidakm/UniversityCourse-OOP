import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Формат строки: STUDENT,name,email,studentId,password  или  ADMIN,name,email,department,password
public class UserFileManager {

    public List<User> loadUsers(String filename) throws IOException {
        List<User> users = new ArrayList<>();
        if (!Files.exists(Path.of(filename))) {
            return users; // файла ещё нет - это не ошибка
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(",");
                if (p.length != 5) {
                    System.out.println("Error (users line " + lineNumber + "): expected 5 fields, found " + p.length);
                    continue;
                }
                String type = p[0].trim().toUpperCase();
                User u;
                if (type.equals("ADMIN")) {
                    u = new Administrator(p[1].trim(), p[2].trim(), p[3].trim());
                } else if (type.equals("STUDENT")) {
                    u = new Student(p[1].trim(), p[2].trim(), p[3].trim());
                } else {
                    System.out.println("Error (users line " + lineNumber + "): unknown type '" + p[0].trim() + "'");
                    continue;
                }
                u.setPassword(p[4].trim());
                users.add(u);
            }
        }
        return users;
    }

    public void saveUsers(Collection<User> users, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (User u : users) {
                writer.write(toFileLine(u));
                writer.newLine();
            }
        }
    }

    private String toFileLine(User u) {
        if (u instanceof Administrator) {
            Administrator a = (Administrator) u;
            return "ADMIN," + a.getName() + "," + a.getEmail() + "," + a.getDepartment() + "," + a.getPassword();
        }
        Student s = (Student) u;
        return "STUDENT," + s.getName() + "," + s.getEmail() + "," + s.getStudentId() + "," + s.getPassword();
    }
}

