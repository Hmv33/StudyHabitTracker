import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudyTracker tracker = new StudyTracker("sessions.csv");
        tracker.loadFromFile();

        System.out.println("Welcome to Study Habit Tracker");
        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Add new session");
            System.out.println("2. View summary (today/last 7 days/all time)");
            System.out.println("3. List all sessions");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addSessionFlow(sc, tracker);
                    tracker.saveToFile();
                    break;
                case "2":
                    summaryFlow(sc, tracker);
                    break;
                case "3":
                    for (StudySession s : tracker.getAllSessions()) {
                        System.out.println(s);
                    }
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        tracker.saveToFile();
        System.out.println("Goodbye!");
    }

    private static void addSessionFlow(Scanner sc, StudyTracker tracker) {
        System.out.print("Subject: ");
        String subject = sc.nextLine().trim();
        if (subject.isEmpty()) {
            System.out.println("Subject cannot be empty.");
            return;
        }

        System.out.print("Minutes (e.g., 90): ");
        int minutes = readInt(sc);
        if (minutes <= 0) {
            System.out.println("Minutes must be positive.");
            return;
        }

        System.out.print("Date [YYYY-MM-DD, blank for today]: ");
        String dateStr = sc.nextLine().trim();
        LocalDate date = LocalDate.now();
        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Using today.");
            }
        }

        StudySession s = new StudySession(date, subject, minutes);
        tracker.addSession(s);
        System.out.println("Added: " + s);
    }

    private static void summaryFlow(Scanner sc, StudyTracker tracker) {
        LocalDate today = LocalDate.now();
        System.out.println("\nChoose range: ");
        System.out.println("1. Today");
        System.out.println("2. Last 7 days");
        System.out.println("3. All time");
        System.out.print("Option: ");
        String opt = sc.nextLine().trim();

        LocalDate from;
        LocalDate to = today;

        switch (opt) {
            case "1": from = today; break;
            case "2": from = today.minusDays(6); break;
            case "3": from = LocalDate.of(1970, 1, 1); break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        Map<String, Integer> bySubject = tracker.minutesBySubject(from, to);
        if (bySubject.isEmpty()) {
            System.out.println("No sessions in this range.");
            return;
        }

        System.out.println("\nSummary " + FMT.format(from) + " to " + FMT.format(to));
        for (Map.Entry<String, Integer> e : bySubject.entrySet()) {
            System.out.println(e.getKey() + ": " + minutesToHoursMins(e.getValue()));
        }
        int total = tracker.totalMinutes(from, to);
        System.out.println("Total: " + minutesToHoursMins(total));
    }

    private static int readInt(Scanner sc) {
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String minutesToHoursMins(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        if (h > 0) return h + "h " + m + "m";
        return m + "m";
    }
}
