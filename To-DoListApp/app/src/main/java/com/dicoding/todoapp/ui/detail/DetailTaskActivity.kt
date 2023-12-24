package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {
    private lateinit var detailTaskViewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        var etTitle = findViewById<EditText>(R.id.detail_ed_title)
        var etDescription = findViewById<EditText>(R.id.detail_ed_description)
        var etDueDate = findViewById<EditText>(R.id.detail_ed_due_date)
        var btnDelete = findViewById<Button>(R.id.btn_delete_task)

        //TODO 11 : Show detail task and implement delete action
        val taskId = intent.getIntExtra(TASK_ID, 1)

        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        detailTaskViewModel.setTaskId(taskId)
        detailTaskViewModel.task.observe(this) {
            if (it != null) {
                etTitle.setText(it.title)
                etDescription.setText(it.description)
                etDueDate.setText(DateConverter.convertMillisToString(it.dueDateMillis))
            }
        }

        btnDelete.setOnClickListener {
            detailTaskViewModel.deleteTask()
            finish()
        }
    }
}