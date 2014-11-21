package com.etrust.stategrid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;

public class LoginActivity extends Activity implements OnClickListener {
	private EditText username_editText;
	private EditText password_editText;
	private DatabasesTransaction db;
	private App app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		db = DatabasesTransaction.getInstance(this);
		app = (App) getApplication();

		setupView();
	}

	private void setupView() {
		this.findViewById(R.id.next_step_button).setOnClickListener(this);
		username_editText = (EditText) findViewById(R.id.username_editText);
		password_editText = (EditText) findViewById(R.id.password_editText);
	}

	private void doLogin() {
		String uname = username_editText.getEditableText().toString();
		String pword = password_editText.getEditableText().toString();
		if (uname.isEmpty()) {
			Toast.makeText(this, "帐号不能为空", Toast.LENGTH_LONG).show();
			return;
		} else if (pword.isEmpty()) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
			return;
		}

		String loginSql = "SELECT * FROM " + Constant.T_USER
				+ " WHERE userid = '" + uname + "' AND password = '" + pword
				+ "'";
		Cursor cursor = db.selectSql(loginSql);
		if (cursor.moveToNext()) {//循环查询数据表中数据，进行匹配
			String userid = cursor.getString(cursor
					.getColumnIndexOrThrow("userid"));
			String username = cursor.getString(cursor
					.getColumnIndexOrThrow("username"));
			String password = cursor.getString(cursor
					.getColumnIndexOrThrow("password"));
			String Deptname = cursor.getString(cursor
					.getColumnIndexOrThrow("Deptname"));
			String task = cursor
					.getString(cursor.getColumnIndexOrThrow("task"));
			UserBean u = new UserBean();
			u.userid = userid;
			u.username = username;
			u.password = password;
			u.Deptname = Deptname;
			u.task = task;
			app.setCurrentUser(u);
			Intent it = new Intent(LoginActivity.this, TabMainActivity.class);
			it.putExtra("userBean", u);
			startActivity(it);
			finish();
		} else {
			Toast.makeText(this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_step_button:
			doLogin();
			break;
		}
	}
}
