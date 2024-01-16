package com.nl.clientachatmobile.Models.Protocols.OVESP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.nl.clientachatmobile.Models.Data.Article;
import com.nl.clientachatmobile.Models.Data.CurrentArticleManager;
import com.nl.clientachatmobile.Models.Data.ShoppingCart;
import com.nl.clientachatmobile.Models.Exceptions.AchatArticleException;
import com.nl.clientachatmobile.Models.Exceptions.DataBaseException;
import com.nl.clientachatmobile.Models.Protocols.Protocol;
import com.nl.clientachatmobile.Models.Requests.BuyRequest;
import com.nl.clientachatmobile.Models.Requests.CancelAllRequest;
import com.nl.clientachatmobile.Models.Requests.CancelRequest;
import com.nl.clientachatmobile.Models.Requests.ConfirmRequest;
import com.nl.clientachatmobile.Models.Requests.ConsultRequest;
import com.nl.clientachatmobile.Models.Requests.LoginRequest;
import com.nl.clientachatmobile.Models.Requests.LogoutRequest;
import com.nl.clientachatmobile.Models.Requests.Request;
import com.nl.clientachatmobile.Models.Responses.BuyResponse;
import com.nl.clientachatmobile.Models.Responses.CancelAllResponse;
import com.nl.clientachatmobile.Models.Responses.CancelResponse;
import com.nl.clientachatmobile.Models.Responses.ConfirmResponse;
import com.nl.clientachatmobile.Models.Responses.ConsultResponse;
import com.nl.clientachatmobile.Models.Responses.LoginResponse;
import com.nl.clientachatmobile.Models.Responses.Response;
import com.nl.clientachatmobile.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousByteChannel;
import java.util.ArrayList;

public class Ovesp implements Protocol {

    private DataTransfer dataTransfer;
    private String loginUser;
    private static Ovesp instance;
    private CurrentArticleManager currentArticleManager;

    private ShoppingCart shoppingCart;

    private Context context;


    private Ovesp() {
        loginUser = "";
        currentArticleManager = new CurrentArticleManager();
        shoppingCart = new ShoppingCart(new ArrayList<Article>());
        dataTransfer = null;
    }

    public static Ovesp getInstance() {
        if(instance == null) {
            instance = new Ovesp();
        }
        return instance;
    }

    public ShoppingCart getShoppingCart() { return shoppingCart; }
    public CurrentArticleManager getCurrentArticleManager() { return currentArticleManager; }

    public String getUsername() { return loginUser; }

    @SuppressLint("StaticFieldLeak")
    public boolean init(InputStream inputStream, Context context) {
        this.context = context;
        if(dataTransfer == null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        dataTransfer = new DataTransfer(inputStream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            }.execute();
            return true;
        }
        return false;
    }

    @Override
    public synchronized Response handleRequest(Request request) throws Exception {
        if(request instanceof LoginRequest) {
            return handleLoginRequest((LoginRequest) request);
        }
        if(request instanceof ConsultRequest) {
            return handleConsultRequest((ConsultRequest) request);
        }
        if(request instanceof BuyRequest) {
            return handleBuyRequest((BuyRequest) request);
        }
        if(request instanceof CancelRequest) {
            return handleCancelRequest((CancelRequest) request);
        }
        if(request instanceof CancelAllRequest) {
            return handleCancelAllRequest();
        }
        if(request instanceof ConfirmRequest) {
            return handleConfirmRequest((ConfirmRequest) request);
        }
        if(request instanceof LogoutRequest) {
            handleLogoutRequest();
        }

        return null;
    }

    private LoginResponse handleLoginRequest(LoginRequest loginRequest) throws Exception {
        String request="";
        if(loginRequest.isNewClient()) {
            request = "REGISTER#" + loginRequest.getUsername() + "#" + loginRequest.getPassword();
        }
        else {
            request = "LOGIN#" + loginRequest.getUsername() + "#" + loginRequest.getPassword();
        }

        String response = dataTransfer.exchange(request);
        Log.i("Ovesp DEBUG", "Response: " + response);

        String[] responseElements = response.split("#");
        if(responseElements[1].equals("ok")) {
            loginUser = loginRequest.getUsername();
            return new LoginResponse(true);
        }
        else {
            String errorMsg = responseElements[2];
            throw new Exception(errorMsg);
        }
    }

