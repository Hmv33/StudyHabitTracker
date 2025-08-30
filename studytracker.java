import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class StudyTracker {
    private final List<StudySession> sessions = new ArrayList<>();
    private final File storage;

    public StudyTracker(String filename) {
        this.storage = new File(filename);
    }

    public void loadFromFile() {
        sessions.clear();
        if (!storage.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(storage))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StudySession s = StudySession.fromCsv(line);
                if (s != null) sessions.add(s);
            }
        } catch (IOException e) {
            System.out.println("Couldn't read " + storage.getName() + ": " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(storage))) {
            for (StudySession s : sessions) {
                pw.println(s.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Couldn't save: " + e.getMessage());
        }
    }

    public void addSession(StudySession s) {
        sessions.add(s);
    }

    public Map<String, Integer> minutesBySubject(LocalDate from, LocalDate to) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (StudySession s : sessions) {
            if (!s.getDate().isBefore(from) && !s.getDate().isAfter(to)) {
                map.put(s.getSubject(), map.getOrDefault(s.getSubject(), 0) + s.getMinutes());
            }
        }
        return sortByValueDescending(map);
    }

    public int totalMinutes(LocalDate from, LocalDate to) {
        int total = 0;
        for (StudySession s : sessions) {
            if (!s.getDate().isBefore(from) && !s.getDate().isAfter(to)) {
                total += s.getMinutes();
            }
        }
        return total;
    }

    public List<StudySession> getAllSessions() {
        return new ArrayList<>(sessions);
    }

    private static Map<String, Integer> sortByValueDescending(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        Map<String, Integer> res = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : list) res.put(e.getKey(), e.getValue());
        return res;
    }
}
