package cn.dcx.privatebox.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;


import cn.dcx.privatebox.bean.FileMemo;
import cn.dcx.privatebox.bean.FileNode;


public class FileNodeDBMExternal extends DBOptExternal {


	public FileNodeDBMExternal(Context paramContext,String dbDir,String dbName) {
		super(paramContext, dbDir,dbName,"FileNode");
	}

	public boolean addFileNode_Root(FileNode fileNode){
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("parent_id", 0);
		localContentValues.put("node_name", fileNode.getName());
		return insert(localContentValues);
	}
	public boolean addFileNode_Common(FileNode fileNode){
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("parent_id", fileNode.getPid());
		localContentValues.put("node_name", fileNode.getName());
		return insert(localContentValues);
	}
	public boolean delFileNode(int nodeID){
		List<FileNode> fileNodeList = getFileNodeListByPID(nodeID);
		for(int i=0;i<fileNodeList.size();i++){
			delFileNode(fileNodeList.get(i).getId());
		}
		String[] arrayOfString = new String[1];
		arrayOfString[0] = ""+nodeID;
		return delete("node_id = ? ", arrayOfString);
	}
	public boolean updFileNode(FileNode fileNode){
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "" + fileNode.getId();
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("node_name", fileNode.getName());
		return update(localContentValues, "node_id = ?", arrayOfString);
	}
	public List<FileNode> getFileNodeList() {
		List<FileNode> rtnFileNodes = new ArrayList<FileNode>();
		Cursor localCursor = query();
		if ((localCursor != null) && (localCursor.getCount() > 0)) {

			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return rtnFileNodes;
				}
				FileNode fileNode = new FileNode();
				fileNode.setId(localCursor.getInt(localCursor
						.getColumnIndex("node_id")));
				fileNode.setPid(localCursor.getInt(localCursor
						.getColumnIndex("parent_id")));
				fileNode.setName(localCursor.getString(localCursor
						.getColumnIndex("node_name")));
				rtnFileNodes.add(fileNode);
			}
		}
		return rtnFileNodes;
	}
	public List<FileNode> getFileNodeListByPID(int parent_id) {
		List<FileNode> rtnFileNodes = new ArrayList<FileNode>();
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "" + parent_id;
		Cursor localCursor = query(null, "parent_id = ?",
				arrayOfString, null, null, null);
		if ((localCursor != null) && (localCursor.getCount() > 0)) {

			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return rtnFileNodes;
				}
				FileNode fileNode = new FileNode();
				fileNode.setId(localCursor.getInt(localCursor
						.getColumnIndex("node_id")));
				fileNode.setPid(localCursor.getInt(localCursor
						.getColumnIndex("parent_id")));
				fileNode.setName(localCursor.getString(localCursor
						.getColumnIndex("node_name")));
				rtnFileNodes.add(fileNode);
			}
		}
		return rtnFileNodes;
	}

	public List<FileMemo> getFileMemoList() {
		List<FileMemo> rtnFileNodes = new ArrayList<FileMemo>();
		Cursor localCursor = query();
		if ((localCursor != null) && (localCursor.getCount() > 0)) {

			while (true) {
				if (!(localCursor.moveToNext())) {
					localCursor.close();
					return rtnFileNodes;
				}
				FileMemo fileMemo = new FileMemo();
				fileMemo.setFileNodeID(localCursor.getInt(localCursor
						.getColumnIndex("node_id")));
				fileMemo.setFileID(localCursor.getInt(localCursor
						.getColumnIndex("file_id")));
				fileMemo.setFileName(localCursor.getString(localCursor
						.getColumnIndex("node_name")));
				rtnFileNodes.add(fileMemo);
			}
		}
		return rtnFileNodes;
	}
}