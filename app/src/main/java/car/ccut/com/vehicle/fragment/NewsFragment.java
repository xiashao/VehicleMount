package car.ccut.com.vehicle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.NewsFragPageAdp;
import car.ccut.com.vehicle.base.BaseFragment;
import car.ccut.com.vehicle.bean.NewsCate;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.util.Utils;
import car.ccut.com.vehicle.view.ViewPagerIndicator;

/**
 * *
 * へ　　　　　／|
 * 　　/＼7　　　 ∠＿/
 * 　 /　│　　 ／　／
 * 　│　Z ＿,＜　／　　 /`ヽ
 * 　│　　　　　ヽ　　 /　　〉
 * 　 Y　　　　　`　 /　　/
 * 　ｲ●　､　●　　⊂⊃〈　　/
 * 　()　 へ　　　　|　＼〈
 * 　　>ｰ ､_　 ィ　 │ ／／      去吧！
 * 　 / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　 ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　7　　　　　　　|／
 * 　　＞―r￣￣`ｰ―＿
 * Created by WangXin on 2016/7/28 0028.
 */
public class NewsFragment extends BaseFragment{
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private List<NewsCate> cateList = new ArrayList<>();
    @Bind(R.id.news_pager)
    ViewPager mViewPager;
    @Bind(R.id.indicator)
    ViewPagerIndicator indicator;
    @Bind(R.id.network_fault)
    TextView networkFault;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWaitDialog();
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_news,container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void initData() {
        for (int i=0;i< ConstantValue.newsCate.length;i++){
            NewsCate cate = new NewsCate(ConstantValue.newsId[i],ConstantValue.newsCate[i]);
            cateList.add(cate);
        }
    }

    @Override
    public void initView(View view) {
        if (Utils.hasNetwork(getActivity())){
            networkFault.setVisibility(View.GONE);
            if (cateList!=null&&!cateList.isEmpty()){
                initFragment();
            }
        }else {
            networkFault.setVisibility(View.VISIBLE);
        }
    }

    /**
     *  初始化Fragment
     * */
    private void initFragment() {
        fragments.clear();//清空
        int count = cateList.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("text", cateList.get(i).getCateName());
            data.putString("cate_id", cateList.get(i).getCateId());
            NewsDetailsFragment newfragment = new NewsDetailsFragment();
            newfragment.setArguments(data);
            fragments.add(newfragment);
        }
        mViewPager.setAdapter(new NewsFragPageAdp(this.getChildFragmentManager(), fragments, cateList));
        indicator.setTabItemTitles(cateList);
        indicator.setViewPager(mViewPager,0);
    }
}
