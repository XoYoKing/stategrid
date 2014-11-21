package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.etrust.stategrid.adapter.FragmentsAdapter;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.view.LucienViewPager;

public class TabMainActivity extends FragmentActivity implements
		OnPageChangeListener, OnClickListener, OnCheckedChangeListener {
	private String TAG = TabMainActivity.class.getSimpleName();

	private RadioGroup radiodGroup;
	private LucienViewPager pager;
	//tab title
	private ImageView tab_title_icon;
	private TextView tab_title_text;
	private TextView tab_title_user;
	
	private UserBean user;
	private List<Fragment> views = new ArrayList<Fragment>();
	private FragmentPagerAdapter adapter;
	int[] firstUse=new int[]{0,0,0,0,0};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = (UserBean) getIntent().getSerializableExtra("userBean");
		setContentView(R.layout.activity_tabmain);
		setupView();
//		int id =getIntent().getIntExtra("tiaozhuan", 1);
//		if (id==1) {
//			onCheckedChanged(radiodGroup, 0);
//		}
	}

	private void setupView() {
		radiodGroup = (RadioGroup) findViewById(R.id.main_tab_group);
		radiodGroup.setOnCheckedChangeListener(this);

		views.clear();
		pager = (LucienViewPager) findViewById(R.id.pager);
		
		views.add(new TaskListFragment());
		views.add(new DataQueryFragment());//
		views.add(new DataManageFragment());//
		views.add(new TrajectoryManageFragment());//
		views.add(new SystemSetFragment());//

		adapter= new FragmentsAdapter(
				this.getSupportFragmentManager(), views, new String[] { "a",
						"b", "c", "d", "e" });

		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		
		//tab title
		tab_title_icon = (ImageView) findViewById(R.id.tab_title_icon);
		tab_title_text = (TextView) findViewById(R.id.tab_title_text);
		tab_title_user = (TextView) findViewById(R.id.tab_title_user);
		tab_title_user.setText(user.Deptname + " " + user.username);//会报空指针异常（我猜想是因为重新跳转后没有经过输入账号密码这一级造成的）
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.tab_task_manage:
			pager.setCurrentItem(0);
			tab_title_icon.setImageResource(R.drawable.task_lab_icon);
			tab_title_text.setText(R.string.tab_title_task);
			break;
		case R.id.tab_data_query:
			pager.setCurrentItem(1);
			tab_title_icon.setImageResource(R.drawable.task_lab_icon);
			tab_title_text.setText(R.string.tab_title_query);
			break;
		case R.id.tab_data_manage:
			pager.setCurrentItem(2);
			tab_title_icon.setImageResource(R.drawable.task_lab_icon);
			tab_title_text.setText(R.string.tab_title_data);
			break;
		case R.id.tab_trajectory_manage:
			pager.setCurrentItem(3);
			tab_title_icon.setImageResource(R.drawable.task_lab_icon);
			tab_title_text.setText(R.string.tab_title_traject);			
			break;
		case R.id.tab_system_set:
			pager.setCurrentItem(4);
			tab_title_icon.setImageResource(R.drawable.sys_lab_icon);
			tab_title_text.setText(R.string.tab_title_sys);
			break;
		}
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}
	
	
}
