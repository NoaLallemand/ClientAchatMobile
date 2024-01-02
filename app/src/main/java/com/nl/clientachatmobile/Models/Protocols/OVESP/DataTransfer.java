package com.nl.clientachatmobile.Models.Protocols.OVESP;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Properties;

public class DataTransfer {
    private final Socket s;
    private Properties configProperties;

    public Socket getSocket() { return s; }
    private int getDefaultPort() { return Integer.parseInt(configProperties.getProperty("defaultPort")); }
    private String getServerIp() { return configProperties.getProperty("serverIp"); }

    public DataTransfer(InputStream inputStream) throws IOException {
        loadProperties(inputStream);
        int port = getDefaultPort();
        String serverIp = getServerIp();
        Log.i("DataTransfer Info", "Server adress: " + serverIp + ":" + port);
        s = new Socket(serverIp, port);
    }

    private void loadProperties(InputStream inputStream) {
        try {
            configProperties = new Properties();
            configProperties.load(inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int send(String data) {
        if(data.isEmpty()) {
            return -1;
        }
        else {
            int taille = data.length();
            String tailleStr = String.valueOf(taille);
            int nbZero = 4 - tailleStr.length();
            StringBuilder enTete = new StringBuilder();

            for(int i=0; i < nbZero; i++) {
                enTete.append('0');
            }
            enTete.append(tailleStr);

            StringBuilder request = new StringBuilder(enTete);
            request.append(data);

            System.out.println(request);

            int tailleReelle = request.length();

            try {
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeBytes(request.toString());
            }
            catch(IOException e) {
                e.printStackTrace();
                return -1;
            }
            return tailleReelle;
        }
    }

    private String receive() throws IOException {
        StringBuilder response = new StringBuilder();
        String charsetName = "UTF-8";
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());

            int dataLength = 0;
            for(int i=0, j=3; i < 4; i++, j--) {
                byte b = dis.readByte();
                char c = (char)b;
                dataLength += Integer.parseInt(String.valueOf(c)) * Math.pow(10, j);
            }

            byte[] bytes = new byte[dataLength];
            for(int i=0; i<dataLength; i++) {
                bytes[i] = dis.readByte();
            }
            response.append(new String(bytes, charsetName));
        }
        catch(IOException | NumberFormatException e) {
            e.printStackTrace();
            throw e;
        }
        return response.toString();
    }

    public String exchange(String request) throws IOException {

        String response = "";
        Log.d("DataTransfer Info" ,"Requete envoyée: " + request);
        if(send(request) == -1) {
            Log.e("DataTransfer Error", "Erreur de send()...");
            s.close();
        }
        else {
            try {
                response = receive();
            }
            catch(IOException e) {
                Log.e("DataTransfer Error", "Erreur de receive()...");
                s.close();
                throw e;
            }

        }
        return response;
    }
}
