package com.nl.clientachatmobile.Models.Requests;

import com.nl.clientachatmobile.Network.CancelManager;

public class CancelRequest implements Request {
    private int articleId;
    private int quantity;
    private int articleIndice;

    public CancelRequest(int articleId, int quantity, int articleIndice) {
        this.articleId = articleId;
        this.quantity = quantity;
        this.articleIndice = articleIndice;
    }

    public int getArticleId() { return articleId; }
    public int getQuantity() { return quantity; }
    public int getArticleIndice() { return articleIndice; }
}
