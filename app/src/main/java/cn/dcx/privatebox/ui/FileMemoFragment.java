package cn.dcx.privatebox.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dcx.privatebox.R;
import cn.dcx.privatebox.bean.FileMemo;
import cn.dcx.privatebox.database.FileMemoDBMExternal;
import cn.dcx.privatebox.database.FileStoreDBMExternal;
import cn.dcx.privatebox.utils.FileUtil;
import cn.dcx.privatebox.utils.OpenFileUtil;
import cn.dcx.privatebox.utils.UtilTools;
import cn.dcx.sweetalert.SweetAlertDialog;

public class FileMemoFragment extends MainFragment implements OnItemClickListener, AdapterView.OnItemLongClickListener {
    private SimpleAdapter simpleAdapter;
    private GridView gridView;
    private List<Map<String, Object>> dataList;
    public static final int REQUESTCODE_INPUTFILEMEMO_RENAME = 660;
    public static final int REQUESTCODE_FILEPICKER_ADDFILES = 700;
    public static final int REQUESTCODE_TAKE_PICTURE = 750;
    public static final int REQUESTCODE_RECORD_SOUND = 760;
    public static final int REQUESTCODE_RECORD_VIDEO = 800;
    private int curNodeID = -1;
    private String m_CurrentSound = "";
    private String m_CurrentSoundName = "";

    private String m_CurrentCamaraPhoto = "";
    private String m_CurrentCamaraPhotoName = "";

    private String m_CurrentCamaraVideo = "";
    private String m_CurrentCamaraVideoName = "";

    private List<String> al_filePath_save = null;

    public FileMemoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_filememo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = new ArrayList<Map<String, Object>>();
        gridView = (GridView) view.findViewById(R.id.fileMemo_gridView);
        dataList = new ArrayList<Map<String, Object>>();
        simpleAdapter = new SimpleAdapter(view.getContext(), dataList, R.layout.layout_filememo_item, new String[]{"image", "file_name"}, new int[]{R.id.imageView1, R.id.textView1});
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(this);
        if (this.getOnViewCreatedCallBack() != null) {
            this.getOnViewCreatedCallBack().execute();
        }

