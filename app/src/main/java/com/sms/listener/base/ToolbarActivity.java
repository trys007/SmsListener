package com.sms.listener.base;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sms.listener.R;
import com.sms.listener.utils.ColorUtils;


/**
 * 带有Toolbar的Activity基类
 */
public abstract class ToolbarActivity extends BaseActivity implements View.OnClickListener {

    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    private TextView mTitle;
    private View toolbarBottomLine;

    private ImageView mLeftIv; // 左图片
    private ImageView mRightIv; // 右图片
    protected TextView mRightTv; // 右标题
    private ImageView mRight2Iv; // 右二图片（靠左的那个）

    private GoTopListener mGoTopListener;

    private boolean doubleFlag = false;

    private float currentToolbarAlpha = 1;//当前透明度
    //private float mineToolbarAlpha=1;//当前透明度
     /*toolbar渐变背景*/
    GradientDrawable gradientStateNormal;

    @Override
    protected void init() {
        super.init();

        initToolBar();
    }

    @Override
    protected boolean setTranslucentStatusBar() {
        return false;
    }


    /*创建toolbar渐变背景*/
    public GradientDrawable createToolBartDrawable() {
        if (gradientStateNormal == null) {
            gradientStateNormal = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{Color.BLACK, Color.TRANSPARENT
                    });
            gradientStateNormal.setAlpha((int) (0.5 * 255));
        }
        return gradientStateNormal;
    }


    /**
     * 初始化工具栏
     * 包括标题和返回键
     */
    private void initToolBar() {

        gradientStateNormal = createToolBartDrawable();


        currentToolbarAlpha = 1;

        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mLeftIv = (ImageView) findViewById(R.id.left_iv);
        mRightIv = (ImageView) findViewById(R.id.right_iv);
        mRight2Iv = (ImageView) findViewById(R.id.right2_iv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbarBottomLine = (View) findViewById(R.id.toolbar_bottom_line);
        if (mToolbar == null || mAppBarLayout == null) {
            throw new IllegalStateException(
                    "The subclass of ToolbarActivity must contain a toolbar.");
        }
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 设置是否显示标题
            actionBar.setDisplayShowTitleEnabled(false);
        }
        // 设置返回键监听事件
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        // 设置双击标题栏返回顶部事件
        mToolbar.setOnClickListener(this);
        showBackBtn(false);
        showLeftIv(false);
        showRightIv(false);
        showRightTv(false);
        setListener();
        initToolBarColorWithAlpha();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //恢复透明度
            currentToolbarAlpha = savedInstanceState.getFloat("CURRENT_TOOLBAR_ALPHA");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //保存透明度
        outState.putFloat("CURRENT_TOOLBAR_ALPHA", currentToolbarAlpha);
        super.onSaveInstanceState(outState);
    }


    /**
     * 设置标题文字
     *
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
        super.setTitle(title);
    }

    /**
     * 设置标题栏监听
     */
    private void setListener() {
        mLeftIv.setOnClickListener(this);
        mRightIv.setOnClickListener(this);
        mRight2Iv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_iv: {
                onLeftIvClick(view);
            }
            break;
            case R.id.right_iv: {
                onRightIvClick(view);
            }
            break;
            case R.id.right2_iv: {
                onRight2IvClick(view);
            }
            break;
            case R.id.right_tv: {
                onRightTvClick((TextView) view);
            }
            break;
            case R.id.toolbar: {
                if (doubleFlag) {
                    if (mGoTopListener == null) {
                        return;
                    }
                    mGoTopListener.scrollToTop();
                } else {
                    doubleFlag = true;
                    new Handler().postDelayed(() -> doubleFlag = false, 350);
                }
            }
            break;
        }
    }


    /**
     * 左图片点击
     *
     * @param view
     */
    protected void onLeftIvClick(View view) {

    }

    /**
     * 右标题点击
     */
    protected void onRightTvClick(TextView view) {
    }


    /**
     * 右图片点击
     *
     * @param view
     */
    protected void onRightIvClick(View view) {

    }

    /**
     * 右二图片点击
     */
    protected void onRight2IvClick(View view) {

    }

    /**
     * 设置标题颜色
     *
     * @param textColor
     */
    @Override
    public void setTitleColor(int textColor) {
        mTitle.setTextColor(textColor);
        super.setTitleColor(textColor);
    }

    /**
     * 显示返回键
     *
     * @param show true 显示, false 不显示
     */
    protected void showBackBtn(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        // 设置是否显示返回键
        actionBar.setDisplayHomeAsUpEnabled(show);
    }


    /**
     * 显示工具栏
     *
     * @param show true 显示, false 不显示
     */
    protected void showToolbar(boolean show) {
        if (show) {
            mAppBarLayout.setVisibility(View.VISIBLE);
            return;
        }
        mAppBarLayout.setVisibility(View.GONE);
    }


    /**
     * 设置左侧图片
     *
     * @param resId
     */
    public void setLeftIv(@DrawableRes int resId) {
        mLeftIv.setImageResource(resId);
    }

    /**
     * 设置左侧图片显示或隐藏
     *
     * @param show
     */
    public void showLeftIv(boolean show) {
        if (show) {
            mLeftIv.setVisibility(View.VISIBLE);
        } else {
            mLeftIv.setVisibility(View.GONE);
        }
    }


    /**
     * 设置右侧图片
     *
     * @param resId
     */
    public void setRightIv(int resId) {
        mRightIv.setImageResource(resId);
    }

    /**
     * 设置右二图片
     *
     * @param resId
     */
    public void setRight2Iv(int resId) {
        mRight2Iv.setImageResource(resId);
    }

    /**
     * 设置右二图片
     *
     * @param resId
     * @param tag
     */
    public void setRight2Iv(int resId, String tag) {
        mRight2Iv.setImageResource(resId);
        mRight2Iv.setTag(tag);
    }

    /**
     * 设置右侧图片
     *
     * @param resId
     * @param tag
     */
    public void setRightIv(int resId, String tag) {
        mRightIv.setImageResource(resId);
        mRightIv.setTag(tag);
    }

    /**
     * 设置右侧图片显示或隐藏
     *
     * @param show
     */
    public void showRightIv(boolean show) {
        if (show) {
            mRightIv.setVisibility(View.VISIBLE);
        } else {
            mRightIv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右二图片显示或隐藏
     *
     * @param show
     */
    public void showRight2Iv(boolean show) {
        if (show) {
            mRight2Iv.setVisibility(View.VISIBLE);
        } else {
            mRight2Iv.setVisibility(View.GONE);
        }
    }

    public void setRightTv(String str) {
        mRightTv.setText(str);
    }

    public void showRightTv(boolean show) {
        if (show) {
            mRightTv.setVisibility(View.VISIBLE);
        } else {
            mRightTv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右标题颜色
     *
     * @param textColor
     */
    protected void setRightTvColor(@ColorInt int textColor) {
        mRightTv.setTextColor(textColor);
    }


    /**
     * 设置标题背景颜色
     *
     * @param textColor
     */
    public void setToolBarBackGroundColor(int textColor) {
        mToolbar.setBackgroundResource(textColor);
    }

    public void addGoTopListener(GoTopListener listener) {
        mGoTopListener = listener;
    }

    public void removeGoTopListener() {
        mGoTopListener = null;
    }

    // int totalDy=0;
   /*
    根据垂y轴滑动动百分比设置透明度
    */
    public void setToolBarColorWithAlpha(int percentage, int target) {
        // totalDy+=dy;

        currentToolbarAlpha = (float) percentage / target;

        // LogUtils.d("appBarLayout.getHeight:"+"dy:"+percentage+" setToolBarColorWithAlpha:  = " +currentToolbarAlpha );

        setToolBarColorWithAlpha(currentToolbarAlpha);
    }


    //初始化toolbar透明度,不透明
    public void initToolBarColorWithAlpha() {
        setToolBarColorWithAlpha(1);
    }

    //初始化toolbar透明度,透明
    public void initToolBarColorWithTransparent() {

        setToolbarAlpha(0, false);
        setImageAlpha(1);

    }


    //恢复toolbar透明度
    public void restoretToolBarColorWithAlpha() {
        setToolBarColorWithAlpha(currentToolbarAlpha);
    }
//    //恢复我的状态栏
//    public void restoretMineToolBarColorWithAlpha(){
//        setImageAlpha(mineToolbarAlpha);
//    }

    private void setToolBarColorWithAlpha(float alpha) {
        // totalDy+=dy;
        setToolbarAlpha(alpha, true);
        setImageAlpha(alpha);
    }

    public void setImageAlpha(float alpha) {
        if (mRightIv == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRightIv.setImageAlpha((int) (alpha * 255));
            mRight2Iv.setImageAlpha((int) (alpha * 255));
        } else {
            mRightIv.setAlpha(alpha * 255);
            mRight2Iv.setAlpha(alpha * 255);
        }
    }


//    public void setImageAlphaMine(float alpha) {
//        mineToolbarAlpha=alpha;
//        setImageAlpha(mineToolbarAlpha);
//    }


    /*设置toolba*/
    private void setToolbarAlpha(float alpha, boolean isShowShadow) {

        if (mToolbar == null) return;

        if (alpha == 0 && isShowShadow) {
            //显示阴影
            mToolbar.setBackgroundDrawable(createToolBartDrawable());
        } else {
            //不显示阴影
            mToolbar.setBackgroundColor(ColorUtils.getColorWithAlpha(alpha, getResources().getColor(R.color.white)));
        }
        mTitle.setTextColor(ColorUtils.getColorWithAlpha(alpha, getResources().getColor(R.color.textColorPrimary)));
        toolbarBottomLine.setBackgroundColor(ColorUtils.getColorWithAlpha(alpha, getResources().getColor(R.color.gray)));

    }


}
