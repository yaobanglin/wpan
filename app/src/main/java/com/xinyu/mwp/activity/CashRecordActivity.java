package com.xinyu.mwp.activity;

import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import com.xinyu.mwp.R;
import com.xinyu.mwp.activity.base.BaseRefreshAbsListControllerActivity;
import com.xinyu.mwp.adapter.CashRecordAdapter;
import com.xinyu.mwp.adapter.base.IListAdapter;
import com.xinyu.mwp.entity.CashRecordEntity;
import com.xinyu.mwp.entity.WithDrawCashReturnEntity;
import com.xinyu.mwp.listener.OnAPIListener;
import com.xinyu.mwp.listener.OnItemChildViewClickListener;
import com.xinyu.mwp.listener.OnRefreshListener;
import com.xinyu.mwp.listener.OnRefreshPageListener;
import com.xinyu.mwp.networkapi.NetworkAPIFactoryImpl;
import com.xinyu.mwp.util.LogUtil;
import com.xinyu.mwp.util.ToastUtils;
import com.xinyu.mwp.view.SpaceView;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Benjamin on 17/1/13.
 */

public class CashRecordActivity extends BaseRefreshAbsListControllerActivity<WithDrawCashReturnEntity> {
    @ViewInject(R.id.contentView)
    private ListView contentView;
    private CashRecordAdapter adapter;

    private int start = 1;
    private int count = 10;
    private List<WithDrawCashReturnEntity> drawCashEntityList;

    @Override
    protected IListAdapter<WithDrawCashReturnEntity> createAdapter() {
        return adapter = new CashRecordAdapter(context);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_listview;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("提现记录");
        contentView.addHeaderView(new SpaceView(context));
        requestNetData(null, start, count);
    }

    private void requestNetData(String status, int start, int count) {
        NetworkAPIFactoryImpl.getDealAPI().cashList(status, start, count, new OnAPIListener<List<WithDrawCashReturnEntity>>() {
            @Override
            public void onError(Throwable ex) {
                ex.printStackTrace();
                LogUtil.d("提现列表请求网络失败");
            }

            @Override
            public void onSuccess(List<WithDrawCashReturnEntity> withDrawCashReturnEntities) {
                LogUtil.d("提现列表请求网络成功:" + withDrawCashReturnEntities.toString());
                drawCashEntityList = withDrawCashReturnEntities;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        setOnRefreshPageListener(new OnRefreshPageListener() {
            @Override
            public void onRefresh(int pageIndex) {
                if (pageIndex == 1) {
                    start = 0;
                    doRefresh(start);
                } else {
                    start = start + 10;
                    doRefresh(start);
                }
            }
        });

        adapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
//                next(CashDetailActivity.class);
                ToastUtils.show(context, "暂未处理");
            }
        });
    }

    private void doRefresh(int number) {
        requestNetData(null, number, count);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (drawCashEntityList == null) {
                    ToastUtils.show(context, "网络链接失败-----数据为空");
                }
                getRefreshController().refreshComplete(drawCashEntityList);
            }
        }, 2000);
    }
}
