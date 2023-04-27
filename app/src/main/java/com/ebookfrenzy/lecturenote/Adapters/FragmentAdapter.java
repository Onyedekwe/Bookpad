package com.ebookfrenzy.lecturenote.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ebookfrenzy.lecturenote.Layout1;
import com.ebookfrenzy.lecturenote.Layout2;
import com.ebookfrenzy.lecturenote.Layout3;
import com.ebookfrenzy.lecturenote.Layout4;
import com.ebookfrenzy.lecturenote.Layout5;

public class FragmentAdapter extends FragmentPagerAdapter {

    int numCount;

    public FragmentAdapter(@NonNull FragmentManager fm, int numCount) {
        super(fm);
        this.numCount = numCount;
    }

    @NonNull
    @Override

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Layout1();

            case 1:
                return new Layout2();

            case 2:
                return new Layout3();

            case 3:
                return new Layout4();

            case 4:
                return new Layout5();
        }

        return null;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Home";
            case 1:
                return "Content";
            case 2:
                return "Detail";
            case 3:
                return "Checklist";
            case 4:
                return "Backup & Restore";
        }
        return null;
    }

    @Override
    public int getCount() {
        return numCount;
    }
}
