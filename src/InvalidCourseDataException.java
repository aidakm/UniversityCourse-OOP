// Собственное (custom) проверяемое исключение.
// extends Exception => checked: компилятор заставит нас обработать его (try-catch) или объявить (throws)
public class InvalidCourseDataException extends Exception {
    private final int lineNumber;

    public InvalidCourseDataException(int lineNumber, String message) {
        super("Line " + lineNumber + ": " + message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
