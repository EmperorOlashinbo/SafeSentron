import java.util.*

object HealthyHabits {
    private val habits: MutableList<String> = ArrayList()
    private val random = Random()

    // Initialize the list of healthy habits
    init {
        habits.add("Exercise for at least 30 minutes daily.")
        habits.add("Eat a balanced diet with plenty of fruits and vegetables.")
        habits.add("Stay hydrated by drinking at least 8 glasses of water per day.")
        habits.add("Get at least 7-8 hours of quality sleep each night.")
        habits.add("Take a walk for at least 30 minutes daily.")
        habits.add("Eat some protein bars")
        habits.add("Take some vitamin D")
        // Add more healthy habits as needed
    }

    val randomHabit: String
        // Method to get a random healthy habit recommendation
        get() {
            val index = random.nextInt(habits.size)
            return habits[index]
        }

    // Method to track user's adherence to healthy habits
    fun trackHabitAdherence(habit: String, adhered: Boolean) {
        // Implement logic to track user's adherence to healthy habits
        // For example, you could log the habit and whether the user adhered to it
        if (adhered) {
            println("User adhered to the habit: $habit")
            // Additional logic for tracking adherence
        } else {
            println("User did not adhere to the habit: $habit")
            // Additional logic for handling non-adherence
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // Example usage:
        val randomHabit = randomHabit
        println("Random Healthy Habit Recommendation: $randomHabit")

        // Simulate tracking adherence to the random habit (true for adherence, false for non-adherence)
        val adhered = random.nextBoolean() // Generate random boolean value
        trackHabitAdherence(randomHabit, adhered)
    }
}
