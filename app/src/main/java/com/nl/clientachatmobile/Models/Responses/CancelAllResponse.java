package com.nl.clientachatmobile.Models.Responses;

public class CancelAllResponse implements Response {
    private boolean success;

    public CancelAllResponse(boolean areAllCanceled) {
        this.success = areAllCanceled;
    }

    public boolean areAllCanceled() { return success; }
}
