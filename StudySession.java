import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudySession {
    private LocalDate date;
    private String subject;
    private int minutes;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public StudySession(LocalDate date, String subject, int minutes) {
        this.date = date;
        this.subject = subject.trim();
        this.minutes = minutes;
    }

    public LocalDate getDate() { return date; }
    public String getSubject() { return subject; }
    public int getMinutes() { return minutes; }

    public String toCsv() {
        return FMT.format(date) + "," + subject.replace(",", " ") + "," + minutes;
    }

    public static StudySession fromCsv(String line) {
        String[] parts = line.split(",", 3);
        if (parts.length != 3) return null;
        LocalDate d = LocalDate.parse(parts[0].trim(), FMT);
        String subj = parts[1].trim();
        int mins = Integer.parseInt(parts[2].trim());
        return new StudySession(d, subj, mins);
    }

    @Override
    public String toString() {
        return FMT.format(date) + " - " + subject + " : " + minutes + " mins";
    }
}
