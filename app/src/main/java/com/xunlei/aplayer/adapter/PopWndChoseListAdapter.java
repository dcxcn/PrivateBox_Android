package com.xunlei.aplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xunlei.aplayer.model.PopWndOptionsCategory;
import cn.dcx.privatebox.R;
import com.xunlei.aplayer.model.PopWndOptionsIteamParam;

import java.util.List;

/**
 * Created by admin on 2016/7/14.
 */
public class PopWndChoseListAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private Context mContext = null;
    private PopWndOptionsCategory mPopWndOptionsCategory  = null;

    public PopWndChoseListAdapter(Context context, PopWndOptionsCategory popWndOptionsCategory) {
        super();

        mContext = context;
        mPopWndOptionsCategory = popWndOptionsCategory;
        layoutInflater = LayoutInflater.from(context);
    }

    public final class Components
    {
        public TextView mIteamTextView;
        public ImageView mImageView;
    }

    @Override
    public int getCount() {
        List<PopWndOptionsIteamParam> iteams = mPopWndOptionsCategory.getmIteamList();
        return (null != iteams) ? iteams.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        List<PopWndOptionsIteamParam> iteams = mPopWndOptionsCategory.getmIteamList();
        if(null == iteams || iteams.size() <= i)
        {
            return null;
        }

        return iteams.get(i);
    }

    @Override
    public long getItemId(int postion) {
        return postion;
    }

    @Override
    public View getView(int postion, View view, ViewGroup viewGroup) {

        Components components = null;
        if(null == view)
        {
            components = new Components();
            view =  layoutInflater.inflate(R.layout.popwnd_list_iteam, null);
            components.mIteamTextView = (TextView) view.findViewById(R.id.popwnd_iteam_text);
            components.mImageView = (ImageView)view.findViewById(R.id.popwnd_iteam_image);
            view.setTag(components);
        }
        else
        {
            components = (Components)view.getTag();
        }

        PopWndOptionsIteamParam popWndOptionsIteamParam = mPopWndOptionsCategory.getmIteamList().get(postion);

        boolean isCanSelected = popWndOptionsIteamParam.isCanSelected();
        boolean isSelected = popWndOptionsIteamParam.isSelected();
        String title = popWndOptionsIteamParam.getTitle();


        int backColor = isCanSelected ?
                mContext.getResources().getColor(R.color.pop_windwon_select_disable_back_ground) :
                mContext.getResources().getColor(R.color.pop_windwon_select_enable_back_ground);
        int textColor = isSelected ? Color.WHITE : Color.GRAY;
        int imageVisable = isSelected ? View.VISIBLE : View.GONE;

        components.mImageView.setVisibility(imageVisable);
        view.setBackgroundColor(backColor);
        components.mIteamTextView.setTextColor(textColor);
        components.mIteamTextView.setText(title);

        return view;
    }

    //实现自定义单击相应事件（如果选中，对勾符号可见）
    public static class IteamListener implements AdapterView.OnItemClickListener
    {
        private PopWndOptionsCategory mPopWndOptionsCategory = null;
        private IteamClickListener mListener = null;

        public interface IteamClickListener {
            public void onItemClick(int pos, String strIteamTitle);
        }

        public IteamListener(PopWndOptionsCategory popWndOptionsCategory, IteamClickListener listener) {
            mPopWndOptionsCategory = popWndOptionsCategory;
            mListener = listener;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(!mPopWndOptionsCategory.getmIteamList().get(i).isCanSelected())
            {
                return;
            }

            boolean isDataChang = false;
            for (int pos = 0; pos < mPopWndOptionsCategory.getmIteamList().size(); ++pos)
            {
                PopWndOptionsIteamParam currentIteam = mPopWndOptionsCategory.getmIteamList().get(pos);
                boolean iteamStation = currentIteam.isSelected();
                boolean isSelected = (pos == i);

                if(isSelected != iteamStation)
                {
                    currentIteam.setIsSelected(isSelected);
                    isDataChang = true;
                }
            }

            if(isDataChang)
            {
                PopWndChoseListAdapter adapter = (PopWndChoseListAdapter)adapterView.getAdapter();
                adapter.notifyDataSetChanged();

                if(null != mListener)
                {
                    String choseIteamTitle = mPopWndOptionsCategory.getmIteamList().get(i).getTitle();
                    mListener.onItemClick(i, choseIteamTitle);
                }
            }
        }
    }
}
