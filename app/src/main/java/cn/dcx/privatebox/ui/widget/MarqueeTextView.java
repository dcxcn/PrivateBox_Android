package cn.dcx.privatebox.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;


/**
 * Created by DCX on 2018/12/3.
 */

public class MarqueeTextView extends AppCompatTextView {
    public MarqueeTextView(Context context) {
        super(context);
    }
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    @Override
    public boolean isFocused() {
        return true;
    }
}
