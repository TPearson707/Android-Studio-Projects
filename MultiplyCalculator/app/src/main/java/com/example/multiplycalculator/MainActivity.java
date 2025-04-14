package com.example.multiplycalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickMultiply(View view) {
        // Get reference to the input field
        TextInputEditText inputField = findViewById(R.id.input);
        TextView resultTextView = findViewById(R.id.textView);

        // Get the input text and convert it to a number
        String inputText = inputField.getText().toString();
        String currentText = resultTextView.getText().toString();

        // Check if the input is a valid number
        if (!inputText.isEmpty()) {
            // Convert the input text to a number and multiply it by the current number
            double inputNumber = Double.parseDouble(inputText);
            double currentNumber = Double.parseDouble(currentText);

            // Check if the current number is 0 and if so, set it to 1 before multiplying
            if (currentNumber == 0) {
                currentNumber += 1;
            }

            // Multiply the input number by the current number and update the text view
            double result = inputNumber * currentNumber;
            resultTextView.setText(String.valueOf(result));
        }

    }

    public void onClickClear(View view) {
        // Get a reference to the text view
        TextView textView = findViewById(R.id.textView);
        // Set the text of the text view to "0"
        textView.setText("0");
    }
}