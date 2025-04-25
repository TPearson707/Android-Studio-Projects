package com.example.todolistfirebase;

public class Task {
    public String text;
    public boolean completed;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    }

    public Task(String text, boolean completed) {
        this.text = text;
        this.completed = completed;
    }
}