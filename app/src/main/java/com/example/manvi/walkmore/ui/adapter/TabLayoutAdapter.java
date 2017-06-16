package com.example.manvi.walkmore.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.manvi.walkmore.ui.fragment.GraphFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manvi on 31/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class TabLayoutAdapter extends FragmentPagerAdapter {
    private final List<String> mFragmentTitleList = new ArrayList<>();

    /**
     * Create a new {@link TabLayoutAdapter} object.
     *
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public TabLayoutAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        return GraphFragment.newInstance(position);
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return mFragmentTitleList.size();
    }

    public void addFragment(String title)
    {
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
// Generate title based on item position
        return mFragmentTitleList.get(position);
    }
}