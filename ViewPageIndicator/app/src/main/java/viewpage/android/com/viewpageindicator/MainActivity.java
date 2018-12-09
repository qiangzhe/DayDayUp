package viewpage.android.com.viewpageindicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private ViewPageIndicator mIndicator;
    private List<String> mTitles= Arrays.asList("短信1","收藏2","推荐3");
    private List<VpSimpleFragment> mContents=new ArrayList<VpSimpleFragment>();
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initViews();
        initData();

        mIndicator.setVisibleTabCount(3);
        mIndicator.setTabItemTitles(mTitles);

        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager,0);

    }

    private void initData() {
        for(String title:mTitles){
            VpSimpleFragment fragment=VpSimpleFragment.newInstance(title);
            mContents.add(fragment);
        }

        mAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };
    }

    private void initViews() {
        mViewPager= (ViewPager) findViewById(R.id.id_viewpager);
        mIndicator= (ViewPageIndicator) findViewById(R.id.id_indicator);
    }


}
