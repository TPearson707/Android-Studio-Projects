package com.example.eatcookie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public void eatCookie(View view) {
        // update ImageView to show orgasmic face
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.full);

        // update TextView to show "I'm so full"
        TextView textView = findViewById(R.id.textView);
        textView.setText(R.string.not_hungry);

        // update Button to show "Done" and disable it
        Button button = findViewById(R.id.button);
        button.setText(R.string.done_eating_button_text);

        button.setOnClickListener(v -> {
           finish();
        });
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