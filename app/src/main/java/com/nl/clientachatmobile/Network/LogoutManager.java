package com.nl.clientachatmobile.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Models.Requests.LogoutRequest;

public class LogoutManager {

    public void performLogoutAsync(OnLogoutCompleteListener listener) {
        new LogoutTask(listener).execute();
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {

        private OnLogoutCompleteListener listener;

        public LogoutTask(OnLogoutCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LogoutRequest logoutRequest = new LogoutRequest();
            try {
                Ovesp.getInstance().handleRequest(logoutRequest);
                listener.onLogoutComplete();
            }
            catch(Exception e) {
                Log.e("LogoutManager DEBUG", e.getMessage());
                listener.onLogoutFailed(e.getMessage());
            }
            return null;
        }
    }

    public interface OnLogoutCompleteListener {
        void onLogoutComplete();

        void onLogoutFailed(String errorMsg);
    }
}
