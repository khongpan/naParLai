package th.or.nectec.naparlai;

//import th.or.nectec.nParLAI.R;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

//import th.or.nectec.nParLAI.AzimuthZenithAngle;
//import th.or.nectec.nParLAI.PSA;
import th.or.nectec.naparlai.MyIOIOService.LocalBinder;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private MyIOIOService mIOIOService;
	private boolean mBound = false;

	private double currentlat, currentlong;
	private LocationManager locationManager;
	private String location_provider;
	private MyLocationListener my_location_listener;
	
	SharedPreferences sharePref;
	
	float SensorSensitivity=999;

	private TextView tvPAR, txtLAI,txtGPS;

	private DataCollector data_collector = new DataCollector(20);
	private LeafAreaIndexCalculator LAIcalc = new LeafAreaIndexCalculator();
	
	private double currentPAR=0;
	

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mIOIOService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocalService
		Intent intent = new Intent(this, MyIOIOService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar bar = getActionBar();
		//for color
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00904B")));
		
		sharePref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

		startService(new Intent(this, MyIOIOService.class));

		tvPAR = (TextView) findViewById(R.id.textViewPAR);
		txtLAI = (TextView) findViewById(R.id.TextViewLAI);
		
		//txtGPS = (TextView)findViewById(R.id.textView5);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		// criteria.setPowerRequirement(criteria.POWER_LOW);
		location_provider = locationManager.getBestProvider(criteria, true);

		my_location_listener = new MyLocationListener();
		locationManager.requestLocationUpdates(location_provider, 2000, 3,
				my_location_listener);

		Thread threadUIupdate = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								updateActivity();
							}
						});
					}
				} catch (InterruptedException e) {
				}
			}
		};

		threadUIupdate.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (location_provider != null){
		// locationManager.requestLocationUpdates(location_provider, 400, 1,
		// this);
		// }
	}

	private void updateActivity() {
		// noteTS = Calendar.getInstance().getTime();

		// time = "hh:mm"; // 12:00
		// tvTime.setText(DateFormat.format(time, noteTS));

		// date = "dd MMMMM yyyy"; // 01 January 2013
		// tvDate.setText(DateFormat.format(date, noteTS));
		float val = mIOIOService.getMeasurement();
		
		float sensor_sensitivity = sharePref.getFloat("SensorSensitivity", 1000);
		
		currentPAR = val*sensor_sensitivity;
		
		
		// tvPAR.setText(Float.toString(val));

		// Add by mew
		DecimalFormat dformat = new DecimalFormat("####");

		String S_val = String.valueOf(dformat.format(currentPAR));
		tvPAR.setText(S_val);
		// //
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent is = new Intent(getApplicationContext(),SettingActivity.class);
			startActivity(is);
			return true;
		}
		
	
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		
		txtLAI.setText("- -  - -");
		if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
			//float val = mIOIOService.getMeasurement();
					
			double val = currentPAR;

			data_collector.addNewData(val, 'u');
			Toast.makeText(this, "Upper PAR " + val + " umol/m2/s",
					Toast.LENGTH_SHORT).show();
			return true;
		} else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			//float val = mIOIOService.getMeasurement();
			double val = currentPAR;
			data_collector.addNewData(val, 'l');
			Toast.makeText(this, "Below PAR " + val + " umol/m2/s",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		


		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// code here to show dialog
		//Toast.makeText(this, "key bk: ", Toast.LENGTH_SHORT).show();
		super.onBackPressed(); // optional depending on your needs
	}

	/**
	 * Called when a button is clicked (the button in the layout file attaches
	 * to this method with the android:onClick attribute)
	 */
	public void onButtonCalcClick(View v) {
		double zenithAngle;
		double LAI;
		double upperPAR;
		double lowerPAR;

		if (mBound) {
			// Call a method from the LocalService.
			// However, if this call were something that might hang, then this
			// request should
			// occur in a separate thread to avoid slowing down the activity
			// performance.
			// float num = mIOIOService.getMeasurement();
			// Toast.makeText(this, "number: " + num,
			// Toast.LENGTH_SHORT).show();

			Criteria criteria = new Criteria();
			// criteria.setPowerRequirement(criteria.POWER_LOW);
			location_provider = locationManager.getBestProvider(criteria, true);
			Location devicelocation = locationManager
					.getLastKnownLocation(location_provider);

			if (devicelocation == null) {
				Toast.makeText(this, location_provider + " error no location ",
						Toast.LENGTH_SHORT).show();
				return;
			}

			currentlat = devicelocation.getLatitude();
			currentlong = devicelocation.getLongitude();

			// Toast.makeText(this, location_provider + " lat " +
			// Double.toString(currentlat) + " long "+
			// Double.toString(currentlong) , Toast.LENGTH_SHORT).show();

			GregorianCalendar time = new GregorianCalendar(new SimpleTimeZone(
					+7 * 60 * 60 * 1000, "LST"));
			// time.set(Year, Month, Date, Hour, Minute, Second); // 17 October
			// 2003, 12:30:30 LST-07:00

			AzimuthZenithAngle result = PSA.calculateSolarPosition(time,
					currentlat, currentlong);

			zenithAngle = result.getZenithAngle();

			upperPAR = data_collector.getAvg('u');
			lowerPAR = data_collector.getAvg('l');

			if ((upperPAR == 0) || (lowerPAR == 0)) {
				Toast.makeText(this, "No input data!", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			LAIcalc.setPara(upperPAR, lowerPAR, zenithAngle, 1);

			LAI = LAIcalc.CalLAI();

			// Toast.makeText(this, "Zenit Angle "+zenithAngle+"\nLAI "+
			// String.valueOf(LAI) , Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "LAI " + String.valueOf(LAI),
					Toast.LENGTH_SHORT).show();

			DecimalFormat dformat = new DecimalFormat("#.##");
			
			String S_LAI = String.valueOf(dformat.format(LAI));
			txtLAI.setText(S_LAI);
			
			DecimalFormat dformat2 = new DecimalFormat("##.######");
			
			String S_Lat = String.valueOf(dformat2.format(currentlat));
			String S_Long = String.valueOf(dformat2.format(currentlong));
			//txtGPS.setText("Lat/Long " + S_Lat +"," +S_Long);
			
	
			
			
			// //

			// write report file

			try {
				int fileNum = 0;

				File report_file = new File(
						Environment.getExternalStorageDirectory(), "nParLai");
				report_file.mkdirs();
				String fileName = "nParLai_" + String.valueOf(fileNum) + ".txt";
				File output = new File(report_file, fileName);
				while (output.exists()) {
					fileNum++;
					fileName = "nParLai_" + String.valueOf(fileNum) + ".txt";
					output = new File(report_file, fileName);
				}

				// if exists create text file
				// if(!output.exists())
				{
					output.createNewFile();
				}

				// Write Text File
				FileWriter writer = new FileWriter(output, true); // True =
																	// Append to
																	// file,
																	// false =
																	// Overwrite
				writer.write("@ParLai Report\n");
				writer.write(DateFormat.getDateTimeInstance()
						.format(new Date()) + "\n\n");
				writer.write("Leaf Area Index (LAI) : " + LAI + "\n");

				writer.write("Average upper PAR : " + upperPAR
						+ " umol/m2/sec\n");
				writer.write("Average below PAR : " + lowerPAR
						+ " umol/m2/sec\n");
				writer.write("zenithAngle : " + zenithAngle + "\n");
				writer.write("Latitude : " + currentlat + "\n");
				writer.write("Longitude : " + currentlong + "\n\n");
				writer.write(data_collector.getReport());
				writer.write("\n\nend report.");

				writer.close();

				Toast.makeText(MainActivity.this, "File Save Ok",
						Toast.LENGTH_LONG).show();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(MainActivity.this,
						"File Save Failed! = " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			data_collector.reset(); // prepare for next sample set

			// For Test Only
			/*
			 * { int hour=6; for (int i=0; i < 12;i++){
			 * 
			 * time.set(2014, 04, 23, hour , 0, 0); // equinox day result =
			 * PSA.calculateSolarPosition(time, currentlat, currentlong);
			 * zenithAngle = result.getZenithAngle(); Log.d("nParLai", hour +
			 * " " + zenithAngle); hour++; }
			 * 
			 * }
			 */

		}
	}

	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			currentlat = location.getLatitude();
			currentlong = location.getLongitude();
			// Initialize the location fields
			// Toast.makeText(MainActivity.this,
			// "Latitude: "+String.valueOf(location.getLatitude()),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(MainActivity.this,
			// "Longitude: "+String.valueOf(location.getLongitude()),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(MainActivity.this, location_provider +
			// " location_provider has been selected.",
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(MainActivity.this, "Location changed!",
			// Toast.LENGTH_SHORT).show();
			//Toast.makeText(
			//		MainActivity.this,
			//		"loc change " + location_provider + " Lat "
			//				+ String.valueOf(currentlat) + " long "
			//				+ String.valueOf(currentlong), Toast.LENGTH_SHORT)
			//		.show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//Toast.makeText(MainActivity.this,
			//		provider + "'s status changed to " + status + "!",
			//		Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(MainActivity.this,
					"Provider " + provider + " enabled!", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(MainActivity.this,
					"Provider " + provider + " disabled!", Toast.LENGTH_SHORT)
					.show();

		}

	}

}
