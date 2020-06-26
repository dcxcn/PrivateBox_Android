package cn.dcx.privatebox.database;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import cn.dcx.privatebox.bean.FileAdmin;

public class FileAdminDBMExternal extends DBOptExternal {


	public FileAdminDBMExternal(Context paramContext, String dbDir, String dbName) {
		super(paramContext, dbDir,dbName,"FileAdmin");
	}


	public List<FileAdmin> getFileAdminList() {
		List<FileAdmin> rtnAdmins = new ArrayList<FileAdmin>();
		Cursor localCursor = query();
		if ((localCursor != null) && (localCursor.getCount() > 0)) {

			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return rtnAdmins;
				}
				FileAdmin fileAdmin = new FileAdmin();
				fileAdmin.setAdminID(localCursor.getInt(localCursor
						.getColumnIndex("admin_id")));
				fileAdmin.setAdminName(localCursor.getString(localCursor
						.getColumnIndex("admin_name")));
				fileAdmin.setAdminPwd(localCursor.getString(localCursor
						.getColumnIndex("admin_pwd")));
				rtnAdmins.add(fileAdmin);
			}
		}
		return rtnAdmins;
	}

}