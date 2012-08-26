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

26.08.2012 Initial version of the library will be available in several days from now. Stay tuned and subscribe/star
the project to get notified :)
