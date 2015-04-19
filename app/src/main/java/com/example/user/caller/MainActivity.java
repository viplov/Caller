package com.example.user.caller;


import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by viplov on 19/04/2015.
 */
public class MainActivity extends Activity {
    ProgressDialog pDialog;
    //constants

   private static final String app_id = "MAOTK3ODVMOTU2YZK2YZ";
    private static final String app_token = "MzdmNDVkYjU1Y2NjYmFkNzgzZDBiNDE0OGUwZGRm";

    //url
    private static final String url ="https://api.plivo.com/v1/Account/" + app_id + "/Call/";
    //Json nodes
    public static String msg="No Network.";

    EditText e;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e = (EditText)findViewById(R.id.editText);
        Button b=(Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MakeCall().execute();
            }
        });

    }

    /**
     * Background Async Task to Make call
     * */
    class MakeCall extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Calling. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * signing in in background thread
         */
        protected String doInBackground(String... params) {
            // Building Parameters
            HttpPost httpPost = new HttpPost(url);
            DefaultHttpClient httpClient = new DefaultHttpClient();


            JSONObject jGroup = new JSONObject();

            try {
                jGroup.put("from","918266806997");
                jGroup.put("to","91" + e.getText().toString());
                jGroup.put("answer_url","http://google.com");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            httpPost.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(app_id, app_token),
                    "UTF-8", false));
            httpPost.setHeader("content-type", "application/json");
            httpPost.setHeader( "User-Agent", "AndroidPlivo");



            //HttpContext For Sending Cookies To HttpPost Request
            HttpContext localContext = null;
            final String STR = "UTF-8";
            try {
                localContext   = new BasicHttpContext();
                httpPost.setEntity(new StringEntity(jGroup.toString(), STR));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse response = httpClient.execute(httpPost, localContext);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    String result = sb.toString();

                    Log.e("This is what we get : ", result);
                    final JSONObject json = new JSONObject(result);
                    if(json.has("error")) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                msg = null;
                                try {
                                    msg = json.getString("error");
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                    if(json.has("message")) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                msg = null;
                                try {
                                    msg = json.getString("message");
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         * */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

}