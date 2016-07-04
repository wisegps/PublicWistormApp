package widget;

import java.util.List;

import com.wicare.wistormpublicdemo.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author Wu
 * 底部弹窗
 */
public class CustomerPopupWindow extends PopupWindow {
	
	private Context mContext;
	private ListView lv_pop; //listview popupwindow上面的按键
	private OnItemClickListener onItemClickListener;//按键监听的接口
	private PopupWindow mPopupWindow;
	
	public CustomerPopupWindow(Context context){
		mContext = context;
	}
	
	/**
	 * @param v  A parent view to get the android.view.View.getWindowToken() token from
	 */
	public void initView(View v,int width,int height){
		LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View popunwindwow = mLayoutInflater.inflate(R.layout.customer_popupwindow,null);
        mPopupWindow = new PopupWindow(popunwindwow, width, height);
        //mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.setAnimationStyle(R.style.ws_anim_menu_bottom_bar);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        lv_pop = (ListView)popunwindwow.findViewById(R.id.lv_pop);  
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半  
        int xPos = v.getWidth() / 2 - mPopupWindow.getWidth() / 2;   
        mPopupWindow.showAsDropDown(v, xPos, 0);    //P偏移的位置要看传入的 View 为参考 
	}
	
	
	/**
	 * 隐藏pupupwindow 
	 */
	public void dismiss(){
        mPopupWindow.dismiss();
	}
	
	/**
	 * @param items pupupwindow的按键名称（items的长度就是按键的个数）
	 */
	public void setData(List<String> items){
		lv_pop.setAdapter(new ItemAdapter(items));
	}
	
	/**
	 * @author Wu
	 * popupwindow 按键的点击事件的接口
	 */
	public interface OnItemClickListener{
		public abstract void OnItemClick(int index);
	}
	
	/**
	 * @param onItemClickListener popupwindow 按键点击事件监听
	 */
	public void SetOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener = onItemClickListener;
	}
	
	/**
	 * @author Wu
	 * popupwindow 的按键适配器
	 */
	class ItemAdapter extends BaseAdapter{
		private LayoutInflater layoutInflater;
		List<String> datas;
		public ItemAdapter(List<String> items){
			layoutInflater = LayoutInflater.from(mContext);
			datas = items;
		}
		@Override
		public int getCount() {
			return datas.size();
		}
		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.item_list_popupwindow, null);
	            holder = new ViewHolder();
	            holder.tv_item_pop = (TextView) convertView.findViewById(R.id.bt_item_pop);
	            convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_item_pop.setText(datas.get(position));
			holder.tv_item_pop.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(onItemClickListener != null){
						onItemClickListener.OnItemClick(position);
					}
				}
			});
			return convertView;
		}
		private class ViewHolder {
	        TextView tv_item_pop;
	    }
	}
}
