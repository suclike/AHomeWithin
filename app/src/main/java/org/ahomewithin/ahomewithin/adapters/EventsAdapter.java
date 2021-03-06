package org.ahomewithin.ahomewithin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ahomewithin.ahomewithin.R;
import org.ahomewithin.ahomewithin.models.Event;
import org.ahomewithin.ahomewithin.util.EventClickListener;

import java.util.List;

/**
 * Created by barbara on 3/6/16.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder>
        implements SimpleListAdapter {

    private final EventClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llEventContainer;
        public TextView tvEventName;
        public ImageView ivImage;
        public TextView tvDescription;
        public TextView tvDateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            llEventContainer = (LinearLayout) itemView.findViewById(R.id.llEventContainer);
            tvEventName = (TextView) itemView.findViewById(R.id.tvEventName);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

    private Context mContext;
    private List<Event> events;

    public EventsAdapter(Context context, List<Event> items, EventClickListener eventClickListener) {
        mContext = context;
        events = items;
        mListener = eventClickListener;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.item_event, parent, false);
        return (new ViewHolder(contactView));
    }

    @Override
    public void onBindViewHolder(final EventsAdapter.ViewHolder viewHolder, int position) {
        final Event event = events.get(position);
        viewHolder.tvEventName.setText(event.eventName);
        viewHolder.tvDateTime.setText(event.getDateTime());

        final int resourceId = mContext.getResources().getIdentifier(
                event.imageUrl, "drawable", mContext.getPackageName());
        viewHolder.ivImage.setImageDrawable(mContext.getResources().getDrawable(resourceId));


        String url = event.getUrl();
        if (url != null) {
            // http://www.androidauthority.com/using-shared-element-transitions-activities-fragments-631996/
            viewHolder.llEventContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEventClicked(viewHolder, event);

                }
            });
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return this;
    }

    public int getItemCount() {
        return events.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    private List<Event> convert(List<?> items) {
        return (List<Event>) items;
    }

    // Add a list of items
    public void addAll(List<?> items) {
        int curSize = events.size();
        events.addAll(convert(items));
        notifyItemRangeInserted(curSize, events.size() - 1);
    }

    public void insertAll(List<?> items) {
        events.addAll(0, convert(items));
        notifyItemRangeInserted(0, items.size() - 1);
    }

    public void addOrInsertAll(List<?> items) {
        if ((items.size() > 0) && (events.size() > 0)) {
            Event item = (Event) items.get(0);
            if (item.isMoreRecent(events.get(0))) {
                insertAll(items);
                return;
            }
        }
        addAll(items);
    }
}

