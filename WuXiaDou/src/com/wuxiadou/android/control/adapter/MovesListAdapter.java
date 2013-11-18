package com.wuxiadou.android.control.adapter;

import java.util.List;

import com.wuxiadou.android.R;
import com.wuxiadou.android.model.Moves;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MovesListAdapter extends BaseAdapter{
	Context context;
	List<Moves> list;
	public MovesListAdapter(Context context,List<Moves> list)
	{
		this.context=context;
		this.list=list;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("MovesListAdapter","execute getView()");
		ViewHolder holder = null;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.moves_list_item, null);
			holder=new ViewHolder();
			holder.moves_TV=(TextView)convertView.
					findViewById(R.id.item_moves_tv);
			convertView.setTag(holder);
		}else
		{
			holder=(ViewHolder)convertView.getTag();
		}
			initViewHolder(position, holder);
		return convertView;
	}
	private void initViewHolder(int position,ViewHolder holder)
	{	Moves moves=null;
		if(position<list.size()){
			moves=list.get(position);
		}
		if(moves==null||moves.getKey()==-1){
			String str="";
			switch(position){
			case 0:
				str="壹";
				break;
			case 1:
				str="贰";
				break;
			case 2:
				str="叁";
				break;
			case 3:
				str="肆";
				break;
			case 4:
				str="伍";
				break;
			}
			holder.moves_TV.setText(str);
		}else{
			Log.i("MovesListAdapter","initViewHolder:"+moves.getName());
			holder.moves_TV.setText(moves.getName());
		}
	}
	class ViewHolder
	{
		public TextView moves_TV;
	}
}
