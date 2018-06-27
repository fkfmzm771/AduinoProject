package naveropenapi.example.com.aduinoproject.Ui.MainViewPager;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    public static final int ITEMCOUNT = 4;
    Fragment[] mFragments = {new VPager01(), new VPager02(), new VPager03(), new VPager04()};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

//        position = (position + 1) % ITEMCOUNT;

        return mFragments[position];

    }

    @Override
    public int getCount() {
        return ITEMCOUNT;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        position = (position + 1) % ITEMCOUNT;

        switch (position) {
            case 0:
                return "MisakiAR";
            case 1:
                return "MEMO";
            case 2:
                return "COLOR";
            case 3:
                return "COMMAND";
            default:
                return null;
        }
    }
}
