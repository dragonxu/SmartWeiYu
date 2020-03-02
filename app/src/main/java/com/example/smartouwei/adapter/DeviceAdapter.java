package com.example.smartouwei.adapter;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import com.ant.liao.GifView;
import com.example.smartbathroom.R;
import com.gisicisky.smasterFitment.data.DataAnalysisHelper;
import com.gisicisky.smasterFitment.data.DeviceInfoCache;
import com.gisicisky.smasterFitment.data.DeviceState;
import com.gisicisky.smasterFitment.data.GroupInfoCache;
import com.gisicisky.smasterFitment.data.UserInfoCache;
import com.gisicisky.smasterFitment.utl.BaseVolume;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {

    List<DeviceInfoCache> list;
    DeviceInfoCache deviceInfo;
    Context con;
    ViewHolder holder;
    int number = 0;
    private DeviceState nowDeviceState;

    public DeviceAdapter(List<DeviceInfoCache> li, Context con) {
        this.list = li;
        this.con = con;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void updateList(List<DeviceInfoCache> li) {
        this.list = li;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(con).inflate(R.layout.item_device, null);
            holder = new ViewHolder();
            holder.m_imgICON = (ImageView) convertView.findViewById(R.id.imgICON);
            holder.m_tvDeviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            holder.m_tvDeviceType = (TextView) convertView.findViewById(R.id.tvDeviceType);
            holder.m_tvState = (TextView) convertView.findViewById(R.id.tvState);
            holder.m_imgLock = (ImageView) convertView.findViewById(R.id.imgLock);
            holder.gif2 = (GifImageView) convertView.findViewById(R.id.gif2);
            holder.gif2.setBackgroundResource(R.drawable.img_wait_gif);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        deviceInfo = list.get(position);
        holder.m_tvDeviceName.setText(deviceInfo.getName());
        holder.m_tvDeviceType.setText("MAC:" + deviceInfo.getMac());


        // 未激活
        if (list.get(position).getOnLine() == BaseVolume.DEVICE_NOT_ACTIVE) {
            holder.m_imgLock.setVisibility(View.VISIBLE);
            holder.gif2.setVisibility(View.GONE);

//			holder.progressBar.setVisibility(View.GONE);
//			holder.m_tvState.setVisibility(View.GONE);
        }
        // 正在连接 或 密码校验
        else if (list.get(position).getOnLine() == BaseVolume.DEVICE_CONNECTING
                || list.get(position).getOnLine() == BaseVolume.DEVICE_CHECK_PWD) {
            holder.m_imgLock.setVisibility(View.GONE);
            holder.gif2.setVisibility(View.VISIBLE);
//			holder.progressBar.setVisibility(View.VISIBLE);
//			holder.m_tvState.setVisibility(View.GONE);
        }
        // 在线
        else if (list.get(position).getOnLine() == BaseVolume.DEVICE_ON_LINE) {
            holder.m_imgLock.setVisibility(View.GONE);
            holder.gif2.setVisibility(View.GONE);
//			holder.progressBar.setVisibility(View.GONE);
            holder.m_tvState.setVisibility(View.VISIBLE);
            holder.m_tvState.setText(con.getString(R.string.zai_xian));
            nowDeviceState = DataAnalysisHelper.getSelf(con).getDataBufferByMac(deviceInfo.getMac());
            if (nowDeviceState != null) {
                String type = nowDeviceState.getZhuJiType();
                if (type.equalsIgnoreCase("GB")) {
                    holder.m_imgICON.setImageResource(R.drawable.img_gb);

                } // 蒸房
                else if (type.equalsIgnoreCase("ZF")) {
                    holder.m_imgICON.setImageResource(R.drawable.img_sz);

                }// 浴缸
                else if (type.equalsIgnoreCase("YG")) {
                    holder.m_imgICON.setImageResource(R.drawable.img_yg);

                } else {
                    holder.m_imgICON.setImageResource(R.drawable.img_device_online);
                }
            } else {
                holder.m_imgICON.setImageResource(R.drawable.img_device_online);
            }
            holder.m_tvState.setTextColor(con.getResources().getColor(R.color.black1));
            holder.m_tvDeviceType.setTextColor(con.getResources().getColor(R.color.black1));
            holder.m_tvDeviceName.setTextColor(con.getResources().getColor(R.color.mo_lv));

        }
        // 离线
        else if (list.get(position).getOnLine() == BaseVolume.DEVICE_NOT_LINE) {
            holder.m_imgLock.setVisibility(View.GONE);
            holder.gif2.setVisibility(View.GONE);
//			holder.progressBar.setVisibility(View.GONE);
            holder.m_tvState.setVisibility(View.VISIBLE);
            holder.m_tvState.setText(con.getString(R.string.li_xian));

            holder.m_imgICON.setImageResource(R.drawable.img_device_offline);
            holder.m_tvState.setTextColor(con.getResources().getColor(R.color.mo_hui));
            holder.m_tvDeviceType.setTextColor(con.getResources().getColor(R.color.mo_hui));
            holder.m_tvDeviceName.setTextColor(con.getResources().getColor(R.color.mo_hui));
        }


        return convertView;
    }

    private class ViewHolder {
        private TextView m_tvDeviceName;
        private TextView m_tvDeviceType;
        private TextView m_tvState;

        private ImageView m_imgICON;
        private ImageView m_imgLock;
        private GifImageView gif2;

    }

}
