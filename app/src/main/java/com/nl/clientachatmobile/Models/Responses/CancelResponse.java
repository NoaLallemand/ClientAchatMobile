package com.nl.clientachatmobile.Models.Responses;

public class CancelResponse implements Response {
    private boolean isCanceled;

    public CancelResponse(boolean success) {
        this.isCanceled = success;
    }

    public boolean isCanceled() { return isCanceled;}
}
