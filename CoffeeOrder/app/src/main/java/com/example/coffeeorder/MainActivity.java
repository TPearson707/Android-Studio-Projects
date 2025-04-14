package com.example.coffeeorder;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public void minusClicked(View view) {
        TextView textView = findViewById(R.id.quantityText);
        int quantity = Integer.parseInt(textView.getText().toString());
        if (quantity >= 1) {
            quantity--;
            textView.setText(String.valueOf(quantity));
        }
    }

    public void plusClicked(View view) {
        TextView textView = findViewById(R.id.quantityText);
        int quantity = Integer.parseInt(textView.getText().toString());
        quantity++;
        textView.setText(String.valueOf(quantity));
    }

    public void orderClicked(View view) {
        // Get the quantity
        TextView textView = findViewById(R.id.quantityText);
        int quantity = Integer.parseInt(textView.getText().toString());

        // Set default prices
        double drinkPrice = 4.00;
        double chocolatePrice = 0;
        double creamPrice = 0;

        // Get references to the check boxes
        CheckBox chocolateCheckBox = findViewById(R.id.chocolate);
        CheckBox creamCheckBox = findViewById(R.id.cream);

        // Set default values
        String isChocolate = "no";
        String isCream = "no";

        // Check if chocolate is checked
        if (chocolateCheckBox.isChecked()) {
            chocolatePrice = 1.00;
            isChocolate = "yes";
        } else {
            chocolatePrice = 0;
            isChocolate = "no";
        }

        // Check if cream is checked
        if (creamCheckBox.isChecked()) {
            creamPrice = 0.50;
            isCream = "yes";
        } else {
            creamPrice = 0;
            isCream = "no";
        }

        // Calculate the total price
        double totalPrice = quantity * (drinkPrice + chocolatePrice + creamPrice);
        String stringifyPrice = String.format(Locale.US,"%.2f", totalPrice);

        // Create the order summary
        String orderSummary = "Add whipped cream? " + isCream + "\nAdd chocolate? " + isChocolate + "\nQuantity: " + quantity + "\n\nPrice: $" + stringifyPrice + "\nTHANK YOU!";
        TextView summaryTextView = findViewById(R.id.summary);
        summaryTextView.setText(orderSummary);
    }

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
}