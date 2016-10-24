package com.kuaibao.skuaidi.readidcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.yunmai.android.vo.IDCard;

public class IDCardInfoReaderActivity extends RxRetrofitBaseActivity implements
		View.OnClickListener {
	private final String TAG = "IDCardInfoReaderActivity";
	private Button btn_getphoto;
	private EditText edit_address;
	private EditText edit_create;
	private EditText edit_date;
	private EditText edit_id;
	private EditText edit_limit_date_end;
	private EditText edit_limit_date_start;
	private EditText edit_name;
	private EditText edit_sex;
	private EditText edit_type;
	private IDCard idCard;
	private boolean isReadAll = false;
	private TextView tv_title_des;
	private Button bt_title_more;
	private static final int REQ_CODE_CAPTURE = 100;
	private ImageView iv_title_back;
	private boolean isInfoNull = true;

	protected void onBack(View v) {

	}

	private void init() {
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_title_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBack(arg0);
				finish();
			}
		});

		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("身份证信息预览");
		bt_title_more=(Button) findViewById(R.id.bt_title_more);
		bt_title_more.setText("拍照");
		bt_title_more.setVisibility(View.VISIBLE);
		bt_title_more.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goToPreViewActivity();
			}
		});

		this.edit_name = ((EditText) findViewById(R.id.edit_name));
		this.edit_id = ((EditText) findViewById(R.id.edit_id));
		this.edit_sex = ((EditText) findViewById(R.id.edit_sex));
		this.edit_type = ((EditText) findViewById(R.id.edit_type));
		this.edit_date = ((EditText) findViewById(R.id.edit_date));
		this.edit_address = ((EditText) findViewById(R.id.edit_address));
		this.edit_create = ((EditText) findViewById(R.id.edit_create));
		this.edit_limit_date_start = ((EditText) findViewById(R.id.edit_limit_date_start));
		this.edit_limit_date_end = ((EditText) findViewById(R.id.edit_limit_date_end));
		this.btn_getphoto = ((Button) findViewById(R.id.btn_get_idcard_photo));
		this.btn_getphoto.setOnClickListener(this);
		if (this.isReadAll) {
//			this.btn_getphoto.setText("开始身份证识别(正面/反面))");
			findViewById(R.id.layout_create).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_limit_date_end)
					.setVisibility(View.VISIBLE);
			findViewById(R.id.layout_limit_date_start).setVisibility(
					View.VISIBLE);
			return;
		}
//		this.btn_getphoto.setText("开始身份证识别");
		findViewById(R.id.layout_create).setVisibility(View.GONE);
		findViewById(R.id.layout_limit_date_end).setVisibility(View.GONE);
		findViewById(R.id.layout_limit_date_start).setVisibility(View.GONE);
	}

	private void goToPreViewActivity() {
		Intent intent = new Intent(IDCardInfoReaderActivity.this, PreviewActivity.class);
		intent.putExtra("type", true);
		intent.putExtra("box", true);
		startActivityForResult(intent, REQ_CODE_CAPTURE);
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == REQ_CODE_CAPTURE){
			if(data != null){
				idCard = (IDCard) data.getSerializableExtra("Data");
				isInfoNull = false;
				setIdCardText();
			}

		}else{
			if(isInfoNull){
				finish();
			}
		}
	}

	public void onClick(View paramView) {
		if(paramView.getId() == R.id.btn_get_idcard_photo){
			String name = edit_name.getText().toString().trim();
			String number = edit_id.getText().toString().trim();
			String sex = edit_sex.getText().toString().trim();
			String date = edit_date.getText().toString().trim();
			String type = edit_type.getText().toString().trim();
			String adress = edit_address.getText().toString().trim();
			IDCard idCard = new IDCard(name,number,sex,date,type, adress);
			if(idCard.checkIDCardData()){
				Toast.makeText(IDCardInfoReaderActivity.this,"信息不完整",Toast.LENGTH_LONG).show();
				return;
			}

			Intent intent = new Intent();
			intent.putExtra("Data", idCard);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.read_idcard_info_view);
		init();
		goToPreViewActivity();
	}

	private void setIdCardText(){

		if (idCard != null) {
				Log.e("TAG", "iDCardInfo = "+idCard.toString());
				edit_name.setText(idCard.getName());
				edit_id.setText(idCard.getCardNo());
				edit_sex.setText(idCard.getSex());
				edit_date.setText(idCard.getBirth());
				edit_address.setText(idCard.getAddress());
				edit_type.setText(idCard.getEthnicity());
				edit_create.setText(idCard.getAuthority());
			if ((idCard.getPeriod() != null)
					&& (!idCard.getPeriod().trim().equals(""))
					&& (idCard
					.getPeriod().contains("-"))) {
				String period = idCard
						.getPeriod().substring(0,
								idCard.getPeriod().indexOf("-"));
				edit_limit_date_start
						.setText(period);
				String period2 = idCard
						.getPeriod().substring(
								1 + idCard.getPeriod().indexOf("-"),
								idCard.getPeriod().length());
				edit_limit_date_end
						.setText(period2);
			}
		}
	}
}