package com.nl.clientachatmobile.Models.Protocols.OVESP;

import android.annotation.SuppressLint;
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
import com.nl.clientachatmobile.Models.Requests.ConsultRequest;
import com.nl.clientachatmobile.Models.Requests.LoginRequest;
import com.nl.clientachatmobile.Models.Requests.Request;
import com.nl.clientachatmobile.Models.Responses.BuyResponse;
import com.nl.clientachatmobile.Models.Responses.ConsultResponse;
import com.nl.clientachatmobile.Models.Responses.LoginResponse;
import com.nl.clientachatmobile.Models.Responses.Response;

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

    @SuppressLint("StaticFieldLeak")
    public boolean init(InputStream inputStream) {
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
        Log.i("Ovesp Info", "Response: " + response);

        String[] responseElements = response.split("#");
        if(responseElements[1].equals("ok")) {
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

        String[] responseElements = response.split("#");
        if(responseElements[1].equals("KO")) {
            int errCode = Integer.parseInt(responseElements[2]);
            String msgError;
            if(errCode == DataBaseException.QUERY_ERROR) {
                msgError = "Une erreur est survenue lors de l'envoi de la requete...Veuillez reessayer!";
            }
            else if(errCode == DataBaseException.EMPTY_RESULT_SET) {
                msgError = "Aucun article correspondant a votre demande n'a ete trouve!";
            }
            else {
                msgError = "Erreur inconnue...";
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

        String[] responseElements = response.split("#");
        if(responseElements[1].equals("KO")) {
            String message;
            int errCode = Integer.parseInt(responseElements[2]);
            switch(errCode) {
                case DataBaseException.QUERY_ERROR:
                    message = "Une erreur est survenue lors de l'envoi de la requete...Veuillez reessayer!";
                    break;

                case DataBaseException.EMPTY_RESULT_SET:
                    message = "Aucun article correspondant a votre demande n'a ete trouve!";
                    break;

                case AchatArticleException.INSUFFICIENT_STOCK:
                    message = responseElements[3];
                    break;

                default:
                    message = "Erreur inconnue...";
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
}
