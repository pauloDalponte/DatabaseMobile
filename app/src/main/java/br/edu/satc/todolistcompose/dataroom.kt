package br.edu.satc.todolistcompose

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity
data class TaskTable(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "status") val status: Boolean?
)
@Dao
interface TaskDao {

    @Query("select * from TaskTable")
    fun getAll(): List<TaskTable>

    @Update
    fun updateAll(vararg tasks: TaskTable)

    @Insert
    fun insertAll(vararg tasks: TaskTable)

    @Query("DELETE FROM TaskTable")
    fun deleteAllTasks()

}

    @Database(entities = [TaskTable::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun taskDao(): TaskDao
}