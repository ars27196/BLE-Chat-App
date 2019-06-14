package de.kai_morich.simple_bluetooth_le_terminal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.kai_morich.simple_bluetooth_le_terminal.chat.ChatFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    FragmentManager fragmentManager;

    public PagerAdapter( int NumOfTabs, FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        this.mNumOfTabs = NumOfTabs;
        fragmentManager=supportFragmentManager;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ChatFragment tab1 = new ChatFragment();
                return tab1;
            case 1:
                Constants.fragmentManager=fragmentManager;
                BeaconFragment tab2 = new BeaconFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
