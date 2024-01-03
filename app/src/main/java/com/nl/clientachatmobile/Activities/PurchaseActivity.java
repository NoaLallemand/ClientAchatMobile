package com.nl.clientachatmobile.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nl.clientachatmobile.Models.Data.Article;
import com.nl.clientachatmobile.Models.Protocols.OVESP.Ovesp;
import com.nl.clientachatmobile.Network.ConsultManager;
import com.nl.clientachatmobile.R;

import java.util.Locale;

public class PurchaseActivity extends Activity implements View.OnClickListener {

    private ConsultManager consultManager;

    private ImageView imageViewArticle;
    private TextView txtViewArticleName;
    private TextView txtViewArticlePrice;
    private TextView txtViewArticleStock;
    private EditText editTextArticleQty;

    private Button btnNextArticle;
    private Button btnPreviousArticle;
    private Button btnBuyArticle;
    private Button btnDeleteArticle;
    private Button btnClearCart;
    private Button btnConfirmPurchase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_screen);

        consultManager = new ConsultManager();

        imageViewArticle = findViewById(R.id.imageViewArticle);
        txtViewArticleName = findViewById(R.id.textViewArticleName);
        txtViewArticlePrice = findViewById(R.id.textViewArticlePrice);
        txtViewArticleStock = findViewById(R.id.textViewArticleStock);
        editTextArticleQty = findViewById(R.id.editTextArticleQty);

        btnNextArticle = findViewById(R.id.btnNextArticle);
        btnPreviousArticle = findViewById(R.id.btnPreviousArticle);
        btnBuyArticle = findViewById(R.id.btnBuy);
        btnDeleteArticle = findViewById(R.id.btnDeleteArticle);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase);

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

        }
        else if(v == btnDeleteArticle) {

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
}
