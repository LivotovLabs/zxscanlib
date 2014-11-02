ZXScanLib
=========

Embeddable Android library QR (and other codes) scanner, based on ZXing project.
(both barcode core and barcode scanner application).

ZXing Barcode Scanner application is great but it lacks one important feature - it does not have an android
library other developers can easily embed to their apps and use.

Yes, thanks to Android architecture, it is trivial to delegate scanning task to another Barcode Scanner application
(such as ZXing) and receive the results back to your own app. However, in several use cases,
this is not acceptable or simply not convenient (many reasons, from "no deps" policies to company guidelines and customization
reqs, etc etc...).

So the goal of this project is to create and maintain the android library, which will allow one easily embed Barcode Scanner
functionality into its own android application.


STATUS
------

- 02.11.2014 Version 0.9.1 (LATEST)
    - Added gradle project layout and maven repository support

- 18.12.2012 Alpha version is available for basic usage

- 27.08.2012 This is very first working version. Just adopted ZXing code and added ZXScanHelper. Only QR Codes are
being scanned, no sound or other options applied. Will be adding more features next days, however,
basic scanning is functional now.


HOW TO GET IT
-------------

Maven repository: http://maven.livotovlabs.pro/content/groups/public
Group: eu.livotov.labs
Artifact ID: zxscanlib

=Gradle=
```groovy

repositories {
    ...
    maven { url 'http://maven.livotovlabs.pro/content/groups/public' }
    ...
}


compile group: "eu.livotov.labs", name: "zxscanlib", version: "0.9.1", ext: "aar"

```



LICENSE
-------

This library is licensed under Apache 2.0 license. Feel free to use it in any commercial or opensource projects.
More license info and links will be added later.


Quick Start
-----------

1) Attach this project as an Android library to your app main project

2) Add the following set of permissions (if you don't have ones already) to your main project AndroidMnifest.xml:

```xml

    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.FLASHLIGHT"/>

```

3) Add scan activity reference to your main project AndroidManifest.xml:

```xml

     <activity android:name="com.google.zxing.client.android.CaptureActivity"
                       android:clearTaskOnLaunch="true"
                       android:stateNotNeeded="true"
                       android:configChanges="orientation|keyboardHidden"
                       android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
                       android:windowSoftInputMode="stateAlwaysHidden">
     </activity>

```     

4) Now to scan a barcode, call the helper method from any place of your app activity:
   (of course, you can use any request code instead of 12345)

```java

     ZXScanHelper.scan(this,12345);

```     

5) Override (if not already) onActivityResult method in your activity and add checking block for your request code from
   section (4) above:

```java

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == 12345)
        {
            String scannedCode = ZXScanHelper.getScannedCode(data);
        }
    }

```    

6) That's all :)



Fine Tuning
-----------

If you want to fine tune the scanner behaviour or provide your own layout for scanner screen, you can always
set customization values into the ZXScanHelper class before calling scan(...) method:

1) Using your own layout for scanner screen: ZXScanHelper.setCustomScanLayout(int layoutResId);

Set your own scanner activity layout resource identifier. Note, that your layout
resource file must contain the following required view, which provides live image stream from the camera:

```xml

        <SurfaceView
            android:id="@+id/preview_view"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent" />

```

You can set your own values to width/size/other view attributes, however you must retain the "id" attribute set to "preview_view"


2)  ZXScanHelper.setCustomScanSound(int rawResId);

Set your .mp3 or .ogg resource identifier from the res/raw folder. This will play on code read instead of regular "beep" sound.


3) ZXScanHelper.setPlaySoundOnRead(boolean b) and ZXScanHelper.setVibrateOnRead(boolean b)

Allow you to turn on or off sound alert and vibration when barcode is recognized


4) ZXScanHelper.setUseExternalApplicationIfAvailable(boolean b);

If set to TRUE (default is FALSE), ZXScanHelper will try to use native ZXing Barcode Scanner application first, if installed on device.


5) ZXScanHelper.setUserCallback(ZXUserCallback cb);

Allow to plug-in callback handler to receive events for scanner activity lifecycle. Callback also allows to reject some codes being recognized,
so the scanner activity will continue scanning for such cases. See javadoc for the ZXUserCallback interface for more details.
