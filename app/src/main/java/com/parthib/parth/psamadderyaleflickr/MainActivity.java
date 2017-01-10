package com.parthib.parth.psamadderyaleflickr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String apikey = "822bc242d893d649a81ab76336c18e82";
    private String apisecret = "51bb4775577a400f";
    private String user_id = "12208415%40N08";
    private String requestPhotosetURL = "https://api.flickr.com/services/rest/?method=flickr.photosets.getList&api_key=";
    private String requestPhotoURL = "https://api.flickr.com/services/rest/?method=flickr.photosets.getPhotos&api_key=";
    private Context myContext;
    private GridView gridview;
    private ArrayList<Bitmap> bitmapList;

    //hardcoded because using the getparcelable for intents was not working
    public static String selectedURL;


    private XMLParser parser;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this;

        requestPhotosetURL = requestPhotosetURL + apikey + "&user_id=" + user_id + "&page=&format=rest";
        requestPhotoURL = requestPhotoURL + apikey + "&photoset_id=72157653287786219" + "&format=rest";

        parser = new XMLParser();

        this.bitmapList = new ArrayList<Bitmap>();

        new XMLAsyncTask().execute(requestPhotoURL);


        run();

    }

    public String urlBuilder(int i) {
        return "https://farm" + parser.getFarm().get(i) + ".staticflickr.com/" +
                parser.getServer().get(i) + "/" + parser.getPhotoid().get(i) + "_" + parser.getSecret().get(i) + ".jpg";
    }

    public void run() {

    }

    public class XMLAsyncTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line ="";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parser.parse(result);
            for (int i = 0; i < parser.getPhotoid().size(); i++) {
                new getBitmapThread().execute(urlBuilder(i));
            }

        }
    }


    public class getBitmapThread extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bitmapList.add(result);

            gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(myContext, bitmapList));

            gridview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Toast.makeText(MainActivity.this, "" + position,
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(myContext, SelectedImageActivity.class);
                    intent.putExtra("BitmapImage", urlBuilder(position));
                    selectedURL = urlBuilder(position);
                    System.out.println(urlBuilder(position));
                    startActivity(intent);

                }
            });
        }
    }

}
