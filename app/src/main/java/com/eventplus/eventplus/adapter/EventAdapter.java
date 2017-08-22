package com.eventplus.eventplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eventplus.eventplus.R;
import com.eventplus.eventplus.model.Event;
import com.eventplus.eventplus.ui.EventDetailsActivity;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {


    private Event[] events;
    private Context context;
    private static final String HTML_LINK = "HTML LINK";

    public EventAdapter(Context context, Event[] events) {
        this.context = context;
        this.events = events;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView eventNameLabel;
        public TextView eventTimeLabel;
        private ImageView eventLogo;
        private String htmlLink;

        public EventViewHolder(View itemView) {
            super(itemView);

            eventTimeLabel = (TextView) itemView.findViewById(R.id.eventStartTimeLabel);
            eventNameLabel = (TextView) itemView.findViewById(R.id.eventNameLabel);
            eventLogo = (ImageView) itemView.findViewById(R.id.eventLogo);

            itemView.setOnClickListener(this);
        }

        public void bindEvent(Event event){
            eventTimeLabel.setText(event.getStartTime());
            eventNameLabel.setText(event.getName());
            htmlLink = event.getHtmlLink();
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra(HTML_LINK, htmlLink);
            context.startActivity(intent);
        }
    }


    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.bindEvent(events[position]);
        if(events[position].getLogo() != null){
            Glide.with(context).load(events[position].getLogo()).into(holder.eventLogo);
        }else{
            Glide.clear(holder.eventLogo);
        }
    }

    @Override
    public int getItemCount() {
        return events.length;
    }

}
