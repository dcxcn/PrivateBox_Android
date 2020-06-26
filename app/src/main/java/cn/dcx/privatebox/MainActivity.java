package cn.dcx.privatebox;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import cn.dcx.privatebox.callback.OnViewCreatedCallBack;
import cn.dcx.privatebox.ui.FileMemoFragment;
import cn.dcx.privatebox.ui.FileNodeFragment;
import cn.dcx.privatebox.ui.FileSearchFragment;
import cn.dcx.privatebox.utils.UtilTools;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    public static final int REQUESTCODE_LOCKSCREEN_SETTING = 3000;
    private FileSearchFragment fileSearchFragment;
    private FileNodeFragment fileNodeFragment;
    private FileMemoFragment fileMemoFragment;
    private ProgressDialog progressDialog = null;
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    private String dbDir;
    private String dbName;
    private Integer fileNodeID;
    private Boolean pboxOpened = false;
    private Toast mToast;
    private View layout_file_search_zone;
    private View layout_file_node_zone;
    private View layout_file_memo_zone;
    private View layout_myself_zone;

    private ImageView img_file_search_zone;
    private ImageView img_file_node_zone;
    private ImageView img_file_memo_zone;
    private ImageView img_myself_zone;

    private TextView txt_file_search_zone;
    private TextView txt_file_node_zone;
    private TextView txt_file_memo_zone;
    private TextView txt_myself_zone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        // 初始化布局元素
        initViews();
        fragmentManager = getFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);
    }

    public  void showProgressDialog(String msg,Context context) {
        if(context!=null){
            progressDialog = new ProgressDialog(context);
        }else{
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public  void updateProgressDialog(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public  void hideProgressDialog() {
        progressDialog.dismiss();
    }
    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {
        layout_file_search_zone = findViewById(R.id.layout_file_search_zone);
        layout_file_node_zone = findViewById(R.id.layout_file_node_zone);
        layout_file_memo_zone = findViewById(R.id.layout_file_memo_zone);
        layout_myself_zone = findViewById(R.id.layout_myself_zone);
        img_file_search_zone = (ImageView) findViewById(R.id.img_file_search_zone);
        img_file_node_zone = (ImageView) findViewById(R.id.img_file_node_zone);
        img_file_memo_zone = (ImageView) findViewById(R.id.img_file_memo_zone);
        img_myself_zone = (ImageView) findViewById(R.id.img_myself_zone);
        txt_file_search_zone = (TextView) findViewById(R.id.txt_file_search_zone);
        txt_file_node_zone = (TextView) findViewById(R.id.txt_file_node_zone);
        txt_file_memo_zone = (TextView) findViewById(R.id.txt_file_memo_zone);
        txt_myself_zone = (TextView) findViewById(R.id.txt_myself_zone);
        layout_file_search_zone.setOnClickListener(this);
        layout_file_node_zone.setOnClickListener(this);
        layout_file_memo_zone.setOnClickListener(this);
        layout_myself_zone.setOnClickListener(this);
        layout_file_search_zone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showFileSearchMenu(img_file_search_zone);
                return false;
            }
        });
        layout_file_node_zone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showFileNodeMenu(img_file_node_zone);
                return false;
            }
        });
        layout_file_memo_zone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(dbDir != null && dbName != null){
                    showFileMemoMenu(layout_file_memo_zone);
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_file_search_zone:
                // 当点击了主页tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.layout_file_node_zone:
                // 当点击了导航tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.layout_file_memo_zone:
                // 当点击了更多tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.layout_myself_zone:
                showMySelfMenu(layout_myself_zone);
                break;
            default:
                break;
        }
    }
    public String getDbDir() {
        return dbDir;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbDataName() {
        return dbName.replace("pbox", "pdata").replace("PBOX", "PDATA");
    }

    public Integer getFileNodeID() {
        return fileNodeID;
    }

    public void setFileNodeID(Integer fileNodeID) {
        this.fileNodeID = fileNodeID;
    }

    public String getDbTempName() {
        return dbName.replace("pbox", "ptmp").replace("PBOX", "PTMP");
    }

    public void setDbDir(String dbDir) {
        this.dbDir = dbDir;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Boolean getPboxOpened() {
        return pboxOpened;
    }

    public void setPboxOpened(Boolean pboxOpened) {
        this.pboxOpened = pboxOpened;
    }

    public void showFileNodeTree() {
        setTabSelection(1);
    }

    public void showFileMemos() {
        setTabSelection(2);
    }

    private void showFileSearchMenu(View v){
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_file_search, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.btn_select_pbox:
                        fileSearchFragment.openFilePicker();
                        break;
                    case R.id.btn_create_pbox:
                        fileSearchFragment.openFilePicker_createPBox();
                        break;
                    case R.id.btn_search_pbox:
                        fileSearchFragment.searchLocalFile();
                        break;
                    case R.id.btn_search_pbox_clear:
                        fileSearchFragment.searchLocalFile_Clear();
                        break;
                }
                return false;
            }
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    private void showFileNodeMenu(View v){
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_file_node, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.btn_file_addrootfnode:
                    fileNodeFragment.addRootNode();
                    break;
            }
            return false;
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }
    private void showFileMemoMenu(View v){
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_file_memo, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.btn_take_picture:
                    fileMemoFragment.onPhoto();
                    break;
                case R.id.btn_record_sound:
                    RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
                    rxPermissions.request(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    fileMemoFragment.onRecordSound();
                                } else {
                                    // At least one permission is denied
                                }
                            });

                    break;
                case R.id.btn_record_video:
                    fileMemoFragment.onRecordVideo();
                    break;
                case R.id.btn_file_add:
                    fileMemoFragment.addFiles();
                    break;
                case R.id.btn_clear_ptmp:
                    fileMemoFragment.clearPtmp();
                    break;
            }
            return false;
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }


    private void showMySelfMenu(View v){
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_file_myself, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.btn_myself_history_clear:
                        fileSearchFragment.clearAllTmpDir();
                        break;
                    case R.id.btn_myself_lock_setting:
                        MainActivity.this.startActivityForResult(new Intent("cn.dcx.privatebox.ui.LockScreenActivity"), REQUESTCODE_LOCKSCREEN_SETTING);;
                        break;
                }
                return false;
            }
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    /**
     * 根据传入的index参数来设置选中的tab页。 * * @param index 每个tab页对应的下标。0表示主页，1表示导航，2表示更多
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了主页tab时，改变控件的图片和文字颜色
                img_file_search_zone.setImageResource(R.drawable.file_search_zone_select);
                txt_file_search_zone.setTextColor(Color.parseColor("#0079FF"));
                if (fileSearchFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fileSearchFragment = new FileSearchFragment();
                    transaction.add(R.id.content, fileSearchFragment);
                } else {
                    // 如果fileSearchFragment不为空，则直接将它显示出来
                    transaction.show(fileSearchFragment);
                }
                break;
            case 1: // 当点击了导航tab时，改变控件的图片和文字颜色
                img_file_node_zone.setImageResource(R.drawable.file_node_zone_select);
                txt_file_node_zone.setTextColor(Color.BLUE);
                if (fileNodeFragment == null) {
                    // 如果navigationFragment为空，则创建一个并添加到界面上
                    fileNodeFragment = new FileNodeFragment();
                    transaction.add(R.id.content, fileNodeFragment);
                } else {
                    // 如果navigationFragment不为空，则直接将它显示出来
                    transaction.show(fileNodeFragment);
                }
                if(dbDir!=null && dbName!=null){
                    if(fileNodeFragment.isAdded()){
                        fileNodeFragment.showFileNodeTree();
                    }else{
                        fileNodeFragment.setOnViewCreatedCallBack(new OnViewCreatedCallBack(){
                            @Override
                            public void execute(){
                                fileNodeFragment.showFileNodeTree();
                            }
                        });
                    }
                }
                break;
            case 2:
                // 当点击了更多tab时，改变控件的图片和文字颜色
                img_file_memo_zone.setImageResource(R.drawable.file_memo_zone_select);
                txt_file_memo_zone.setTextColor(Color.BLUE);
                if (fileMemoFragment == null) {
                    // 如果moreFragment为空，则创建一个并添加到界面上
                    fileMemoFragment = new FileMemoFragment();
                    transaction.add(R.id.content, fileMemoFragment);
                } else {
                    // 如果moreFragment不为空，则直接将它显示出来
                    transaction.show(fileMemoFragment);
                }
                if(fileNodeID != null){
                    if(fileMemoFragment.isAdded()){
                        fileMemoFragment.showFileMemos(fileNodeID);
                    }else {
                        fileMemoFragment.setOnViewCreatedCallBack(new OnViewCreatedCallBack(){
                            @Override
                            public void execute(){
                                fileMemoFragment.showFileMemos(fileNodeID);
                            }
                        });
                    }
                }
                break;
        }
        transaction.commit();
    }


    /***
     * 将所有的Fragment都置为隐藏状态。
     *  @param transaction 用于对Fragment执行操作的事务
     * */
    private void hideFragments(FragmentTransaction transaction) {
        if (fileSearchFragment != null) {
            transaction.hide(fileSearchFragment);
        }
        if (fileNodeFragment != null) {
            transaction.hide(fileNodeFragment);
        }
        if (fileMemoFragment != null) {
            transaction.hide(fileMemoFragment);
        }
    }

    /***
     * 清除掉所有的选中状态。
     * */
    private void clearSelection() {
        img_file_search_zone.setImageResource(R.drawable.file_search_zone_normal);
        txt_file_search_zone.setTextColor(Color.parseColor("#929598"));
        img_file_node_zone.setImageResource(R.drawable.file_node_zone_normal);
        txt_file_node_zone.setTextColor(Color.parseColor("#929598"));
        img_file_memo_zone.setImageResource(R.drawable.file_memo_zone_normal);
        txt_file_memo_zone.setTextColor(Color.parseColor("#929598"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public void onBackPressed() {
        if(mToast == null){
            mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        }
       if(mToast.getView().getParent() == null){
            mToast.setText("再次点击返回退出应用");
            mToast.show();
        }else{
            super.onBackPressed();
        }
    }
}

