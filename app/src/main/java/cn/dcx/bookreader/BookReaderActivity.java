package cn.dcx.bookreader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.dcx.privatebox.base.BaseActivity;
import cn.dcx.bookreader.bean.Book;
import cn.dcx.bookreader.common.SPHelper;
import cn.dcx.bookreader.common.Util;
import cn.dcx.bookreader.db.DBHelper;
import cn.dcx.bookreader.page.PageActivity;
import cn.dcx.bookreader.page.PageInfo;
import cn.dcx.privatebox.R;


public class BookReaderActivity extends BaseActivity {
    private static final int RESTART_REQUEST = 123;
    private Toast mToast;
    private BookListAdapter mAdapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(SPHelper.getInstance().isNightMode() ? R.style.AppNightTheme : R.style.AppDayTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reader);
        initializeView();
        String filePath = this.getIntent().getStringExtra("filePath");
        if(filePath!=null && !"".equals(filePath)){
            List<File> files  = new ArrayList<>();
            files.add(new File(filePath));
            mAdapter.addBookFromFile(BookReaderActivity.this,files);
        }
    }


    private void initializeView(){
        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new BookListAdapter(this);

        mAdapter.setOnClickCallback(new BookListAdapter.ClickCallback() {
            @Override
            public void onClick(final Book book) {
                final PageInfo info = new PageInfo(book);
                info.prepare(new PageInfo.ReadCallback() {
                    @Override
                    public void onStart() {
                        Util.createProgressDialog(BookReaderActivity.this,"读取中...");
                    }

                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(BookReaderActivity.this,PageActivity.class);
                       /* if(book.getEncoding() == null){
                            book.setEncoding(Util.getEncoding(book));
                            DBHelper.getInstance().updateBook(book);
                        }*/
                        intent.putExtra(PageInfo.PAGE_INFO,info);
                        startActivityForResult(intent,RESTART_REQUEST);
                    }

                    @Override
                    public void onBookInvalid() {

                    }
                });

            }
            @Override
            public void onLongClick() {
                //turnIntoMoveMode(true);
            }
        });
        recyclerView.setAdapter(mAdapter);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_holo_dark_no_trim_no_padding);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_management:
                mAdapter.showDeleteButton(!mAdapter.isDeleteButtonVisible());
                break;
            case R.id.main_menu_delete_all:
                showDeleteAllDialog();
                 break;
            case R.id.main_menu_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:837578856@qq.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"阅读器建议/问题反馈");
                startActivity(emailIntent);
                break;
        }

        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RESTART_REQUEST && resultCode == Activity.RESULT_OK){
            startActivity(new Intent(this,BookReaderActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(mToast == null){
            mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        }
        if (mAdapter.isDeleteButtonVisible()){
            mAdapter.showDeleteButton(false);
        }else{
            super.onBackPressed();
        }
    }
    private void showDeleteAllDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppDialogTheme);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAdapter.clearData();
                DBHelper.getInstance().clearAllData();
                SPHelper.getInstance().clearAllBookMarkData();
                Util.makeToast("已删除");
            }
        });
        builder.setMessage("确认删除全部书籍？");
        builder.setNegativeButton("取消",null);
        builder.show();
    }
}
