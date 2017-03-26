package us.dian.screenshot;

import gdg.ninja.croplib.Crop;

import java.io.File;

import us.dian.screenshot.ShakePhone.IShakeListener;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class ScreenshotService extends Service implements IShakeListener{

	private ShakePhone mshakePhone = null;
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	
	
	@Override
	public void onCreate() {
		mshakePhone = new ShakePhone(this);
		mshakePhone.setOnShakeListener(this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void onDestroy() {
		mshakePhone.stop();
		super.onDestroy();
	}



	@Override
	public boolean onShake() {
		try {
			String src = ScreenShotUtils.screenshot(this);
			new Crop(Uri.fromFile(new File(src))).start(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
