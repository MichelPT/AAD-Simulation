package com.dicoding.courseschedule.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {
    private lateinit var viewModel: AddCourseViewModel
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_course)

        val factory = AddCourseViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]

        val spinner: Spinner = findViewById(R.id.spinner_day)
        ArrayAdapter.createFromResource(
            this,
            R.array.day,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        viewModel.saved.observe(this) {
            if (it.getContentIfNotHandled() != true) {
                val message = getString(R.string.input_empty_message)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                onBackPressed()
            }
        }

        val startButton = findViewById<ImageButton>(R.id.ib_start_time)
        startButton.setOnClickListener {
            showStartTimePicker(it)
        }
        val endButton = findViewById<ImageButton>(R.id.ib_end_time)
        endButton.setOnClickListener {
            showEndTimePicker(it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                val courseName =
                    findViewById<TextInputEditText>(R.id.ed_course_name).text.toString().trim()
                val day = findViewById<Spinner>(R.id.spinner_day).selectedItemPosition
                val startTime =
                    findViewById<TextView>(R.id.tv_start_time).text.toString().trim()
                val endTime = findViewById<TextView>(R.id.tv_end_time).text.toString().trim()
                val lecturer =
                    findViewById<TextInputEditText>(R.id.ed_lecturer).text.toString().trim()
                val note = findViewById<TextInputEditText>(R.id.ed_note).text.toString().trim()


                if (courseName.isEmpty() || lecturer.isEmpty() || note.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Please fill in the fields first",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.insertCourse(courseName, day, startTime, endTime, lecturer, note)
                    finish()
                    Toast.makeText(applicationContext, "Course added", Toast.LENGTH_SHORT).show()
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showStartTimePicker(view: View) {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.show(supportFragmentManager, "startPicker")
        this.view = view
    }

    fun showEndTimePicker(view: View) {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.show(supportFragmentManager, "endPicker")
        this.view = view
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calender = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (view.id) {
            R.id.ib_start_time -> {
                findViewById<TextView>(R.id.tv_start_time).text =
                    timeFormat.format(calender.time)
            }

            R.id.ib_end_time -> {
                findViewById<TextView>(R.id.tv_end_time).text =
                    timeFormat.format(calender.time)

            }
        }
    }
}