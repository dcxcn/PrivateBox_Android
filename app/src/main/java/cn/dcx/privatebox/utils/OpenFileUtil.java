package cn.dcx.privatebox.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.xunlei.aplayer.PlayerActivity;

import java.io.File;
import java.util.Locale;

import cn.dcx.bookreader.BookReaderActivity;
import cn.dcx.privatebox.ui.HtmlViewerActivity;
import cn.dcx.privatebox.ui.SingleImgViewActivity;

public class OpenFileUtil {


    /**
     * 声明各种类型文件的dataType
     **/
    private static final String DATA_TYPE_ALL = "*/*";
    //未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
    private static final String DATA_TYPE_VIDEO = "video/*";
    private static final String DATA_TYPE_AUDIO = "audio/*";
    private static final String DATA_TYPE_HTML = "text/html";
    private static final String DATA_TYPE_IMAGE = "image/*";
    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
    private static final String DATA_TYPE_WORD = "application/msword";
    private static final String DATA_TYPE_CHM = "application/x-chm";
    private static final String DATA_TYPE_TXT = "text/plain";
    private static final String DATA_TYPE_PDF = "application/pdf";

    /**
     * 打开文件 * @param filePath 文件的全路径，包括到文件名
     */
    public static void openFile(Context mContext, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            //如果文件不存在
            Toast.makeText(mContext, "打开失败，原因：文件已经被移动或者删除", Toast.LENGTH_SHORT).show();
            return;
        }
            /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
            /* 依扩展名的类型决定MimeType */
        Intent intent = null;
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            intent = generateVideoAudioIntent(mContext,filePath, DATA_TYPE_AUDIO);
        } else if (end.equals("3gp") || end.equals("mp4") || end.equals("flv")) {
            intent = generateVideoAudioIntent(mContext,filePath, DATA_TYPE_VIDEO);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            //intent = generateCommonIntent(mContext,filePath, DATA_TYPE_IMAGE);
            intent = new Intent( mContext,SingleImgViewActivity.class);
            intent.putExtra("imgPath", filePath);
        } else if (end.equals("apk")) {
            intent = generateCommonIntent(mContext,filePath, DATA_TYPE_APK);
        } else if (end.equals("html") || end.equals("htm")) {
            intent = generateHtmlFileIntent(mContext,filePath);
        }else if (end.equals("mht")) {
            String newHtmlFilePath= filePath.replace(".mht",".html");
            Mht2HtmlUtil.mht2html(filePath,newHtmlFilePath);
            intent = generateHtmlFileIntent(mContext,newHtmlFilePath);
        } else if (end.equals("ppt")) {
            intent = generateCommonIntent(mContext,filePath, DATA_TYPE_PPT);
        } else if (end.equals("xls")) {
            intent = generateCommonIntent(mContext,filePath, DATA_TYPE_EXCEL);
        } else if (end.equals("doc")) {
            intent = generateCommonIntent(mContext,filePath, DATA_TYPE_WORD);
        } else if (end.equals("pdf")) {
            intent = generateCommonIntent(mContext,filePath, DATA_TYPE_PDF);
        } else if (end.equals("chm")) {
            intent = generateCommonIntent(mContext,filePath, DATA_TYPE_CHM);
        } else if (end.equals("txt")) {
            intent = new Intent( mContext,BookReaderActivity.class);
            intent.putExtra("filePath", filePath);
        } else {
            intent = generateCommonIntent(mContext,filePath, DATA_TYPE_ALL);
        }
        mContext.startActivity(intent);
    }

    /**
     * 产生打开视频或音频的Intent * @param filePath 文件路径 * @param dataType 文件类型 * @return
     */
    private static Intent generateVideoAudioIntent(Context context,String filePath, String dataType) {
        //APlayerParam aPlayerParam = new APlayerParam(mediaFilePath, null);
        Intent intent = new Intent(context, PlayerActivity.class);
        //Log.e(DEBUG_TAG, Uri.encode(mediaFilePath));
        intent.setData(Uri.parse(filePath));
        return intent;
    }

    /**
     * 产生打开网页文件的Intent * @param filePath 文件路径 * @return
     */
    private static Intent generateHtmlFileIntent(Context context,String filePath) {
        Intent intent = new Intent(context, HtmlViewerActivity.class);
        intent.putExtra("url","file:///"+filePath);
        return intent;
    }


    /**
     * 产生除了视频、音频、网页文件外，打开其他类型文件的Intent * @param filePath 文件路径 * @param dataType 文件类型 * @return
     */
    private static Intent generateCommonIntent(Context context,String filePath, String dataType) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(filePath);
        Uri uri = getUri(context,intent, file);
        intent.setDataAndType(uri, dataType);
        return intent;
    }

    private static Uri getUri(Context context,Intent intent,File file){
        Uri localUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            localUri = FileProvider.getUriForFile(context, "com.chinadgis.android.psys.fileProvider", file);
        }else{
            localUri = Uri.fromFile(file);
        }
        return localUri;
    }
}
