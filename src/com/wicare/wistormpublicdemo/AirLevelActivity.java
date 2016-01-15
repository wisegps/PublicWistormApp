package com.wicare.wistormpublicdemo;

import java.util.ArrayList;
import java.util.List;

import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class AirLevelActivity extends Activity {
	
	
	/*	折线   */
	private LineChartView lineChart;
	String[] weeks = {"周一","周二","周三","周四","周五","周六","周日"};//X轴的标注
	int[] weather = {0,690,32,986,5000,3000,2000};//图表的数据
	String[] yList = {"0","200","600","1000"};
	private List<PointValue> mPointValues = new ArrayList<PointValue>();
	private List<AxisValue> mAxisValues = new ArrayList<AxisValue>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_air_level);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		initView();
		getLineChartAxisLables();//获取x轴的标注
        getLineChartAxisPoints();//获取坐标点
        initLineChart();
	}
	
	
	
	private void initView(){
		findViewById(R.id.iv_air_down).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						AirLevelActivity.this.finish();
					}
				});
		FrameLayout layout = (FrameLayout) findViewById(R.id.flytAirChat);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lineChart = new LineChartView(this);
		layout.addView(lineChart,layoutParams);
	}
	
	
	   /**
  * 初始化LineChart的一些设置
  */
 private void initLineChart(){
        Line line = new Line(mPointValues).setColor(Color.WHITE).setCubic(false);  //折线的颜色
	    List<Line> lines = new ArrayList<Line>();    
	    line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
	    line.setCubic(true);//曲线是否平滑
		line.setFilled(false);//是否填充曲线的面积
		line.setHasLabels(true);//曲线的数据坐标是否加上备注
//		line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
		line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示	
		line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示	
		
	    lines.add(line);  
	    LineChartData data = new LineChartData();  
	    data.setLines(lines);  
	     
	    lineChart.setLineChartData(data);  
	    lineChart.setVisibility(View.VISIBLE);  
 }
 
	 /**
	  * X 轴的显示
	  */
	 private void getLineChartAxisLables(){
	     for (int i = 0; i < weeks.length; i++) {    
	     	mAxisValues.add(new AxisValue(i).setLabel(weeks[i]));    
	     }    	
	 }
	 
	
	
	 /**
	  * 图表的每个点的显示
	  */
	 private void getLineChartAxisPoints(){
	     for (int i = 0; i < weather.length; i++) {    
	     	mPointValues.add(new PointValue(i, weather[i]));      
	     }    	
	 }
 
	 
	 @Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			ActivityCollector.removeActivity(this);
		}
	
}
