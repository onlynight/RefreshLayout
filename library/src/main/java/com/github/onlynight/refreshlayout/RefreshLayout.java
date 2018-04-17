package com.github.onlynight.refreshlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class RefreshLayout extends FrameLayout {

    private static final int STATE_START = 0;
    private static final int STATE_REFRESHING_DOWN = STATE_START + 1;
    private static final int STATE_REFRESHING_UP = STATE_START + 2;
    private static final int STATE_PULL = STATE_START + 3;
    private static final int STATE_CANCEL = STATE_START + 4;

    private RefreshHeaderView mRefreshHeaderView;
    private View mContentView;

    private boolean mRefreshingEnable = true;
    private boolean mOnLayoutFinish = true;

    private float mLastY;
    private float mLastYIntercept;
    private float mMoveY = 0;

    private float mHeaderViewHeight = 0;
    private float mFinalHeaderY = 0;
    private long mAnimTime = 500;

    private int mState = STATE_START;
    private ValueAnimator mAnimator;

    private boolean mPostFinish = false;

    private OnRefreshListener mOnRefreshListener;

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
        changeState(STATE_START);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        initContentView();
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            changeState(STATE_REFRESHING_DOWN);
        } else {
            if (mAnimator != null) {
                if (mAnimator.isRunning()) {
                    mAnimator.cancel();
                    changeState(STATE_CANCEL);
                } else {
                    if (mState != STATE_START && mState != STATE_CANCEL) {
                        changeState(STATE_START);
                    }
                }
            } else {
                if (STATE_START != mState) {
                    mPostFinish = true;
                }
            }
        }
    }

    private void refreshingAnim() {
        if (mHeaderViewHeight <= 0) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mOnLayoutFinish && mRefreshHeaderView != null) {
                        mOnLayoutFinish = false;
                        mHeaderViewHeight = mRefreshHeaderView.getHeight();
                        mMoveY = 0;
                        mFinalHeaderY = mState == STATE_REFRESHING_DOWN ? mHeaderViewHeight : 0;
                        refreshingAnim();
                    }
                }
            });
        } else {
            mAnimator = ValueAnimator.ofFloat(mMoveY, mFinalHeaderY);
            mAnimator.setDuration(mAnimTime);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mMoveY = (Float) animation.getAnimatedValue();
                    setViewY(mMoveY);
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mPostFinish) {
                        mPostFinish = false;
                        changeState(STATE_START);
                    } else {
                        if (mState == STATE_CANCEL) {
                            mState = STATE_START;
                        }
                        if (mRefreshHeaderView != null && mState == STATE_START) {
                            mRefreshHeaderView.onBackToOriginalState();
                        }

                        if (mState == STATE_REFRESHING_UP) {
                            if (mOnRefreshListener != null) {
                                mOnRefreshListener.onRefreshing(RefreshLayout.this);
                            }
                        }
                    }
                }

            });
            mAnimator.start();
        }
    }

    private boolean isRefresh(MotionEvent ev) {
        float startY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = (startY - mLastY) / 2;
                mMoveY = moveY;
                if (moveY > 0 && checkCanPull()) {
                    changeState(STATE_PULL);
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                if (mState == STATE_PULL) {
                    if (mMoveY >= mHeaderViewHeight) {
                        changeState(STATE_REFRESHING_UP);
                    } else {
                        changeState(STATE_CANCEL);
                    }
                }
                break;
        }
        mLastY = startY;
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mRefreshingEnable) {
            isRefresh(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float startY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = (startY - mLastYIntercept) / 3;
                boolean intercept = moveY > 0 && checkCanPull();
                if (intercept && mRefreshHeaderView != null) {
                    mRefreshHeaderView.onStartRefresh();
                }
                return intercept;
            case MotionEvent.ACTION_UP:
                mLastYIntercept = 0;
                break;
        }

        mLastYIntercept = startY;

        return super.onInterceptTouchEvent(ev);
    }

    private void initContentView() {
        if (mContentView == null && getChildCount() > 0) {
            mContentView = getChildAt(0);
        }
    }

    private void changeState(int state) {
        this.mState = state;
        postViewY(mMoveY);
    }

    private void postViewY(float moveY) {
        mAnimTime = 300;
        switch (mState) {
            case STATE_START:
                if (mRefreshHeaderView != null) {
                    mRefreshHeaderView.onFinishRefresh();
                }
                mMoveY = mHeaderViewHeight;
                mFinalHeaderY = 0;
                refreshingAnim();
                break;
            case STATE_CANCEL:
                if (mRefreshHeaderView != null) {
                    mRefreshHeaderView.onCancel();
                }
                mFinalHeaderY = 0;
                refreshingAnim();
                break;
            case STATE_REFRESHING_UP:
                if (mRefreshHeaderView != null) {
                    mRefreshHeaderView.onRefreshing();
                }
                mFinalHeaderY = mHeaderViewHeight;
                refreshingAnim();
                break;
            case STATE_REFRESHING_DOWN:
                if (mRefreshHeaderView != null) {
                    mRefreshHeaderView.onRefreshing();
                }
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

                if (mRefreshHeaderView != null) {
                    if (moveY > mHeaderViewHeight) {
                        mRefreshHeaderView.onReleaseToRefresh();
                    } else {
                        mRefreshHeaderView.onStartRefresh();
                    }
                }

                setViewY(moveY);
                break;
        }
    }

    private void setViewY(float moveY) {
        if (mRefreshHeaderView != null) {
            mRefreshHeaderView.setY(-mHeaderViewHeight + moveY);
        }
        if (mContentView != null) {
            mContentView.setY(moveY);
        }
    }

    private boolean checkCanPull() {
        return mState != STATE_REFRESHING_UP &&
                mState != STATE_REFRESHING_DOWN &&
                checkSupportViewCanPull();
    }

    protected boolean checkSupportViewCanPull() {

        if (mContentView instanceof ScrollView) {
            int posY = mContentView.getScrollY();
            return posY <= 0;
        }

        return true;

    }

    public void setHeaderView(RefreshHeaderView headerView) {
        this.mRefreshHeaderView = headerView;
        if (mRefreshHeaderView != null) {
            mRefreshHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.addView(mRefreshHeaderView);
            mRefreshHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mOnLayoutFinish) {
                        mOnLayoutFinish = false;
                        mHeaderViewHeight = mRefreshHeaderView.getHeight();
                        mFinalHeaderY = mState == STATE_REFRESHING_DOWN ? mHeaderViewHeight : 0;
                        mMoveY = 0;
                    }
                }
            });
        }
    }

    public void setRefreshingEnable(boolean refreshingEnable) {
        this.mRefreshingEnable = refreshingEnable;
    }

    public interface IRefreshHeaderView {

        void onStartRefresh();

        void onReleaseToRefresh();

        void onRefreshing();

        void onFinishRefresh();

        void onBackToOriginalState();

        void onCancel();

    }

    public abstract static class RefreshHeaderView extends FrameLayout implements IRefreshHeaderView {

        public RefreshHeaderView(@NonNull Context context) {
            super(context);
        }

        public RefreshHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public RefreshHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(21)
        public RefreshHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

    }

    public interface OnRefreshListener {

        void onRefreshing(RefreshLayout refreshLayout);

    }

}
