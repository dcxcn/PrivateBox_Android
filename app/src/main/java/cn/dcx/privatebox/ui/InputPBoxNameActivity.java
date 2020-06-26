package cn.dcx.privatebox.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import cn.dcx.privatebox.R;

public class InputPBoxNameActivity extends Activity {

	private EditText et_pBoxName;
	private Button btn_sure;
	private Button btn_cancel;

	protected void onCreate(Bundle paramBundle) {
		requestWindowFeature(1);
		super.onCreate(paramBundle);
		setContentView(R.layout.layout_pbox_add);
		et_pBoxName = findViewById(R.id.et_pBoxName);
		btn_sure = findViewById(R.id.btn_sure);
		btn_cancel = findViewById(R.id.btn_cancel);
		String pBox_path = this.getIntent().getStringExtra("pBox_path");
		String pBox_name_old = this.getIntent().getStringExtra("pBox_name_old");
		et_pBoxName.setText(pBox_name_old);
		btn_sure.setOnClickListener(view -> {
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
			bundle.putString("pBox_path", pBox_path);
            bundle.putString("pBox_name_old", pBox_name_old);
			bundle.putString("pBox_name", ""+et_pBoxName.getText());
			intent.putExtras(bundle);
			InputPBoxNameActivity.this.setResult(FileSearchFragment.REQUESTCODE_INPUTPBOXNAME_CREATE, intent);
			InputPBoxNameActivity.this.finish();
		});

		btn_cancel.setOnClickListener(view -> InputPBoxNameActivity.this.finish());
	}


}