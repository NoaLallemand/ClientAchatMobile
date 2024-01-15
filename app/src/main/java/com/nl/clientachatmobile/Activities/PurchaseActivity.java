package com.nl.clientachatmobile.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nl.clientachatmobile.Models.Adapters.ArticleAdapter;
import com.nl.clientachatmobile.Models.Data.Article;
import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Network.BuyArticleManager;
import com.nl.clientachatmobile.Network.CancelManager;
import com.nl.clientachatmobile.Network.ConsultManager;
import com.nl.clientachatmobile.R;

import java.util.List;
import java.util.Locale;

public class PurchaseActivity extends Activity implements View.OnClickListener {

    private ConsultManager consultManager;

    private ImageView imageViewArticle;
    private TextView txtViewArticleName;
    private TextView txtViewArticlePrice;
    private TextView txtViewArticleStock;
    private TextView txtViewTotalToPay;
    private EditText editTextArticleQty;

    private Button btnNextArticle;
    private Button btnPreviousArticle;
    private Button btnBuyArticle;
    private Button btnDeleteArticle;
    private Button btnClearCart;
    private Button btnConfirmPurchase;

    private ListView listViewArticles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_screen);

        consultManager = new ConsultManager();

        imageViewArticle = findViewById(R.id.imageViewArticle);
        txtViewArticleName = findViewById(R.id.textViewArticleName);
        txtViewArticlePrice = findViewById(R.id.textViewArticlePrice);
        txtViewArticleStock = findViewById(R.id.textViewArticleStock);
        txtViewTotalToPay = findViewById(R.id.textViewDisplayTotalToPay);
        editTextArticleQty = findViewById(R.id.editTextArticleQty);

        btnNextArticle = findViewById(R.id.btnNextArticle);
        btnPreviousArticle = findViewById(R.id.btnPreviousArticle);
        btnBuyArticle = findViewById(R.id.btnBuy);
        btnDeleteArticle = findViewById(R.id.btnDeleteArticle);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase);

        listViewArticles = findViewById(R.id.listViewArticles);
        ArticleAdapter adapter = new ArticleAdapter(this, Ovesp.getInstance().getShoppingCart().getArticles());
        listViewArticles.setAdapter(adapter);
        listViewArticles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        btnNextArticle.setOnClickListener(this);
        btnPreviousArticle.setOnClickListener(this);
        btnBuyArticle.setOnClickListener(this);
        btnDeleteArticle.setOnClickListener(this);
        btnClearCart.setOnClickListener(this);
        btnConfirmPurchase.setOnClickListener(this);

        consultArticle(true);
    }

    @Override
    public void onClick(View v) {
        if(v == btnNextArticle) {
            consultArticle(true);
        }
        else if(v == btnPreviousArticle) {
            consultArticle(false);
        }
        else if(v == btnBuyArticle) {
            int quantity = Integer.parseInt(editTextArticleQty.getText().toString());
            if(quantity > 0) {
                buyArticle(quantity);
            }
            else {
                Toast.makeText(this, "Veuillez choisir au moins 1 article!", Toast.LENGTH_LONG).show();
            }
        }
        else if(v == btnDeleteArticle) {
            deleteArticle();
        }
        else if(v == btnClearCart) {

        }
        else if(v == btnConfirmPurchase) {

        }
    }

    private void consultArticle(boolean isActionNextArticle) {
        consultManager.performConsultAsync(isActionNextArticle, new ConsultManager.OnConsultCompleteListener() {
            @Override
            public void onConsultComplete() {
                updateCurrentArticleOnUI();
            }

            @Override
            public void onConsultFailed(String errorMsg) {
                runOnUiThread(() -> {
                    Log.e("PurchaseActivity DEBUG", errorMsg);
                    Toast.makeText(PurchaseActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void buyArticle(int quantity) {
        BuyArticleManager buyArticleManager = new BuyArticleManager();
        buyArticleManager.performBuyAsync(quantity, new BuyArticleManager.OnBuyCompleteListener() {
            @Override
            public void onBuyComplete() {
                updateCurrentArticleOnUI();
                updateTotalToPay();
                ArticleAdapter adapter = (ArticleAdapter) listViewArticles.getAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onBuyFailed(String errorMsg) {
                runOnUiThread(() -> {
                    Log.e("PurchaseActivity DEBUG", errorMsg);
                    Toast.makeText(PurchaseActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void deleteArticle() {
        int indice = listViewArticles.getCheckedItemPosition();
        if(indice != -1) {
            Article selectedItem = (Article) listViewArticles.getAdapter().getItem(indice);
            int articleId = selectedItem.getId();
            int quantity = selectedItem.getQuantite();

            CancelManager cancelManager = new CancelManager();
            cancelManager.performCancelAsync(articleId, quantity, indice, new CancelManager.OnCancelCompleteListener() {
                @Override
                public void onCancelComplete() {
                    Ovesp.getInstance().getShoppingCart().getArticles().remove(indice);
                    ArticleAdapter adapter = (ArticleAdapter) listViewArticles.getAdapter();
                    adapter.notifyDataSetChanged();
                    updateTotalToPay();
                }

                @Override
                public void onConcelFailed(String errorMsg) {
                    runOnUiThread(() -> {
                        Log.e("PurchaseActivity DEBUG", errorMsg);
                        Toast.makeText(PurchaseActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }
    }

    private void updateCurrentArticleOnUI() {
        Article currentArticle = Ovesp.getInstance().getCurrentArticleManager().getCurrentArticle();

        txtViewArticleName.setText(currentArticle.getIntitule());
        txtViewArticlePrice.setText(String.format(Locale.getDefault(),"%.2f €", currentArticle.getPrix()));
        txtViewArticleStock.setText(String.valueOf(currentArticle.getQuantite()));

        String image = currentArticle.getImage();
        int imageLength = image.length();
        String imageNameInResources = image.substring(0, imageLength - 4);
        int resId = getResources().getIdentifier(imageNameInResources, "drawable", getPackageName());
        imageViewArticle.setImageResource(resId);
    }

    public void updateTotalToPay() {
        List<Article> shoppingCart = Ovesp.getInstance().getShoppingCart().getArticles();

        float totalAmount = 0;
        for(Article a : shoppingCart) {
            totalAmount += a.getPrix() * a.getQuantite();
        }
        txtViewTotalToPay.setText(String.format(Locale.getDefault(), "%.2f €", totalAmount));
    }
}
