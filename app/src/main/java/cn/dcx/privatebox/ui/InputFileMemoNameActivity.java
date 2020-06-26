package cn.dcx.privatebox.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import cn.dcx.privatebox.R;

public class InputFileMemoNameActivity extends Activity {

	private EditText et_fileMemoName;
	private Button btn_sure;
	private Button btn_cancel;


	protected void onCreate(Bundle paramBundle) {
		requestWindowFeature(1);
		super.onCreate(paramBundle);
		setContentView(R.layout.layout_filememo_rename);
		et_fileMemoName = findViewById(R.id.et_fileMemoName);
		btn_sure = findViewById(R.id.btn_sure);
		btn_cancel = findViewById(R.id.btn_cancel);
		String fileMemo_name_old = this.getIntent().getStringExtra("fileMemo_name_old");
		Integer file_id = this.getIntent().getIntExtra("file_id",1);
		et_fileMemoName.setText(fileMemo_name_old);
		btn_sure.setOnClickListener(view -> {
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
            bundle.putString("fileMemo_name_old", fileMemo_name_old);
			bundle.putInt("file_id", file_id);
			bundle.putString("fileMemo_name", ""+et_fileMemoName.getText());
			intent.putExtras(bundle);
			InputFileMemoNameActivity.this.setResult(FileMemoFragment.REQUESTCODE_INPUTFILEMEMO_RENAME, intent);
			InputFileMemoNameActivity.this.finish();
		});

		btn_cancel.setOnClickListener(view -> InputFileMemoNameActivity.this.finish());
	}


}