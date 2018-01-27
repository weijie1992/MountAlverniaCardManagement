package com.example.admin.mountalverniacardmanagement;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by admin on 1/24/2018.
 */

public class FinishISO extends AsyncTask<String, Void, String>  {
    Context context;
    String initAddress = Address.getAddress();

    FinishISO (Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String updateURL = initAddress + "forceISOFinish.php";

        String id = params[0];
        String bednum = params[1];

//        URL url = null;
        try {
            URL url = new URL(updateURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("iddata", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                    + "&" + URLEncoder.encode("bednum", "UTF-8") + "=" + URLEncoder.encode(bednum, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }
}
