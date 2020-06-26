package cn.dcx.privatebox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.List;

import cn.dcx.privatebox.bean.FileMemo;
import cn.dcx.privatebox.bean.FileNode;

public class FileStoreDBMExternal extends DBOptExternal {

	private  int mb_1 = 1048576;
	public FileStoreDBMExternal(Context paramContext, String dbDir, String dbName) {
		super(paramContext, dbDir,dbName,"FileStore");
	}

	public List<byte[]> getFileDataByFileID(Integer file_id) {
		List<byte[]> al_fileData = new ArrayList<>();
		Cursor localCursor = query(null, "file_id = ?", new String[] { ""
				+ file_id }, null, null, "file_part_id asc");
		if ((localCursor != null) && (localCursor.getCount() > 0)) {
			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return al_fileData;
				}
				al_fileData.add(localCursor.getBlob(localCursor.getColumnIndex("file_part_data")));
			}
		}
		return al_fileData;
	}

	public boolean addFileStore(long file_id, byte[] file_data){
		ContentValues localContentValues = new ContentValues();
		Boolean rtnB = true;
		if (file_data.length > mb_1)
		{
			int partCount = (file_data.length + mb_1 - 1) / mb_1;
			for (int i = 1; i <= partCount; i++)
			{
				localContentValues.put("file_id", file_id);
				localContentValues.put("file_part_id", i);
				localContentValues.put("file_part_data",getPartFileData(file_data,i));
				rtnB = rtnB && insert(localContentValues);
			}
		}else{
			localContentValues.put("file_id", file_id);
			localContentValues.put("file_part_id", 1);
			localContentValues.put("file_part_data",file_data);
			rtnB = rtnB && insert(localContentValues);
		}

		return rtnB;
	}

	public byte[] getPartFileData(byte[] file_data, int filePartID) {

		int startIndex = (filePartID - 1) * mb_1;
		int endIndex = (filePartID * mb_1 <= file_data.length) ? filePartID * mb_1 : file_data.length;
		int size = endIndex - startIndex;
		byte[] tempByte = new byte[size];
		for (int i = startIndex,j=0; i < endIndex; i++,j++) {
			tempByte[j] = file_data[i];
		}
		return tempByte;
	}

	public boolean delFileStore(int file_id){
		String[] arrayOfString = new String[1];
		arrayOfString[0] = ""+file_id;
		return delete("file_id = ? ", arrayOfString);
	}
}