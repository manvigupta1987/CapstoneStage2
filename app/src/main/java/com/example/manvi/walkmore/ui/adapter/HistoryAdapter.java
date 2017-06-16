package com.example.manvi.walkmore.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.data.FitnessContract;

import java.util.Locale;

/**
 * Created by manvi on 31/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.historyAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private View mEmptyView;

    public HistoryAdapter(Context context, View emptyView) {
        mContext = context;
        mEmptyView = emptyView;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class historyAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mDistanceView;
        public final TextView mStepsView;
        public final TextView mCalorieView;
        public final TextView mDateView;

        public historyAdapterViewHolder(View view) {
            super(view);
            mDateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            mDistanceView = (TextView) view.findViewById(R.id.list_item_distance_textview);
            mStepsView = (TextView) view.findViewById(R.id.list_item_steps_textview);
            mCalorieView = (TextView) view.findViewById(R.id.list_item_calorie_textview);
        }
    }

    /*
        This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
        RecyclerView that will be used to contain the view, so that it can get the current
        ItemSelectionManager from the view.
        One could implement this pattern without modifying RecyclerView by taking advantage
        of the view tag to store the ItemChoiceManager.
     */
    @Override
    public historyAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_fitness_data, viewGroup, false);
            view.setFocusable(true);
            return new historyAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(historyAdapterViewHolder AdapterViewHolder, int position) {
        mCursor.moveToPosition(position);
        String distance = String.format(Locale.getDefault(),"%.2f", mCursor.getDouble(mCursor.getColumnIndex(FitnessContract.fitnessDataEntry.COLUMN_DISTANCE)));
        int calories = mCursor.getInt(mCursor.getColumnIndex(FitnessContract.fitnessDataEntry.COLUMN_CALORIES));
        int steps = mCursor.getInt(mCursor.getColumnIndex(FitnessContract.fitnessDataEntry.COLUMN_STEPS));
        // Read date from cursor
        String dateInMillis = mCursor.getString(mCursor.getColumnIndex(FitnessContract.fitnessDataEntry.COLUMN_DATE));
        int idIndex = mCursor.getColumnIndex(FitnessContract.fitnessDataEntry._ID);

        AdapterViewHolder.itemView.setTag(mCursor.getInt(idIndex));
        AdapterViewHolder.mDistanceView.setText(mContext.getString(R.string.list_item_distance, distance));
        AdapterViewHolder.mDistanceView.setContentDescription(mContext.getString(R.string.a11y_list_item_distance, distance));
        AdapterViewHolder.mStepsView.setText(mContext.getString(R.string.list_item_steps, steps));
        AdapterViewHolder.mStepsView.setContentDescription(mContext.getString(R.string.a11y_list_item_steps, steps));
        AdapterViewHolder.mCalorieView.setText(mContext.getString(R.string.total_calories, calories));
        AdapterViewHolder.mCalorieView.setContentDescription(mContext.getString(R.string.a11y_list_item_calories, calories));
        AdapterViewHolder.mDateView.setText(dateInMillis);
        AdapterViewHolder.mDateView.setContentDescription(mContext.getString(R.string.a11y_today_date, dateInMillis));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

}
