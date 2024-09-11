package br.edu.satc.todolistcompose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import br.edu.satc.todolistcompose.ui.screens.HomeScreen
import br.edu.satc.todolistcompose.ui.theme.ToDoListComposeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // Inicializa o banco de dados com Room
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "tasks.satc"
        )
            .allowMainThreadQueries() // Permite consultas na thread principal
            .build()

        // Inicializa o TaskDao
        val taskDao = db.taskDao()
        
        // Definindo o conteúdo da tela
        setContent {
            ToDoListComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(taskDao) // Passando o taskDao para a HomeScreen
                }
            }
        }
    }
}
