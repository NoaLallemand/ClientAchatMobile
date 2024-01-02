package com.nl.clientachatmobile.Models.Protocols.OVESP;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.nl.clientachatmobile.Models.Protocols.Protocol;
import com.nl.clientachatmobile.Models.Requests.LoginRequest;
import com.nl.clientachatmobile.Models.Requests.Request;
import com.nl.clientachatmobile.Models.Responses.LoginResponse;
import com.nl.clientachatmobile.Models.Responses.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousByteChannel;

public class Ovesp implements Protocol {

    private DataTransfer dataTransfer;

    private String loginUser;

    private static Ovesp instance;

    private Ovesp() {
        loginUser = "";
    }

    public static Ovesp getInstance() {
        if(instance == null) {
            instance = new Ovesp();
        }
        return instance;
    }

    @SuppressLint("StaticFieldLeak")
    public boolean init(InputStream inputStream) {
        if(dataTransfer == null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        dataTransfer = new DataTransfer(inputStream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            }.execute();
            return true;
        }
        return false;
    }

    @Override
    public synchronized Response handleRequest(Request request) throws Exception {
        if(request instanceof LoginRequest) {
            return handleLoginRequest((LoginRequest) request);
        }

        return null;
    }

    private LoginResponse handleLoginRequest(LoginRequest loginRequest) throws Exception {
        String request="";
        if(loginRequest.isNewClient()) {
            request = "REGISTER#" + loginRequest.getUsername() + "#" + loginRequest.getPassword();
        }
        else {
            request = "LOGIN#" + loginRequest.getUsername() + "#" + loginRequest.getPassword();
        }

        String response = dataTransfer.exchange(request);
        Log.i("Ovesp Info", "Response: " + response);

        String[] responseElements = response.split("#");
        if(responseElements[1].equals("ok")) {
            return new LoginResponse(true);
        }
        else {
            String errorMsg = responseElements[2];
            throw new Exception(errorMsg);
        }
    }
}
