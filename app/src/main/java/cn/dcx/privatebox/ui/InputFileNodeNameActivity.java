package cn.dcx.privatebox.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import cn.dcx.privatebox.R;

public class InputFileNodeNameActivity extends Activity {

	private EditText et_fileNodeName;
	private Button btn_sure;
	private Button btn_cancel;

	protected void onCreate(Bundle paramBundle) {
		requestWindowFeature(1);
		super.onCreate(paramBundle);
		setContentView(R.layout.layout_filenode_add);
		et_fileNodeName = findViewById(R.id.et_fileNodeName);
		btn_sure = findViewById(R.id.btn_sure);
		btn_cancel = findViewById(R.id.btn_cancel);
		String fileNode_name_old = this.getIntent().getStringExtra("fileNode_name_old");
		Integer fileNode_pid = this.getIntent().getIntExtra("fileNode_pid",0);
		Integer fileNode_id = this.getIntent().getIntExtra("fileNode_id",1);
		et_fileNodeName.setText(fileNode_name_old);
		btn_sure.setOnClickListener(view -> {
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
            bundle.putString("fileNode_name_old", fileNode_name_old);
			bundle.putInt("fileNode_pid", fileNode_pid);
			bundle.putInt("fileNode_id", fileNode_id);
			bundle.putString("fileNode_name", ""+et_fileNodeName.getText());
			intent.putExtras(bundle);
			InputFileNodeNameActivity.this.setResult(FileNodeFragment.REQUESTCODE_INPUTFILENODE_ADD_ROOT, intent);
			InputFileNodeNameActivity.this.finish();
		});

		btn_cancel.setOnClickListener(view -> InputFileNodeNameActivity.this.finish());
	}


}