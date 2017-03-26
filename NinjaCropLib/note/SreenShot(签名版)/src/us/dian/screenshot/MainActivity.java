package us.dian.screenshot;
import java.io.File;

import us.dian.screenshot.ShakePhone.IShakeListener;
import us.dian.screenshot.R;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity implements IShakeListener
{
	public static final int SCREEN_SHOT_NOTIFI_ID = 10001;
	private NotificationManager mNotificationManager = null;
	
	
	private ShakePhone mshakePhone = null;

	public static final String Pre_Screenshot = "fun_screenshot";
	public static final String Pre_Exit = "fun_exit";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mshakePhone = new ShakePhone(this);
		mshakePhone.setOnShakeListener(this);
		mNotificationManager = (NotificationManager) getApplication()
				.getSystemService(NOTIFICATION_SERVICE);
		
		SettingsFragment sf = new SettingsFragment();
		sf.setOnClickListener(listener);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
				sf).commit();
		
		
	}
	
	/**
	 *  I am so clever.
	 */
	public IPreferenceClickListener listener = new IPreferenceClickListener()
	{
		@Override
		public void onClick(Preference pf)
		{
			if (pf.getKey().equals(Pre_Exit))
			{
				onBackPressed();
			}else if (pf.getKey().equals(Pre_Screenshot)) 
			{
				try
				{
					
					showNotification(ScreenShotUtils.screenshot(MainActivity.this));//ScreenShotUtils.screenshot(MainActivity.this)
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					Toast.makeText(MainActivity.this,getString(R.string.error_message)+"Not Found"+
							e.getMessage(),Toast.LENGTH_SHORT).show();
				}catch (Exception e) {
					Toast.makeText(MainActivity.this,getString(R.string.error_message)+
							e.getMessage(),Toast.LENGTH_SHORT).show();
				}
			}
		}
	};


	private final void showNotification(String pathName)
	{
		// mNotificationManager.cancel(SCREEN_SHOT_NOTIFI_ID);
		NotificationCompat.BigPictureStyle notifySytle = new NotificationCompat.BigPictureStyle();
		notifySytle.bigPicture(BitmapFactory.decodeFile(pathName));
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				MainActivity.this);
		builder.setSmallIcon(R.drawable.ic_launcher);
		//builder.setTicker(getString(R.string.ticker_text));//will show large icon
		builder.setContentTitle(getString(R.string.notification));
		builder.setStyle(notifySytle);
		builder.addAction(R.drawable.ic_share, getString(R.string.share),
				PendingIntent.getActivity(this, 0, getShareIntent(pathName),PendingIntent.FLAG_UPDATE_CURRENT));
		builder.setContentIntent(PendingIntent.getActivity(this, 0,
				openApp(), 0));
		builder.setAutoCancel(true);
		builder.setDefaults(Notification.DEFAULT_VIBRATE);
		mNotificationManager.notify(SCREEN_SHOT_NOTIFI_ID, builder.build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		return super.onCreateOptionsMenu(menu);

	}
	

	@Override
	public void onBackPressed()
	{
		mshakePhone.stop();
		mNotificationManager.cancel(SCREEN_SHOT_NOTIFI_ID);
		super.onBackPressed();
	}

	public final Intent getShareIntent(String path)
	{

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/png");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path))); 
		return intent;
	}

	/**
	 * 2014_05_12 change to intent the app 
	 *  cannot use static because we use MainActivity.this
	 * @return
	 */
	public  final Intent openApp()
	{
		Intent intent  = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(new ComponentName(MainActivity.this,MainActivity.class));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		
		return intent;
	}
	
	/**
	 * open the screenshot picture
	 * @param param
	 * @return
	 */
	public static final Intent getImageFileIntent(String param)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	@Override
	public boolean onShake()
	{

		try
		{
			
			showNotification(ScreenShotUtils.screenshot(MainActivity.this));
		} catch (Exception e)
		{
			Toast.makeText(MainActivity.this,getString(R.string.error_message)+
					e.getMessage(),Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	
	
	
	public static  class SettingsFragment extends PreferenceFragment
	{

		private IPreferenceClickListener mListener = null;
		public void setOnClickListener(IPreferenceClickListener listener)
		{
			mListener = listener;
		}
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.perferences);
			Preference pfExit =  findPreference(Pre_Exit) ;
			Preference pfScreenshot = findPreference(Pre_Screenshot);
			pfExit.setOnPreferenceClickListener(listenerClick);
			pfScreenshot.setOnPreferenceClickListener(listenerClick);
		}
	
		public OnPreferenceClickListener listenerClick= new OnPreferenceClickListener()
		{

			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				if(mListener != null )
					mListener.onClick(preference);
				return false;
			}
					
		};
	}
	
	
	public interface IPreferenceClickListener
	{
		void onClick(Preference pf);
	}
}
