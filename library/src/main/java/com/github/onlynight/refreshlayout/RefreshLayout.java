package com.github.onlynight.refreshlayout;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RefreshLayout extends FrameLayout {

    private LinearLayout mHeader;
    private View mContentView;

    private boolean mRefreshing = false;
    private boolean mRefreshingEnable = true;

    private boolean mStartAnimFlag = false;
    private boolean mEndAnimFlag = true;
    private float startY;
    private float startX;
    private float mMoveY;

    private float mHeaderViewHeight = 0;
    private float mFinalHeaderY = 0;
    private long mAnimTime = 500;

    private static final int STATE_START = 0;
    private static final int STATE_REFRESHING_DOWN = STATE_START + 1;
    private static final int STATE_REFRESHING_UP = STATE_START + 2;
    private static final int STATE_PULL = STATE_START + 3;

    private int mState = STATE_START;
    private ValueAnimator animator;

    public RefreshLayout(Context context) {
        super(context);
        initView(null);
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.RefreshLayout);
        initView(array);
        array.recycle();
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.RefreshLayout, defStyleAttr, 0);
        initView(array);
        array.recycle();
    }

    @TargetApi(21)
    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.RefreshLayout, defStyleAttr, defStyleRes);
        initView(array);
        array.recycle();
    }

    private void initView(TypedArray array) {
        addRefreshHeader();

        changeState(STATE_START);
    }

    private void addRefreshHeader() {
        mHeader = new LinearLayout(getContext());
        LayoutParams headerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeader.setLayoutParams(headerParams);
        mHeader.setGravity(Gravity.CENTER);
        mHeader.setBackgroundColor(Color.YELLOW);

        final TextView text = new TextView(getContext());
        text.setText("Title");
        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), text.getText(), Toast.LENGTH_SHORT).show();
                refreshingAnim();
            }
        });

        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        marginLayoutParams.bottomMargin = 50;
        marginLayoutParams.topMargin = 50;
        marginLayoutParams.leftMargin = 50;
        marginLayoutParams.rightMargin = 50;
        text.setLayoutParams(marginLayoutParams);

        mHeader.addView(text);

        addView(mHeader);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        initContentView();
    }

    public void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;
        changeState(refreshing ? STATE_REFRESHING_DOWN : STATE_START);
    }

    private void refreshingAnim() {

        if (mHeaderViewHeight <= 0) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mEndAnimFlag) {
                        mEndAnimFlag = false;
                        mHeaderViewHeight = mHeader.getHeight();
                        mMoveY = 0;
                        mFinalHeaderY = mHeaderViewHeight;
                        refreshingAnim();
                    }
                }
            });
        } else {
            animator = ValueAnimator.ofFloat(mMoveY, mFinalHeaderY);
            animator.setDuration(mAnimTime);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setViewY((Float) valueAnimator.getAnimatedValue());
                }
            });
            animator.start();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mRefreshingEnable && (isRefresh(ev) || super.dispatchTouchEvent(ev));
    }

    private boolean isRefresh(MotionEvent ev) {
//        if (mState == STATE_REFRESHING_UP || mState == STATE_REFRESHING_DOWN) {
//            return true;
//        }

        if (mContentView instanceof ScrollView) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = ev.getY();
                    startX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mMoveY = (ev.getY() - startY) / 3;
                    if (mMoveY > 0 && checkCanPull()) {
                        changeState(STATE_PULL);
                        return true;
                    } else {
                        return false;
                    }
                case MotionEvent.ACTION_UP:
                    if (mState == STATE_PULL) {
                        changeState(STATE_REFRESHING_UP);
                    }
                    startY = 0;
                    break;
            }
        }

        return false;
    }

    private void initContentView() {
        if (mContentView == null && getChildCount() > 1) {
            mContentView = getChildAt(1);
        }
    }

    private void changeState(int state) {
        this.mState = state;
        postViewY(mMoveY);
    }

    private void postViewY(float moveY) {
        mAnimTime = 200;
        switch (mState) {
            case STATE_START:
                mMoveY = mHeaderViewHeight;
                mFinalHeaderY = 0;
                refreshingAnim();
                break;
            case STATE_REFRESHING_UP:
                mMoveY += mHeaderViewHeight;
                mFinalHeaderY = mHeaderViewHeight;
                refreshingAnim();
                break;
            case STATE_REFRESHING_DOWN:
                if (mHeaderViewHeight > 0) {
                    mMoveY = 0;
                    mFinalHeaderY = mHeaderViewHeight;
                }
                mAnimTime = 500;
                refreshingAnim();
                break;
            case STATE_PULL:
                if (mFinalHeaderY > 0) {
                    moveY += mFinalHeaderY;
                }
                setViewY(moveY);
                break;
        }
    }

    private void setViewY(float moveY) {
        mHeader.setY(-mHeaderViewHeight + moveY);
        if (mContentView != null) {
            mContentView.setY(moveY);
        }
    }

    private boolean checkCanPull() {
        if (mState == STATE_REFRESHING_UP || mState == STATE_REFRESHING_DOWN) {
            return false;
        }

        if (mContentView instanceof ScrollView) {
            int posY = mContentView.getScrollY();
            return posY <= 0;
        }

        return true;
    }

    public void setRefreshingEnable(boolean refreshingEnable) {
        this.mRefreshingEnable = refreshingEnable;
    }
}
