package com.nl.clientachatmobile.Models.Requests;

public class ConfirmRequest implements Request {
    private String login;

    public ConfirmRequest(String login) {
        this.login = login;
    }

    public String getLogin() { return login; }
}
