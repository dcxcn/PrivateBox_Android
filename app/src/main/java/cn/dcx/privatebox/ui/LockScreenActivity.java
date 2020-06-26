package cn.dcx.privatebox.ui;

import android.os.Bundle;

import cn.dcx.privatebox.R;
import cn.dcx.privatebox.base.BaseActivity;
import cn.dcx.privatebox.ui.lock.NineGridLockView;

/**
 * Created by DCX on 2018/12/6.
 */

public class LockScreenActivity extends BaseActivity {
    private NineGridLockView nineGridLockView;
    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_lockscreen);
        nineGridLockView = findViewById(R.id.nineGridLockView);
        //nineGridLockView.add
    }
}
