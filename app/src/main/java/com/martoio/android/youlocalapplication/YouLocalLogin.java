package com.martoio.android.youlocalapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martin on 7/9/2016 for YouLocalApplication.
 */
public class YouLocalLogin {

    private static final String TAG = "Login"; //Debugging tag;

    public byte[] getUrlBytes(String urlSpec, String PostParams) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        //connection.setFixedLengthStreamingMode(PostParams.getBytes("UTF-8").length);

        OutputStream os = connection.getOutputStream();
        //Write POST body
        os.write(PostParams.getBytes("UTF-8"));
        os.close();

        //read response info and set into a byte[]
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ( (bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        } catch (MalformedURLException error) {
            Log.e(TAG, "MALFORMED URL", error);
            return null;
        }catch (IOException error) {
            Log.e(TAG, "IO", error);
            return null;
        }
        finally {
            connection.disconnect();
        }

    }

    public String getUrlString(String urlSpec, Map<String, String> paramsMap) throws IOException {
        //Build the URL string and body parameters for POST
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //encode
        for (Map.Entry<String, String> entry : paramsMap.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        //get results;
        byte[] URLbytes = getUrlBytes(urlSpec, result.toString());
        if (URLbytes != null){
            return new String(URLbytes);
        }

        return null;

    }

    public YouLocalUser fetchUser(String email, String password){
        YouLocalUser user = new YouLocalUser();

        try {
            //URL to call from
            String url = "https://www.youlocalapp.com/oauth2/2.0/signin";
            //Parameters for POST call
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("email", email);
            paramsMap.put("password", password);

            //JSON result
            String jsonString = getUrlString(url, paramsMap);

            //return nothing if error occurs;
            if (jsonString == null){
                return null;
            }

            //Parse JSON object into YouLocalUser object
            JSONObject jsonBody = new JSONObject(jsonString);
            parseJSONUser(user, jsonBody);
            return user;

        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch JSON", ioe);
        }
        //If error, return nothing;
        return null;
    }
    /*
    * Parses the JSON result into the user variable;
    * */
    private void parseJSONUser(YouLocalUser user, JSONObject jsonBody) throws IOException, JSONException{
        user.setAbout(jsonBody.getString("aboutMe"));
        user.setAvatarURL(jsonBody.getString("avatar"));
        user.setEmail(jsonBody.getString("email"));
        user.setFullName(jsonBody.getString("fullname"));
        user.setMid(jsonBody.getString("userId"));
        user.setLatitude(jsonBody.getDouble("latitude"));
        user.setLongitude(jsonBody.getDouble("longitude"));
    }



}
