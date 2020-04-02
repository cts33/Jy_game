package com.example.jy_game;

import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected TextView mName;
    protected ImageView mImageView;
    protected MySelfGridView mGridview;
     String[] drawablePaths ;
    int l, t;

    String hostDrawable;

    int maxPics = 6;

    protected int startX = 0;
    protected int startY = 0;
    protected int mTop;
    protected int mLeft;



    protected RelativeLayout.LayoutParams layoutParams;

    private final String TAG =  getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawablePaths = new String[6];



    }

    protected abstract void initView();

    @Override
    protected void onResume() {
        super.onResume();

        layoutParams = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();

        layoutParams.width = mGridview.getImageItemWidth();
        layoutParams.height = mGridview.getImageItemWidth();
        mImageView.setLayoutParams(layoutParams);

        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
            Log.i(TAG, "------------actionBarHeight=" + actionBarHeight);
        }

        l = DensityUtil.getScreenWidth(this) - layoutParams.width;
        t = DensityUtil.getScreenHeight(this) - layoutParams.width
                - actionBarHeight ;

        Log.d(TAG, "onResume: " + t);

        setImageViewMargin(l / 2, l / 2);

    }


    /**
     * 获取随机数 和设置图片
     */
    public void getRandomNum() {

        //清除数组

        for (int i = 0; i < drawablePaths.length; i++) {
            drawablePaths[i]="";
        }

        getEachHostPic();
        getEachPageList();



    }

    /**
     * 获取每个页面的主图片
     */

    protected abstract void getEachHostPic();
    /**
     * 获取每个页面的要对比的图片集合
     */

    protected abstract void getEachPageList();


    /**
     * 动态设置控件的marginTop 和 marginLeft的值
     *
     * @param dX x轴的偏移量
     * @param dY y轴的偏移量
     */
    protected void setImageViewMargin(int dX, int dY) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
        int top = mTop + dY;
        int left = mLeft + dX;

        Log.d(TAG, "setImageViewMargin: top=" + top + "   left=" + left);
        //设置left和top的边界值
        if (left < 0) {
            left = 0;
        } else if (left > l) {
            left = l;
        }
        if (top < 0) {
            top = 0;
        } else if (top > t) {
            top = t;
        }
        layoutParams.topMargin = top;
        layoutParams.leftMargin = left;
        mImageView.setLayoutParams(layoutParams);
    }
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //初始化开始位置
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                mTop = mImageView.getTop();
                mLeft = mImageView.getLeft();
                break;
            case MotionEvent.ACTION_MOVE:
                //手势移动的dX和dY为控件的marginLeft和marginTop
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();
                int dX = moveX - startX;
                int dY = moveY - startY;
                setImageViewMargin(dX, dY);
                break;
            case MotionEvent.ACTION_UP:
                //初始值重置
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                mTop = mImageView.getTop();
                mLeft = mImageView.getLeft();


                int[] location = new int[2];
                mImageView.getLocationOnScreen(location);
                int left = location[0];
                int top = location[1];

                Log.d(TAG, "onTouch: onTop=" + mTop + "  mLeft=" + mLeft);
                final View viewAtActivity = DensityUtil.findViewByXY(mGridview, left, top);

                ifSame(viewAtActivity);
                ifLike(viewAtActivity);


                break;
        }
        return true;
    }

    protected abstract void ifLike(View view);

    protected abstract void ifSame(View view);



    protected void setPicView(ImageView mImageView, MySelfGridView mGridview) {

        try {
            mImageView.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(hostDrawable)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mGridview.initGridList(this, drawablePaths, new MySelfGridView.IUpdateUIListener() {
                @Override
                public void setItem(Object o, ImageView img) {

                    String path = (String) o;
                    try {
                        img.setTag(path);
                        img.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(path)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void anim(){

        final int yuan = l/2;
        Log.d(TAG, "anim: "+yuan+"  top="+mTop+"   mLeft="+mLeft);

        // 指示器旋转
        ValueAnimator valueAnimator1 = ValueAnimator.ofInt( mTop,yuan);

        valueAnimator1.setDuration(500);

        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                Log.d(TAG, "anim: value="+value );
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                layoutParams.topMargin = value;
                int progress = (value-yuan)*100/(mTop-yuan);
                Log.d(TAG, "anim: progress="+progress );
                layoutParams.leftMargin= (mLeft-yuan)*progress/100+yuan;
                Log.d(TAG, "anim: leftMargin="+ layoutParams.leftMargin );


                mImageView.setLayoutParams(layoutParams);
            }
        });


        valueAnimator1.start();
    }
}
