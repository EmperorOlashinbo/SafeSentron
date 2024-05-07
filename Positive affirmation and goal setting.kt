import java.util.*

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        // Example usage of AffirmationsAndGoals
        println("Positive Affirmation: " + AffirmationsAndGoals.randomAffirmation)
        println("Goal Setting: " + AffirmationsAndGoals.randomGoal)

        // Generate random online activities
        ActivityReportsAndAlerts.generateRandomOnlineActivities(10) // Change the number as needed

        // Generate activity report
        val report = ActivityReportsAndAlerts.generateActivityReport()
        println(report)

        // Check for concerning activity and send alert if detected
        ActivityReportsAndAlerts.sendAlertToParent()
    }

    object AffirmationsAndGoals {
        private val affirmations = arrayOf(
            "I am capable of achieving anything I set my mind to.",
            "I am confident and determined to succeed.",
            "I believe in myself and my abilities.",
            "I am worthy of love, success, and happiness.",
            "I embrace challenges as opportunities for growth.",
            "I am the GOAT (GREATEST OF ALL TIME)" // Add more affirmations as needed
        )

        private val goals = arrayOf(
            "My goal is to practice gratitude every day.",
            "My goal is to prioritize self-care and well-being.",
            "My goal is to learn something new each day.",
            "My goal is to cultivate positive relationships.",
            "My goal is to pursue my passions and dreams with enthusiasm." // Add more goals as needed
        )

        private val random = Random()

        val randomAffirmation: String
            get() {
                val index = random.nextInt(affirmations.size)
                return affirmations[index]
            }

        val randomGoal: String
            get() {
                val index = random.nextInt(goals.size)
                return goals[index]
            }
    }

    object ActivityReportsAndAlerts {
        // Simulated data representing the child's online activities
        private val onlineActivities: MutableList<String> = ArrayList()
        private val random = Random()

        // Method to add online activity
        fun addOnlineActivity(activity: String) {
            onlineActivities.add(activity)
        }

        // Method to generate activity report
        fun generateActivityReport(): String {
            val report = StringBuilder()
            report.append("Activity Report:\n")
            for (activity in onlineActivities) {
                report.append("- ").append(activity).append("\n")
            }
            return report.toString()
        }

        // Method to generate random online activities
        fun generateRandomOnlineActivities(numActivities: Int) {
            val activities = arrayOf(
                "Visited educational website",
                "Played online game",
                "Chat with friends",
                "Watched video tutorial",
                "Searched for information",
                "Posted on social media",
                "Read news articles",
                "Listened to music",
                "Completed online quiz",  // Add more activities as needed
            )
            for (i in 0 until numActivities) {
                val randomIndex = random.nextInt(activities.size)
                onlineActivities.add(activities[randomIndex])
            }
        }

        // Method to check for potentially risky behavior or concerning trends
        fun checkForConcerningActivity(): Boolean {
            // Logic to analyze online activities and detect concerning patterns
            // For simplicity, let's assume a basic check for specific keywords
            for (activity in onlineActivities) {
                if (activity.contains("inappropriate") || activity.contains("dangerous")) {
                    return true // Found concerning activity
                }
            }
            return false
        }

        // Method to send alert to parent if concerning activity is detected
        fun sendAlertToParent() {
            if (checkForConcerningActivity()) {
                // Simulated alert sending (replace with actual implementation)
                println("ALERT: Concerning activity detected! Please check the activity report.")
            }
        }
    }
}