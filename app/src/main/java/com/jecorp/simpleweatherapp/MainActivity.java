package com.jecorp.simpleweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView input_city, tv_result;
    Button btn_check;

    class Weather extends AsyncTask<String, Void, String>{
        //String pertama artinya URL bentuknya String, Void tidak memiliki arti, String ketiga artinya bentuk pengembaliannya String

        @Override
        protected String doInBackground(String... address) {

            //String... artinya banyak alamat yang bisa dikirim. Berfungi sebagai array
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Memulai koneksi dengan alamat
                connection.connect();

                //Mengambil data dari URL
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //Mengambil dan mengembalikan data sebagai String
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1){
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void checkWeather(View view){
        input_city = findViewById(R.id.input_city);
        tv_result = findViewById(R.id.tv_result);
        btn_check = findViewById(R.id.btn_check);

        String apiKey = "b61dc3ffad9cf3dde9d55847f5ef3f2c";
        String cityName = input_city.getText().toString();
        String measureUnits = "metric";

        String content;
        Weather weather = new Weather();
        try {
            content = weather.execute("https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid="+apiKey+"&units="+measureUnits).get();
            //Pertama, cek apakah datanya berhasil diambil atau tidak
            Log.i("content",content);

            //JSON
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemperature = jsonObject.getString("main"); //bagian ini tidak termasuk array weather, namun merupakan variabel berbeda seperti weather
            double visibility;
            Log.i("weatherData",weatherData);

            //Data weather bentuknya array
            JSONArray array = new JSONArray(weatherData);

            String main ="";
            String description = "";
            String temperature = "";

            for(int i=0; i<array.length(); i++){
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }

            JSONObject mainPart = new JSONObject(mainTemperature);
            temperature = mainPart.getString("temp");

            visibility = Double.parseDouble(jsonObject.getString("visibility"));
            //secara default visibility satuannya meter
            int visibilityKM = (int) visibility/1000;

            Log.i("Temperature",temperature);

//            Log.i("main",main);
//            Log.i("description",description);

            //Sekarang kita akan menampilkan hasil di layar
            String result = "Main: "+main
                    +"\nDescription: " +description
                    +"\nTemperature: " +temperature+" *C"
                    +"\nVisibility: "+visibilityKM+" KM";
            tv_result.setText(result);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
