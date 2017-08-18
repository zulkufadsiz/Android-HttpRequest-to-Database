package com.example.zulkuf.langdoc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;

public class main extends AppCompatActivity {
    private static final String TAG = "Http Connection";
    private ListView lw1;
    private ArrayAdapter arrayAdapter = null;

    private static final int REQUEST_INTERNET = 123;

    private String[] wordlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionControl();
        lw1 = (ListView)findViewById(R.id.lw1);
        final String url = "http://zulkufadsiz.com/langdoc/public/api/wordlist";

        new HttpAsyncTask().execute(url);

    }
    private void permissionControl(){
       if (ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

       }else {
           if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
           }else{
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
           }
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0){
            if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                new AlertDialog.Builder(main.this)
                        .setMessage("The app needs these permissions to work, Exit?")
                        .setTitle("Permission Denied")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                permissionControl();
                            }
                        })
                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
            }
        }
    }

    public  class HttpAsyncTask extends AsyncTask<String, Void, Integer>{

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputstream  = null;
            Integer result = 0;
            try {
                //create Apache HttpClient
                HttpClient httpClient = new DefaultHttpClient();

                //HttpGet Method
                HttpGet httpGet = new HttpGet(strings[0]);

                //Optional request header
                httpGet.setHeader("Content-Type", "application/json");

                //Make http request call
                HttpResponse httpResponse = httpClient.execute(httpGet);

                int statusCode = httpResponse.getStatusLine().getStatusCode();

                //200 represent HTTP Ok
                if (statusCode == 200){
                    inputstream = httpResponse.getEntity().getContent();

                    String response = convertInputStreamToString(inputstream);
                    //Log.d("RESPONSE",response);
                    parseResult(response);

                    result = 1; // Successful
                }else {
                    result = 0; //Failed to fetch data.
                }

            } catch (Exception e) {
                Log.d(TAG,e.getLocalizedMessage());
            }
            return result; // Failed to fetch data.
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1){
                arrayAdapter = new ArrayAdapter(main.this,android.R.layout.simple_list_item_1,wordlist);
                lw1.setAdapter(arrayAdapter);
            }else{
                Log.e(TAG,"Failed to fetch data");
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String  line  = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            //Close stream
            if (null != inputStream){
                inputStream.close();
            }

            return result;
        }

        private void parseResult(String result){
            try {

                Log.e("LARA",result);

                JSONObject response = new JSONObject(result) ;
                Log.e("LARA",response.toString());
                JSONArray data = response.optJSONArray("post");
                //Log.e("WESTEROS",data.toString());
                wordlist = new String[data.length()];
                for (int i = 0; i < data.length(); i++){
                   wordlist[i] = data.getString(i);
                }
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
        }
    }
}
