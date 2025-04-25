package com.example.nordiccal;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LoginActivity handles user authentication including:
 * - Login and signup toggling
 * - Password reset dialog
 * - Verification dialog after signup
 * - Redirecting to MainActivity on successful login/signup or if already logged in
 */
public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        checkIfUserAlreadyLoggedIn();
    }

    /**
     * Checks if a user is already logged in and verified.
     * If verified, skip auth and redirect to MainActivity. Otherwise, sign them out.
     */
    private void checkIfUserAlreadyLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful() && user.isEmailVerified()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    FirebaseAuth.getInstance().signOut();
                }
            });
        }
    }

    /**
     * Called when the login/signup button is clicked.
     * Delegates to the appropriate handler based on login mode.
     */
    public void onAuthButtonClick(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isLoginMode) {
            handleLogin(email, password);
        } else {
            handleSignup(email, password);
        }
    }

    /** Handles user sign-in using FirebaseAuth */
    private void handleLogin(String email, String password) {
        AuthManager.signIn(email, password, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Please verify your email before continuing.", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                }
            } else {
                Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Handles user sign-up using FirebaseAuth */
    private void handleSignup(String email, String password) {
        AuthManager.signUp(this, email, password, task -> {
            if (task.isSuccessful()) {
                showVerificationDialog();
            } else {
                Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Toggles UI between login and sign-up mode */
    public void onToggleSignLogTextClick(View view) {
        isLoginMode = !isLoginMode;
        updateAuthModeUI();
    }

    /** Updates UI labels and button text based on the current auth mode */
    private void updateAuthModeUI() {
        ((TextView) findViewById(R.id.welcomeBackTextView)).setText(isLoginMode ? "Welcome Back" : "Create Account");
        ((Button) findViewById(R.id.loginButton)).setText(isLoginMode ? "Log In" : "Sign Up");
        ((TextView) findViewById(R.id.signUpText)).setText(isLoginMode ? "Sign up" : "Log in");
    }

    /** Launches the forgot password dialog */
    public void onForgotPasswordTextClick(View view) {
        showForgotPasswordDialog();
    }

    /** Constructs and shows the forgot password modal */
    private void showForgotPasswordDialog() {
        Dialog dialog = createStyledDialog(R.layout.forgot_password_dialog);
        setupForgotPasswordLogic(dialog);
        dialog.show();
    }

    /** Constructs and shows the email verification dialog */
    private void showVerificationDialog() {
        Dialog dialog = createStyledDialog(R.layout.verification_dialog);
        setupVerificationDialogLogic(dialog);
        dialog.show();
    }

    /**
     * Creates a reusable, styled dialog for modals
     * @param layoutId the layout resource to use
     * @return a Dialog instance with window styling applied
     */
    private Dialog createStyledDialog(int layoutId) {
        Dialog dialog = new Dialog(this, R.style.DialogFadePop);
        dialog.setContentView(layoutId);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.6f);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }

    /**
     * Handles the logic for sending the password reset email via Firebase
     * @param dialog the dialog instance containing the UI elements
     */
    private void setupForgotPasswordLogic(Dialog dialog) {
        EditText emailInput = dialog.findViewById(R.id.forgotEmailInput);
        Button sendButton = dialog.findViewById(R.id.sendResetEmailButton);

        sendButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthManager.sendPasswordResetEmail(email, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reset email sent.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Handles the verification dialog functionality, including resend cooldown and continue button
     * @param dialog the dialog instance containing the UI elements
     */
    private void setupVerificationDialogLogic(Dialog dialog) {
        Button openEmailButton = dialog.findViewById(R.id.openEmailButton);
        Button resendEmailButton = dialog.findViewById(R.id.resendEmailButton);

        openEmailButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            startActivity(intent);
        });

        resendEmailButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !user.isEmailVerified()) {
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Verification email resent.", Toast.LENGTH_SHORT).show();
                        startResendCooldown(resendEmailButton);
                    } else {
                        Toast.makeText(this, "Failed to resend verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Disables the resend button for 60 seconds and updates its text with a countdown
     * @param button the resend button to disable and update
     */
    private void startResendCooldown(Button button) {
        button.setEnabled(false);

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                String text = "Resend in " + (millisUntilFinished / 1000) + "s";

            }

            public void onFinish() {
                String text = "Resend Email";
                button.setText(text);
                button.setEnabled(true);
            }
        }.start();
    }

    /**
     * Called when the activity resumes from a paused state, such as when the user returns from the email app.
     * If the user has signed up but hasn't been verified yet, this method checks whether their email is now verified.
     * If verified, it redirects the user to the main dashboard.
     */
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !isLoginMode) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful() && user.isEmailVerified()) {
                    Toast.makeText(this, "Email verified! Redirecting...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            });
        }
    }
}
