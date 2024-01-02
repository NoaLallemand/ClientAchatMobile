package com.nl.clientachatmobile.Models.Responses;

public class LoginResponse implements Response {
    private boolean isSuccessful;
    private String errorMsg;

    public LoginResponse(boolean success) {
        this(success, "");
    }

    public LoginResponse(boolean success, String errorMsg) {
        this.isSuccessful = success;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccessful() { return this.isSuccessful; }
    public String getErrorMsg() { return this.errorMsg; }
}
