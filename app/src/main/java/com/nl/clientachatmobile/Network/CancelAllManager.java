package com.nl.clientachatmobile.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Models.Requests.CancelAllRequest;
import com.nl.clientachatmobile.Models.Responses.CancelAllResponse;

public class CancelAllManager {

    public void performCancelAllAsync(OnCancelAllCompleteListener listener) {
        new CancelAllTask(listener).execute();
    }

    private class CancelAllTask extends AsyncTask<Void, Void, Void> {

        private OnCancelAllCompleteListener listener;

        public CancelAllTask(OnCancelAllCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            CancelAllRequest request = new CancelAllRequest();
            try {
                CancelAllResponse response = (CancelAllResponse) Ovesp.getInstance().handleRequest(request);
                if(response.areAllCanceled()) {
                    listener.onCancelAllComplete();
                }
                else {
                    listener.onCancelAllFailed("Une erreur est survenue...");
                }

            }
            catch (Exception e) {
                Log.e("CancelAllManager DEBUG", e.getMessage());
                listener.onCancelAllFailed(e.getMessage());
            }
            return null;
        }
    }

    public interface OnCancelAllCompleteListener {
        void onCancelAllComplete();

        void onCancelAllFailed(String errorMsg);
    }
}
