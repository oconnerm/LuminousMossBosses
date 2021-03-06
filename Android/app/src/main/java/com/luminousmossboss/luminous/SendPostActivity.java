package com.luminousmossboss.luminous;

/**
 * Created by andrey on 3/9/15.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.luminousmossboss.luminous.model.Observation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

public class SendPostActivity extends AsyncTask<Object,Void,Integer>{

    private int observationId;
    private Context context;
    private Button sendButton;
    private Observation observation;

    private final String POST_URL = "http://luminousid.com/_post_observation";
    private final int STATUS_OK = 200;


    public SendPostActivity(Context context, Observation observation, Button sendButton) {
        this.context = context;
        observationId = observation.getId();
        this.sendButton = sendButton;
        this.observation = observation;
    }
    @Override
    protected void onPreExecute(){
        observation.setSent(true);
        sendButton.setEnabled(false);
        sendButton.setVisibility(View.GONE);
    }

    @Override
    protected Integer doInBackground(Object... arg0) {

        Observation observation= (Observation) arg0[0];
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(POST_URL);
        File image = new File(observation.getIcon().getPath());


        try {
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("picture", new FileBody(image));
            entity.addPart("Time", new StringBody(observation.getFullDate().substring(10)));
            entity.addPart("Date", new StringBody(observation.getDate()));
            entity.addPart("Latitude", new StringBody(String.valueOf(observation.getLatitude())));
            entity.addPart("Longitude", new StringBody(String.valueOf(observation.getLongitude())));
            entity.addPart("DeviceId", new StringBody(Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID)));
            entity.addPart("DeviceType", new StringBody("AndroidPhone"));
            entity.addPart("LocationError", new StringBody(String.valueOf(observation.getAccuracy())));
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);

            int statusCode =response.getStatusLine().getStatusCode();

            return statusCode;
        } catch (Exception e) {return e.hashCode();}


        }





    @Override
    protected void onPostExecute(Integer result){
        if (result == STATUS_OK){
            Toast.makeText(context,R.string.post_obs_success,Toast.LENGTH_LONG).show();

            ObservationDBHandler db = new ObservationDBHandler(context);
            db.updateSyncedStatus(observationId);


        }
        else {
            Toast.makeText(context,R.string.post_obs_failure,Toast.LENGTH_LONG).show();
            observation.setSent(false);
            sendButton.setEnabled(true);
            sendButton.setVisibility(View.VISIBLE);


        }

    }
}