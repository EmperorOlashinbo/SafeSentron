package com.example.age_qna

import android.content.res.AssetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {

    private val questionsAndAnswersMap = mutableMapOf<String, List<Pair<String, String>>>()
    private val selectedAgeGroup = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(selectedAgeGroup, questionsAndAnswersMap)
            readAndProcessQuestions()
        }
    }

    private fun readAndProcessQuestions() {
        lifecycleScope.launch(Dispatchers.IO) {
            val assetManager: AssetManager = applicationContext.assets
            val inputStream = assetManager.open("age_qna.txt")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            var currentAgeGroup = ""
            val currentQuestionsAndAnswers = mutableListOf<Pair<String, String>>()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                if (line!!.startsWith("# age: ")) {
                    // Store previous age group questions and answers
                    if (currentAgeGroup.isNotEmpty()) {
                        questionsAndAnswersMap[currentAgeGroup] = currentQuestionsAndAnswers.toList()
                        currentQuestionsAndAnswers.clear()
                    }
                    currentAgeGroup = line!!.substringAfter("# age: ")
                } else if (line!!.startsWith("Q.") && line!!.contains("A.")) {
                    // Extract question and answer from the line
                    val question = line!!.substringAfter("Q.").trim()
                    val answer = line!!.substringAfter("A.").trim()
                    currentQuestionsAndAnswers.add(question to answer)
                }
            }

            // Store the last age group questions and answers
            if (currentAgeGroup.isNotEmpty()) {
                questionsAndAnswersMap[currentAgeGroup] = currentQuestionsAndAnswers.toList()
            }

            // Close the BufferedReader after reading
            bufferedReader.close()
        }
    }
}

@Composable
fun MainContent(
    selectedAgeGroup: MutableStateFlow<String>,
    questionsAndAnswersMap: MutableMap<String, List<Pair<String, String>>>
) {
    val focusManager = LocalFocusManager.current

    var age by remember { mutableStateOf("") }

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Enter your age") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        val ageGroup = determineAgeGroup(age.toIntOrNull())
                        if (ageGroup.isNotEmpty()) {
                            selectedAgeGroup.value = ageGroup
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val ageGroup = selectedAgeGroup.collectAsState().value
            if (ageGroup.isNotEmpty()) {
                val questionsAndAnswers = questionsAndAnswersMap[ageGroup]
                if (questionsAndAnswers != null) {
                    QuestionAndAnswerSection(questionsAndAnswers)
                } else {
                    Text("No questions found for age group $ageGroup", style = MaterialTheme.typography.body1)
                }
            }
        }
    }
}

@Composable
fun QuestionAndAnswerSection(questionsAndAnswers: List<Pair<String, String>>) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf("") }

    if (questionsAndAnswers.isEmpty()) {
        Text("No questions found", style = MaterialTheme.typography.body1)
    } else {
        val currentQuestion = questionsAndAnswers[currentQuestionIndex]

        Column {
            Text(text = "Question ${currentQuestionIndex + 1}: ${currentQuestion.first}")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text("Your answer") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val correctAnswer = currentQuestion.second
                        if (answer.contains(correctAnswer, ignoreCase = true)) {
                            // Correct answer
                            // Move to the next question
                            currentQuestionIndex++
                            if (currentQuestionIndex >= questionsAndAnswers.size) {
                                // End of questions
                                // You can handle this as per your requirement
                                // Maybe show a success message or navigate to another screen
                            } else {
                                answer = "" // Clear the answer for the next question
                            }
                        } else {
                            // Incorrect answer
                            // You can handle this as per your requirement
                            // Maybe show a message to try again or provide the correct answer
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainContent(selectedAgeGroup = MutableStateFlow(""), questionsAndAnswersMap = mutableMapOf())
}

fun determineAgeGroup(age: Int?): String {
    return when (age) {
        in 4..6 -> "4-6"
        in 7..9 -> "7-9"
        in 10..14 -> "10-14"
        in 15..18 -> "15-18"
        else -> ""
    }
}
