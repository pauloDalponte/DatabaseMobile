@file:OptIn(ExperimentalMaterial3Api::class)

package br.edu.satc.todolistcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.satc.todolistcompose.TaskDao
import br.edu.satc.todolistcompose.TaskTable
import br.edu.satc.todolistcompose.ui.components.TaskCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(taskDao: TaskDao) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var tasks by remember { mutableStateOf(emptyList<TaskTable>()) }

    // Atualiza a lista de tarefas ao inicializar
    LaunchedEffect(Unit) {
        tasks = taskDao.getAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ToDoList UniSATC") },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nova tarefa") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                onClick = { showBottomSheet = true }
            )
        }
    ) { innerPadding ->
        HomeContent(innerPadding, tasks, taskDao)
        NewTask(showBottomSheet = showBottomSheet, taskDao = taskDao) {
            showBottomSheet = false
            // Atualiza a lista de tarefas após a inserção
            tasks = taskDao.getAll()
        }
    }
}

@Composable
fun HomeContent(innerPadding: PaddingValues, tasks: List<TaskTable>, taskDao: TaskDao) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        for (task in tasks) {
            TaskCard(
                title = task.title ?: "",
                description = task.description ?: "",
                isChecked = task.status ?: false,
                onCheckedChange = { checked ->
                    // Atualiza o status da tarefa no banco de dados
                    taskDao.updateTaskStatus(task.uid, checked)
                }
            )
        }
    }
}

@Composable
fun NewTask(showBottomSheet: Boolean, taskDao: TaskDao, onComplete: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onComplete()
            },
            sheetState = sheetState,
        ) {
            // Conteúdo da Bottom Sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text(text = "Título da tarefa") }
                )
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text(text = "Descrição da tarefa") }
                )
                Button(modifier = Modifier.padding(top = 4.dp), onClick = {
                    // Insere a nova tarefa no banco de dados
                    scope.launch {
                        try {
                            taskDao.insertTask(
                                TaskTable(
                                    uid = 0, // AutoGenerate cuida disso
                                    title = taskTitle,
                                    description = taskDescription,
                                    status = false
                                )
                            )
                            sheetState.hide()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onComplete() // Fecha o BottomSheet após a inserção
                        }
                    }
                }) {
                    Text("Salvar")
                }
            }
        }
    }
}
