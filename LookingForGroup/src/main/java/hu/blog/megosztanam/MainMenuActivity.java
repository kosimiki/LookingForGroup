package hu.blog.megosztanam;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.sub.menu.ApplicationsFragment;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;
import hu.blog.megosztanam.sub.menu.UserProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {
    private UserProfileFragment userProfileFragment;
    private NoticeBoardFragment noticeBoardFragment;
    private ApplicationsFragment applicationsFragment;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(getIntent().getExtras());
        noticeBoardFragment = new NoticeBoardFragment();
        noticeBoardFragment.setArguments(getIntent().getExtras());
        applicationsFragment = new ApplicationsFragment();
        applicationsFragment.setArguments(getIntent().getExtras());

        setContentView(R.layout.main_menu_layout);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                updatePosts(position);
            }

            @Override
            public void onPageSelected(int position) {
                updatePosts(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }
    private void updatePosts(Integer position){
        if(position == 1 && noticeBoardFragment.canLoad()){
            noticeBoardFragment.loadPosts();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, "User Profile");
        adapter.addFragment(noticeBoardFragment, "Notice Board");
        adapter.addFragment(applicationsFragment, "Applicants");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    @Override
    public void onBackPressed() {
    }
}