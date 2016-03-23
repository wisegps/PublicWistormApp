package com.wicare.wistormpublicdemo.ui;

import java.util.ArrayList;
import java.util.List;

import com.wicare.wistormpublicdemo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * @author Wu
 *
 *	净化器定时器列表适配器
 */
public class AirTimersListAdapter extends BaseAdapter{

	private Context mContext;
	private List<String> listTimers = new ArrayList<String>();
	
	private OnDeleteTimerListener onDeleteTimerListener;
	
	
	public AirTimersListAdapter(Context context){
		this.mContext = context;
	}
	
	
	@Override
	public int getCount() {
		return listTimers.size();
	}

	@Override
	public Object getItem(int position) {
		return listTimers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = null;
		ViewHolder viewHolder;		
		if(convertView == null){
			view = LayoutInflater.from(mContext).inflate(R.layout.item_list_timers, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_time   = (TextView)view.findViewById(R.id.tv_time);
			viewHolder.sw_timers = (Switch)view.findViewById(R.id.sw_timers);
			viewHolder.iv_delete = (ImageView)view.findViewById(R.id.iv_delete_timer);
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onDeleteTimerPosition(position);
				}
			});
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder =(ViewHolder)view.getTag();
		}
		
		viewHolder.tv_time.setText(listTimers.get(position));

		return view;
	}
	
	
	
	/**
	 * @param list
	 */
	public void setDatas(List<String> list){
		this.listTimers= list;
	}
	
	/**
	 * @author Wu
	 * item_list UI控件
	 */
	class ViewHolder{
		TextView  tv_time;
		Switch    sw_timers;
		ImageView iv_delete;
	}	
	
	/**
	 * @author Wu
	 * 
	 * 删除定时器接口
	 */
	public interface OnDeleteTimerListener{
		void onDeleteTimer(int position);
	}

	/**
	 * @param listener
	 */
	public void setOnDeleteTimerListener(OnDeleteTimerListener listener){
		this.onDeleteTimerListener = listener;
	}
	
	private void onDeleteTimerPosition(int position){
		if(onDeleteTimerListener != null){
			onDeleteTimerListener.onDeleteTimer(position);
		}
	}
}
