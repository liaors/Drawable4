package comt.example.administrator.drawable4;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SlideMenu extends FrameLayout {
    private View menuView, mainView;
    private int menuWidth = 0;
    private Scroller scroller;
    public boolean isOpen;

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext());
    }

    /**
     * 当1级的子view全部加载完调用，可以用初始化子view的引用
     * 注意，这里无法获取子view的宽高
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menuView = getChildAt(0);
        mainView = getChildAt(1);
        menuWidth = menuView.getLayoutParams().width;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (ev.getX() - downX);

                if (Math.abs(deltaX) > 8) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * l: 当前子view的左边在父view的坐标系中的x坐标
     * t: 当前子view的顶边在父view的坐标系中的y坐标
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        menuView.layout(r, 0, r + menuWidth, menuView.getMeasuredHeight());
        mainView.layout(0, 0, r, b);
    }

    private int downX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int deltaX = moveX - downX;

                int newScrollX = getScrollX() - deltaX;

                if (newScrollX > menuWidth) newScrollX = menuWidth;
                if (newScrollX < 0) newScrollX = 0;

                scrollTo(newScrollX, 0);
                downX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                if (isOpen &&getScrollX() < menuWidth * 4 / 5) {
//				//关闭菜单
                    closeMenu();
                }else if(getScrollX() > menuWidth / 5){
                    //打开菜单
                    openMenu();
                }else {
                    closeMenu();
                }

                break;
        }
        return true;
    }

    private void closeMenu() {
        isOpen = false;
        scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 400);
        invalidate();
    }

    public void openMenu() {
        isOpen = true;
        scroller.startScroll(getScrollX(), 0, menuWidth - getScrollX(), 0, 400);
        invalidate();
    }

    /**
     * Scroller不主动去调用这个方法
     * invalidate->draw->computeScroll
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {//返回true,表示动画没结束
            scrollTo(scroller.getCurrX(), 0);
            invalidate();
        }
    }

    /**
     * 切换菜单的开和关
     */
    public void switchMenu() {
        if (getScrollX() == 0) {
            //需要打开
            openMenu();
        } else {
            //需要关闭
            closeMenu();
        }
    }

    public void clickClose() {
        if (getScrollX() != 0) {
            //需要打开
            closeMenu();
        }
    }


}
