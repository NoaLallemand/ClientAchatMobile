package com.nl.clientachatmobile.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Models.Requests.LoginRequest;
import com.nl.clientachatmobile.Models.Responses.LoginResponse;

public class LoginManager {
    public void performLoginAsync(String username, String password, boolean isNewClient, OnLoginCompleteListener listener) {
        new LoginTask(listener).execute(username, password, isNewClient);
    }

    private class LoginTask extends AsyncTask<Object, Void, Void> {
        private final OnLoginCompleteListener listener;

        public LoginTask(OnLoginCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Object... params) {
            String username = (String) params[0];
            String password = (String) params[1];
            boolean isNewClient = (boolean) params[2];

            if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
                listener.onLoginFailed("Veuillez renseigner un nom d'utilisateur et un mot de passe puis réessayer.");
                return null;
            }

            LoginResponse response = new LoginResponse(false);
            try {
                LoginRequest request = new LoginRequest(isNewClient, username, password);
                response = (LoginResponse) Ovesp.getInstance().handleRequest(request);
            }
            catch (Exception e) {
                Log.e("LoginManager DEBUG", e.getMessage());
                listener.onLoginFailed(e.getMessage());
            }
            finally {
                if(response.isSuccessful()) {
                    listener.onLoginComplete();
                }
                else {
                    listener.onLoginFailed(response.getErrorMsg());
                }
            }
            return null;
        }
    }

    public interface OnLoginCompleteListener {
        void onLoginComplete();
        void onLoginFailed(String errorMsg);
    }
}
