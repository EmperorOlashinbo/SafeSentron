import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static class AffirmationsAndGoals {
        private static final String[] affirmations = {
                "I am capable of achieving anything I set my mind to.",
                "I am confident and determined to succeed.",
                "I believe in myself and my abilities.",
                "I am worthy of love, success, and happiness.",
                "I embrace challenges as opportunities for growth.",
                "I am the GOAT (GREATEST OF ALL TIME)"
                // Add more affirmations as needed
        };

        private static final String[] goals = {
                "My goal is to practice gratitude every day.",
                "My goal is to prioritize self-care and well-being.",
                "My goal is to learn something new each day.",
                "My goal is to cultivate positive relationships.",
                "My goal is to pursue my passions and dreams with enthusiasm."
                // Add more goals as needed
        };

        private static final Random random = new Random();

        public static String getRandomAffirmation() {
            int index = random.nextInt(affirmations.length);
            return affirmations[index];
        }

        public static String getRandomGoal() {
            int index = random.nextInt(goals.length);
            return goals[index];
        }
    }

    public static class ActivityReportsAndAlerts {
        // Simulated data representing the child's online activities
        private static final List<String> onlineActivities = new ArrayList<>();
        private static final Random random = new Random();

        // Method to add online activity
        public static void addOnlineActivity(String activity) {
            onlineActivities.add(activity);
        }

        // Method to generate activity report
        public static String generateActivityReport() {
            StringBuilder report = new StringBuilder();
            report.append("Activity Report:\n");
            for (String activity : onlineActivities) {
                report.append("- ").append(activity).append("\n");
            }
            return report.toString();
        }

        // Method to generate random online activities
        public static void generateRandomOnlineActivities(int numActivities) {
            String[] activities = {
                    "Visited educational website",
                    "Played online game",
                    "Chat with friends",
                    "Watched video tutorial",
                    "Searched for information",
                    "Posted on social media",
                    "Read news articles",
                    "Listened to music",
                    "Completed online quiz",
                    // Add more activities as needed
            };
            for (int i = 0; i < numActivities; i++) {
                int randomIndex = random.nextInt(activities.length);
                onlineActivities.add(activities[randomIndex]);
            }
        }

        // Method to check for potentially risky behavior or concerning trends
        public static boolean checkForConcerningActivity() {
            // Logic to analyze online activities and detect concerning patterns
            // For simplicity, let's assume a basic check for specific keywords
            for (String activity : onlineActivities) {
                if (activity.contains("inappropriate") || activity.contains("dangerous")) {
                    return true; // Found concerning activity
                }
            }
            return false;
        }

        // Method to send alert to parent if concerning activity is detected
        public static void sendAlertToParent() {
            if (checkForConcerningActivity()) {
                // Simulated alert sending (replace with actual implementation)
                System.out.println("ALERT: Concerning activity detected! Please check the activity report.");
            }
        }
    }

    public static void main(String[] args) {
        // Example usage of AffirmationsAndGoals
        System.out.println("Positive Affirmation: " + AffirmationsAndGoals.getRandomAffirmation());
        System.out.println("Goal Setting: " + AffirmationsAndGoals.getRandomGoal());

        // Generate random online activities
        ActivityReportsAndAlerts.generateRandomOnlineActivities(10); // Change the number as needed

        // Generate activity report
        String report = ActivityReportsAndAlerts.generateActivityReport();
        System.out.println(report);

        // Check for concerning activity and send alert if detected
        ActivityReportsAndAlerts.sendAlertToParent();
    }
}
