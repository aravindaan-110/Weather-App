package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTextView;
    String message ="";
    String message2="";

    public void clickFunction(View view){

        message ="";
        message2="";

        editText.getText().toString();

        try {
            DownloadTAsk task = new DownloadTAsk();
            task.execute("https://openweathermap.org/data/2.5/weather?q="+editText.getText().toString()+"&appid=439d4b804bc8187953eb36d2a8c26a02").get();


        }catch (Exception e){
            e.printStackTrace();
        }

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);



    }

    public class DownloadTAsk extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data=reader.read();

                while(data!=-1){
                    char current = (char)data;
                    result+=current;
                    data = reader.read();
                }

            }catch(Exception e){
                resultTextView.setText("No city found !");
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject jsonObject = new JSONObject(s);



                String weatherInfo = jsonObject.getString("weather");
                String tempinfo = jsonObject.getString("main");
                String coordinfo = jsonObject.getString("coord");


                Log.i("Weather",weatherInfo);
                Log.i("Tempinfo",tempinfo);


                JSONArray arr = new JSONArray(weatherInfo);



                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main =jsonPart.getString("main");
                    String description = jsonPart.getString("description");


                    if(!main.equals("") && !description.equals("")){
                        message += editText.getText().toString().toUpperCase()+"\r\n\n"+main + ":" + description + "\r\n\n";
                    }


                }
                    JSONObject temppart = new JSONObject((tempinfo));
                JSONObject coordpart = new JSONObject((coordinfo));
                String temp=temppart.getString("temp");
                String temp1=temppart.getString("feels_like");
                String lon = coordpart.getString("lon");
                String lat = coordpart.getString("lat");


                if(!message.equals("")){
                    resultTextView.setText(message+"Temperature : "+temp+" °C"+"\r\n"+"Feels Like ="+temp1+" °C"+"\r\n"+"Longitude:"+lon+"\r\n"+"Latitude:"+lat);
                }else
                    resultTextView.setText("No city found ! else");

            } catch (Exception e) {
                resultTextView.setText("No city found ! exception");
                e.printStackTrace();}




        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText)findViewById(R.id.editText);
        resultTextView=(TextView)findViewById(R.id.resultTextView);


    }
}
