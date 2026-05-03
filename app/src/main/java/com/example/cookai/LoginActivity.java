// FILE:
// app > java > com.example.cookai > LoginActivity.java

package com.example.cookai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    // =========================
    // VIEW
    // =========================
    private EditText email;
    private EditText password;

    private Button loginButton;
    private Button googleButton;

    private TextView registerText;

    // =========================
    // FIREBASE
    // =========================
    private FirebaseAuth auth;

    // =========================
    // GOOGLE SIGN-IN
    // =========================
    private GoogleSignInClient googleSignInClient;

    private static final int RC_SIGN_IN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================
        // LAYOUT
        // =========================
        setContentView(R.layout.activity_login);

        // =========================
        // FIREBASE INIT
        // =========================
        auth = FirebaseAuth.getInstance();

        // =========================
        // VIEW BINDING
        // =========================
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);

        loginButton = findViewById(R.id.loginButton);
        googleButton = findViewById(R.id.googleButton);

        registerText = findViewById(R.id.registerText);

        // =========================
        // GOOGLE CONFIG
        // =========================
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(
                                getString(
                                        R.string.default_web_client_id
                                )
                        )
                        .requestEmail()
                        .build();

        googleSignInClient =
                GoogleSignIn.getClient(this, gso);

        // =========================
        // LOGIN EMAIL
        // =========================
        loginButton.setOnClickListener(
                v -> loginUser()
        );

        // =========================
        // LOGIN GOOGLE
        // =========================
        googleButton.setOnClickListener(v -> {

            Intent signInIntent =
                    googleSignInClient.getSignInIntent();

            startActivityForResult(
                    signInIntent,
                    RC_SIGN_IN
            );
        });

        // =========================
        // GO TO REGISTER
        // =========================
        registerText.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    // =========================
    // EMAIL LOGIN
    // =========================
    private void loginUser() {

        String userEmail =
                email.getText()
                        .toString()
                        .trim();

        String userPassword =
                password.getText()
                        .toString()
                        .trim();

        // =========================
        // VALIDATION
        // =========================
        if (userEmail.isEmpty()) {

            email.setError("Email wajib diisi");
            email.requestFocus();

            return;
        }

        if (userPassword.isEmpty()) {

            password.setError("Password wajib diisi");
            password.requestFocus();

            return;
        }

        // =========================
        // FIREBASE LOGIN
        // =========================
        auth.signInWithEmailAndPassword(
                        userEmail,
                        userPassword
                )
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "✅ Login berhasil",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                MainActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Login gagal: "
                                        + task.getException()
                                        .getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =========================
    // GOOGLE RESULT
    // =========================
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn
                            .getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account =
                        task.getResult(
                                ApiException.class
                        );

                if (account != null) {

                    firebaseAuthWithGoogle(
                            account.getIdToken()
                    );
                }

            } catch (ApiException e) {

                Toast.makeText(
                        LoginActivity.this,
                        "Google Sign-In gagal",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // =========================
    // FIREBASE GOOGLE LOGIN
    // =========================
    private void firebaseAuthWithGoogle(
            String idToken
    ) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(
                        idToken,
                        null
                );

        auth.signInWithCredential(credential)
                .addOnCompleteListener(
                        this,
                        task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        LoginActivity.this,
                                        "✅ Login Google berhasil",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent =
                                        new Intent(
                                                LoginActivity.this,
                                                MainActivity.class
                                        );

                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(
                                        LoginActivity.this,
                                        "Autentikasi Google gagal",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
    }
}