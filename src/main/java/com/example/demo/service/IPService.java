package com.example.demo.service;

import com.example.demo.model.IPModel;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class IPService {
    public static String getIP(String message) throws IOException {
        URL url = new URL("https://api.ipify.org/?format=json");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()){
            result +=scanner.nextLine();
        }
        IPModel model = new IPModel();
        JSONObject object = new JSONObject(result);
        model.setIP(object.getString("ip"));

        return "Ваш ip : " + model.getIP();

    }
}
