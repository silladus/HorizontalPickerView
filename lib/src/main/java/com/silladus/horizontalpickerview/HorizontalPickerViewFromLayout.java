package com.silladus.horizontalpickerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by silladus on 2017/11/7/0007.
 * GitHub: https://github.com/silladus
 * Description:通过布局实现
 */

public class HorizontalPickerViewFromLayout extends RelativeLayout {
    /**
     * 高度，可设置
     */
    private int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
    /**
     * 可设置的数据数组
     */
    private String[] data = new String[]{
            "",
            "",
            "16",
            "17",
            "18",
            "19",
            "20",
            "",
            ""
    };
    private MyViewGroup myViewGroup;

    public void setHeight(int height) {
        this.height = height;
    }

    public void setData(String[] data) {
        this.data = new String[4 + data.length];
        for (int i = 0; i < this.data.length; i++) {
            if (i < 2 || i > this.data.length - 3) {
                this.data[i] = "";
            } else {
                this.data[i] = data[i - 2];
            }
        }
        //
        // 初始化文字，需在完成数据初始化之后
        //
        for (int i = 0; i < 5; i++) {
            myViewGroup.showText(i);
        }
    }

    public HorizontalPickerViewFromLayout(Context context) {
        this(context, null);
    }

    public HorizontalPickerViewFromLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalPickerViewFromLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MyView myView = new MyView(context);
        addView(myView);

        myViewGroup = new MyViewGroup(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(myViewGroup, lp);
    }

    class MyViewGroup extends LinearLayout implements GestureDetector.OnGestureListener {
        /**
         * 手势监听器
         */
        private GestureDetector mGestureDetector;
        /**
         * 左右滑动时数据的偏移
         */
        private int offset;
        /**
         * 初始选中的下标
         */
        private int showIndex = 5;
        /**
         * 滑行动画
         */
        private ValueAnimator mFlingAnimator;
        /**
         * 应该滑行的位移
         */
        private float mScrollX;
        /**
         * 单位距离
         */
        private float indexX;
        /**
         * 滑行方向
         */
        private int direction = 0;
        /**
         * 用于显示的5个TextView
         */
        private TextView[] textViews = new TextView[7];

        public void setShowIndex(int showIndex) {
            this.showIndex = showIndex;
        }

        public MyViewGroup(Context context) {
            this(context, null);
        }

        public MyViewGroup(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initEvent(context);
            initText(context);
        }

        private void initText(Context context) {
            LayoutParams lp = new LayoutParams(0, height, 1f);
            LayoutParams lp1 = new LayoutParams(0, height, 0.5f);
            int[] textSizeArr = new int[]{6, 10, 14, 18, 14, 10, 6};
            for (int i = 0; i < 7; i++) {
                TextView mTextView = new TextView(context);
                if (i == 3) {
                    mTextView.setTextColor(Color.parseColor("#F00A00"));
                } else {
                    mTextView.setTextColor(Color.parseColor("#5D5D5D"));
                }
                mTextView.setTextSize(textSizeArr[i]);
                mTextView.setGravity(Gravity.CENTER);
                if (i > 0 && i < 6) {
                    addView(mTextView, lp);
                } else {
                    addView(mTextView, lp1);
                }

                textViews[i] = mTextView;
            }
        }

        void showText(int index) {
            textViews[index + 1].setText(data[index + showIndex + offset]);
        }

        /**
         * 手势监听
         */
        private void initEvent(Context context) {
            setClickable(true);
            mGestureDetector = new GestureDetector(context, this);
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    performClick();
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            indexX = w / 6f;
        }

        @Override
        public void scrollTo(int x, int y) {
            super.scrollTo(x, y);
            for (int i = 0; i < 5; i++) {
                if (i == 2) {
                    if (mSelectListener != null) {
                        mSelectListener.currentItem(data[i + showIndex + offset]);
                    }
                }
                showText(i);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // 第一个
            if (e.getX() < 1.5 * indexX) {
                if (offset > -showIndex + 1) {
                    offset -= 2;
                    direction = -1;
                    doFlingAnimator(2 * indexX);
                }
            }
            // 第二个
            if (e.getX() > 1.5 * indexX && e.getX() < indexX * 2.5) {
                if (offset > -showIndex) {
                    offset--;
                    direction = -1;
                    doFlingAnimator(indexX);
                }
            }
            // 第四个
            if (e.getX() > indexX * 3.5 && e.getX() < indexX * 4.5) {
                if (offset < data.length - 5 - showIndex) {
                    offset++;
                    direction = 1;
                    doFlingAnimator(indexX);
                }
            }
            // 第五个
            if (e.getX() > indexX * 4.5) {
                if (offset < data.length - 6 - showIndex) {
                    offset += 2;
                    direction = 1;
                    doFlingAnimator(2 * indexX);
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() < e2.getX()) {// 向左滑
                if (offset > -showIndex) {
                    offset--;
                    direction = -1;
                } else {
                    direction = 1;
                }
            }
            if (e1.getX() > e2.getX()) {// 向右滑
                if (offset < data.length - 5 - showIndex) {
                    offset++;
                    direction = 1;
                } else {
                    direction = -1;
                }
            }
            doFlingAnimator(indexX);
            return false;
        }

        /**
         * 滑行动画
         */
        private void doFlingAnimator(float scrollX) {
            mFlingAnimator = ValueAnimator.ofFloat(scrollX, 0);
            mFlingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mScrollX = (float) animation.getAnimatedValue();
                    scrollTo((int) (-mScrollX * direction), 0);
                }
            });

            mFlingAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
//                    mVelocity = mMinStartUpSpeed - 1;
//                    mSliding = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
//                    mSliding = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                }
            });

            mFlingAnimator.setDuration(1020);
            mFlingAnimator.setInterpolator(new DecelerateInterpolator());
            mFlingAnimator.start();
        }
    }

    private class MyView extends View {
        /**
         * 图形画笔
         */
        private Paint mPaint;

        /**
         * 图形画笔
         */
        private void initPaint() {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
            mPaint.setColor(Color.RED);
        }

        public MyView(Context context) {
            this(context, null);
        }

        public MyView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initPaint();
        }

        float xx;

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            xx = w / 6f;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < 5; i++) {
                if (i == 2) {
                    float centerY = height / 2f;
                    canvas.drawCircle(xx * (1 + i), centerY, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), mPaint);
                }
            }
        }
    }

    public interface SelectListener {
        void currentItem(String currentObject);
    }

    private SelectListener mSelectListener;

    public void setSelectListener(SelectListener mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

}
