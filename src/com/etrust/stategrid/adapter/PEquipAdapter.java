package com.etrust.stategrid.adapter;
import java.util.List;

import com.etrust.stategrid.R;
import com.etrust.stategrid.bean.PlanEquip;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Map;
import java.util.HashMap;
public class PEquipAdapter extends BaseAdapter{
	private List<Map<String,String>> data;
	private Context context;
	private LayoutInflater inflater;
	public Map<Integer,PlanEquipHolder> holderMap;
	public HashMap<Integer, View> mView ;
	public PEquipAdapter(Context context,List<Map<String,String>> data){
		this.context=context;
		this.data=data;
		this.inflater = LayoutInflater.from(context);
		holderMap=new HashMap<Integer,PlanEquipHolder>();
		mView=new HashMap<Integer,View>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		//View view=mView.get(position);
		PlanEquipHolder holder=null;
		if(view==null){
			view = inflater.inflate(R.layout.item_plan_equip,null);
			holder=new PlanEquipHolder();
			holder.title=(TextView)view.findViewById(R.id.tv_item_plan_equip);
			holder.checkBox=(CheckBox)view.findViewById(R.id.cb_item_plan_equip);
			holder.content=(EditText)view.findViewById(R.id.et_item_plan_equip);
			holderMap.put(position, holder);
			mView.put(position, view);
			view.setTag(holder);
		}else{
			holder=(PlanEquipHolder)view.getTag();
		}
		holder.content.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if (!hasFocus) {
					Map<String,String> itemData=data.get(position);
					itemData.put("content", ((EditText)v).getText().toString().trim());
				}
			}
		});
		holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					data.get(position).put("checked", "1");
				}else{
					data.get(position).put("checked", "0");
				}
			}
		});
		holder.title.setText(data.get(position).get("title"));
		holder.content.setText(data.get(position).get("content"));
		if("1".equals(data.get(position).get("checked"))){
			holder.checkBox.setChecked(true);
		}else{
			holder.checkBox.setChecked(false);
		}
		return view;
	}
}
