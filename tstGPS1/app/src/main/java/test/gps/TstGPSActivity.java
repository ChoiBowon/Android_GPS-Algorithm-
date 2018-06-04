package test.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class TstGPSActivity extends Activity {
    //private LocationManager lm;
    private  LocationListener LL;

	//새로 들어온 위치 정보 location
	public EditText etProvider;
    public EditText etLatitude;
    public EditText etLongitude;	
    public EditText etAccuracy;
    public EditText etSpeed;
    public EditText etAltitude;
    public EditText etSatellites;
	public EditText etNetwork;
	public EditText etResult;

	//current Best Location : slocation
	public EditText stProvider;
	public EditText stLongitdue;
	public EditText stLatitude;
	public EditText stAccuracy;
	public EditText stSpeed;
	public EditText stAltitude;
	public EditText stSatellites;

	private static Location slocation = null;

	//GPS 값 표시
    public EditText etNmea0183;

	//network,gps count
	private static int count = 0;
	private static int exceedcount = 0;
	private static int gps_count = 0;
	private static int gps_exceedcount = 0;


	//Time
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	//Nmea 받아오기, 짤라서 위성 수 받아오기
    private class MyNmeaListener implements GpsStatus.NmeaListener {
    	@Override
    	public void onNmeaReceived(long timestamp, String nmea) {
    		Editable e = etNmea0183.getText();
    		int len = e.length();
    		if(len > 5000) {
    			e.delete(0, 2500);
    		}
    		
    		e.append(nmea);
    		
    		len = e.length();
    		etNmea0183.setSelection(len-1, len-1);
    		
    		if(nmea.startsWith("$GPGGA")) {
    			 String tokens[] = nmea.split(",");
				if(tokens[6].equals("0")){
					etSatellites.setText("0");
				}else{
					etSatellites.setText(tokens[7]);
				}

    		}

    	}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		etProvider = findViewById(R.id.etProvider);
        etLatitude = (EditText)findViewById(R.id.etLatitude);
        etLongitude = (EditText)findViewById(R.id.etLongitude);
        etAccuracy = (EditText)findViewById(R.id.etAccuracy);
        etSpeed = (EditText)findViewById(R.id.etSpeed);
        etAltitude = (EditText)findViewById(R.id.etAltitude);
        etSatellites = (EditText)findViewById(R.id.etSatellites);
		etNetwork = (EditText)findViewById(R.id.etNetwork);
		etResult = (EditText)findViewById(R.id.etResult);

		stProvider = (EditText)findViewById(R.id.stProvider);
		stLongitdue = (EditText)findViewById(R.id.stLongitdue);
		stLatitude = (EditText)findViewById(R.id.stLatitude);
		stAccuracy = (EditText)findViewById(R.id.stAccuracy);
		stSpeed = (EditText)findViewById(R.id.stSpeed);
		stAltitude = (EditText)findViewById(R.id.stAltitude);
		stSatellites = (EditText)findViewById(R.id.stSatellites);


        etNmea0183 = (EditText)findViewById(R.id.etNmea0183);

		//LocationManger 객체 받아옴
        final LocationManager Lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		if(Lm !=null){
			//Toast.makeText(TstGPSActivity.this,"시작!! ", Toast.LENGTH_SHORT).show();
			boolean isGPSEnabled = Lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = Lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if(isGPSEnabled || isNetworkEnabled){
				final List<String> m_listProviders = Lm.getProviders(false);
				LL = new LocationListener() {
					@Override
					public void onLocationChanged(Location location) {
						Log.e("onLocationChanged", "onLocationChanged");

						etProvider.setText(location.getProvider());
						etLatitude.setText(Double.toString(location.getLatitude()));
						etLongitude.setText(Double.toString(location.getLongitude()));
						etAccuracy.setText(Float.toString(location.getAccuracy()));
						etSpeed.setText(Float.toString(location.getSpeed()));
						etAltitude.setText(Double.toString(location.getAltitude()));



						int number = Integer.parseInt(etSatellites.getText().toString());

						if(number <= 8){ //실내일 경우
							gps_count = 0;
							gps_exceedcount = 0;
							if(location.getProvider().equals("network")){   // network가 2번 나왔을 때 뿐만 아니라 gps 측정 정확도가 100 이상 나는게 몇 번 이상이면 그것도 의심 해볼 만하다!!!!!
								count++;
								etNetwork.setText("network가 나온 횟수 " + count);
								if(count > 2){
									etResult.setText("네트워크 수 때문에 실내로 들어왔습니다");
								}
							}else if(location.getAccuracy() > 100){
								exceedcount++;
								if(exceedcount >2 ){
									etResult.setText("정확도 때문에 실내로 들어왔습니다");
								}
							}

							if(count>2 && exceedcount>2){

								etResult.setText("확실히 실내로 들어왔습니다");
							}

						}else{ //실외일 경우
							count = 0;
							exceedcount =0;
							Toast.makeText(TstGPSActivity.this,"실외입니다!! ", Toast.LENGTH_SHORT).show();
							if(location.getProvider().equals("gps")){
								gps_count++;
								etNetwork.setText("gps가 나온 횟수 " + gps_count);
								if(gps_count >2){
									etResult.setText("gps 수 때문에 실외로 나갔습니다");
								}
							}else if(location.getAccuracy()> 100){
								gps_exceedcount++;
								if(gps_exceedcount >2){
									etResult.setText("정확도 때문에 실외로 나갔습니다");
								}
							}
							if(gps_count>2 && gps_exceedcount>2){
								etResult.setText("확실히 실외입니다");
							}
						}



						if(isBetterLocation(location,slocation)){  //location : 새로 들어온 것 slocation = currentBestlocation
							slocation = location;

							stProvider.setText(slocation.getProvider());
							stLongitdue.setText(Double.toString(slocation.getLongitude()));
							stLatitude.setText(Double.toString(slocation.getLatitude()));
							stAccuracy.setText(Float.toString(slocation.getAccuracy()));
							stSpeed.setText(Float.toString(slocation.getSpeed()));
							stAltitude.setText(Double.toString(slocation.getAltitude()));



						}


					}

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {
						switch(status) {
							case GpsStatus.GPS_EVENT_STARTED:
								Toast.makeText(TstGPSActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
								break;

							case GpsStatus.GPS_EVENT_STOPPED:
								Toast.makeText(TstGPSActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
								break;

							case GpsStatus.GPS_EVENT_FIRST_FIX:
								Toast.makeText(TstGPSActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
								break;

							case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
								Toast.makeText(TstGPSActivity.this, "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
								break;
						}

					}

					@Override
					public void onProviderEnabled(String provider) {
						Toast.makeText(TstGPSActivity.this, provider + " Enabled", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onProviderDisabled(String provider) {
						Toast.makeText(TstGPSActivity.this, provider + " Disabled", Toast.LENGTH_SHORT).show();
					}
				};

				MyNmeaListener nmeaListen = new MyNmeaListener();
				Lm.addNmeaListener(nmeaListen);


				for (String name : m_listProviders) {
					Toast.makeText(TstGPSActivity.this," 새로 측정합니다! ", Toast.LENGTH_SHORT).show();
					Lm.requestLocationUpdates(name, 5000, 0, LL);
					//name : 등록할 위치 제공자 , 1000 : 통지 사이의 최소 시간 간격(miliSecond) , 1 : 통지 사이의 최소 변경 거리(m)
				}

			}else { //GPS 가 활성화 되어 있지 않으면 사용자 설명 페이지로 넘어간다
				Log.e("GPS Enable", "false");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
			}

		}else {
			Lm.removeUpdates(LL); // 미수신할 때는 반드시 자원해체를 해줘야 함
		}
    }

	protected boolean isBetterLocation(Location location, Location currentBestLocation){
		if(currentBestLocation == null){  // 기존의 위치 정보가 없다면 새로 들어온 정보가 Best!
			return true;
		}

		// 시간 비교
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		if(isSignificantlyNewer){ // 새로 들어온 위치 정보가 최신이다!
			return true;
		}else if (isSignificantlyOlder){ // 새로 들어온 위치 정보가 2분 이전의 정보이므로 안 좋다!
			return false;
		}

		// 정확성 비교
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0; // 새로 들어온 위치 정보의 정확성이 더 안좋다
		boolean isMoreAccurate = accuracyDelta < 0; // 새로 들어온 위치 정보의 정확성이 더 좋다
		boolean isSignificantlyLessAccurate = accuracyDelta >200;

		// 같은 위치 정보 제공자인지 비교

		boolean isFromSameProvider = isSameProvider(location.getProvider(),currentBestLocation.getProvider());

		if(isMoreAccurate){
			Log.e("1","1");
			return true;
		}else if(isNewer && !isLessAccurate){
			Log.e("2","2");
			return true;
		}else if(isNewer && !isSignificantlyLessAccurate && isFromSameProvider){
			Log.e("3","3");
			return true;
		}
		Log.e("isBetterLocation ", "false");
		return false;

	}

	private boolean isSameProvider(String provider1, String provider2){
		if(provider1 == null){
			return provider2 == null;
		}
		return provider1.equals(provider2); // 두개의 위치 정보 제공자가 같으면 true 리턴!
	}
}