package com.silladus.horizontalpickerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by silladus on 2017/11/7/0007.
 * GitHub: https://github.com/silladus
 * Description:通过绘制实现
 */

public class HorizontalPickerViewFromDraw extends RelativeLayout {
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
    }

    public HorizontalPickerViewFromDraw(Context context) {
        this(context, null);
    }

    public HorizontalPickerViewFromDraw(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalPickerViewFromDraw(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MyView myView = new MyView(context);
        addView(myView);

        TextListView mTextListView = new TextListView(context);
        addView(mTextListView);
    }

    class TextListView extends View implements GestureDetector.OnGestureListener {
        /**
         * 文字画笔
         */
        private TextPaint mTextPaint;
        /**
         * 手势监听器
         */
        private GestureDetector mGestureDetector;
        /**
         * 文字显示区域
         */
        private Rect lineBound;
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
         * 字体尺寸
         */
        private int[] sizes = new int[5];
        /**
         * 文字绘制位置
         */
        private float[] centerXs = new float[5];
        private float[] centerYs = new float[5];

        public void setShowIndex(int showIndex) {
            this.showIndex = showIndex;
        }

        public TextListView(Context context) {
            this(context, null);
        }

        public TextListView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public TextListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initTextPaint();
            initEvent(context);
        }

        /**
         * 文字画笔
         */
        private void initTextPaint() {
            mTextPaint = new TextPaint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(Color.parseColor("#5D5D5D"));
            mTextPaint.setTextSize(24);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
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
            float centerY = height / 2f;
            for (int i = 0; i < 5; i++) {
                if (i == 0 || i == 4) {
                    sizes[i] = 10;
                }
                if (i == 1 || i == 3) {
                    sizes[i] = 14;
                }
                if (i == 2) {
                    sizes[i] = 18;
                }
                mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizes[i], getResources().getDisplayMetrics()));
                centerYs[i] = centerY + calculateFontHeight() / 2f;
                centerXs[i] = indexX * (1 + i);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < 5; i++) {
                if (i == 2) {
                    mTextPaint.setColor(Color.parseColor("#F00A00"));
                    if (mSelectListener != null) {
                        mSelectListener.currentItem(data[i + showIndex + offset]);
                    }
                } else {
                    mTextPaint.setColor(Color.parseColor("#5D5D5D"));
                }
                mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizes[i], getResources().getDisplayMetrics()));
                canvas.drawText(data[i + showIndex + offset], centerXs[i], centerYs[i], mTextPaint);
            }
        }

        /**
         * 测量字体尺寸
         */
        private int calculateFontHeight() {
            if (lineBound == null) {
                lineBound = new Rect();
            }
            mTextPaint.getTextBounds(data[2], 0, data[2].length(), lineBound);
            return lineBound.height();
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
            return true;
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
                    for (int i = 0; i < 5; i++) {
                        centerXs[i] = indexX * (1 + i) + mScrollX *direction;
                    }
                    invalidate();
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
        private float indexX;

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

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            indexX = w / 6f;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < 5; i++) {
                if (i == 2) {
                    float centerY = height / 2f;
                    canvas.drawCircle(indexX * (1 + i), centerY, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()), mPaint);
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
