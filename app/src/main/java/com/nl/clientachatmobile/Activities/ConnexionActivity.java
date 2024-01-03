package com.nl.clientachatmobile.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Network.LoginManager;
import com.nl.clientachatmobile.R;

import java.io.IOException;
import java.io.InputStream;

public class ConnexionActivity extends Activity implements View.OnClickListener {

    private Button btnLogin;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private CheckBox checkBoxNewClient;
    private LoginManager loginManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion_screen);

        InputStream inputStream = getResources().openRawResource(R.raw.config);
        Ovesp.getInstance().init(inputStream); //Initialisation de l'objet DataTransfer contenu dans Ovesop et donc de la Socket qui va tenter de joindre le serveur.
        loginManager = new LoginManager();

        btnLogin = findViewById(R.id.btnLogin);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxNewClient = findViewById(R.id.checkBoxNewClient);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        boolean isNewClient = checkBoxNewClient.isChecked();

        loginManager.performLoginAsync(username, password, isNewClient,
                new LoginManager.OnLoginCompleteListener() {
                    @Override
                    public void onLoginComplete() {
                        Intent intent = new Intent(ConnexionActivity.this, PurchaseActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onLoginFailed(String errorMsg) {
                        runOnUiThread(() -> {
                            Log.e("ConnexionActivity DEBUG", errorMsg);
                            Toast.makeText(ConnexionActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }
}
