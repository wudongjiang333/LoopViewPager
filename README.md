# LoopViewPager
基于ViewPager实现的轮播图控件

# Android：实现一个轮播图(Banner)
## 前言 && 需求
在Android App中，轮播图还是很常见的，如Splash闪屏页的引导图，电商App首页上的广告轮播图等等。其实github是有很多此类组件，但是实现起来也不难，不妨自己去尝试尝试。

## 思路
此博文是基于ViewPager实现的轮播图。

1. 我们也知道，ViewPager是不能实现循环播放的，但我们可以巧妙的利用ViewPager的方法:

`setCurrentItem(int item, boolean smoothScroll)`

**如果当前item与下一个item的视图一致时，smoothScroll置为false，UI上是看不到视图重新绘制过程的，这是实现ViewPager无限轮播的关键**

2. 基于上述的方法，我们将要显示的视图，设计如下：
![](https://i.imgur.com/uewYNqG.png)

轮播流程：
View1 -->View2 --> View n --> View1（完成一次循环）-->View2 -->View3....
当显示View n的时候，立刻切换到View1（Viewn和View1显示的内容是相同的），这样就实现了图片轮播(利用setCurrentItem())。

## 实现
自定义一个轮播控件Banner:

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

banner的布局：

	<?xml version="1.0" encoding="utf-8"?>
	<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent">
	
	    <android.support.v4.view.ViewPager
	        android:id="@+id/vp_dataset"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent">
	    </android.support.v4.view.ViewPager>
	
	    <LinearLayout
	        android:id="@+id/ll_indicator"
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:layout_gravity="bottom"
	        android:gravity="center"
	        android:orientation="horizontal">
	    </LinearLayout>
	
	</FrameLayout>

GetViewPagerItemView：
	
	package cn.snaptech.loopviewpager.widget.Banner;
	
	import android.view.View;
	import android.view.ViewGroup;
	
	public interface GetViewPagerItemView {
	    /**
	     * 获取每个ViewPager 的 item 要显示的视图
	     * @param url
	     * @param position
	     * @return
	     */
	    View getViewPagerItemView(String url, int position);
	
	    /**
	     * 设置item的背景
	     * @param parentView
	     * @param url
	     */
	    void setItemBackground(ViewGroup parentView, String url);
	}

## 说明：
轮播过程中出现：

	java.lang.IllegalStateException: The specified child already has a parent

意思是一个子View已经存在一个父View，你必须先调用该子视图的父视图的 removeView() 方法，这种情况通常出现在动态添加视图的情况下，出现这种错误的原因是一个子控件只允许存在一个父控件，而很多时候在动态添加视图的时候，我们不知道该子视图是否已存在父视图，当已存在的时候就会报错。

那么解决方式就很明了了：

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

设置background，是因为采取以上方法解决IllegalStateException问题后，首尾轮播图片时，会造成闪屏，所以为了过渡自然，设置背景。

## 效果图
![在这里插入图片描述](https://img-blog.csdn.net/20181021140048904?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3d1ZG9uZ2ppYW5nMzMz/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

## 源码
[https://github.com/wudongjiang333/LoopViewPager](https://github.com/wudongjiang333/LoopViewPager "https://github.com/wudongjiang333/LoopViewPager")
