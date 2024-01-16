package com.nl.clientachatmobile.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nl.clientachatmobile.Models.Data.LanguageSelector;
import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Network.LoginManager;
import com.nl.clientachatmobile.R;

import java.io.IOException;
import java.io.InputStream;

public class ConnexionActivity extends AppCompatActivity implements View.OnClickListener {

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
        Ovesp.getInstance().init(inputStream, this); //Initialisation de l'objet DataTransfer contenu dans Ovesop et donc de la Socket qui va tenter de joindre le serveur.
        loginManager = new LoginManager();

        btnLogin = findViewById(R.id.btnLogin);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxNewClient = findViewById(R.id.checkBoxNewClient);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem itemDeconnexion = menu.findItem(R.id.iconDeconnexion);
        itemDeconnexion.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemlanguageFr) {
            LanguageSelector.selectLanguage(this, "fr");
            recreate();
        }
        else if(item.getItemId() == R.id.itemlanguageEn) {
            LanguageSelector.selectLanguage(this, "en");
            recreate();
        }
        return super.onOptionsItemSelected(item);
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
                            displayToUser(errorMsg);
                        });
                    }
                }
        );
    }

    private void displayToUser(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                });

        builder.create().show();

    }
}
