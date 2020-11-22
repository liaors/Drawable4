package com.rs.pressanimator;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 双层抽屉
 * Created by Administrator on 2016/11/22 0022.
 */

public class DoubleDrawerLayout extends ViewGroup {
    private static final int MIN_FLING_VELOCITY = 400;

    private View contentView;
    private View firstMenuView;
    private View secondMenuView;

    private ViewDragHelper viewDragHelper;

    private FirstMenuStateCallBack firstMenuStateCallBack;
    private SecondMenuStateCallBack secondMenuStateCallBack;
    private boolean firstMenuLock;
    private boolean secondMenuLock;

    /**
     * drawer显示出来的占自身的百分比
     */
    private float firstMenuOnScreen = 1.0f;
    private float secondMenuOnScreen = 1.0f;

    public DoubleDrawerLayout(Context context) {
        this(context, null);
    }

    public DoubleDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            public boolean tryCaptureView(View child, int pointerId) {
                log("tryCaptureView");
                return (child == firstMenuView && !firstMenuLock) || (child == secondMenuView && !secondMenuLock);
            }

            public int clampViewPositionHorizontal(View child, int left, int dx) {
                log("clampViewPositionHorizontal");
                int leftBound = getWidth() - child.getWidth();
                int rightBound = getWidth();

                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                log("onViewReleased");
                final int childWidth = releasedChild.getWidth();
                float offset = (childWidth - releasedChild.getLeft()) * 1.0f / childWidth;
                final int width = getWidth();
                viewDragHelper.settleCapturedViewAt(xvel < 0 || xvel == 0 && offset > 0.5f ? width - childWidth : width, releasedChild.getTop());
                invalidate();
            }

            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                final int childWidth = changedView.getWidth();
                float offset = (float) (getWidth() - left) / childWidth;
                if (changedView == firstMenuView) {
                    firstMenuOnScreen = offset;
                    if (firstMenuStateCallBack != null) {
                        if (firstMenuOnScreen == 0.0f) {
                            firstMenuStateCallBack.firstMenuStateCallBack(false);
                            firstMenuOnScreen = 1.0f;
                        } else if (firstMenuOnScreen == 1.0f) {
                            firstMenuStateCallBack.firstMenuStateCallBack(true);
                            firstMenuOnScreen = 0.0f;
                        }
                    }
                } else if (changedView == secondMenuView) {
                    secondMenuOnScreen = offset;
                    if (secondMenuStateCallBack != null) {
                        if (secondMenuOnScreen == 0.0f) {
                            secondMenuStateCallBack.secondMenuStateCallBack(false);
                            secondMenuOnScreen = 1.0f;
                        } else if (secondMenuOnScreen == 1.0f) {
                            secondMenuStateCallBack.secondMenuStateCallBack(true);
                            secondMenuOnScreen = 0.0f;
                        }
                    }
                }
                changedView.setVisibility(offset == 0 ? View.INVISIBLE : View.VISIBLE);
                invalidate();
            }

            public int getViewHorizontalDragRange(View child) {
                log("getViewHorizontalDragRange");
                int result = 0;
                if (child == firstMenuView || child == secondMenuView) {
                    result = child.getWidth();
                }

                return result;
            }
        });
        viewDragHelper.setMinVelocity(minVel);
    }

    public void openFirstDrawer() {
        if (firstMenuView == null) {
            return;
        }
        View menuView = firstMenuView;
        //  firstMenuOnScreen = 1.0f;
        firstMenuOnScreen = 0f;
        boolean flag = viewDragHelper.smoothSlideViewTo(menuView, getWidth() - menuView.getWidth(), menuView.getTop());
        Log.d("mo", "flag : " + flag);
        postInvalidate();
    }

    public void closeFirstDrawer() {
        if (firstMenuView == null) {
            return;
        }
        View menuView = firstMenuView;
        //      firstMenuOnScreen = 0f;
        firstMenuOnScreen = 1f;
        viewDragHelper.smoothSlideViewTo(menuView, getWidth(), menuView.getTop());
        postInvalidate();
    }

    public void openSecondDrawer() {
        if (secondMenuView == null) {
            return;
        }
        View menuView = secondMenuView;
        //    secondMenuOnScreen = 1.0f;
        secondMenuOnScreen = 0f;
        viewDragHelper.smoothSlideViewTo(menuView, getWidth() - menuView.getWidth(), menuView.getTop());
        postInvalidate();
        if (firstMenuStateCallBack != null) {
            firstMenuStateCallBack.firstMenuStateCallBack(false);
        }
    }

    public void closeSecondDrawer() {
        if (secondMenuView == null) {
            return;
        }
        View menuView = secondMenuView;
        //  secondMenuOnScreen = 0f;
        secondMenuOnScreen = 1f;
        viewDragHelper.smoothSlideViewTo(menuView, getWidth(), menuView.getTop());
        postInvalidate();
        if (secondMenuStateCallBack != null) {
            secondMenuStateCallBack.secondMenuStateCallBack(false);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log("onMeasure");
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);

        if (getChildCount() >= 2 && getChildAt(1) != null) {
            View firstView = getChildAt(1);
            MarginLayoutParams lp = (MarginLayoutParams) firstView.getLayoutParams();
            int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height);
            firstView.measure(drawerWidthSpec, drawerHeightSpec);
            this.firstMenuView = firstView;
        }

        if (getChildCount() >= 3 && getChildAt(2) != null) {
            View secondView = getChildAt(2);
            MarginLayoutParams lp = (MarginLayoutParams) secondView.getLayoutParams();
            int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height);
            secondView.measure(drawerWidthSpec, drawerHeightSpec);
            this.secondMenuView = secondView;
        }

        View mainView = getChildAt(0);
        MarginLayoutParams lp = (MarginLayoutParams) mainView.getLayoutParams();
        final int contentWidthSpec = MeasureSpec.makeMeasureSpec(widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
        final int contentHeightSpec = MeasureSpec.makeMeasureSpec(heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
        mainView.measure(contentWidthSpec, contentHeightSpec);
        this.contentView = mainView;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        log("onLayout firstMenuOnScreen : " + firstMenuOnScreen + "  secondMenuOnScreen : " + secondMenuOnScreen);
        View firstView = firstMenuView;
        View secondView = secondMenuView;
        View mainView = contentView;

        MarginLayoutParams lp = (MarginLayoutParams) mainView.getLayoutParams();
        mainView.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + mainView.getMeasuredWidth(), lp.topMargin + mainView.getMeasuredHeight());

        int width = getWidth();
        if (firstView != null) {
            lp = (MarginLayoutParams) firstView.getLayoutParams();
            int menuWidth = firstView.getMeasuredWidth();
            int childLeft = width - menuWidth + (int) (menuWidth * firstMenuOnScreen);
            firstView.layout(childLeft, lp.topMargin, childLeft + menuWidth, lp.topMargin + firstView.getMeasuredHeight());
        }

        if (secondView != null) {
            lp = (MarginLayoutParams) secondView.getLayoutParams();
            int menuWidth = secondView.getMeasuredWidth();
            int childLeft = width - menuWidth + (int) (menuWidth * secondMenuOnScreen);
            secondView.layout(childLeft, lp.topMargin, childLeft + menuWidth, lp.topMargin + secondView.getMeasuredHeight());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldInterceptTouchEvent = viewDragHelper.shouldInterceptTouchEvent(ev);
        return shouldInterceptTouchEvent;
    }

    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public void setFirstMenuStateCallBack(FirstMenuStateCallBack firstMenuStateCallBack) {
        this.firstMenuStateCallBack = firstMenuStateCallBack;
    }

    public void setSecondMenuStateCallBack(SecondMenuStateCallBack secondMenuStateCallBack) {
        this.secondMenuStateCallBack = secondMenuStateCallBack;
    }

    public void setFirstMenuLock(boolean firstMenuLock) {
        this.firstMenuLock = firstMenuLock;
    }

    public void setSecondMenuLock(boolean secondMenuLock) {
        this.secondMenuLock = secondMenuLock;
    }

    public interface FirstMenuStateCallBack {
        void firstMenuStateCallBack(boolean isOpen);
    }

    public interface SecondMenuStateCallBack {
        void secondMenuStateCallBack(boolean isOpen);
    }

    private void log(String msg) {
    }

}
