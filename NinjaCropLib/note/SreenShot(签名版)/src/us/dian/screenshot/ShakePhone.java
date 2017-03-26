package us.dian.screenshot;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * copy from :http://blog.csdn.net/hjm4702192/article/details/8771680
 * make better by hujianfei
 */
public class ShakePhone implements SensorEventListener
{
	// the shake margin
	private static final int SPEED_SHRESHOLD = 3000;
	// two times Interval
	private static final int UPTATE_INTERVAL_TIME =70;
	
	private static final int SHAKE_COUNT  = 2;
	
	private SensorManager sensorManager;

	private Sensor sensor;
	private IShakeListener ShakeListener;

	private Context mContext;

	private float lastX;
	private float lastY;
	private float lastZ;

	private long lastUpdateTime;
	private  int shakeCount = 0;
	
	public ShakePhone(Context c)
	{
		mContext = c;
		start();
	}

	public void start()
	{

		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null)
		{
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		if (sensor != null)
		{
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void stop()
	{
		sensorManager.unregisterListener(this);
	}

	public void setOnShakeListener(IShakeListener listener)
	{
		ShakeListener = listener;
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
		{
			return ;
		}

		long currentUpdateTime = System.currentTimeMillis();

		long timeInterval = currentUpdateTime - lastUpdateTime;

		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;

		lastUpdateTime = currentUpdateTime;
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		lastX = x;
		lastY = y;
		lastZ = z;
		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ)
				/ timeInterval * 10000;

		if (speed >= SPEED_SHRESHOLD 
				&& ShakeListener != null && ++shakeCount >= SHAKE_COUNT)
		{
		  ShakeListener.onShake();
		  shakeCount = 0;
		  
		}
		
			
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	
	public interface IShakeListener
	{
		public boolean onShake();
	}

}
