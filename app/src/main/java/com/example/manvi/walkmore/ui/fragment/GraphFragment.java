package com.example.manvi.walkmore.ui.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.WalkMore;
import com.example.manvi.walkmore.data.FitnessContract;
import com.example.manvi.walkmore.utils.ConstantUtils;
import com.example.manvi.walkmore.utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static int GRAPH_LOADER  = 2001;
    private static final String[] PROJECTION = new String[] {
            FitnessContract.fitnessDataEntry.COLUMN_DATE,
            FitnessContract.fitnessDataEntry.COLUMN_STEPS
    };

    private static final String ARG_FRAGMENT_NAME = "fragment_name";

    private BarChart mBarChart;
    private XAxis xAxis;
    private int mTabPosition;

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance(int param1) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_NAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_graph, container, false);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_FRAGMENT_NAME)) {
                mTabPosition = args.getInt(ARG_FRAGMENT_NAME);
            }
        }
        mBarChart = (BarChart)rootView.findViewById(R.id.chart);
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupBarChart();
        setupXAxis();
        setupYAxis();
        Legend l = mBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        getLoaderManager().restartLoader(GRAPH_LOADER, null, this);

        WalkMore application = (WalkMore) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.graph_screen));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupBarChart(){
        mBarChart.setDrawGridBackground(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mBarChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);
        mBarChart.setFitBars(true);

        mBarChart.setDrawGridBackground(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupXAxis(){
        xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
    }

    private void setupYAxis(){
        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = FitnessContract.fitnessDataEntry.buildFitnessDataUriWithTabID(mTabPosition);
        return new CursorLoader(getActivity(),uri,
                PROJECTION,
                null,
                null,
                FitnessContract.fitnessDataEntry.COLUMN_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null && data.getCount()!=0) {
            ArrayList<String> datesList = new ArrayList<>();
            ArrayList<BarEntry> barEntriesList = new ArrayList<>();
            int i = 0;
            try {
                while (data.moveToNext()) {
                    String date = data.getString(data.getColumnIndex(FitnessContract.fitnessDataEntry.COLUMN_DATE));
                    if(mTabPosition!= ConstantUtils.YEAR_TAB) {
                        datesList.add(DateUtils.changeDateFormat(date, mTabPosition));
                    }else {
                        datesList.add(DateUtils.changeMonthFormat(date));
                    }
                    barEntriesList.add(new BarEntry(i++, data.getInt(data.getColumnIndex(FitnessContract.fitnessDataEntry.COLUMN_STEPS))));
                }
            } finally {
                data.close();
            }
            Collections.sort(barEntriesList, new EntryXComparator());
            BarDataSet set1;
            setXAxisDataValues(datesList);

            if (mBarChart.getData() != null &&
                    mBarChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
                set1.setValues(barEntriesList);
                mBarChart.getData().notifyDataChanged();
                mBarChart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(barEntriesList, getString(R.string.graph_description));
                set1.setDrawIcons(false);
                set1.setColors(ColorTemplate.COLORFUL_COLORS);
                set1.setValueTextColor(ColorTemplate.getHoloBlue());
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                BarData barData = new BarData(dataSets);
                barData.setValueTextSize(10f);
                mBarChart.setData(barData);
                mBarChart.animateY(3000);
                mBarChart.invalidate();
            }
        }
    }

    private void setXAxisDataValues(ArrayList<String> dateInMilis) {
        final String[] closeDates = dateInMilis.toArray(new String[dateInMilis.size()]);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return closeDates[(int) value];
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
