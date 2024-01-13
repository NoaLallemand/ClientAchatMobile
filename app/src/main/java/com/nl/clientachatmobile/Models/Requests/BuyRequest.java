package com.nl.clientachatmobile.Models.Requests;

public class BuyRequest implements Request {

    private int idArticle;
    private int quantity;

    public BuyRequest(int idArticle, int quantity) {
        this.idArticle = idArticle;
        this.quantity = quantity;
    }

    public int getIdArticle() { return idArticle; }
    public int getQuantity() { return quantity; }
}
