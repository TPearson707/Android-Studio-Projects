package com.example.todolistfirebase;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        // Load tasks from Firebase
        loadTasksFromFirebase();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    public void addTask(View view) {
        EditText editText = findViewById(R.id.editTextText);
        String taskText = editText.getText().toString();

        if (!taskText.isEmpty()) {
            saveTaskToFirebase(taskText);
            editText.setText("");
        }
    }


    // Function to set a long press listener for removing the checkbox
    private void setLongClickListenerForRemovalFirebase(CheckBox checkBox, String taskId) {
        checkBox.setOnLongClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference taskRef = database.getReference("tasks").child(taskId);
            taskRef.removeValue();

            LinearLayout checkboxContainer = findViewById(R.id.checkboxContainer);
            checkboxContainer.removeView(v);
            return true;
        });
    }

    // Function to add the checkbox to the UI and save it to Firebase
    private void saveTaskToFirebase(String taskText) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("tasks");

        String taskId = tasksRef.push().getKey();
        if (taskId != null) {
            Task newTask = new Task(taskText, false);

            tasksRef.child(taskId).setValue(newTask).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Only add checkbox after the task is saved
                    runOnUiThread(() -> {
                        LinearLayout checkboxContainer = findViewById(R.id.checkboxContainer);
                        CheckBox checkBox = new CheckBox(this);
                        checkBox.setText(taskText);
                        checkBox.setChecked(false);
                        checkBox.setTextColor(ContextCompat.getColor(this, R.color.white));
                        checkBox.setBackgroundColor(ContextCompat.getColor(this, R.color.darker_nordic));
                        checkBox.setTextSize(24);

                        // Listen for checkbox changes and update Firebase
                        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            tasksRef.child(taskId).child("completed").setValue(isChecked);
                        });

                        // Long-press to delete
                        setLongClickListenerForRemovalFirebase(checkBox, taskId);

                        checkboxContainer.addView(checkBox);
                    });
                } else {
                    Log.e("Firebase", "Failed to save task", task.getException());
                }
            });
        }
    }


    // Function to load tasks from the file and display them
    private void loadTasksFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("tasks");
        LinearLayout checkboxContainer = findViewById(R.id.checkboxContainer);
        Context context = this;

        tasksRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkboxContainer.removeAllViews();

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Task taskObj = snapshot.getValue(Task.class);
                    if (taskObj == null) continue;

                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setText(taskObj.text);
                    checkBox.setChecked(taskObj.completed);
                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.white));
                    checkBox.setBackgroundColor(ContextCompat.getColor(context, R.color.darker_nordic));
                    checkBox.setTextSize(24);

                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        snapshot.getRef().child("completed").setValue(isChecked);
                    });

                    setLongClickListenerForRemovalFirebase(checkBox, snapshot.getKey());
                    checkboxContainer.addView(checkBox);
                }
            } else {
                Log.e("Firebase", "Failed to load tasks", task.getException());
            }
        });
    }


}