        gridView.setOnItemLongClickListener(this);
    }

    /**
     * 拍照
     */
    public void onPhoto() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String str1 = this.getMainActivity().getExternalCacheDir().getPath();
            String str2 = UtilTools.getCurrentTimeString() + ".jpg";
            File localFile = new File(str1);
            if (!(localFile.exists()))
                localFile.mkdirs();
            this.m_CurrentCamaraPhoto = str1 + "/" + str2;
            this.m_CurrentCamaraPhotoName = str2;

            Uri localUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                localUri = FileProvider.getUriForFile(this.getMainActivity(), "cn.dcx.privatebox.fileProvider", new File(str1, str2));
            } else {
                localUri = Uri.fromFile(new File(str1, str2));
            }
            Intent localIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            localIntent.putExtra("output", localUri);
            localIntent.putExtra("android.intent.extra.videoQuality", 0);
            this.startActivityForResult(localIntent, REQUESTCODE_TAKE_PICTURE);
        } else {
            Toast.makeText(this.getMainActivity(), "无SD卡", Toast.LENGTH_LONG).show();
        }

    }
    public void onRecordSound() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String str1 = this.getMainActivity().getExternalCacheDir().getPath();
            String str2 = UtilTools.getCurrentTimeString() + ".mp3";
            File localFile = new File(str1);
            if (!(localFile.exists()))
                localFile.mkdirs();
            this.m_CurrentSound = str1 + "/" + str2;
            this.m_CurrentSoundName = str2;

            Intent localIntent = new Intent(
                    "cn.dcx.privatebox.ui.RecordSoundActivity");
            localIntent.putExtra("soundFilePath",
                    this.m_CurrentSound);
            this.startActivityForResult(localIntent, REQUESTCODE_RECORD_SOUND);
        } else {
            Toast.makeText(this.getMainActivity(), "无SD卡", Toast.LENGTH_LONG).show();
        }
    }
    public void onRecordVideo() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String str1 = this.getMainActivity().getExternalCacheDir().getPath();
            String str2 = UtilTools.getCurrentTimeString() + ".mp4";
            File localFile = new File(str1);
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
            this.m_CurrentCamaraVideo = str1 + "/" + str2;
            this.m_CurrentCamaraVideoName = str2;
            Uri localUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                localUri = FileProvider.getUriForFile(this.getMainActivity(), "cn.dcx.privatebox.fileProvider", new File(str1, str2));
            } else {
                localUri = Uri.fromFile(new File(str1, str2));
            }
            Intent openVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            openVideoIntent.putExtra("output", localUri);
            openVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            this.startActivityForResult(openVideoIntent, REQUESTCODE_RECORD_VIDEO);
        } else {
            UtilTools.MsgBox(this.getMainActivity(), "无SD卡");
        }

    }

    public void addFiles() {
        new LFilePicker()
                .withFragment(FileMemoFragment.this)
                .withRequestCode(REQUESTCODE_FILEPICKER_ADDFILES)
                .withStartPath("/")
                .start();
    }

    public void clearPtmp() {
        FileUtil.deleteDir(getMainActivity().getDbDir() + "/" + getMainActivity().getDbTempName());
        Toast.makeText(getMainActivity(), "缓存目录已清除！", Toast.LENGTH_LONG).show();
    }

    public void showFileMemos(int node_id) {
        curNodeID = node_id;
        dataList.clear();
        FileMemoDBMExternal fileNodeDBMExternal = new FileMemoDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbName());
        List<FileMemo> al_fm = fileNodeDBMExternal.getFileMemosByFileNodeID(node_id);
        for (FileMemo fileMemo : al_fm) {
            HashMap<String, Object> fmo = new HashMap<>();
            fmo.put("file_name", fileMemo.getFileName());
            fmo.put("file_id", fileMemo.getFileID());
            fmo.put("file_type", fileMemo.getFileType());
            switch (fileMemo.getFileType()) {
                case ".txt":
                    fmo.put("image", R.drawable.ic_file_txt);
                    break;
                case ".jpg":
                    fmo.put("image", R.drawable.ic_file_jpg);
                    break;
                case ".mp3":
                    fmo.put("image", R.drawable.ic_file_mp3);
                    break;
                case ".htm":
                case ".html":
                    fmo.put("image", R.drawable.ic_file_html);
                    break;
                case ".mht":
                    fmo.put("image", R.drawable.ic_file_mht);
                    break;
                case ".mp4":
                    fmo.put("image", R.drawable.ic_file_mp4);
                    break;
                case ".flv":
                    fmo.put("image", R.drawable.ic_file_flv);
                    break;
                default:
                    fmo.put("image", R.drawable.ic_file_def);
                    break;
            }
            dataList.add(fmo);
        }
        simpleAdapter.notifyDataSetChanged();
    }

    private void renameFileMemo(int file_id, String fileMemo_name_old) {
        Intent intent = new Intent("cn.dcx.privatebox.ui.InputFileMemoNameActivity");
        intent.putExtra("file_id", file_id);
        intent.putExtra("fileMemo_name_old", fileMemo_name_old);
        startActivityForResult(intent, REQUESTCODE_INPUTFILEMEMO_RENAME);
    }

    private void showFileMemoEditMenu(Integer file_id, String file_name, View v) {
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(this.getMainActivity(), v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_filememo_edit, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.btn_filememo_rename:
                        renameFileMemo(file_id, file_name);
                        break;
                    case R.id.btn_filememo_del:
                        new SweetAlertDialog(FileMemoFragment.this.getMainActivity(), SweetAlertDialog.WARNING_TYPE)
                                .showCancelButton(true)
                                .setTitleText("你确认吗?")
                                .setContentText("删除就恢复不了的!")
                                .setConfirmText("是的!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        deleteFileMemo(file_id);
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .setCancelButton("不删了", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();

                        break;
                }
                return false;
            }
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Integer file_id = Integer.parseInt(dataList.get(position).get("file_id").toString());
        String file_name = dataList.get(position).get("file_name").toString();
        showFileMemoEditMenu(file_id, file_name, view);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
        Integer file_id = Integer.parseInt(dataList.get(position).get("file_id").toString());
        String file_name = dataList.get(position).get("file_name").toString();
        String file_type = dataList.get(position).get("file_type").toString();
        FileStoreDBMExternal fsdm = new FileStoreDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbDataName());
        List<byte[]> fdata = fsdm.getFileDataByFileID(file_id);
        makeDirectory(getMainActivity().getDbDir() + "/" + getMainActivity().getDbTempName());
        File f = new File(getMainActivity().getDbDir() + "/" + getMainActivity().getDbTempName() + "/" + file_name + file_type);

        if (f.exists()) {
            OpenFileUtil.openFile(getMainActivity(), f.getPath());
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(f);
                for (int i = 0; i < fdata.size(); i++) {
                    fos.write(fdata.get(i), 0, fdata.get(i).length);
                }
                fos.close();
                OpenFileUtil.openFile(getMainActivity(), f.getPath());
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    private void deleteFileMemo(Integer file_id) {
        try {
            FileStoreDBMExternal fileStoreDBMExternal = new FileStoreDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbDataName());
            fileStoreDBMExternal.delFileStore(file_id);
            FileMemoDBMExternal fileMemoDBMExternal = new FileMemoDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbName());
            fileMemoDBMExternal.delFileMemo(file_id);
            for (Map<String, Object> map : dataList) {
                if (map.get("file_id").equals(file_id)) {
                    dataList.remove(map);
                    break;
                }
            }
            simpleAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // 生成文件夹
    public static void makeDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public void createBitMap(String dir, String filename) throws IOException {

        //得到当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = format.format(date);

        String str = time;
        String fileName = dir + filename;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
        Bitmap photo = BitmapFactory.decodeFile(fileName, options);
        int width = photo.getWidth(), hight = photo.getHeight();
        Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
        Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上

        Paint photoPaint = new Paint(); //建立画笔
        photoPaint.setDither(true); //获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);//过滤一些

        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
        canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔
        textPaint.setTextSize(30.0f);//字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
        textPaint.setColor(Color.RED);//采用的颜色
        canvas.drawText(str, width - 450, hight - 60, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制
        canvas.save();
        canvas.restore();
        saveMyBitmap(fileName, icon);
    }

    /**
     * 保存加了水印的图片
     *
     * @param filePath
     * @param b
     * @throws IOException
     */
    public void saveMyBitmap(String filePath, Bitmap b) throws IOException {
        File f = new File(filePath);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_INPUTFILEMEMO_RENAME) {
            String fileMemo_name = data.getStringExtra("fileMemo_name");
            Integer file_id = data.getIntExtra("file_id", 1);
            FileMemo fileMemo = new FileMemo();
            fileMemo.setFileID(file_id);
            fileMemo.setFileName(fileMemo_name);
            FileMemoDBMExternal fileMemoDBMExternal = new FileMemoDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbName());
            fileMemoDBMExternal.updFileMemo(fileMemo);
            for (Map<String, Object> map : dataList) {
                if (map.get("file_id").equals(file_id)) {
                    map.put("file_name",fileMemo_name);
                    break;
                }
            }
            simpleAdapter.notifyDataSetChanged();

        } else if (requestCode == REQUESTCODE_FILEPICKER_ADDFILES) {
            if (data == null) return;
            al_filePath_save = null;
            List<String> list = data.getStringArrayListExtra("paths");
            al_filePath_save = list;
            new SaveFileTask(getMainActivity()).execute();
        } else if (requestCode == REQUESTCODE_TAKE_PICTURE) {
            al_filePath_save = null;
            al_filePath_save = new ArrayList<>();
            al_filePath_save.add(m_CurrentCamaraPhoto);
            new SaveFileTask(getMainActivity()).execute();
        } else if (requestCode == REQUESTCODE_RECORD_SOUND) {
            al_filePath_save = null;
            al_filePath_save = new ArrayList<>();
            if(resultCode==0){
                al_filePath_save.add(m_CurrentSound);
                new SaveFileTask(getMainActivity()).execute();
            }

        }else if (requestCode == REQUESTCODE_RECORD_VIDEO) {
            al_filePath_save = null;
            al_filePath_save = new ArrayList<>();
            al_filePath_save.add(m_CurrentCamaraVideo);
            new SaveFileTask(getMainActivity()).execute();

        }

    }


    class SaveFileTask extends AsyncTask<String, Integer, String> {
        // 可变长的输入参数，与AsyncTask.exucute()对应
        public SaveFileTask(Context context) {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (al_filePath_save != null && al_filePath_save.size() > 0) {
                    FileMemoDBMExternal fileMemoDBMExternal = new FileMemoDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbName());
                    FileStoreDBMExternal fileStoreDBMExternal = new FileStoreDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbName().replace(".pbox", ".pdata"));

                    for (int i = 0; i < al_filePath_save.size(); i++) {
                        //先保存描述信息，获取file_id，再保存二进制数据

                        String fpath = al_filePath_save.get(i);
                        byte[] fdata = getBytes(fpath);
                        int start = fpath.lastIndexOf("/");
                        int end = fpath.lastIndexOf(".");
                        String fType = fpath.substring(end, fpath.length());
                        String fName = fpath.substring(start + 1, end);
                        FileMemo fileMemo = new FileMemo();
                        fileMemo.setFileNodeID(curNodeID);
                        fileMemo.setFileName(fName);
                        fileMemo.setFileType(fType);
                        fileMemo.setFileSize(fdata.length);
                        long fileID = fileMemoDBMExternal.addFileMemo(fileMemo);
                        fileStoreDBMExternal.addFileStore(fileID, fdata);
                    }
                }

            } catch (Exception ex) {
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            getMainActivity().showProgressDialog("保存中，请稍等...", getMainActivity());
        }

        @Override
        protected void onPostExecute(String result) {
            showFileMemos(curNodeID);
            getMainActivity().hideProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
