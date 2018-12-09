package viewpage.android.com.viewpageindicator;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mr Yang on 2017/8/13 0013 15:02
 */

public class VpSimpleFragment extends Fragment {
    private String mTitle;
    public static final String BUNDLE_TITLE="title";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        if(bundle!=null){
            mTitle=bundle.getString(BUNDLE_TITLE);
        }

        View view=inflater.inflate(R.layout.fragment_main,container,false);
        TextView tv=view.findViewById(R.id.tv_content);
        tv.setText(mTitle);
        return view;
    }

    public static VpSimpleFragment newInstance(String title){
        Bundle bundle=new Bundle();
        bundle.putString(BUNDLE_TITLE,title);
        VpSimpleFragment vpSimpleFragment=new VpSimpleFragment();
        vpSimpleFragment.setArguments(bundle);
        return vpSimpleFragment;
    }

}
