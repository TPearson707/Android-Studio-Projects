package com.example.nordiccal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;

/**
 * AuthManager provides a clean abstraction over FirebaseAuth methods:
 * - Sign in
 * - Sign up
 * - Send password reset
 * - Get current user
 * - Sign out
 */
public class AuthManager {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = AuthManager.class.getSimpleName();


    /** Returns the currently logged-in user, or null if none */
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /** Attempts to sign in with the given email/password */
    public static void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    /** Attempts to create a new user with email/password */
    public static void signUp(Context context, String email, String password, OnCompleteListener<AuthResult> listener) {
        createUserAndSendVerification(context, email, password, listener);
    }

    /**
     * Creates a new user with email/password and sends a verification email.
     * This helper is used internally by signUp() and should not be called directly.
     */
    private static void createUserAndSendVerification(Context context, String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && !user.isEmailVerified()) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            Log.d(TAG, "Verification email sent to " + user.getEmail());
                                            Toast.makeText(context, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.e(TAG, "Failed to send verification email", verifyTask.getException());
                                            Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                    listener.onComplete(task);
                });
    }

    /** Sends a password reset email to the given address */
    public static void sendPasswordResetEmail(String email, OnCompleteListener<Void> listener) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(listener);
    }

    /** Signs out the currently authenticated user */
    public static void signOut() {
        mAuth.signOut();
    }
}
