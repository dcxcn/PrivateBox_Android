package cn.dcx.privatebox.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cn.dcx.privatebox.R;
import cn.dcx.privatebox.adapter.PBoxListAdapter;
import cn.dcx.privatebox.bean.FileAdmin;
import cn.dcx.privatebox.bean.PBox;
import cn.dcx.privatebox.database.FileAdminDBMExternal;
import cn.dcx.privatebox.utils.FileUtil;
import cn.dcx.privatebox.utils.MD5;
import cn.dcx.privatebox.utils.SharedPreferencesUtil;

public class FileSearchFragment extends MainFragment {
    public static final int REQUESTCODE_FILEPICKER = 1000;
    public static final int REQUESTCODE_FILEPICKER_CREATE = 1500;
    public static final int REQUESTCODE_LOGINCHECK = 2000;
    public static final int REQUESTCODE_INPUTPBOXNAME_CREATE =3000;
    public static final int REQUESTCODE_INPUTPBOXNAME_RENAME = 3500;
    private List<PBox> pBoxList;
    private PBoxListAdapter mAdapter;
    public FileSearchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_filesearch, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeView();
    }

    private void initializeView(){

        RecyclerView recyclerView = this.getMainActivity().findViewById(R.id.search_list);
        DividerItemDecoration divider = new DividerItemDecoration(this.getMainActivity(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getMainActivity()));

        mAdapter = new PBoxListAdapter(this.getMainActivity());

        mAdapter.setOnClickCallback(new PBoxListAdapter.ClickCallback() {
            @Override
            public void onClick(View v, PBox pbox) {
                String dbDir = pbox.getDir();
                String dbName = pbox.getName();
                loginCheck(dbDir, dbName+".pbox");
            }
            @Override
            public void onLongClick(View v,final PBox pbox) {
                showPBoxMenu(v,pbox);
            }
        });
        pBoxList = SharedPreferencesUtil.getListData("PBoxList",PBox.class);
        mAdapter.setPBoxs(pBoxList);
        recyclerView.setAdapter(mAdapter);
    }
    public void openFilePicker() {
        new LFilePicker()
                .withFragment(FileSearchFragment.this)
                .withRequestCode(REQUESTCODE_FILEPICKER)
                .withStartPath("/")
                .withFileFilter(new String[]{".pbox"})
                .start();
    }
    public void openFilePicker_createPBox() {
        new LFilePicker()
                .withFragment(FileSearchFragment.this)
                .withRequestCode(REQUESTCODE_FILEPICKER_CREATE)
                .withStartPath("/")
                .withMutilyMode(false)
                .withChooseMode(false)
                .start();
    }
    public void searchLocalFile() {
        new GetPBoxListTask(getMainActivity()).execute();
    }
    public void searchLocalFile_Clear() {
        if(pBoxList !=null){
            pBoxList.clear();
            SharedPreferencesUtil.clearData("PBoxList");
            mAdapter.setPBoxs(pBoxList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void clearAllTmpDir(){
        if(pBoxList !=null){
          for(PBox pbox: pBoxList){
             File ftmp = new File(pbox.getPath());
             if(ftmp.exists()){
                 File[] fary = ftmp.listFiles();
                 if(fary!=null){
                     for(File f:fary){
                         if(f.isDirectory() && (f.getName().endsWith(".ptmp")||f.getName().endsWith(".PTMP"))){
                             FileUtil.deleteDirWithFile(f);
                         }
                     }
                 }
             }
          }
        }
        Toast.makeText(getMainActivity(), "您的浏览记录已清除！", Toast.LENGTH_LONG).show();
    }
    class GetPBoxListTask extends AsyncTask<String, Integer, String> {
        // 可变长的输入参数，与AsyncTask.exucute()对应
        public GetPBoxListTask(Context context) {
        }

        @Override
        protected String doInBackground(String... params) {
            //pBoxList = listPBoxs(".pbox", getMainActivity());
            pBoxList = search(new File("/sdcard"),".pbox");
            SharedPreferencesUtil.putListData("PBoxList", pBoxList);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            getMainActivity().showProgressDialog("正在查找中，请等待...", getMainActivity());
        }

        @Override
        protected void onPostExecute(String result) {
            getMainActivity().hideProgressDialog();
            mAdapter.setPBoxs(pBoxList);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }

    private List<PBox> search(File fileold, String key) {
        List<PBox> al_fsItem = new ArrayList<>();
        try {
            File[] files = fileold.listFiles();
            if (files.length > 0) {
                for (int j = 0; j < files.length; j++) {
                    if (!files[j].isDirectory()) {
                        if (files[j].getName().endsWith(key) && files[j].getName().startsWith(".") == false && files[j].getName().startsWith("_") == false) {
                            String path=files[j].getPath();
                            int start = path.lastIndexOf("/");
                            int end = path.lastIndexOf(".");
                            String dbDir = path.substring(0, start);
                            String dbName =  path.substring(start+1, end);
                            PBox pbox = new PBox();
                            pbox.setPath(path);
                            pbox.setName(dbName);
                            pbox.setDir(dbDir);
                            al_fsItem.add(pbox);
                        }
                    } else if ("tencent".equals(files[j].getName()) == false && files[j].getName().startsWith(".") == false && files[j].getName().startsWith("com.") == false) {
                        al_fsItem.addAll(search(files[j], key));
                    }
                }
            }
        } catch (Exception e) {

        }
        return al_fsItem;
    }

    private List<PBox> listPBoxs(String str, Context context) {
        List<PBox> rtnlist = new ArrayList<PBox>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA};

        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Files.getContentUri("external"), projection, null, null, null);
        if (cursor == null)
            return rtnlist;
        else if (cursor.getCount() > 0 && cursor.moveToFirst()) {

            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                if (path != null && path.endsWith(str)) {
                    int start = path.lastIndexOf("/");
                    int end = path.length();
                    String dbDir = path.substring(0, start);
                    String dbName =  path.substring(start+1, end);
                    PBox pbox = new PBox();
                    pbox.setPath(path);
                    pbox.setName(dbName);
                    pbox.setDir(dbDir);
                    rtnlist.add(pbox);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rtnlist;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_FILEPICKER) {
            if (data == null) return;
            List<String> list = data.getStringArrayListExtra("paths");
            Toast.makeText(FileSearchFragment.this.getActivity().getApplicationContext(), "selected " + list.size(), Toast.LENGTH_SHORT).show();
            String path = data.getStringExtra("path");
            String dbFilePath = list.get(0);
            int start = dbFilePath.lastIndexOf("/");
            int end = dbFilePath.lastIndexOf(".");
            String dbName = dbFilePath.substring(start + 1, end);
            Toast.makeText(FileSearchFragment.this.getActivity().getApplicationContext(), "The selected path is:" + path, Toast.LENGTH_SHORT).show();
            PBox pbox = new PBox();
            pbox.setPath(dbFilePath);
            pbox.setName(dbName);
            pbox.setDir(path);
            if(pBoxList ==null){
                pBoxList = new ArrayList<>();
            }
            pBoxList.add(pbox);
            mAdapter.setPBoxs(pBoxList);
            mAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUESTCODE_FILEPICKER_CREATE) {
            if (data == null) return;
            String path = data.getStringExtra("path");
            Intent intent = new Intent("cn.dcx.privatebox.ui.InputPBoxNameActivity");
            intent.putExtra("pBox_path",path);
            startActivityForResult(intent, REQUESTCODE_INPUTPBOXNAME_CREATE);

        }else if (requestCode == REQUESTCODE_INPUTPBOXNAME_CREATE) {
            if (data == null) return;
            String pBox_path = data.getStringExtra("pBox_path");
            String pBox_name = data.getStringExtra("pBox_name");
            String pBox_filename_pbox = pBox_path+"/"+pBox_name+".pbox";
            unZipSeed(pBox_path,pBox_name);

            PBox pbox = new PBox();
            pbox.setPath(pBox_filename_pbox);
            pbox.setName(pBox_name);
            pbox.setDir(pBox_path);

            if(pBoxList ==null){
                pBoxList = new ArrayList<>();
            }
            pBoxList.add(pbox);
            mAdapter.setPBoxs(pBoxList);
            mAdapter.notifyDataSetChanged();
        }else if (requestCode == REQUESTCODE_INPUTPBOXNAME_RENAME) {
            if (data == null) return;
            try{
                String pBox_path = data.getStringExtra("pBox_path");
                String pBox_name_old = data.getStringExtra("pBox_name_old");
                String pBox_name = data.getStringExtra("pBox_name");
                String pBox_fname_pbox_old = pBox_path+"/"+pBox_name_old+".pbox";
                String pBox_fname_pdata_old= pBox_path+"/"+pBox_name_old+".pdata";

                String pBox_fname_pbox_new = pBox_path+"/"+pBox_name+".pbox";
                String pBox_name_pdata_new= pBox_path+"/"+pBox_name+".pdata";

                File f_pbox = new File(pBox_fname_pbox_old);
                File f_pbox_new = new File(pBox_fname_pbox_new);
                File f_pdata = new File(pBox_fname_pdata_old);
                File f_pdata_new = new File(pBox_name_pdata_new);
                f_pbox.renameTo(f_pbox_new);
                f_pdata.renameTo(f_pdata_new);
                curPBox_rename.setName(pBox_name);
                curPBox_rename.setPath(pBox_fname_pbox_new);
                mAdapter.setPBoxs(pBoxList);
                mAdapter.notifyDataSetChanged();
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }else if (requestCode == REQUESTCODE_LOGINCHECK) {
            if (data == null) return;
            String uname = data.getExtras().getString("uname");
            String upwd = data.getExtras().getString("upwd");
            if ("".equals(uname) || "".equals(upwd) || uname == null || upwd == null) {
                Toast.makeText(FileSearchFragment.this.getActivity().getApplicationContext(), "账号密码请填写完整！", Toast.LENGTH_LONG).show();
            } else {
                try {
                    String md5Pwd = MD5.GetMD5Code(upwd);
                    FileAdminDBMExternal fileAdminDBMExternal = new FileAdminDBMExternal(getMainActivity(), getMainActivity().getDbDir(), getMainActivity().getDbName());
                    List<FileAdmin> fileAdminList = fileAdminDBMExternal.getFileAdminList();
                    if (fileAdminList.size() > 0) {
                        Boolean userExist = false;
                        for (FileAdmin fa : fileAdminList) {
                            if (uname.equals(fa.getAdminName()) && md5Pwd.equals(fa.getAdminPwd())) {
                                userExist = true;
                                break;
                            }
                        }
                        if (userExist) {
                            getMainActivity().setPboxOpened(true);
                            getMainActivity().showFileNodeTree();
                        } else {
                            getMainActivity().setPboxOpened(false);
                            Toast.makeText(FileSearchFragment.this.getActivity().getApplicationContext(), "账号密码不匹配！", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        getMainActivity().setPboxOpened(true);
                        getMainActivity().showFileNodeTree();
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }

        }
    }

    public void loginCheck(String dbDir, String dbName) {
        this.getMainActivity().setDbDir(dbDir);
        this.getMainActivity().setDbName(dbName);

        FileAdminDBMExternal fileAdminDBMExternal = new FileAdminDBMExternal(this.getActivity(), dbDir, dbName);
        List<FileAdmin> fileAdminList = fileAdminDBMExternal.getFileAdminList();
        if (fileAdminList.size() > 0) {
            getMainActivity().setPboxOpened(false);
            startActivityForResult(new Intent("cn.dcx.privatebox.ui.LoginActivity"), REQUESTCODE_LOGINCHECK);
        } else {
            getMainActivity().setPboxOpened(true);
            getMainActivity().showFileNodeTree();
        }
    }

    private void unZipSeed(String pBox_path,String pBox_name){
        try {

            InputStream is = this.getResources()
                    .openRawResource(R.raw.seed);
            ZipInputStream zis= new ZipInputStream(is);
            ZipEntry entry = null;
            while((entry = zis.getNextEntry()) != null){
                File file = null;
                System.out.println("filename----"+entry.getName());
                if("Seed.pbox".equals(entry.getName())){
                    file = new File(pBox_path+"/"+pBox_name+".pbox");
                }else{
                    file = new File(pBox_path+"/"+pBox_name+".pdata");
                }
                if(entry.isDirectory()){
                    file.mkdirs();
                    continue;
                }else{
                    File entryDir = new File(file.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }
                    file.createNewFile();
                    OutputStream myOutput = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = zis.read(buffer)) != -1) {
                        myOutput.write(buffer, 0, count);
                    }
                    myOutput.close();
                }
            }
            zis.close();
            is.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private PBox curPBox_rename = null;
    private void showPBoxMenu(View v,PBox pbox){
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(this.getActivity(), v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_pbox_edit, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.btn_pbox_rename:
                    curPBox_rename = pbox;
                    String path = pbox.getDir();
                    Intent intent = new Intent("cn.dcx.privatebox.ui.InputPBoxNameActivity");
                    intent.putExtra("pBox_path",path);
                    intent.putExtra("pBox_name_old",pbox.getName());
                    startActivityForResult(intent, REQUESTCODE_INPUTPBOXNAME_RENAME);
                    break;
                case R.id.btn_pbox_del:
                    try{
                        File f_pbox = new File(pbox.getPath());
                        File f_pdata = new File((pbox.getPath().toLowerCase().replace(".pbox",".pdata")));
                        if(f_pbox.exists()){
                            f_pbox.delete();
                        }
                        if(f_pdata.exists()){
                            f_pdata.delete();
                        }
                        pBoxList.remove(pbox);
                        mAdapter.setPBoxs(pBoxList);
                        mAdapter.notifyDataSetChanged();
                        SharedPreferencesUtil.putListData("PBoxList", pBoxList);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    break;
            }
            return false;
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }
}
