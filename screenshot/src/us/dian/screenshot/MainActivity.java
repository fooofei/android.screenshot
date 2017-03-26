package us.dian.screenshot;

import gdg.ninja.croplib.Crop;
import java.io.File;
import us.dian.screenshot.R;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

/**
 * 2014_07_21 update add service
 * @author hujianfei
 *
 */
public class MainActivity extends Activity  
{
	public static final int SCREEN_SHOT_NOTIFI_ID = 10001;

	public static final String Pre_Screenshot = "fun_screenshot";
	public static final String Pre_Exit = "fun_exit";
	
	public static final String SERVICE_NAME = "us.dian.screenshot.screenshotservice";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		SettingsFragment sf = new SettingsFragment();
		sf.setOnClickListener(listener);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
				sf).commit();
		Intent intent = new Intent(SERVICE_NAME);
		startService(intent);
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
					String src =  ScreenShotUtils.screenshot(MainActivity.this);
					new Crop(Uri.fromFile(new File(src))).start(MainActivity.this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(SERVICE_NAME);
		stopService(intent);
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
