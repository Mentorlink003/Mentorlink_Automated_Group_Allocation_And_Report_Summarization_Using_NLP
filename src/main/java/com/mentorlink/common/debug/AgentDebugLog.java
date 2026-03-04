package com.mentorlink.common.debug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class AgentDebugLog {
    private AgentDebugLog() {}

    public static void log(String sessionId, String runId, String hypothesisId, String location, String message, String dataJson) {
        try {
            long ts = System.currentTimeMillis();
            String safeData = dataJson == null ? "{}" : dataJson;
            String line = "{\"sessionId\":\"" + esc(sessionId) + "\"," +
                    "\"runId\":\"" + esc(runId) + "\"," +
                    "\"hypothesisId\":\"" + esc(hypothesisId) + "\"," +
                    "\"location\":\"" + esc(location) + "\"," +
                    "\"message\":\"" + esc(message) + "\"," +
                    "\"data\":" + safeData + "," +
                    "\"timestamp\":" + ts +
                    "}\n";
            Path p = Paths.get(System.getProperty("user.dir"), "debug-99a5a7.log");
            Files.writeString(p, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

