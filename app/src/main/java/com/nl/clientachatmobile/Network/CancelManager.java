package com.nl.clientachatmobile.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Models.Requests.CancelRequest;
import com.nl.clientachatmobile.Models.Responses.CancelResponse;

public class CancelManager {

    public void performCancelAsync(int articleId, int quantity, int indice, OnCancelCompleteListener listener) {
        new CancelTask(listener).execute(articleId, quantity, indice);
    }

    private class CancelTask extends AsyncTask<Integer, Void, Void> {

        private OnCancelCompleteListener listener;

        public CancelTask(OnCancelCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int articleId = params[0];
            int quantity = params[1];
            int articleIndice = params[2];
            CancelRequest request = new CancelRequest(articleId, quantity, articleIndice);

            try {
                Ovesp.getInstance().handleRequest(request); //Ici on ne récupère pas l'objet CancelResponse renvoyé par handleRequest...
                                                    //On ne s'en sert pas...si erreur -> Exception lancée et catchée juste en bas.
                listener.onCancelComplete();
            }
            catch(Exception e) {
                Log.e("CancelManager DEBUG", e.getMessage());
                listener.onConcelFailed(e.getMessage());
            }
            return null;
        }
    }

    public interface OnCancelCompleteListener {
        void onCancelComplete();

        void onConcelFailed(String errorMsg);
    }
}
