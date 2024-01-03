package com.nl.clientachatmobile.Models.Responses;

import com.nl.clientachatmobile.Models.Data.Article;
import com.nl.clientachatmobile.Models.Requests.ConsultRequest;

public class ConsultResponse implements Response {
    private Article article;

    public ConsultResponse(Article a) {
        this.article = a;
    }

    public Article getArticle() { return article; }
}
