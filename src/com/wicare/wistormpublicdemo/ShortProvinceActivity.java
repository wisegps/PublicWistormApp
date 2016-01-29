package com.wicare.wistormpublicdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Wu
 * 
 * 省份间写页面
 */
public class ShortProvinceActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_short_provice);
		
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("车辆地区选择");
		
		GridView gv_provices = (GridView) findViewById(R.id.gv_provices);
		gv_provices.setAdapter(new ProvincesAdapter());
		gv_provices.setOnItemClickListener(onItemClickListener);
		
		
	}
	
	
	
	/**
	 * 点击事件监听
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.iv_top_back://返回
				finish();
				break;
			}
		}
	};
	
	/**
	 * GridView 适配器点击事件
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String province = provinces[arg2];
			if(province.equals("")){
				
			}else{
				Intent intent = new Intent();
				intent.putExtra("province", province);
				ShortProvinceActivity.this.setResult(2, intent);
				ShortProvinceActivity.this.finish();
			}
		}
	};
	
	private String[] provinces = { "京", "津", "沪", "渝", "冀", "豫", "云", "辽", "黑",
			"湘", "皖", "鲁", "新", "苏", "浙", "赣", "鄂", "桂", "甘", "晋", "蒙", "陕",
			"吉", "闽", "贵", "粤", "青", "藏", "川", "宁", "琼", "" };

	/**
	 * @author Wu
	 * 
	 * GridView 适配器
	 *
	 */
	class ProvincesAdapter extends BaseAdapter {
		LayoutInflater inflater = LayoutInflater
				.from(ShortProvinceActivity.this);

		@Override
		public int getCount() {
			return provinces.length;
		}

		@Override
		public Object getItem(int position) {
			return provinces[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_short_province,
						null);
				holder = new ViewHolder();
				holder.tv_province = (TextView) convertView
						.findViewById(R.id.tv_province);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_province.setBackgroundColor(getResources().getColor(
					R.color.white));
			holder.tv_province.setText(provinces[position]);
			return convertView;
		}

		class ViewHolder {
			TextView tv_province;
		}
	}
	

}
