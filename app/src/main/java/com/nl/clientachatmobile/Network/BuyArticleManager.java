package com.nl.clientachatmobile.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.nl.clientachatmobile.Models.Data.Article;
import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Models.Requests.BuyRequest;
import com.nl.clientachatmobile.Models.Responses.BuyResponse;

import java.util.List;

public class BuyArticleManager {

    public void performBuyAsync(int quantity, OnBuyCompleteListener listener) {
        new BuyArticleTask(listener).execute(quantity);
    }

    private class BuyArticleTask extends AsyncTask<Integer, Void, Article> {

        private OnBuyCompleteListener listener;

        public BuyArticleTask(OnBuyCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Article doInBackground(Integer... params) {
            int idArticle = Ovesp.getInstance().getCurrentArticleManager().getCurrentArticle().getId();
            int quantity = params[0];

            BuyRequest request = new BuyRequest(idArticle, quantity);
            try {
                BuyResponse response = (BuyResponse) Ovesp.getInstance().handleRequest(request);
                return response.getArticle();
            }
            catch(Exception e) {
                Log.e("BuyArticleManager DEBUG", e.getMessage());
                listener.onBuyFailed(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Article article) {
            if(article != null) {
                Ovesp.getInstance().getShoppingCart().addArticle(article);

                //Récupération de la qte de l'article avant l'achat pour calculer la qte restante pour pouvoir actualiser l'UI dans PurchaseActivity.
                Article a = Ovesp.getInstance().getCurrentArticleManager().getCurrentArticle();
                int quantityBeforePurchase = a.getQuantite();
                int remainingQuantity = quantityBeforePurchase - article.getQuantite();
                a.setQuantite(remainingQuantity);
                Ovesp.getInstance().getCurrentArticleManager().setCurrentArticle(a);

                listener.onBuyComplete();
            }
        }
    }

    public interface OnBuyCompleteListener {
        void onBuyComplete();

        void onBuyFailed(String errorMsg);
    }
}
