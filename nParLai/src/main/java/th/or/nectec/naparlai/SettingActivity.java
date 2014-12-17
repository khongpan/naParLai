package th.or.nectec.naparlai;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {
	//Context context;
	SharedPreferences sharePref;
	SharedPreferences.Editor editorPref;
	Button btnSaveSetting;


	EditText edtLeafDistributionPara;
	EditText edtSensorSensitivity;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		ActionBar bar = getActionBar();
		//for color
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00904B")));
		btnSaveSetting =(Button)findViewById(R.id.bottonSaveSetting);
		
		sharePref = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
		editorPref = sharePref.edit();
		float leaf_distribution_parameter = sharePref.getFloat("LeafDistributionParameter", 1);
		float sensor_sensitivity = sharePref.getFloat("SensorSensitivity", 1000);
		//int sensor_choice = sharePref.getInt("LeafDistributionParameter", 1);
				

		edtLeafDistributionPara = (EditText) findViewById(R.id.editTextLeafDistributionPara);
		edtLeafDistributionPara.setText(Float.toString(leaf_distribution_parameter));
		
		edtSensorSensitivity = (EditText) findViewById(R.id.EditTextSensorSensivity);
		edtSensorSensitivity.setText(Float.toString(sensor_sensitivity));
		
		

	}

	public void onRadioButtonClicked(View view) {

		
		switch (view.getId()) {

		case R.id.radio0:
			editorPref.putInt("SensorChoice", R.id.radio0);
			editorPref.commit();
            break;
			
		case R.id.radio1:
			editorPref.putInt("SensorChoice", R.id.radio1);
			editorPref.commit();
            break;
			
		case R.id.radio2:
			editorPref.putInt("SensorChoice", R.id.radio2);
			editorPref.commit();
            break;
		}
	}
	
	
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bottonSaveSetting:

			editorPref.putFloat("LeafDistributionParameter",Float.valueOf(edtLeafDistributionPara.getText().toString()));
			editorPref.putFloat("SensorSensitivity",Float.valueOf(edtSensorSensitivity.getText().toString()));
			
			editorPref.commit();

			
			//txt.setText(ra+"\n"+edt);
		
		}
		
		Toast.makeText(this, "Setting saved", Toast.LENGTH_SHORT).show();
	}
}
