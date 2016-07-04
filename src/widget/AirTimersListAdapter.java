package widget;

import java.util.ArrayList;
import java.util.List;

import com.wicare.wistormpublicdemo.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewHolder;		
		if(convertView == null){
			view = LayoutInflater.from(mContext).inflate(R.layout.item_list_timers, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_time   = (TextView)view.findViewById(R.id.tv_time);
			viewHolder.sw_timers = (Switch)view.findViewById(R.id.sw_timers);
			viewHolder.sw_timers.setOnCheckedChangeListener(viewHolder);
			viewHolder.iv_delete = (ImageView)view.findViewById(R.id.iv_delete_timer);
			viewHolder.iv_delete.setOnClickListener(viewHolder);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder =(ViewHolder)view.getTag();
		}
		viewHolder.tv_time.setText(listTimers.get(position));
		viewHolder.setPostion(position);
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
	class ViewHolder implements OnClickListener,OnCheckedChangeListener{
		TextView  tv_time;
		Switch    sw_timers;
		ImageView iv_delete;
		
		int position;
		
		public void setPostion(int position){
			this.position = position;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onDeleteTimerPosition(position);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked){
				listTimersEnable.add(listTimers.get(position));
				Log.i("AirSettingActivity", "点击的位置：" + position);
			}else{
				if(listTimersEnable.size() > position){
					listTimersEnable.remove(position);
				}else{
					listTimersEnable.add("00:00");
				}
				
				Log.i("AirSettingActivity", "点击的位置：" + position);
			}
		}
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
	
	
	List<String> listTimersEnable = new ArrayList<String>();
	public List<String> getListTimersEnable() {
		return listTimersEnable;
	}


	public void setListTimersEnable(String listTimersEnable) {
		this.listTimersEnable.add(listTimersEnable);
	}
	
}
