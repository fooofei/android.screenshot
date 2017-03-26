有时候有的功能必须要有系统签名才能使用，例如调用系统自带的Surface.screenShot方法时，就必须在androidManifest.xml里声明android:sharedUserId="android.uid.system"

但是这个时候在编译生成的apk很有可能无法安装的情况 并且报这个错误：

INSTALL_FAILED_SHARED_USER_INCOMPATIBLE

这个时候就必须要对APK进行签名了，如果是在linux的安卓源码环境下使用mm编译的话就不会有这个问题，不过想想也觉得对于习惯在windows下开发的人来说是相当麻烦的一件事~

那么windows下对apk进行系统签名的方法如下，首先要进入android源码里找到下面三个文件：

signapk.jar platform.x509.pem platform.pk8

具体路径大家可以去百度。。我这里直接提供一个下载链接：

http://yun.baidu.com/share/link?shareid=3118744382&uk=2215407523

 

然后将这三个文件和你的apk放在同一目录下，进入cmd，进入你放这三个文件和apk的目录，执行命令:

java -jar signapk.jar platform.x509.pem platform.pk8 你的apk名字<绝对路径>.apk 你要输出的apk名字.apk

 