package hu.blog.megosztanam;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import hu.blog.megosztanam.dependency.BackendServiceDependency;
import hu.blog.megosztanam.dependency.GoogleAuthServiceDependency;
import hu.blog.megosztanam.login.GoogleAuthService;
import hu.blog.megosztanam.model.shared.messaging.Messaging;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.sub.menu.ApplicationsFragment;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;
import hu.blog.megosztanam.sub.menu.UserProfileFragment;

public class MainMenuActivity extends AppCompatActivity implements BackendServiceDependency, GoogleAuthServiceDependency {
    private UserProfileFragment userProfileFragment;
    private NoticeBoardFragment noticeBoardFragment;
    private ApplicationsFragment applicationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().subscribeToTopic(Messaging.NEW_POSTS_TOPIC);
        FirebaseMessaging.getInstance().subscribeToTopic(Messaging.POST_DELETED);
        userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(getIntent().getExtras());
        noticeBoardFragment = new NoticeBoardFragment();
        noticeBoardFragment.setArguments(getIntent().getExtras());
        applicationsFragment = new ApplicationsFragment();
        applicationsFragment.setArguments(getIntent().getExtras());

        setContentView(R.layout.main_menu_layout);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, getString(R.string.user_profile_title));
        adapter.addFragment(noticeBoardFragment, getString(R.string.notice_board_title));
        adapter.addFragment(applicationsFragment, getString(R.string.application_tab_title));
        viewPager.setAdapter(adapter);
    }

    @Override
    public ILFGService getLfgService() {
        MainApplication application = (MainApplication) getApplication();
        return application.getServiceContainer().getLfgService();
    }

    @Override
    public GoogleAuthService getAuthService() {
        MainApplication application = (MainApplication) getApplication();
        return application.getServiceContainer().getAuthService();
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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