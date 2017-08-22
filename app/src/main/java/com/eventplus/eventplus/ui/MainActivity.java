package com.eventplus.eventplus.ui;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.eventplus.eventplus.R;
import com.eventplus.eventplus.adapter.EventAdapter;
import com.eventplus.eventplus.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Event[] events;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private final String TAG = MainActivity.class.getSimpleName();
    private static final String EVENT_LIST = "EVENT_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    private void getEvent(){

        String eventURL = "https://www.eventbriteapi.com/v3/events/search/?token=VBUSKKCQ2VTXKPOP34PX";

        if(isNetworkAvailable()){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(eventURL).build();

            Call call = client.newCall(request);

            showLoading();
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();

                        if (response.isSuccessful()) {

                            events = getEventList(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadEventList();
                                    hideLoading();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }else{
            Toast.makeText(this, "network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private Event[] getEventList(String jsonData) throws JSONException {

        JSONObject eventBriteData = new JSONObject(jsonData);
        JSONArray eventArray = eventBriteData.getJSONArray("events");



        Event[] eventList = new Event[eventArray.length()];

        for(int i = 0; i < eventArray.length(); i++){

            JSONObject eventData = eventArray.getJSONObject(i);

            JSONObject eventName = eventData.getJSONObject("name");
            JSONObject eventStartTime = eventData.getJSONObject("start");
            JSONObject eventDescription = eventData.getJSONObject("description");

            JSONObject eventLogo = eventData.getJSONObject("logo");

            Event event = new Event();



            event.setLogo(eventLogo.getString("url"));
            event.setName(eventName.getString("text"));
            event.setHtmlLink(eventDescription.getString("html"));

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date startTime = new Date();
            try {
                startTime = df.parse(eventStartTime.getString("utc"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            event.setStartTime(startTime);

            eventList[i] = event;

        }

        return eventList;
    }


    private void alertUserAboutError() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(getFragmentManager(), "error_dialog");
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }

        return isAvailable;
    }



    public void loadEventList(){

        EventAdapter adapter = new EventAdapter(this, events);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
    }

    public void showLoading(){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Downloading List of Events");
        }
        progressDialog.show();
    }

    public void hideLoading() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
