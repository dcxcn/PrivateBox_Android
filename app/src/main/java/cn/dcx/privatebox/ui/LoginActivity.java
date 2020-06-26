package cn.dcx.privatebox.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.dcx.privatebox.R;

public class LoginActivity extends Activity {

	private EditText et_uname;
	private EditText et_upwd;
	private Button btn_sure;
	private Button btn_cancel;

	protected void onCreate(Bundle paramBundle) {
		requestWindowFeature(1);
		super.onCreate(paramBundle);
		setContentView(R.layout.layout_login);
		et_uname = findViewById(R.id.et_uname);
		et_upwd = findViewById(R.id.et_upwd);

		btn_sure = findViewById(R.id.btn_sure);
		btn_cancel = findViewById(R.id.btn_cancel);
		btn_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putString("uname", ""+et_uname.getText());
				bundle.putString("upwd", ""+et_upwd.getText());
				intent.putExtras(bundle);
				LoginActivity.this.setResult(FileSearchFragment.REQUESTCODE_LOGINCHECK, intent);
				LoginActivity.this.finish();
			}
		});

		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LoginActivity.this.finish();
			}
		});
	}


}