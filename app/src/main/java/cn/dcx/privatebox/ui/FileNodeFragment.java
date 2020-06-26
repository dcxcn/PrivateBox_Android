package cn.dcx.privatebox.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.dcx.privatebox.R;
import cn.dcx.privatebox.adapter.FileNodeTreeListViewAdapter;
import cn.dcx.privatebox.bean.FileNode;
import cn.dcx.privatebox.database.FileNodeDBMExternal;
import cn.dcx.privatebox.ui.tree.Node;
import cn.dcx.privatebox.ui.tree.TreeListViewAdapter;

public class FileNodeFragment extends MainFragment {

    private ListView treeLv;
    //private Button checkSwitchBtn;
    public static final int REQUESTCODE_INPUTFILENODE_ADD_ROOT = 900;
    public static final int REQUESTCODE_INPUTFILENODE_ADD_COMMON = 901;
    public static final int REQUESTCODE_INPUTFILENODE_RENAME = 902;
    //标记是显示Checkbox还是隐藏
    private boolean isHide = true;
    private FileNodeTreeListViewAdapter<FileNode> adapter;
    private List<FileNode> mDatas = new ArrayList<FileNode>();
    FileNodeDBMExternal fileNodeDBMExternal = null;

    public FileNodeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.layout_filenode, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        treeLv = (ListView) view.findViewById(R.id.tree_lv);

//		checkSwitchBtn = (Button)view.findViewById(R.id.check_switch_btn);
//
//		checkSwitchBtn.setOnClickListener(new View.OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				if(isHide){
//					isHide = false;
//				}else{
//					isHide = true;
//				}
//
//				adapter.updateView(isHide);
//			}
//
//		});
        try {
            adapter = new FileNodeTreeListViewAdapter<FileNode>(treeLv, view.getContext(),
                    mDatas, 10, isHide);

            adapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    if (node.isLeaf()) {
                        Toast.makeText(getMainActivity().getApplicationContext(), node.getName(),
                                Toast.LENGTH_SHORT).show();
                        getMainActivity().setFileNodeID(node.getId());
                        getMainActivity().showFileMemos();
                    }
                }

                @Override
                public void onCheckChange(Node node, int position,
                                          List<Node> checkedNodes) {
                    // TODO Auto-generated method stub

                    StringBuffer sb = new StringBuffer();
                    for (Node n : checkedNodes) {
                        int pos = n.getId() - 1;
                        sb.append(mDatas.get(pos).getName()).append("---")
                                .append(pos + 1).append(";");

                    }

                    Toast.makeText(getMainActivity().getApplicationContext(), sb.toString(),
                            Toast.LENGTH_SHORT).show();
                }

            });

            adapter.setOnTreeNodeLongClickListener(new TreeListViewAdapter.OnTreeNodeLongClickListener() {
                @Override
                public void onLongClick(View view, Node node, int position) {
                    showFileNodeMenu(view, node);
                }
            });

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        treeLv.setAdapter(adapter);
        if (this.getOnViewCreatedCallBack() != null) {
            this.getOnViewCreatedCallBack().execute();
        }
    }

    public void initDBM() {
        String dbDir = this.getMainActivity().getDbDir();
        String dbName = this.getMainActivity().getDbName();
        fileNodeDBMExternal = new FileNodeDBMExternal(getMainActivity(), dbDir, dbName);
    }

    public void showFileNodeTree() {
        List<FileNode> al_fn = new ArrayList<>();
        if(this.getMainActivity().getPboxOpened()){
            initDBM();
            al_fn = fileNodeDBMExternal.getFileNodeList();

        }
        try {
            adapter.setTreeData(al_fn, 10, isHide);
            adapter.notifyDataSetChanged();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }


    private void showFileNodeMenu(View v, Node node) {
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(getMainActivity(), v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.menu_filenode_edit, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.btn_filenode_add:
                        addCommonNode(node);
                        break;
                    case R.id.btn_filenode_rename:
                        renameFileNode(node);
                        break;
                    case R.id.btn_filenode_del:
                        delFileNode(node);
                        break;
                }
                return false;
            }
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    public void addRootNode() {
        Intent intent = new Intent("cn.dcx.privatebox.ui.InputFileNodeNameActivity");
        startActivityForResult(intent, FileNodeFragment.REQUESTCODE_INPUTFILENODE_ADD_ROOT);
    }

    public void addCommonNode(Node node) {
        Intent intent = new Intent("cn.dcx.privatebox.ui.InputFileNodeNameActivity");
        intent.putExtra("fileNode_pid", node.getId());
        startActivityForResult(intent, FileNodeFragment.REQUESTCODE_INPUTFILENODE_ADD_COMMON);
    }

    public void delFileNode(Node node) {
        try {
            initDBM();
            fileNodeDBMExternal.delFileNode(node.getId());
            showFileNodeTree();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void renameFileNode(Node node) {
        Intent intent = new Intent("cn.dcx.privatebox.ui.InputFileNodeNameActivity");
        intent.putExtra("fileNode_id", node.getId());
        intent.putExtra("fileNode_name_old", node.getName());
        startActivityForResult(intent, FileNodeFragment.REQUESTCODE_INPUTFILENODE_RENAME);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_INPUTFILENODE_ADD_ROOT) {
            if (data == null) return;
            String fileNode_name = data.getStringExtra("fileNode_name");
            FileNode fileNode = new FileNode();
            fileNode.setName(fileNode_name);
            initDBM();
            fileNodeDBMExternal.addFileNode_Root(fileNode);
            showFileNodeTree();
        } else if (requestCode == REQUESTCODE_INPUTFILENODE_ADD_COMMON) {
            if (data == null) return;
            String fileNode_name = data.getStringExtra("fileNode_name");
            Integer fileNode_pid = data.getIntExtra("fileNode_pid", 0);
            FileNode fileNode = new FileNode();
            fileNode.setName(fileNode_name);
            fileNode.setPid(fileNode_pid);
            initDBM();
            fileNodeDBMExternal.addFileNode_Common(fileNode);
            showFileNodeTree();
        } else if (requestCode == REQUESTCODE_INPUTFILENODE_RENAME) {
            String fileNode_name = data.getStringExtra("fileNode_name");
            Integer fileNode_id = data.getIntExtra("fileNode_id", 1);
            FileNode fileNode = new FileNode();
            fileNode.setId(fileNode_id);
            fileNode.setName(fileNode_name);
            initDBM();
            fileNodeDBMExternal.updFileNode(fileNode);
            showFileNodeTree();
        }

    }

}
