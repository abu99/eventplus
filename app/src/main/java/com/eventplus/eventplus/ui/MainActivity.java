package com.eventplus.eventplus.ui;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

        String eventURL = getString(R.string.api_url);

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
                        Log.e(TAG, getString(R.string.exception), e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }else{
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private Event[] getEventList(String jsonData) throws JSONException {

        JSONObject eventBriteData = new JSONObject(jsonData);
        JSONArray eventArray = eventBriteData.getJSONArray(getString(R.string.json_key_event_list));



        Event[] eventList = new Event[eventArray.length()];

        for(int i = 0; i < eventArray.length(); i++){

            JSONObject eventData = eventArray.getJSONObject(i);

            JSONObject eventName = eventData.getJSONObject(getString(R.string.json_key_event_name));
            JSONObject eventStartTime = eventData.getJSONObject(getString(R.string.json_key_start_time));
            JSONObject eventDescription = eventData.getJSONObject(getString(R.string.json_key_event_description));

            JSONObject eventLogo = eventData.getJSONObject(getString(R.string.json_key_logo));

            Event event = new Event();



            event.setLogo(eventLogo.getString(getString(R.string.json_key_logo_url)));
            event.setName(eventName.getString(getString(R.string.json_key_event_name_as_text)));
            event.setHtmlLink(eventDescription.getString(getString(R.string.json_key_event_html_link)));

            SimpleDateFormat df = new SimpleDateFormat(getString(R.string.date_format1));
            df.setTimeZone(TimeZone.getTimeZone(getString(R.string.gmt)));

            Date startTime = new Date();
            try {
                startTime = df.parse(eventStartTime.getString(getString(R.string.utc)));
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
        alertDialogFragment.show(getFragmentManager(), getString(R.string.error_dialog_name));
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
            progressDialog.setTitle(getString(R.string.loading_dialog_title));
            progressDialog.setMessage(getString(R.string.loading_dialog_message));
        }
        progressDialog.show();
    }

    public void hideLoading() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
