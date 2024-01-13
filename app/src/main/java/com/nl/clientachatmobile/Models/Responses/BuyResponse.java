package com.nl.clientachatmobile.Models.Responses;

import com.nl.clientachatmobile.Models.Data.Article;

public class BuyResponse implements Response {
    private Article article;

    public BuyResponse(Article a) {
        article = a;
    }

    public Article getArticle() { return article; }
}
