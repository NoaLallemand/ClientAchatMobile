package com.nl.clientachatmobile.Models.Data;

public class CurrentArticleManager {
    private final int MAX_ARTICLE_ID = 21;
    private final int MIN_ARTICLE_ID = 1;

    private int currentArticleId;
    private Article currentArticle;

    public CurrentArticleManager() {
        currentArticle = new Article();
        currentArticleId = MIN_ARTICLE_ID - 1;
    }

    public int getNexArticleId() {
        return incrementArticleId();
    }
    public int getPreviousArticleId() {
        return decrementArticleId();
    }
    public void setCurrentArticle(Article newCurrentArticle) { currentArticle = newCurrentArticle; }
    public Article getCurrentArticle() { return currentArticle; }

    private int incrementArticleId() {
        if(currentArticleId == MAX_ARTICLE_ID) {
            currentArticleId = MIN_ARTICLE_ID;
        }
        else {
            currentArticleId++;
        }
        return currentArticleId;
    }

    private int decrementArticleId() {
        if(currentArticleId == MIN_ARTICLE_ID) {
            currentArticleId = MAX_ARTICLE_ID;
        }
        else {
            currentArticleId--;
        }
        return currentArticleId;
    }
}
