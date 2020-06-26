package cn.dcx.privatebox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import cn.dcx.privatebox.bean.FileMemo;
import cn.dcx.privatebox.bean.FileNode;


public class FileMemoDBMExternal extends DBOptExternal {


	public FileMemoDBMExternal(Context paramContext, String dbDir, String dbName) {
		super(paramContext, dbDir,dbName,"FileMemo");
	}

	public List<FileMemo> getFileMemosByFileNodeID(Integer node_id) {
		List<FileMemo> rtnFileNodes = new ArrayList<FileMemo>();
		Cursor localCursor = query(null, "file_nodeid = ?", new String[] { ""
				+ node_id }, null, null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0)) {

			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return rtnFileNodes;
				}
				FileMemo fileMemo = new FileMemo();
				fileMemo.setFileID(localCursor.getInt(localCursor
						.getColumnIndex("file_id")));
				fileMemo.setFileNodeID(localCursor.getInt(localCursor
						.getColumnIndex("file_nodeid")));
				fileMemo.setFileName(localCursor.getString(localCursor
						.getColumnIndex("file_name")));
				fileMemo.setFileType(localCursor.getString(localCursor
						.getColumnIndex("file_type")));
				rtnFileNodes.add(fileMemo);
			}
		}
		return rtnFileNodes;
	}

	public long addFileMemo(FileMemo fileMemo){
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("file_nodeid", fileMemo.getFileNodeID());
		localContentValues.put("file_name", fileMemo.getFileName());
		localContentValues.put("file_type",fileMemo.getFileType());
		localContentValues.put("file_size",fileMemo.getFileSize());
		return insert_rowid(localContentValues);
	}

	public boolean updFileMemo(FileMemo fileMemo){
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "" + fileMemo.getFileID();
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("file_name", fileMemo.getFileName());
		return update(localContentValues, "file_id = ?", arrayOfString);
	}

	public boolean delFileMemo(int file_id){
		String[] arrayOfString = new String[1];
		arrayOfString[0] = ""+file_id;
		return delete("file_id = ? ", arrayOfString);
	}
}