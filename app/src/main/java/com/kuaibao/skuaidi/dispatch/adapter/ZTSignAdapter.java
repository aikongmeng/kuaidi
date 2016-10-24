package com.kuaibao.skuaidi.dispatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dispatch.bean.ZTSignType;
import com.kuaibao.skuaidi.dispatch.view.SignTypeChoiceView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by wang on 2016/10/8.
 */

public class ZTSignAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private LayoutInflater inflater;
    private List<SignType> dataList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private Context context;

    public ZTSignAdapter(Context context, List<ZTSignType> signTypeList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        for (ZTSignType signType : signTypeList) {
            titleList.add(signType.getTitle());
            for (String type : signType.getSignType()) {
                SignType signType1 = new SignType(signType.getTitle(), type);
                dataList.add(signType1);
            }
        }
    }

    public List<String> getTitleList() {
        return titleList;
    }


    public List<String> getSignTypes() {
        List<String> list = new ArrayList<>();
        for (SignType signType : dataList) {
            list.add(signType.type);
        }
        return list;
    }

    public List<SignType> getDataList() {
        return dataList;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.header_view_sign_type_zt, parent, false);
            viewHolder = new HeaderViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (HeaderViewHolder) convertView.getTag();
        }
        viewHolder.tvParentName.setText(dataList.get(position).title);
        return convertView;

    }

    @Override
    public long getHeaderId(int position) {
        return titleList.indexOf(dataList.get(position).title);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SignTypeChoiceView choiceView;
        if (convertView == null) {
            choiceView = new SignTypeChoiceView(context);
        } else {
            choiceView = (SignTypeChoiceView) convertView;
        }
        choiceView.setText(dataList.get(position).type);
        return choiceView;
    }


    static class ViewHolder {
        @BindView(R.id.ctv_signType)
        CheckBox ctvSignType;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class HeaderViewHolder {
        @BindView(R.id.tv_parent_name)
        TextView tvParentName;
        @BindView(R.id.cb_check)
        CheckBox cbCheck;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public class SignType {
        private String title;
        private String type;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        SignType(String title, String type) {
            this.title = title;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SignType signType = (SignType) o;

            if (!title.equals(signType.title)) return false;
            return type.equals(signType.type);

        }

        @Override
        public int hashCode() {
            int result = title.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
