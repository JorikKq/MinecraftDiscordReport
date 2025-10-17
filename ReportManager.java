package com.example.minecraftdiscordreports;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportManager {
    private final Map<UUID, Report> reports = new HashMap<>();

    public static class Report {
        private final String reporter;
        private final String target;
        private final String reason;
        private String moderator;
        private String status;

        public Report(String reporter, String target, String reason) {
            this.reporter = reporter;
            this.target = target;
            this.reason = reason;
            this.moderator = "Никем";
            this.status = "Ожидание";
        }

        public String getModerator() { return moderator; }
        public void setModerator(String moderator) { this.moderator = moderator; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public void createReport(String reporter, String target, String reason) {
        reports.put(UUID.randomUUID(), new Report(reporter, target, reason));
    }
}