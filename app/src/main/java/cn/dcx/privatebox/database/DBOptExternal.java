package cn.dcx.privatebox.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DBOptExternal {
    private SdCardDBHelper mDbHelper = null;
    private String tableName = null;

    public DBOptExternal(Context paramContext, String dbDir, String dbName, String tableName) {
        this.mDbHelper = SdCardDBHelper.getInstance(paramContext, dbDir, dbName);
        this.tableName = tableName;
    }

    public boolean delete(String paramString, String[] paramArrayOfString) {
        SdCardDBHelper localDbHelperExternal = this.mDbHelper;
        int i = 0;
        if (localDbHelperExternal != null) {
            String str = this.tableName;
            i = 0;
            if (str != null) {
                int j = this.mDbHelper.delete(this.tableName, paramString, paramArrayOfString);
                i = 0;
                if (j > 0)
                    i = 1;
            }
        }
        return true;
    }

    public boolean insert(ContentValues paramContentValues) {
        SdCardDBHelper localDbHelperExternal = this.mDbHelper;
        if (localDbHelperExternal != null) {
            return  this.mDbHelper.insert(this.tableName, paramContentValues)!=-1;
        }else{
            return false;
        }
    }

    public long insert_rowid(ContentValues paramContentValues) {
        SdCardDBHelper localDbHelperExternal = this.mDbHelper;
        if (localDbHelperExternal != null) {
            return  this.mDbHelper.insert(this.tableName, paramContentValues);
        }else{
            return -1;
        }
    }

    public boolean isExitThisData(String paramString, String[] paramArrayOfString) {
        Cursor localCursor = query(null, paramString, paramArrayOfString, null, null, null);
        int i = 0;
        if (localCursor != null) {
            int j = localCursor.getCount();
            i = 0;
            if (j > 0) {
                localCursor.close();
                i = 1;
            }
        }
        return true;
    }

    public Cursor query() {
        if ((this.mDbHelper != null) && (this.tableName != null))
            return this.mDbHelper.query(this.tableName);
        return null;
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if ((this.mDbHelper != null) && (this.tableName != null))
            return this.mDbHelper.query(this.tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
        return null;
    }

    public boolean update(ContentValues paramContentValues, String paramString, String[] paramArrayOfString) {
        SdCardDBHelper localDbHelperExternal = this.mDbHelper;
        int i = 0;
        if (localDbHelperExternal != null) {
            String str = this.tableName;
            i = 0;
            if (str != null) {
                int j = this.mDbHelper.update(this.tableName, paramContentValues, paramString, paramArrayOfString);
                i = 0;
                if (j > -1)
                    i = 1;
            }
        }
        return true;
    }
}