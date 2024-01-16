package com.nl.clientachatmobile.Models.Responses;

public class ConfirmResponse implements Response {
    private boolean isConfirmed;

    public ConfirmResponse(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public boolean isConfirmed() { return isConfirmed;}
}
