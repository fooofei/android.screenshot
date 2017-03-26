package us.dian.screenshot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Reference : https://github.com/Simpleyyt/ScreenShotMaster
 * @author hujianfei
 *
 */
public class ScreenShotUtils
{
	public static int width = 0;
	public static int height = 0;
	public static int deepth = 0;
	public static final String file_name = "/dev/graphics/fb0";
	public static final String saveScreenshotFolder = "/DCIM/Screenshot/";
	public static int alpha = 0;

	public static final String getPermission = "chmod 777 /dev/graphics/fb0\n";
	public static final String givePermission = "chmod 660 /dev/graphics/fb0\n";

	public static final String screenshot(Context context) throws Exception
	{
		String path = generatePath();
		// put bitmap to file
		Bitmap bt = getScreenBitmap(context); //invokeScreenshot(context);
		FileOutputStream out = new FileOutputStream(path);
		bt.compress(Bitmap.CompressFormat.PNG, 100, out);
		out.flush();
		out.close();
		return path;
	}
	
	/**
	 * Invoke SurfaceControl.screenshot
	 *  @reference https://github.com/android/platform_frameworks_base/blob/master/core/java/android/view/SurfaceControl.java
	 *  @reference http://www.cnblogs.com/blairsProgrammer/p/3658627.html
	 *  @reference http://www.cnblogs.com/blairsProgrammer/p/3658216.html
	 */
	public static final Bitmap invokeScreenshot(Context context) throws ClassNotFoundException, 
	NoSuchMethodException, IllegalArgumentException, 
	IllegalAccessException, InvocationTargetException, 
	InstantiationException, IOException
	{
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager WM = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = WM.getDefaultDisplay();
			display.getMetrics(metrics);
			height = metrics.heightPixels;
			width = metrics.widthPixels;
			Class<?> surfaceClass = Class.forName("android.view.SurfaceControl");
			Method method = surfaceClass.getMethod("screenshot",
					new Class[]{int.class,int.class});
			return  (Bitmap)method.invoke(null,
					width,height);
	}

	public static final String generatePath()
	{
		String strDir = Environment.getExternalStorageDirectory().toString()
				+saveScreenshotFolder ;
		File file = new File(strDir);
		if (!file.exists())
		{
			file.mkdir();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-MM-SS",
				Locale.getDefault());
		return ( strDir
				+ sdf.format(new java.util.Date()) + ".png" );
	}
	
	
	/**
	 *  Read frameBuffer  /dev/graphics/fb0
	 * 
	 */

	public static void init(Context context)  {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager WM = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = WM.getDefaultDisplay();
		display.getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
		int pixelformat = PixelFormat.RGBA_8888;
		PixelFormat localPixelFormat1 = new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
		deepth = localPixelFormat1.bytesPerPixel;
	}

	public static Bitmap getScreenBitmap(Context context) throws Exception  {
		RootCommand(getPermission);
		init(context);
		byte[] piex = new byte[width * height * deepth];
		InputStream stream = new FileInputStream(new File(file_name));//
		DataInputStream dStream = new DataInputStream(stream);
		dStream.readFully(piex);
		dStream.close();
		stream.close();
		RootCommand(givePermission);
		int[] data = convertToColor(piex);
		return Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
		
	}

	public static InputStream getInputStream() throws Exception {
		FileInputStream buf = new FileInputStream(new File(file_name));
		return buf;
	}

	public static int[] convertToColor(byte[] piex) throws Exception {
		switch (deepth) {
		case 2:
			return convertToColor_2byte(piex);
		case 3:
			return convertToColor_3byte(piex);
		case 4:
			return convertToColor_4byte(piex);
		default:
			throw new Exception("Deepth Error!");
		}
	}

	public static int[] convertToColor_2byte(byte[] piex) {
		int[] colors = new int[width * height];
		int len = piex.length;
		for (int i = 0; i < len; i += 2) {
			int colour = (piex[i+1] & 0xFF) << 8 | (piex[i] & 0xFF);
			int r = ((colour & 0xF800) >> 11)*8;
			int g = ((colour & 0x07E0) >> 5)*4;
			int b = (colour & 0x001F)*8;
			int a = 0xFF;
			colors[i / 2] = (a << 24) + (r << 16) + (g << 8) + b;
		}
		return colors;
	}

	public static int[] convertToColor_3byte(byte[] piex) {
		int[] colors = new int[width * height];
		int len = piex.length;
		for (int i = 0; i < len; i += 3) {
			int r = (piex[i] & 0xFF);
			int g = (piex[i + 1] & 0xFF);
			int b = (piex[i + 2] & 0xFF);
			int a = 0xFF;
			colors[i / 3] = (a << 24) + (r << 16) + (g << 8) + b;
		}
		return colors;
	}

	public static int[] convertToColor_4byte(byte[] piex) {
		int[] colors = new int[width * height];
		int len = piex.length;
		for (int i = 0; i < len; i += 4) {
			int r = (piex[i] & 0xFF);
			int g = (piex[i + 1] & 0xFF);
			int b = (piex[i + 2] & 0xFF);
			int a = (piex[i + 3] & 0xFF);
			colors[i / 4] = (a << 24) + (r << 16) + (g << 8) + b;
		}
		return colors;
	}
	
	
	 public static void writeFile(File file, String data) throws IOException {
	    	OutputStream os = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(data.toCharArray());
			osw.flush();
			osw.close();
			os.close();
	    }
	 
	 /**
	  * NOTE : if phone not root and then it will not have su 
	  */
	  public static void RootCommand(String command) throws IOException, InterruptedException  {
	    	Process process = null;
	        DataOutputStream os = null;
	        process = Runtime.getRuntime().exec("su");///system/xbin/su
	        //process = Runtime.getRuntime().exec(command+"\n");
	        
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes(command+"\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
	        os.close();
	        process.destroy();
	    }
	    
	    public static String readFile(File file) throws IOException  {
	    	String data = null;
	    	InputStream is = new FileInputStream(file);
	    	int length = is.available();
			char[] buff = new char[length];
			InputStreamReader isr = new InputStreamReader(is);
			int a = isr.read(buff);
			data = String.valueOf(buff, 0, a);
			isr.close();
			is.close();
			return data;
	    }

}
