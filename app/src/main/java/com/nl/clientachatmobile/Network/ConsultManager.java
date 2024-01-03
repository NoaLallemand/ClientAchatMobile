package com.nl.clientachatmobile.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.nl.clientachatmobile.Models.Data.Article;
import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Models.Requests.ConsultRequest;
import com.nl.clientachatmobile.Models.Requests.LoginRequest;
import com.nl.clientachatmobile.Models.Responses.ConsultResponse;
import com.nl.clientachatmobile.Models.Responses.LoginResponse;

public class ConsultManager {

    public void performConsultAsync(boolean isActionNextArticle, OnConsultCompleteListener listener) {
        new ConsultTask(listener).execute(isActionNextArticle);
    }

    private class ConsultTask extends AsyncTask<Boolean, Void, Article> {

        private OnConsultCompleteListener listener;

        public ConsultTask(OnConsultCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Article doInBackground(Boolean... params) {
            boolean isActionNextArticle = params[0];
            ConsultResponse response = null;
            try {
                int articleId;
                if(isActionNextArticle) {
                    articleId = Ovesp.getInstance().getCurrentArticleManager().getNexArticleId();
                }
                else {
                    articleId = Ovesp.getInstance().getCurrentArticleManager().getPreviousArticleId();
                }
                ConsultRequest request = new ConsultRequest(articleId);
                response = (ConsultResponse) Ovesp.getInstance().handleRequest(request);
                return response.getArticle();
            }
            catch (Exception e) {
                Log.e("ConsultManager DEBUG", e.getMessage());
                listener.onConsultFailed(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Article article) {
            if (article != null) {
                Ovesp.getInstance().getCurrentArticleManager().setCurrentArticle(article);
                listener.onConsultComplete();
            }
        }
    }

    public interface OnConsultCompleteListener {
        void onConsultComplete();

        void onConsultFailed(String errorMsg);
    }

}
