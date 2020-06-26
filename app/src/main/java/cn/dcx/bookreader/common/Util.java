package cn.dcx.bookreader.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.widget.Toast;


import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.dcx.privatebox.base.MyApplication;
import cn.dcx.bookreader.bean.Book;

/**
 * Created by will on 2016/10/29.
 */

public class Util {
    private static Toast mToast;
    public static void makeToast(String message){
        if(mToast == null){
            mToast = Toast.makeText(MyApplication.getGlobalContext(),"",Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.show();
    }

    public static String getEncoding(Book book){
        UniversalDetector detector = new UniversalDetector(null);
        byte[] bytes = new byte[1024];
        try{
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(book.getPath())));
            int length;
            while ((length = bufferedInputStream.read(bytes)) > 0){
                detector.handleData(bytes,0,length);
            }
            detector.dataEnd();
            bufferedInputStream.close();
        }catch (FileNotFoundException f){
            f.printStackTrace();
        }catch (IOException i){
            i.printStackTrace();
        }
        return detector.getDetectedCharset();
    }
    public static int getPXFromDP(int dp){
        float density = MyApplication.getGlobalContext().getResources().getDisplayMetrics().density;
        return (int)(dp*density);
    }
    public static int getPXFromDP(Context context,int dp){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp*density);
    }
    public static int getColorFromRes(Context context, @ColorRes int res){
        return context.getResources().getColor(res);
    }
    public static ProgressDialog createProgressDialog(Context context,String text){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(text);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }
}