    private ConsultResponse handleConsultRequest(ConsultRequest consultRequest) throws Exception {
        String request = "CONSULT#" + consultRequest.getArticleId();
        String response = dataTransfer.exchange(request);
        Log.i("Ovesp DEBUG", "Reponse reçue: " + response);

        String[] responseElements = response.split("#");
        if(responseElements[1].equals("KO")) {
            int errCode = Integer.parseInt(responseElements[2]);
            String msgError;
            if(errCode == DataBaseException.QUERY_ERROR) {
                msgError = context.getResources().getString(R.string.dbQuerryError);
            }
            else if(errCode == DataBaseException.EMPTY_RESULT_SET) {
                msgError = context.getResources().getString(R.string.dbEmptyResultSetOnArticle);
            }
            else {
                msgError = context.getResources().getString(R.string.unknownErrorMsg);
            }
            throw new Exception(msgError);
        }
        else {
            int id = Integer.parseInt(responseElements[2]);
            String intitule = responseElements[3];
            int stock = Integer.parseInt(responseElements[4]);
            String image = responseElements[5];
            float prix = Float.parseFloat(responseElements[6]);

            Article a = new Article(id, intitule, stock, image, prix);
            return new ConsultResponse(a);
        }
    }

    private BuyResponse handleBuyRequest(BuyRequest buyRequest) throws Exception {
        String request = "ACHAT#" + buyRequest.getIdArticle() + "#" + buyRequest.getQuantity();
        String response = dataTransfer.exchange(request);
        Log.i("Ovesp DEBUG", "Reponse reçue: " + response);

        String[] responseElements = response.split("#");
        if(responseElements[1].equals("KO")) {
            String message;
            int errCode = Integer.parseInt(responseElements[2]);
            switch(errCode) {
                case DataBaseException.QUERY_ERROR:
                    message = context.getResources().getString(R.string.dbQuerryError);
                    break;

                case DataBaseException.EMPTY_RESULT_SET:
                    message = context.getResources().getString(R.string.dbEmptyResultSetOnArticle);
                    break;

                case AchatArticleException.INSUFFICIENT_STOCK:
                    message = responseElements[3];
                    break;

                default:
                    message = context.getResources().getString(R.string.unknownErrorMsg);
            }
            throw new Exception(message);
        }
        else {
            int id = Integer.parseInt(responseElements[2]);
            String intitule = responseElements[3];
            int stock = Integer.parseInt(responseElements[4]);
            String image = responseElements[5];
            float prix = Float.parseFloat(responseElements[6]);

            Article a = new Article(id, intitule, stock, image, prix);
            return new BuyResponse(a);
        }
    }

    private CancelResponse handleCancelRequest(CancelRequest cancelRequest) throws Exception {
        String request = "CANCEL#" + cancelRequest.getArticleId() + "#" + cancelRequest.getQuantity() + "#" + cancelRequest.getArticleIndice();
        try {
            String response = dataTransfer.exchange(request);
            Log.i("Ovesp DEBUG", "Reponse reçue: " + response);

            String[] responseElements = response.split("#");
            if(responseElements[1].equals("OK")) {
                return new CancelResponse(true);
            }
            else {
                return new CancelResponse(false);
            }
        }
        catch(IOException e) {
            throw new Exception(context.getResources().getString(R.string.dbQuerryError));
        }
    }

    private CancelAllResponse handleCancelAllRequest() throws Exception {
        String request = "CANCELALL";
        try {
            String response = dataTransfer.exchange(request);
            Log.i("Ovesp DEBUG", "Reponse reçue: " + response);

            String[] responseElements = response.split("#");
            if(responseElements[1].equals("ok")) {
                shoppingCart.clearCart();
                return new CancelAllResponse(true);
            }
            else {
                return new CancelAllResponse(false);
            }
        }
        catch(Exception e) {
            throw new Exception(context.getResources().getString(R.string.dbQuerryError));
        }
    }

    private ConfirmResponse handleConfirmRequest(ConfirmRequest confirmRequest) throws Exception {
        String request = "CONFIRM#" + confirmRequest.getLogin();
        String reponse = dataTransfer.exchange(request);

        String[] responseElements = reponse.split("#");

        if (responseElements[1].equals("KO")) {
            int errCode = Integer.parseInt(responseElements[2]);
            String message;
            switch(errCode) {
                case DataBaseException.QUERY_ERROR:
                    message = context.getResources().getString(R.string.dbQuerryError);
                    break;

                case DataBaseException.EMPTY_RESULT_SET:
                    message = context.getResources().getString(R.string.dbEmptyResultSetOnConfirm);
                    break;

                default:
                    message = context.getResources().getString(R.string.unknownErrorMsg);
            }
            throw new Exception(message);
        }
        shoppingCart.clearCart();

        return new ConfirmResponse(true);
    }

    private void handleLogoutRequest() throws Exception {
        String request = "LOGOUT";
        try {
            dataTransfer.exchange(request);
            loginUser = "";
            currentArticleManager.reinitialize();
        }
        catch (Exception e) {
            throw new Exception(context.getResources().getString(R.string.dbQuerryError));
        }
    }
}
