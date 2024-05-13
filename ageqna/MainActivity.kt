package com.example.ageqna

import android.content.res.AssetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {

    private val questionsAndAnswersMap = mutableMapOf<String, List<Pair<String, String>>>()
    private val selectedAgeGroup = MutableStateFlow("4-6")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(selectedAgeGroup, questionsAndAnswersMap) { ageGroup ->
                displayQuestionsAndAnswersForAgeGroup(ageGroup)
            }
            readAndProcessQuestions()
        }
    }

    private fun readAndProcessQuestions() {
        GlobalScope.launch(Dispatchers.IO) {
            val assetManager: AssetManager = applicationContext.assets
            val inputStream = assetManager.open("age_qna.txt")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            var currentAgeGroup = ""
            var currentQuestionsAndAnswers = mutableListOf<Pair<String, String>>()
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

    private fun displayQuestionsAndAnswersForAgeGroup(ageGroup: String) {
        selectedAgeGroup.value = ageGroup
    }
}

@Composable
fun MainContent(
    selectedAgeGroup: MutableStateFlow<String>,
    questionsAndAnswersMap: MutableMap<String, List<Pair<String, String>>>,
    onAgeGroupSelected: (String) -> Unit
) {
    val ageGroup = selectedAgeGroup.collectAsState().value

    Surface(color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Age Group: $ageGroup", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.padding(8.dp))
            val questionsAndAnswers = questionsAndAnswersMap[ageGroup]
            if (questionsAndAnswers != null) {
                questionsAndAnswers.forEachIndexed { index, pair ->
                    Text("${index + 1}. ${pair.first}\nA. ${pair.second}", style = MaterialTheme.typography.body1)
                }
            } else {
                Text("No questions found for age group $ageGroup", style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainContent(selectedAgeGroup = MutableStateFlow("4-6"), questionsAndAnswersMap = mutableMapOf(), onAgeGroupSelected = {})
}
