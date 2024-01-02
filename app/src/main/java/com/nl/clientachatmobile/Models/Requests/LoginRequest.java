package com.nl.clientachatmobile.Models.Requests;

public class LoginRequest implements Request {
    private boolean isNewClient;
    private String username;
    private String password;

    public LoginRequest(boolean isNewClient, String username, String password) {
        this.username = username;
        this.password = password;
        this.isNewClient = isNewClient;
    }

    public boolean isNewClient() { return isNewClient; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setNewClient(boolean isNewClient) {
        this.isNewClient = isNewClient;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
