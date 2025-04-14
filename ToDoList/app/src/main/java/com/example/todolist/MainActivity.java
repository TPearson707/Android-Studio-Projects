package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String FILE_NAME = "tasks.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Load tasks from the file when the activity is created
        loadTasksFromFile();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addTask(View view) {
        // Get the task from the input field
        EditText editText = findViewById(R.id.editTextText);
        String task = editText.getText().toString();

        // Create a new checkbox for the task
        CheckBox newCheckBox = new CheckBox(this);
        newCheckBox.setText(task);
        newCheckBox.setTextColor(getResources().getColor(R.color.white));
        newCheckBox.setBackgroundColor(getResources().getColor(R.color.darker_nordic));
        newCheckBox.setTextSize(24);

        // Set the long press listener for removing the checkbox
        setLongClickListenerForRemoval(newCheckBox);

        // Get a reference to the checkbox container
        LinearLayout checkboxContainer = findViewById(R.id.checkboxContainer);

        // Clear the input field
        editText.setText("");

        // Add the new checkbox to the container if the task is not empty
        if (!task.isEmpty()) {
            checkboxContainer.addView(newCheckBox);

            // Save the task to the file
            saveTaskToFile(task);
        }
    }

    // Function to set a long press listener for removing the checkbox
    private void setLongClickListenerForRemoval(CheckBox checkBox) {
        checkBox.setOnLongClickListener(v -> {
            LinearLayout checkboxContainer = findViewById(R.id.checkboxContainer);
            checkboxContainer.removeView(v); // Remove the checkbox from the container

            String taskToRemove = ((CheckBox) v).getText().toString();
            List<String> updatedTasks = new ArrayList<>();

            // Step 1: Read all tasks from the file
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(FILE_NAME)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(taskToRemove)) {
                        updatedTasks.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Step 2: Write updated task list back to the file
            try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                 OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                for (String task : updatedTasks) {
                    osw.write(task + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true; // Indicate that the long press was handled
        });
    }


    // Function to save a task to the file
    private void saveTaskToFile(String task) {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND); // Open file for appending
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(task + "\n"); // Write the task to the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to load tasks from the file and display them
    private void loadTasksFromFile() {
        LinearLayout checkboxContainer = findViewById(R.id.checkboxContainer);
        try (FileInputStream fis = openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {
            String task;
            while ((task = reader.readLine()) != null) {
                // Create a new checkbox for each task
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(task);
                checkBox.setTextColor(getResources().getColor(R.color.white));
                checkBox.setBackgroundColor(getResources().getColor(R.color.darker_nordic));
                checkBox.setTextSize(24);

                // Set the long press listener for removing the checkbox
                setLongClickListenerForRemoval(checkBox);

                // Add the checkbox to the container
                checkboxContainer.addView(checkBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}