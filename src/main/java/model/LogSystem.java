package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LogSystem {
    private static final int MAX_LOG_ENTRIES = 500;
    private LinkedList<String> logEntries;
    private static LogSystem instance;
    private static final String LOG_FILE = "src/main/resources/log.txt"; // ✅ Ensure correct file path
    private String lastLogMessage = "";

    private LogSystem() {
        logEntries = new LinkedList<>();
        clearLogFile();
        loadLogsFromFile(); // ✅ Load logs when LogSystem is initialized
    }

    public static LogSystem getInstance() {
        if (instance == null) {
            instance = new LogSystem();
        }
        return instance;
    }

    /** ✅ Clear log.txt on start */
    private void clearLogFile() {
        try (FileWriter writer = new FileWriter(LOG_FILE, false)) {
            writer.write(""); // Clear content
            java.lang.System.out.println("🗑 log.txt cleared on startup.");
        } catch (IOException e) {
            java.lang.System.out.println("❌ Error clearing log file: " + e.getMessage());
        }
    }

    /** ✅ Load logs from log.txt into memory */
    private void loadLogsFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(LOG_FILE));
            logEntries.addAll(lines);

            // ✅ Keep only the last MAX_LOG_ENTRIES logs
            while (logEntries.size() > MAX_LOG_ENTRIES) {
                logEntries.poll();
            }

//            System.out.println("✅ Loaded " + logEntries.size() + " logs from file.");
        } catch (IOException e) {
            java.lang.System.out.println("❌ Error reading log file: " + e.getMessage());
        }
    }

    /** ✅ Log an event and write to file */
    public void logEvent(String event) {
        if (event.equals(lastLogMessage)) return; // Avoid duplicate logs

        String logEntry = "[" + LocalDateTime.now() + "] " + event;
        lastLogMessage = event;
        logEntries.add(logEntry);

//        System.out.println("📌 Log added: " + logEntry);
//        System.out.println("📝 Total log entries in memory: " + logEntries.size());

        if (logEntries.size() > MAX_LOG_ENTRIES) {
            logEntries.poll(); // Remove oldest log entry
        }

        writeToFile(logEntry);
    }

    /** ✅ Write log entry to log.txt */
    private void writeToFile(String logEntry) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(logEntry + "\n");
        } catch (IOException e) {
            java.lang.System.out.println("❌ Error writing to log file: " + e.getMessage());
        }
    }

    /** ✅ Fetch all logs */
    public String getAllLogs() {
        java.lang.System.out.println("Fetching logs: " + logEntries.size() + " entries found.");
        return String.join("\n", logEntries);
    }

    /** ✅ Filter logs based on type */
    public String getFilteredLogs(String filter) {
        return logEntries.stream()
                .filter(entry -> {
                    switch (filter) {
                        case "Plant Event":
                            return entry.contains("Added") || entry.contains("Removed") || 
                                   entry.contains("harvested") || entry.contains("grown");
                        case "System Action":
                            return entry.contains("system activated") || entry.contains("system deactivated") ||
                                   entry.contains("turned on") || entry.contains("turned off");
                        case "Environmental Change":
                            return entry.contains("Temperature") || entry.contains("Water") || 
                                   entry.contains("Sunlight") || entry.contains("evaporated");
                        case "API Trigger":
                            return entry.contains("API") || entry.contains("triggered") || 
                                   entry.contains("called");
                        case "Logging Error":
                            return entry.contains("Error") || entry.contains("Failed") || 
                                   entry.contains("Exception");
                        default:
                            return true;
                    }
                })
                .collect(Collectors.joining("\n"));
    }
}