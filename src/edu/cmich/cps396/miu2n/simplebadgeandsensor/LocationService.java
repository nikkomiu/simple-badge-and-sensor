package edu.cmich.cps396.miu2n.simplebadgeandsensor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


public class LocationService extends Service {
	private LocationManager locationManager;
	private LocationListen listener;
	private float[] prevDistance;
	private String addressLocation = "";
	private NotificationManager notificationManager;
	public boolean stop = false;
	
	Intent intent;

	@Override
	public void onCreate() {
	    super.onCreate();
	    intent = new Intent("location");
	    prevDistance = new float[3];
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		try {
			Bundle b = intent.getExtras();
			addressLocation = b.getString("location");
			
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    listener = new LocationListen();
		    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (1000 * 60 * 5), 0, listener);
		    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
	    return null;
	}

	@Override
	public void onDestroy() {       
	   // handler.removeCallbacks(sendUpdatesToUI);     
	    super.onDestroy();
	    locationManager.removeUpdates(listener);        
	}   
	
	public static Thread performOnBackgroundThread(final Runnable run) {
	    Thread t = new Thread() {
	        @Override
	        public void run() {
	        	run.run();
	        }
	    };
	    t.start();
	    return t;
	}

	public class LocationListen implements LocationListener {
		public void onLocationChanged(Location loc) {
            intent.putExtra("Latitude", loc.getLatitude());
            intent.putExtra("Longitude", loc.getLongitude());
            intent.putExtra("Provider", loc.getProvider());
            
            try {
            	Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
            	Address address = geo.getFromLocationName(addressLocation, 1).get(0);
            	
            	float[] distance = new float[3];
    			Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), address.getLatitude(), address.getLongitude(), distance);
    			
    			Intent i = new Intent(getApplicationContext(), ShowInformationActivity.class);
    			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		    
    		    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_ONE_SHOT);
    		    
    		    Notification builder = null;
    		    
    		    if (distance[0] < 1000 && prevDistance[0] > 1000) {
    		    	JSONObject obj = ShowInformationActivity.getJSONData(getApplicationContext());
    		    	
	    		    builder = new NotificationCompat.Builder(getApplicationContext())
	    		    .setContentTitle("Moved Inside Range")
	    			.setContentText("You have moved inside of 1000 Meters of your address")
	    			.setContentIntent(pi)
	    			.setLargeIcon(BitmapFactory.decodeFile(Uri.fromFile(new File(obj.getString("image_uri"))).getPath()))
	    			.setSmallIcon(R.drawable.ic_launcher)
	    			.build();
    		    }
    		    
    		    if (distance[0] > 1000 && prevDistance[0] < 1000) {
    		    	JSONObject obj = ShowInformationActivity.getJSONData(getApplicationContext());
    		    	
    		    	builder = new NotificationCompat.Builder(getApplicationContext())
	    		    .setContentTitle("Moved Outside Range")
	    			.setContentText("You have moved outside of 1000 Meters of your address")
	    			.setContentIntent(pi)
	    			.setLargeIcon(BitmapFactory.decodeFile(Uri.fromFile(new File(obj.getString("image_uri"))).getPath()))
	    			.setSmallIcon(R.drawable.ic_launcher)
	    			.build();
    		    }
    		    
    		    if (builder != null) {
    		    	builder.flags |= Notification.FLAG_AUTO_CANCEL;
    		    	notificationManager.notify(0, builder);
    		    }
    		    	
    		    locationDataToJson(loc);
    		    
    			Toast.makeText(getApplicationContext(), "Distance: ~" + (int) distance[0] + " Meters from Address", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
            	e.getStackTrace();
            }
            
            sendBroadcast(intent);
	    }
		
		private void locationDataToJson(Location location) {
			JSONObject obj = null;
			try {
				String str = "";
				Scanner reader = new Scanner(openFileInput("location.json"));
				while (reader.hasNextLine())
					str += reader.nextLine();
				obj = new JSONObject(str);
				reader.close();
			} catch (Exception e) {
				// File probably doesn't exist...
				obj = new JSONObject();
			}
			
			try {
				JSONObject locObj = new JSONObject();
				locObj.put("longitude", location.getLongitude());
				locObj.put("latitude", location.getLatitude());
				locObj.put("altitude", location.getAltitude());
				locObj.put("accuracy", location.getAccuracy());
				locObj.put("bearing", location.getBearing());
				locObj.put("speed", location.getSpeed());
				
				obj.put(location.getTime()+"", locObj.toString());
				
				PrintWriter writer = new PrintWriter(openFileOutput("location.json", MODE_APPEND));
				writer.print(obj.toString());
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	    public void onProviderDisabled(String provider) {
	        Toast.makeText( getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT ).show();
	    }
	
	
	    public void onProviderEnabled(String provider) {
	        Toast.makeText( getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
	    }
	    
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	}
}