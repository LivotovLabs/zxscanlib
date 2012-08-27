ZXScanLib
=========

Android library embeddable QR (and other codes) scanner, based on ZXing project
(both barcode core and barcode scanner application).

ZXing Barcode Scanner application is great but it lacks one important feature - it does not have an android
library, other developers can easily embed to their apps and use. Yes, due to Android architecture, it is trivial
to delegate scanning task to a Barcode Scanner application and receive the result back. However, in several use cases
it is not acceptable or convenient (many reasons, from "no deps" policies to company guidelines and customization
reqs). So, the goal of this project is to create and maintain the android library, which will allow embed Barcode Scanner
functionality to android application and also to maintain it in sync with the ZXing core codebase.

This library will provide many integration and customization options, it can also bypass forked ZXing code and
delegate scanning request to a native Barcode Scanner application (if installed and if you wish so), etc, etc.



LICENSE
=======

This library is licensed under Apache 2.0 license. Feel free to use it in any commercial or opensource projects.
More license info and links will be added later.



STATUS
======

- 27.08.2012 This is very first working version. Just adopted ZXing code and added ZXScanHelper. Only QR Codes are
being scanned, no sound or other options applied. Will be adding more features next days, however,
basic scanning is functional now.



Very Quick Start
===========

1. Attach this project as an Android library to your app main project

2. Add the following set of permissions (if you don't have ones already) to your main project AndroidMnifest.xml:

```xml
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.FLASHLIGHT"/>
```

3. Add scan activity reference to your main project AndroidManifest.xml:

```xml
     <activity android:name="com.google.zxing.client.android.CaptureActivity"
                       android:screenOrientation="landscape"
                       android:clearTaskOnLaunch="true"
                       android:stateNotNeeded="true"
                       android:configChanges="orientation|keyboardHidden"
                       android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
                       android:windowSoftInputMode="stateAlwaysHidden">

                 <intent-filter>
                     <action android:name="eu.livotov.zxscan.SCAN"/>
                     <category android:name="android.intent.category.DEFAULT"/>
                 </intent-filter>

     </activity>
```     

4. Now to scan a barcode, call the helper method from any place of any your own activity:

```java
     ZXScanHelper.scan(this,12345);
```     

5. Add onActivityResult override to your same activity and check scan result:

```java
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == 12345)
        {
            String scannedCode = ZXScanHelper.getScannedCode(data);
        }
    }
```    

6. That's all :)

