package cn.dcx.privatebox.ui.lock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dcx.privatebox.MainActivity;
import cn.dcx.privatebox.adapter.PBoxListAdapter;
import cn.dcx.privatebox.bean.PBox;
import cn.dcx.privatebox.utils.SharedPreferencesUtil;

/**
 * Created by DCX on 2018/12/6.
 */

public class NineGridLockView extends View {
    private int width;//该控件的宽
    private int height;//该控件的高
    private Paint mpaintbigcircle;//用于画外圆
    private Paint mpaintsmallcircle;//用于画内圆
    private Paint mpaintline;//用于画线
    private Paint mpainttext;//用于画文本
    private Path path;//手势划线时需要用到它
    private Map<Integer, float[]> pointcontainer;//存储九个点的坐标
    private List<Integer> pointerslipped;//存储得到的九宫格密码
    public List<Integer> getpointerslipped() {
        return pointerslipped;
    }
    private List<String> pwds = new ArrayList<>();
    private PBoxListAdapter.ClickCallback mCallback;
    public void setpointerslipped(List<Integer> pointerslipped) {
        this.pointerslipped = pointerslipped;
    }
    public NineGridLockView(Context context) {
        super(context);
    }
    public NineGridLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mpaintbigcircle = new Paint();
        mpaintbigcircle.setColor(Color.BLUE);
        mpaintbigcircle.setStyle(Paint.Style.STROKE);//不充满
        mpaintbigcircle.setAntiAlias(true);//抗锯齿打开
        mpaintsmallcircle = new Paint();
        mpaintsmallcircle.setColor(Color.GREEN);
        mpaintsmallcircle.setStyle(Paint.Style.FILL);//充满，即画的几何体为实心
        mpaintsmallcircle.setAntiAlias(true);
        mpaintline = new Paint();
        mpaintline.setColor(Color.GREEN);
        mpaintline.setStyle(Paint.Style.STROKE);
        mpaintline.setStrokeWidth(20);
        mpaintline.setAntiAlias(true);
        mpainttext = new Paint();
        mpainttext.setColor(Color.WHITE);
        mpainttext.setTextAlign(Paint.Align.CENTER);//向中央对齐
        mpainttext.setTextSize(50);
        mpainttext.setAntiAlias(true);
        path = new Path();
        pointcontainer = new HashMap<>();
        pointerslipped = new ArrayList<>();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    }
    private float pivotx;//触屏得到的x坐标
    private float pivoty;//触屏得到的y坐标
    private float selectedx;//当前选中的圆点的x坐标
    private float selectedy;//当前选中的圆点的y坐标
    private float selectedxold;//从前选中的圆点的x坐标
    private float selectedyold;//从前选中的圆点的y坐标
    private boolean ishasmoved = false;//用于判断path是否调用过moveto()方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pivotx = event.getX();
                pivoty = event.getY();
                //每次触屏时需要清空一下pointerslipped，即重置密码
                pointerslipped.clear();
                Log.d("pointtouched", pivotx + "," + pivoty);
                getselectedpointindex(pivotx, pivoty);
                invalidate();//重绘
                break;
            case MotionEvent.ACTION_MOVE:
                pivotx = event.getX();
                pivoty = event.getY();
                getselectedpointindex(pivotx, pivoty);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                /**
                 * 当手指离开屏幕时，重置path
                 */
                path.reset();
                ishasmoved = false;
                String indexSequence = "";
                //打印出上一次手势密码的值
                for(int index:pointerslipped){
                    indexSequence += "/"+index;
                }
                if(pointerslipped.size()>1)
                if(pwds.size()>0){
                    if(pwds.get(0).equals(indexSequence)){
                        pwds.add(indexSequence);
                        Toast.makeText(this.getContext(), "图案密码已设置！", Toast.LENGTH_LONG).show();
                        SharedPreferencesUtil.putData("NineGridLockPwd",indexSequence);
                    }else{
                        pwds.clear();
                        Toast.makeText(this.getContext(), "两次图案不一致！", Toast.LENGTH_LONG).show();
                    }

                }else{
                    pwds.add(indexSequence);
                }
                Log.d("index",indexSequence);
                break;
        }
        invalidate();
        return true;
    }
    /**
     * 得到并存储经过的圆点的序号
     * @param pivotx
     * @param pivoty
     */
    private void getselectedpointindex(float pivotx, float pivoty) {
        int index = 0;
        if (pivotx > patternmargin && pivotx < patternmargin + bigcircleradius * 2) {
            if (pivoty > height / 2 && pivoty < height / 2 + bigcircleradius * 2) {
                selectedx = pointcontainer.get(1)[0];
                selectedy = pointcontainer.get(1)[1];
                index = 1;
                Log.d("selectedpoint", selectedx + "," + selectedy);
            } else if (pivoty > height / 2 + added && pivoty < height / 2 + added + bigcircleradius * 2) {
                selectedx = pointcontainer.get(4)[0];
                selectedy = pointcontainer.get(4)[1];
                index = 4;
            } else if (pivoty > height / 2 + added * 2 && pivoty < height / 2 + added * 2 + bigcircleradius * 2) {
                selectedx = pointcontainer.get(7)[0];
                selectedy = pointcontainer.get(7)[1];
                index = 7;
            }
        } else if (pivotx > patternmargin + added && pivotx < patternmargin + added + bigcircleradius * 2) {
            if (pivoty > height / 2 && pivoty < height / 2 + bigcircleradius * 2) {
                selectedx = pointcontainer.get(2)[0];
                selectedy = pointcontainer.get(2)[1];
                index = 2;
            } else if (pivoty > height / 2 + added && pivoty < height / 2 + added + bigcircleradius * 2) {
                selectedx = pointcontainer.get(5)[0];
                selectedy = pointcontainer.get(5)[1];
                index = 5;
            } else if (pivoty > height / 2 + added * 2 && pivoty <height / 2 + added * 2 + bigcircleradius * 2) {
                selectedx = pointcontainer.get(8)[0];
                selectedy = pointcontainer.get(8)[1];
                index = 8;
            }
        } else if (pivotx > patternmargin + added * 2 && pivotx < patternmargin + added * 2 + bigcircleradius * 2) {
            if (pivoty > height / 2 && pivoty < height / 2 + bigcircleradius * 2) {
                selectedx = pointcontainer.get(3)[0];
                selectedy = pointcontainer.get(3)[1];
                index = 3;
            } else if (pivoty > height / 2 + added && pivoty < height / 2 + added + bigcircleradius * 2) {
                selectedx = pointcontainer.get(6)[0];
                selectedy = pointcontainer.get(6)[1];
                index = 6;
            } else if (pivoty > height / 2 + added * 2 && pivoty < height / 2 + added * 2 + bigcircleradius * 2) {
                selectedx = pointcontainer.get(9)[0];
                selectedy = pointcontainer.get(9)[1];
                index = 9;
            }
        }
        if (selectedx!=selectedxold||selectedy!=selectedyold){
            //当这次的坐标与上次的坐标不同时存储这次点序号
            pointerslipped.add(index);
            selectedxold = selectedx;
            selectedyold = selectedy;
            if (!ishasmoved){
                //当第一次触碰到九个点之一时，path调用moveto;
                path.moveTo(selectedx,selectedy);
                ishasmoved = true;
            }else{
                //path移动至当前圆点坐标
                path.lineTo(selectedx,selectedy);
            }
        }
    }
    private String text = "请绘制解锁图案";
    private float x;//绘制的圆形的x坐标
    private float y;//绘制圆形的纵坐标
    private float added;//水平竖直方向每个圆点中心间距
    private float patternmargin = 100;//九宫格距离边界距离
    private float bigcircleradius = 90;//外圆半径
    private float smallcircleradius = 25;//内圆半径
    private int index;//圆点的序号
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        added = (width - patternmargin * 2) / 3;
        x = patternmargin + added / 2;
        y = added / 2 + height / 2;
        index = 1;
        canvas.drawColor(Color.BLACK);
        canvas.drawText(text, width / 2, height / 4, mpainttext);
        /**
         * 绘制九个圆点图案
         */
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                canvas.drawCircle(x, y, bigcircleradius, mpaintbigcircle);
                canvas.drawCircle(x, y, smallcircleradius, mpaintsmallcircle);
                pointcontainer.put(index, new float[]{x, y});
                index++;
                x += added;
            }
            y += added;
            x = patternmargin + added / 2;
        }
        x = patternmargin + added / 2;
        y = added / 2 + height / 2;
        canvas.drawPath(path, mpaintline);
    }


    public interface TouchCallback{
        void onTouchUp(String pwd);
    }
}
