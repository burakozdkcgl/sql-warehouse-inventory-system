package logic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class ErrorLogger {
    public static void log(Exception e) {
        try (FileOutputStream fos = new FileOutputStream("errorlog.txt", true)) {
            StringBuilder logMessage = new StringBuilder("[" + LocalDateTime.now() + "] " + e + "\\n");
            for (StackTraceElement ste : e.getStackTrace()) {
                logMessage.append("\\tat ").append(ste).append("\\n");
            }
            logMessage.append("\\n");
            fos.write(logMessage.toString().getBytes());
        } catch (IOException ioEx) {
            ioEx.printStackTrace(); // fallback
        }
    }
}