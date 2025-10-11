package com.automation.models;

import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;

/**
 * Bug entity in the bug tracking app.
 *
 * @author Rom
 * @version 1.0
 */
public class Bug {
    private Integer bugId;
    private String date;
    private String title;
    private String stepsToReproduce;
    private String expectedResult;
    private String actualResult;
    private BugStatus status;
    private BugSeverity severity;
    private BugPriority priority;
    private String detectedBy;
    private String fixedBy;
    private String dateClosed;
    private String attachedFile;

    /**
     * Constructor creates an empty Bug instance
     */
    public Bug() {
    }

    /**
     * Private constructor used by the Builder pattern.
     *
     * @param builder The builder instance containing bug data
     */
    private Bug(Builder builder) {
        this.bugId = builder.bugId;
        this.date = builder.date;
        this.title = builder.title;
        this.stepsToReproduce = builder.stepsToReproduce;
        this.expectedResult = builder.expectedResult;
        this.actualResult = builder.actualResult;
        this.status = builder.status;
        this.severity = builder.severity;
        this.priority = builder.priority;
        this.detectedBy = builder.detectedBy;
        this.fixedBy = builder.fixedBy;
        this.dateClosed = builder.dateClosed;
        this.attachedFile = builder.attachedFile;
    }

    // Getters
    public Integer getBugId() {return bugId;}
    public String getDate() {
        return date;
    }
    public String getTitle() {
        return title;
    }
    public String getStepsToReproduce() {
        return stepsToReproduce;
    }
    public String getExpectedResult() {
        return expectedResult;
    }
    public String getActualResult() { return actualResult;}

    public BugStatus getStatus() {return status;}

    public void setStatus(BugStatus status) {this.status = status;}
    public BugSeverity getSeverity() {
        return severity;
    }
    public BugPriority getPriority() {
        return priority;
    }
    public String getDetectedBy() {
        return detectedBy;
    }
    public String getFixedBy() {
        return fixedBy;
    }
    public String getDateClosed() {
        return dateClosed;
    }
    public String getAttachedFile() { return attachedFile;}

    @Override
    public String toString() {
        return "Bug{" +
                "bugId=" + bugId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", severity=" + severity +
                ", priority=" + priority +
                ", detectedBy='" + detectedBy + '\'' +
                '}';
    }

    /**
     * Builder class for constructing Bug instances using the Builder.
     */
    public static class Builder {
        private Integer bugId;
        private String date;
        private String title;
        private String stepsToReproduce;
        private String expectedResult;
        private String actualResult;
        private BugStatus status;
        private BugSeverity severity;
        private BugPriority priority;
        private String detectedBy;
        private String fixedBy;
        private String dateClosed;
        private String attachedFile;

        public Builder bugId(Integer bugId) {
            this.bugId = bugId;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder stepsToReproduce(String stepsToReproduce) {
            this.stepsToReproduce = stepsToReproduce;
            return this;
        }

        public Builder expectedResult(String expectedResult) {
            this.expectedResult = expectedResult;
            return this;
        }

        public Builder actualResult(String actualResult) {
            this.actualResult = actualResult;
            return this;
        }

        public Builder status(BugStatus status) {
            this.status = status;
            return this;
        }

        public Builder severity(BugSeverity severity) {
            this.severity = severity;
            return this;
        }

        public Builder priority(BugPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder detectedBy(String detectedBy) {
            this.detectedBy = detectedBy;
            return this;
        }

        public Builder fixedBy(String fixedBy) {
            this.fixedBy = fixedBy;
            return this;
        }

        public Builder dateClosed(String dateClosed) {
            this.dateClosed = dateClosed;
            return this;
        }

        public Builder attachedFile(String attachedFile) {
            this.attachedFile = attachedFile;
            return this;
        }

        /**
         * Builds and returns the Bug instance.
         *
         * @return A new Bug instance with the configured properties
         */
        public Bug build() {
            return new Bug(this);
        }
    }
}
