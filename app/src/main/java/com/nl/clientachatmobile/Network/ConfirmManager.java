package com.nl.clientachatmobile.Network;

import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Models.Requests.ConfirmRequest;

public class ConfirmManager {

    public void performConfirmAsync(OnConfirmCompleteListener listener) {
        new ConfirmTask(listener).execute();
    }

    private class ConfirmTask extends AsyncTask<Void, Void, Void> {

        private OnConfirmCompleteListener listener;

        public ConfirmTask(OnConfirmCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String login = Ovesp.getInstance().getUsername();
            ConfirmRequest request = new ConfirmRequest(login);

            try {
                Ovesp.getInstance().handleRequest(request);
                listener.onConfirmComplete();
            }
            catch (Exception e) {
                Log.e("ConfirmManager DEBUG", e.getMessage());
                listener.onConfirmFailed(e.getMessage());
            }
            return null;
        }
    }

    public interface OnConfirmCompleteListener {
        void onConfirmComplete();

        void onConfirmFailed(String errorMsg);
    }
}
