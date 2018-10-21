package cn.snaptech.loopviewpager.widget.Banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.snaptech.loopviewpager.R;

public class Banner extends FrameLayout {

    private static final String TAG = "Banner";

    private ViewPager mVp_dataset;
    private LinearLayout mLl_indicator;// 指示器的ViewGroup
    private List<View> mDataView = new ArrayList<>();// 数据View集合
    private List<ImageView> mIndicators = new ArrayList<>();// 指示器View集合
    private List<String> mDatas = new ArrayList<>();// 数据集合
    private Context mContext;
    private int mCurrentIndex;// 当前要显示的View的下标
    private GetViewPagerItemView mGetItemViewInterface;// ViewPager中itemview显示的相关接口

    public Banner(@NonNull Context context) {
        this(context, null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View rootView = View.inflate(context, R.layout.banner, this);
        initViews(rootView);
    }

    private void initViews(View rootView) {
        mVp_dataset = rootView.findViewById(R.id.vp_dataset);
        mLl_indicator = rootView.findViewById(R.id.ll_indicator);
    }

    private void initDataSets(ArrayList<String> list,GetViewPagerItemView itemView) {
        mDataView.clear();
        mDatas.clear();
        // 添加轮播图View，数量为集合数+2
        setGetItemViewInterface(itemView);
        try {
            mDataView.add(mGetItemViewInterface.getViewPagerItemView(list.get(list.size() - 1),list.size() - 1));
            mDatas.add(list.get(list.size() - 1));
            for (int i = 0; i < list.size(); i++) {
                mDataView.add(mGetItemViewInterface.getViewPagerItemView(list.get(i),i));
                mDatas.add(list.get(i));
            }
            mDataView.add(mGetItemViewInterface.getViewPagerItemView(list.get(0),0));
            mDatas.add(list.get(0));
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void setGetItemViewInterface(GetViewPagerItemView interfaze){
        this.mGetItemViewInterface = interfaze;
    }

    /**
     * 设置要显示的数据
     * @param list
     * @param itemView
     */
    public void setDataSet(ArrayList<String> list,GetViewPagerItemView itemView) {
        if (list == null || list.size() == 0) {
            return;
        }
        initDataSets(list,itemView);
        initIndicators(list.size());
        setCurrentIndicatorShow(0);
        mVp_dataset.setFocusable(true);
        mCurrentIndex = 1;
        mVp_dataset.addOnPageChangeListener(new ViewPagerChangeListener());
        mVp_dataset.setAdapter(new ViewPagerAdapter(mContext, (ArrayList<View>) mDataView));
        mVp_dataset.setCurrentItem(mCurrentIndex);
    }

    private void initIndicators(int size) {
        for (int i = 0; i < size; i++) {
            ImageView indicator = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            indicator.setLayoutParams(params);
            mIndicators.add(indicator);
            mLl_indicator.addView(indicator);
        }
    }

    private void setCurrentIndicatorShow(int position) {
        for (int i = 0; i < mIndicators.size(); i++) {
            if (i == position) {
                mIndicators.get(i).setImageResource(R.drawable.shape_indicator_background_focus);
            } else {
                mIndicators.get(i).setImageResource(R.drawable.shape_indicator_background);
            }
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<View> mDatas = new ArrayList<>();

        public ViewPagerAdapter(Context context, ArrayList<View> datas) {
            this.mContext = context;
            this.mDatas.clear();
            this.mDatas.addAll(datas);
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            Log.d(TAG, "--destroyItem() position = " + position);
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, int position) {
            final View childView = mDatas.get(position);
            final ViewParent viewParent = childView.getParent();
            Log.d(TAG, "--instantiateItem() position = " + position + " parenetView is null ?= " + (viewParent == null) + " , container = " + container);
            // TODO Auto-generated method stub
            if (viewParent == null) {
                container.addView(childView);
            } else {
                Log.d(TAG, "--instantiateItem() position = " + position + " parenetView = " + viewParent);
                ((ViewGroup) viewParent).removeView(childView);
                // viewParent设置background的目的：防止最后一张图循环滑动至第一张图时，造成页面闪屏
                if (position == 0) {
                    mGetItemViewInterface.setItemBackground((ViewGroup) viewParent,Banner.this.mDatas.get(1));
                } else if (position == getCount() - 1) {
                    mGetItemViewInterface.setItemBackground((ViewGroup) viewParent,Banner.this.mDatas.get(mDatas.size() -1));
                } else {
                    ((ViewGroup) viewParent).setBackground(null);
                }
                container.post(new Runnable() {
                    @Override
                    public void run() {
                        container.addView(childView);
                        ((ViewGroup) viewParent).setBackground(null);
                    }
                });
            }
            return childView;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected() position = " + position + ", mDataView.size() = " + mDataView.size());
            mCurrentIndex = position;
            if (position == mDataView.size() - 1) {// 最后一张,4
                mCurrentIndex = 1;
                position = 0;
            } else if (position == 0) {// 第一张，0
                mCurrentIndex = mDataView.size() - 2;
                position = mIndicators.size() - 1;
            } else {// 1,2,3
                position = mCurrentIndex - 1;
            }
            setCurrentIndicatorShow(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    Log.d(TAG, "onPageScrollStateChanged() SCROLL_STATE_IDLE mCurrentIndex = " + mCurrentIndex);
                    mVp_dataset.setCurrentItem(mCurrentIndex, false);
                    break;
                default:
                    break;
            }
        }
    }
}
