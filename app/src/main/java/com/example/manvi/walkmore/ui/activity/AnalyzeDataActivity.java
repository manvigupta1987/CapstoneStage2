package com.example.manvi.walkmore.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.ui.adapter.TabLayoutAdapter;

public final class AnalyzeDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_data);
        setupToolBar();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager());


        adapter.addFragment(getString(R.string.string_weeks));
        adapter.addFragment(getString(R.string.string_months));
        adapter.addFragment(getString(R.string.string_year));
        // Create an adapter that knows which fragment should be shown on each page
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupToolBar(){
        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar1);
        mToolBar.setTitle(getString(R.string.analyze_data));
        setSupportActionBar(mToolBar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            mToolBar.setTitleTextColor(android.graphics.Color.WHITE);
            getSupportActionBar().setHomeActionContentDescription(getString(R.string.a11y_previous_screen));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
           case android.R.id.home:
               finish();
               return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
