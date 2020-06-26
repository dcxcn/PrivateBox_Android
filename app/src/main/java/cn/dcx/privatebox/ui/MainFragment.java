package cn.dcx.privatebox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cn.dcx.privatebox.MainActivity;
import cn.dcx.privatebox.callback.OnViewCreatedCallBack;

/**
 * Created by DCX on 2018/11/29.
 */

public class MainFragment extends Fragment {
    OnViewCreatedCallBack onViewCreatedCallBack = null;
    public MainActivity getMainActivity(){
        return (MainActivity)this.getActivity();
    }

    public OnViewCreatedCallBack getOnViewCreatedCallBack() {
        return onViewCreatedCallBack;
    }

    public void setOnViewCreatedCallBack(OnViewCreatedCallBack onViewCreatedCallBack) {
        this.onViewCreatedCallBack = onViewCreatedCallBack;
    }
}
