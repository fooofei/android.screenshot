
1.有两个工程，分别是screenshot(签名版)和screenshot(Root版).
2.两个工程是一样的，只能在eclipse导入一个工程，不能同时导入。
3.两个工程只是在截图使用的方法不同。
4.签名版使用的系统签名，目的是为了无需root即可使用，因为需要root的版本在截图时会把“XXX已获得管理员权限”的toast提示一并截图，很不美观。这种方法在不同机型的表现差别很大。
5.root版需要在root的手机中获取管理员权限，成功率较高。

下面是一些比较靠谱的重要的资料。



Android 高效截图，读取 /dev/graphics/fb0 文件获取屏幕的Bitmap #7
https://github.com/Yhzhtk/note/issues/7


添加Screenshot，从/dev/graphics/fb0读取屏幕截图信息。
https://github.com/Yhzhtk/AiXiaoChu/blob/master/src/com/yh/aixiaochu/system/Screenshot.java

Simpleyyt/ScreenShotMaster
https://github.com/Simpleyyt/ScreenShotMaster

 android4.3 截屏功能的尝试与失败分析
 http://blog.csdn.net/buptgshengod/article/details/20607281
 (里面提到的方法我都尝试过。)