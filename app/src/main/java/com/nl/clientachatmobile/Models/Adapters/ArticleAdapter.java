package com.nl.clientachatmobile.Models.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nl.clientachatmobile.Models.Data.Article;
import com.nl.clientachatmobile.R;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.articleName = convertView.findViewById(R.id.textViewItemArticleName);
            viewHolder.articlePrice = convertView.findViewById(R.id.textViewItemArticlePrice);
            viewHolder.articleQuantity = convertView.findViewById(R.id.textViewItemArticleQuantity);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(article != null) {
            viewHolder.articleName.setText(article.getIntitule());
            String txtPrice = article.getPrix() + "€";
            viewHolder.articlePrice.setText(txtPrice);
            viewHolder.articleQuantity.setText(String.valueOf(article.getQuantite()));
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView articleName;
        public TextView articlePrice;
        public TextView articleQuantity;
    }
}
