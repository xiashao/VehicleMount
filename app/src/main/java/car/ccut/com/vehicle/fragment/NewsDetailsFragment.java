package car.ccut.com.vehicle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.demievil.library.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.BaseFragment;
import car.ccut.com.vehicle.bean.News;
import car.ccut.com.vehicle.bean.NewsResult;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;

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
public class NewsDetailsFragment extends BaseFragment{
    public boolean isLoad = true;
    protected int mCurrentPage = 1;
    @Bind(R.id.listview)
    public ListView listview;
    @Bind(R.id.swipe_container)
    public RefreshLayout mRefreshLayout;
    public TextView load_more_tv;
    public LinearLayout loading_layout;
    public View footerLayout;

    String text;
    String channel_id;
    private List<News> dataList = new ArrayList<>();
    protected QuickAdapter<News> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        text = args != null ? args.getString("text") : "";
        channel_id = args != null ? args.getString("cate_id") : "";
        initData();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.news_listview, container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        footerLayout = inflateView(R.layout.include_load_more);
        load_more_tv = (TextView) footerLayout.findViewById(R.id.load_more_tv);
        loading_layout = (LinearLayout) footerLayout.findViewById(R.id.loading_layout);
        listview.addFooterView(footerLayout);
        mRefreshLayout.setChildView(listview);

        mRefreshLayout.setColorSchemeResources(R.color.app_theme_blue);

        load_more_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(isLoad);
            }
        });

        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 0;
                isLoad = true;
                load_more_tv.setText("加载更多");
                refreshData();
            }
        });
        mRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                loadData(isLoad);
            }
        });
        refreshData();
        return view;
    }


    @OnItemClick(R.id.listview)
    public void itemClick(int position) {
//        Utils.intentActivityInt(getActivity(), NewsDetailActivity.class, "news_id", dataList.get(position).getNews_id());
    }

    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void initData() {
        super.initData();
    }

    public void loadData(boolean isload) {
        if (isload) {
            load_more_tv.setVisibility(View.GONE);
            loading_layout.setVisibility(View.VISIBLE);
            mCurrentPage++;
            loadMoreData();
        }
    }

    public void refreshData() {
        showWaitDialog();
        if ("".equals(channel_id)||"".equals(text)){
            return;
        }
        final Map header = new HashMap();
        header.put("apikey",ConstantValue.NEWS_KEY);
        Map params = new HashMap();
        params.put("channelId",channel_id);
        params.put("page",mCurrentPage+"");
        params.put("needContent","1");
        params.put("needAllList","0");
        JsonRequestWithAuth<NewsResult> getNews = new JsonRequestWithAuth<NewsResult>(ConstantValue.NEWS_URL, NewsResult.class, new Response.Listener<NewsResult>() {
            @Override
            public void onResponse(NewsResult response) {
                hideWaitDialog();
                try {
                    dataList = response.getShowapi_res_body().getPagebean().getContentlist();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dataList!=null&&!dataList.isEmpty()){
                    if (adapter==null){
                        adapter = new QuickAdapter<News>(getActivity(),R.layout.item_news_list,dataList) {
                            @Override
                            protected void convert(BaseAdapterHelper helper, News item) {
                                try {
                                    if (!item.getImageurls().isEmpty()){
                                        helper.setImageUrl(R.id.iv_thumb,item.getImageurls().get(0).getUrl());
                                    }
                                    helper.setText(R.id.tv_title,item.getTitle())
                                            .setText(R.id.tv_abstract,item.getContent());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                    }else {
                        adapter.replaceAll(dataList);
                    }
                    listview.setAdapter(adapter);
                }
            }
        }, header,params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitDialog();
            }
        });
        MyApplication.getHttpQueues().add(getNews);
        MyApplication.getHttpQueues().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void loadMoreData() {
//        BandApi.getNewsList(Constants.GET_ALL_NEWS,mCurrentPage,channel_id,loadMoreHandler());
    }
}
