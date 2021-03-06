package io.github.bakumon.luckpan.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import io.github.bakumon.luckpan.entity.PrizeVo;
import io.github.bakumon.luckpan.utils.DisplayUtils;

/**
 * 抽奖转盘
 * Created by bakumon on 16-11-12.
 */

public class LuckPan extends View {

    public int minCircleNum = 9; // 圈数
    public int maxCircleNum = 15;

    public long minOneCircleMillis = 400; // 平均一圈用时
    public long maxOneCircleMillis = 600;

    private List<PrizeVo> mPrizeVoList;
    private RectF mRectF;

    private Paint dPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Context mContext;

    public LuckPan(Context context) {
        this(context, null);

    }

    public LuckPan(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckPan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        dPaint.setColor(Color.rgb(82, 182, 197));
        sPaint.setColor(Color.rgb(186, 226, 232));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(DisplayUtils.dp2px(16, context));
        mRectF = new RectF();
        mPrizeVoList = new ArrayList<>();
        PrizeVo prizeVo = new PrizeVo();
        prizeVo.id = "";
        prizeVo.rate = "";
        prizeVo.title = "";
        for (int i = 0; i < 16; i++) {
            mPrizeVoList.add(prizeVo);
        }
    }

    /**
     * 设置奖项实体集合
     *
     * @param prizeVoList 奖项实体集合
     */
    public void setPrizeVoList(List<PrizeVo> prizeVoList) {
        mPrizeVoList = prizeVoList;
        invalidate();
    }

    /**
     * 设置转盘交替的深色
     *
     * @param darkColor 深色 默认：Color.rgb(82, 182, 197)
     */
    public void setDarkColor(int darkColor) {
        dPaint.setColor(darkColor);
    }

    /**
     * 设置转盘交替的浅色
     *
     * @param shallowColor 浅色 默认：Color.rgb(186, 226, 232)
     */
    public void setShallowColor(int shallowColor) {
        sPaint.setColor(shallowColor);
    }

    /**
     * 设置转动圈数的范围
     *
     * @param minCircleNum 最小转动圈数
     * @param maxCircleNum 最大转动圈数
     */
    public void setCircleNumRange(int minCircleNum, int maxCircleNum) {
        if (minCircleNum > maxCircleNum) {
            return;
        }
        this.minCircleNum = minCircleNum;
        this.maxCircleNum = maxCircleNum;
    }

    /**
     * 设置平均转动一圈用时
     *
     * @param minOneCircleMillis 最小转动圈数
     * @param maxOneCircleMillis 最大转动圈数
     */
    public void setOneCircleMillisRange(long minOneCircleMillis, long maxOneCircleMillis) {
        if (minOneCircleMillis > maxOneCircleMillis)
            this.minOneCircleMillis = minOneCircleMillis;
        this.maxOneCircleMillis = maxOneCircleMillis;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //wrap_content value
        int mHeight = DisplayUtils.dp2px(300, mContext);
        int mWidth = DisplayUtils.dp2px(300, mContext);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int MinValue = Math.min(width, height);

        int radius = MinValue / 2;
        mRectF.set(getPaddingLeft(), getPaddingTop(), MinValue, MinValue);
        float sweepAngle = (float) (360.0 / mPrizeVoList.size());
        float angle = -90 - sweepAngle / 2;

        for (int i = 0; i < mPrizeVoList.size(); i++) {
            if (i % 2 == 0) {
                canvas.drawArc(mRectF, angle, sweepAngle, true, dPaint);
            } else {
                canvas.drawArc(mRectF, angle, sweepAngle, true, sPaint);
            }
            angle += sweepAngle;
        }
        angle = -90 - sweepAngle / 2;

        for (PrizeVo prizeVo : mPrizeVoList) {
            Path mPath = new Path();
            mPath.addArc(mRectF, angle, sweepAngle);
            float textWidth = textPaint.measureText(prizeVo.title);
            float hOffset = (float) (2 * radius * Math.PI / mPrizeVoList.size() / 2 - textWidth / 2);
            float vOffset = 2 * radius / 2 / 4;
            canvas.drawTextOnPath(prizeVo.title, mPath, hOffset, vOffset, textPaint);
            angle += sweepAngle;
        }
    }

    private float startAngle; // 每次开始转的起始角度

    private OnLuckPanAnimatorEndListener mOnLuckPanAnimatorEndListener;

    public void setOnLuckPanAnimatorEndListener(OnLuckPanAnimatorEndListener listener) {
        this.mOnLuckPanAnimatorEndListener = listener;
    }

    public interface OnLuckPanAnimatorEndListener {
        void onLuckPanAnimatorEnd(PrizeVo choicePrizeVo);
    }

    /**
     * 开始转动 抽奖
     *
     * @param id 要停到对应奖项的 PrizeVo 实体的 id
     */
    public void start(final int id) {
        int choiceIndex1 = 0;
        // 获取选择的id对应 在转盘中的位置 从0开始
        for (int i = 0; i < mPrizeVoList.size(); i++) {
            if (id == Integer.parseInt(mPrizeVoList.get(i).id)) {
                choiceIndex1 = i;
            }
        }
        final int choiceIndex = choiceIndex1;
        // minOneCircleMillis < 平均一圈用时 < maxOneCircleMillis
        long oneCircleTime = (long) (minOneCircleMillis + Math.random() * (maxOneCircleMillis - minOneCircleMillis + 1));
        // minCircleNum < 圈数 < maxCircleNum
        int ringNumber = (int) (minCircleNum + Math.random() * (maxCircleNum - minCircleNum + 1));

        float allAngle = 360 * ringNumber + (360f / mPrizeVoList.size()) * -choiceIndex;

        ObjectAnimator animator = ObjectAnimator.ofFloat(LuckPan.this, "rotation", -startAngle, allAngle);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(oneCircleTime * ringNumber);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startAngle = (360f / mPrizeVoList.size()) * choiceIndex;
                if (mOnLuckPanAnimatorEndListener != null) {
                    mOnLuckPanAnimatorEndListener.onLuckPanAnimatorEnd(mPrizeVoList.get(choiceIndex));
                }

            }
        });
        animator.start();

    }
}
