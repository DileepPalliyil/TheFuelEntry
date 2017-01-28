package in.thefleet.thefuelentry;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class Pager extends FragmentStatePagerAdapter {
    //integer to count number of tabs
    int tabCount;
    public Pager(FragmentManager fm,int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CreateTab createTab = new CreateTab();
                return createTab;
            case 1:
                FleetTab fleetTab = new FleetTab();
                return fleetTab;
            case 2:
                StationTab stationTab = new StationTab();
                return stationTab;
            case 3:
                LinkTab linkTab = new LinkTab();
                return linkTab;
            case 4:
                SavedTab savedTab = new SavedTab();
                return savedTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
