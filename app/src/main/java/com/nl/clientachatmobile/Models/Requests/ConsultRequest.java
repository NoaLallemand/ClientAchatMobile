package com.nl.clientachatmobile.Models.Requests;

public class ConsultRequest implements Request {
    private int articleId;

    public ConsultRequest(int articleId) {
        this.articleId = articleId;
    }

    public int getArticleId() {
        return articleId;
    }
}
