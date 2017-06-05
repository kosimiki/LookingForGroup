package hu.blog.megosztanam;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.sub.menu.ApplicationsFragment;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;
import hu.blog.megosztanam.sub.menu.UserProfileFragment;

public class MainMenuActivity extends Activity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    private UserProfileFragment userProfileFragment;
    private NoticeBoardFragment noticeBoardFragment;
    private ApplicationsFragment applicationsFragment;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        // Instantiate a ViewPager and a PagerAdapter.
        Log.i("ASD", "Reaches this");
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(getIntent().getExtras());
        noticeBoardFragment = new NoticeBoardFragment();
        noticeBoardFragment.setArguments(getIntent().getExtras());
        applicationsFragment = new ApplicationsFragment();
        applicationsFragment.setArguments(getIntent().getExtras());

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 1:  return userProfileFragment;
                case 0:  return noticeBoardFragment;
                case 2:  return applicationsFragment;
                default: return userProfileFragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}