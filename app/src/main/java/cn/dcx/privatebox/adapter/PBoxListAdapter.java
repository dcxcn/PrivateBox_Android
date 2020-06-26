package cn.dcx.privatebox.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import cn.dcx.bookreader.view.CustomLayout;
import cn.dcx.privatebox.R;
import cn.dcx.privatebox.bean.PBox;


public class PBoxListAdapter extends RecyclerView.Adapter<PBoxListAdapter.PBoxViewHolder> {
    private List<PBox> pboxs;
    private Context mContext;
    private ClickCallback mCallback;
    private List<CustomLayout> mChildren = new ArrayList<>();
    private boolean isDeleteButtonVisible;

    public PBoxListAdapter(Context context){
        pboxs = new ArrayList<>() ;
        mContext = context;
    }
    @Override
    public int getItemCount() {
        return pboxs.size();
    }

    @Override
    public void onBindViewHolder(PBoxViewHolder holder, int position) {
        PBox pbox = pboxs.get(position);
        holder.pbox_title.setText(pbox.getName());
        holder.pbox_path.setText(pbox.getPath());
    }

    @Override
    public PBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_filesearch_item,parent,false);
        return new PBoxViewHolder(view);
    }

    public boolean isDeleteButtonVisible(){
        return isDeleteButtonVisible;
    }
    public void setOnClickCallback(ClickCallback callback){
        mCallback = callback;
    }
    class PBoxViewHolder extends RecyclerView.ViewHolder{
        TextView pbox_title,pbox_path;
        CustomLayout root;
        PBoxViewHolder(View view){
            super(view);
            pbox_title = view.findViewById(R.id.pbox_item_title);
            pbox_path = view.findViewById(R.id.pbox_item_path);
            root = view.findViewById(R.id.pbox_item_root);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isDeleteButtonVisible()){
                        showDeleteButton(false);
                        return;
                    }
                    if(mCallback != null){
                        mCallback.onClick(v,pboxs.get(getLayoutPosition()));
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mCallback != null){
                        mCallback.onLongClick(v,pboxs.get(getLayoutPosition()));
                    }
                    return false;
                }
            });
            root.setOnDeleteButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            mChildren.add(root);
        }

    }
    public interface ClickCallback{
        void onClick(View v,PBox pbox);
        void onLongClick(View v,PBox pbox);
    }

    public void setPBoxs( List<PBox> al_pboxs){
        pboxs.clear();
        if(al_pboxs!=null){
            pboxs.addAll(al_pboxs);
        }
        notifyDataSetChanged();
    }
    public void addPBoxs( List<PBox> al_pboxs){
        pboxs.addAll(al_pboxs);
        notifyDataSetChanged();
    }
    public void showDeleteButton(boolean which){
        isDeleteButtonVisible = which;
        for (CustomLayout customLayout : mChildren){
            if(which){
                customLayout.showDeleteButton();
            }else{
                customLayout.hideDeleteButton();
            }

        }
    }

}
