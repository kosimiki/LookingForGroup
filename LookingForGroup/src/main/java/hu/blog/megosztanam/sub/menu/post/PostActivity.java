/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.blog.megosztanam.sub.menu.post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;
import com.tech.freak.wizardpager.ui.ReviewFragment;
import com.tech.freak.wizardpager.ui.StepPagerStrip;

import java.util.ArrayList;
import java.util.List;

import hu.blog.megosztanam.MainApplication;
import hu.blog.megosztanam.MainMenuActivity;
import hu.blog.megosztanam.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.GameType;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.PostType;
import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.elo.Division;
import hu.blog.megosztanam.model.shared.elo.Rank;
import hu.blog.megosztanam.model.shared.elo.Tier;
import hu.blog.megosztanam.rest.ILFGService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.COMMENT;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.GAME_TYPE;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.HOWLING_FJORD;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.MAP_KEY;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.MAX_DIV;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.MAX_TIER;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.MIN_DIV;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.MIN_TIER;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.OPEN_POSITIONS;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.RANKED;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.SUMMONERS_RIFT;
import static hu.blog.megosztanam.sub.menu.post.PostWizardModel.TWISTED_TREELINE;

public class PostActivity extends AppCompatActivity implements
        PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks {

    public static final String NEW_POST_EXTRA = PostActivity.class.getName() + ".new_post_extra";

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private ParcelableLoginResponse userDetails;
    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    private ILFGService lfgService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainApplication application = (MainApplication) getApplication();
        lfgService = application.getServiceContainer().getLfgService();


        userDetails = getIntent().getParcelableExtra(LoginActivity.USER_DETAILS_EXTRA);
        Log.i(this.getClass().getName(), "User: " + userDetails.toString());


        mWizardModel = new PostWizardModel(this);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = findViewById(R.id.strip);
        mStepPagerStrip
                .setOnPageSelectedListener(position -> {
                    position = Math.min(mPagerAdapter.getCount() - 1,
                            position);
                    if (mPager.getCurrentItem() != position) {
                        mPager.setCurrentItem(position);
                    }
                });
        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    Bundle result = mWizardModel.save();
                    Log.i(this.getClass().getName(), result.toString());

                    String map = result.getBundle(MAP_KEY).getString("_");
                    String comment = "";
                    if (!result.getBundle(COMMENT).isEmpty()) {
                        comment = result.getBundle(COMMENT).getString("_");
                    }
                    Rank minRank = new Rank();
                    Rank maxRank = new Rank();

                    boolean isRanked = !map.equals(HOWLING_FJORD) && result.getBundle(map + ":" + GAME_TYPE).getString("_").contains(RANKED);
                    if (isRanked) {
                        String minDiv = result.getBundle(RANKED + " : " + map + ":" + MIN_DIV).getString("_");
                        String maxDiv = result.getBundle(RANKED + " : " + map + ":" + MAX_DIV).getString("_");
                        String minTier = result.getBundle(RANKED + " : " + map + ":" + MIN_TIER).getString("_");
                        String maxTier = result.getBundle(RANKED + " : " + map + ":" + MAX_TIER).getString("_");
                        minRank = new Rank(Tier.valueOf(minTier), Division.valueOf(minDiv));
                        maxRank = new Rank(Tier.valueOf(maxTier), Division.valueOf(maxDiv));
                    }
                    final List<Role> openPositions = new ArrayList<>();
                    List<String> stringPositions = result.getBundle(map+":"+OPEN_POSITIONS).getStringArrayList("_");
                    for (String position: stringPositions){
                        openPositions.add(Role.valueOf(position));
                    }


                    GameMap gameMap = GameMap.SUMMONERS_RIFT;
                    switch (map){
                        case SUMMONERS_RIFT : gameMap =GameMap.SUMMONERS_RIFT;break;
                        case TWISTED_TREELINE : gameMap =GameMap.TWISTED_TREE_LINE;break;
                        case HOWLING_FJORD : gameMap =GameMap.HOWLING_FJORD;break;
                    }

                    Post post = new Post();
                    post.setPostType(PostType.LOOKING_FOR_MEMBER);
                    post.setGameType(new GameType(gameMap, isRanked));
                    post.setDescription(comment);
                    post.setOpenPositions(openPositions);
                    post.setMinimumRank(minRank);
                    post.setMaximumRank(maxRank);
                    post.setPostId(0);
                    post.setUserId(userDetails.getUser().getUserId());
                    post.setOwner(userDetails.getUser().getSummoner());
                    post.setServer(userDetails.getUser().getServer());

                    savePost(post);
                    navigateBack();

                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });
        Log.i(this.getClass().getName(), "setOnClickListener");

        mPrevButton.setOnClickListener(view -> mPager.setCurrentItem(mPager.getCurrentItem() - 1));

        Log.i(this.getClass().getName(), "setOnClickListener");
        onPageTreeChanged();
        updateBottomBar();
    }

    private void navigateBack(){
        Intent intent = new Intent(this, MainMenuActivity.class);
        ParcelableLoginResponse parcelableLoginResponse = new ParcelableLoginResponse(userDetails);
        intent.putExtra(LoginActivity.USER_DETAILS_EXTRA, parcelableLoginResponse);
        intent.putExtra(PostActivity.NEW_POST_EXTRA, true);
        startActivity(intent);
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 =
        // review
        // step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(getString(R.string.finish));
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview ? R.string.review
                    : R.string.next);
            mNextButton
                    .setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v,
                    true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton
                .setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object == mPrimaryItem) {
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
                    : mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }

    private void savePost(Post post) {
        Call<Integer> postResponse = lfgService.savePost(post);
        postResponse.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Log.i(PostActivity.class.getName(), "Response: " + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.i(PostActivity.class.getName(), "Failure: " + t.toString());
            }
        });
    }


}
