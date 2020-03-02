package com.example.smartouwei.view;

import java.util.ArrayList;
import java.util.List;
import com.example.smartbathroom.R;
import com.example.smartouwei.adapter.MenuSelectAdapter;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AddMenuWindowDialog extends  Dialog implements View.OnClickListener {
	private Context context;
	private PeriodListener periodListener;
	private List<String> listMenu = new ArrayList<String>();
	private ListView m_lvMenu;
	private MenuSelectAdapter adapter;
	public AddMenuWindowDialog(Context context) {
		super(context);
		this.context = context;
	}

	public AddMenuWindowDialog(Context context, int theme,List<String> listMenu) {
		super(context, theme);
		this.context = context;
		this.listMenu = listMenu;

	}

	public interface PeriodListener {
		public void refreshListener(int number);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.window_add_menu);

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		//dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM;
		dialogWindow.setAttributes(lp);

		initUI();

	}

	public void setListener(PeriodListener listener) {
		this.periodListener = listener;
	}

	private void initUI() {

		adapter = new MenuSelectAdapter(listMenu, context);
		m_lvMenu = (ListView) findViewById(R.id.lvMenu);
		m_lvMenu.setAdapter(adapter);
		setPullLvHeight(m_lvMenu);
		m_lvMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				periodListener.refreshListener(position);
				dismiss();
			}
		});

		findViewById(R.id.tvConfirm).setOnClickListener(this);

	}

	private void setPullLvHeight(ListView pull){
		int totalHeight = 0;
		ListAdapter adapter= pull.getAdapter();
		for (int i = 0, len = adapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, pull);
			listItem.measure(0, 0); //计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = pull.getLayoutParams();
		params.height = totalHeight + (pull.getDividerHeight() * (pull.getCount() - 1));
		pull.setLayoutParams(params);
	}

	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.tvConfirm:
				dismiss();
				break;

			default:
				break;
		}
	}


}