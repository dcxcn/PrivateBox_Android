package cn.dcx.privatebox.bean;

public class FileMemo {
	/**
	 * 文件Id
	 */
	private int fileID;
	/**
	 * 节点id
	 */
	private int fileNodeID;
	/**
	 * 文件名
	 */
	private String fileName;
	/**
	 *
	 */
	private String fileType;
	/**
	 * 节点名字长度
	 */
	private int fileSize;

	public FileMemo(){

	}

	public int getFileID() {
		return fileID;
	}

	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	public int getFileNodeID() {
		return fileNodeID;
	}

	public void setFileNodeID(int fileNodeID) {
		this.fileNodeID = fileNodeID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
}
