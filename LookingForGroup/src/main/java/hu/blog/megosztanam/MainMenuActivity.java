package hu.blog.megosztanam;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import hu.blog.megosztanam.dependency.BackendServiceDependency;
import hu.blog.megosztanam.dependency.GoogleAuthServiceDependency;
import hu.blog.megosztanam.login.GoogleAuthService;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.sub.menu.ApplicationsFragment;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;
import hu.blog.megosztanam.sub.menu.UserProfileFragment;

public class MainMenuActivity extends AppCompatActivity implements BackendServiceDependency, GoogleAuthServiceDependency {
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

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                updatePosts(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, "User Profile");
        adapter.addFragment(noticeBoardFragment, "Notice Board");
        adapter.addFragment(applicationsFragment, "Applicants");
        viewPager.setAdapter(adapter);
    }

    @Override
    public ILFGService getLfgService() {
        MainApplication application = (MainApplication) getApplication();
        return application.getAppContainer().getLfgService();
    }

    @Override
    public GoogleAuthService getAuthService() {
        MainApplication application = (MainApplication) getApplication();
        return application.getAppContainer().getAuthService();
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