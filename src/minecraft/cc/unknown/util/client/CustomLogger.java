package cc.unknown.util.client;

public class CustomLogger {
    private final String className;

    public CustomLogger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
    }

    public void info(String message) {
        System.out.println(formatMessage(message, null, null));
    }

    public void info(String message, String message2) {
        System.out.println(formatMessage(message, message2, null));
    }

    public void info(String message, String message2, String message3) {
        System.out.println(formatMessage(message, message2, message3));
    }

    public void error(String message) {
        System.err.println(formatMessage(message, null, null));
    }

    public void error(String message, Throwable t) {
        System.err.println(formatMessage(message, null, null) + "\n" + t.getMessage());
    }

    public void debug(String message) {
        System.out.println(formatMessage(message, null, null));
    }

    public void warn(String message) {
        System.out.println(formatMessage(message, null, null));
    }

    private String formatMessage(String message, String message2, String message3) {
        if (message2 != null && message3 != null) {
            return String.format("[%s]: " + message, className, message2, message3);
        }
        if (message2 != null) {
            return String.format("[%s]: " + message, className, message2);
        }
        return String.format("[%s]: %s", className, message);
    }
}