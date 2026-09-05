package securescan.models;

public class Finding {

    private final String rule;
    private final String fileName;
    private final int line;
    private final String message;
    private final String severity;

    public Finding(String rule, String fileName, int line, String message, String severity) {
        this.rule = rule;
        this.fileName = fileName;
        this.line = line;
        this.message = message;
        this.severity = severity;
    }
    public String getRule() {
        return rule;
    }
    public String getFileName() {
        return fileName;
    }
    public int getLine() {
        return line;
    }
    public String getMessage() {
        return message;
    }
    public String getSeverity() {
        return severity;
    }
}