# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

    # 设置混淆的压缩比率 0 ~ 7
    -optimizationpasses 5
    # 混淆的时候不使用大小写混用的类名
    -dontusemixedcaseclassnames
    # 不跳过类库中非public类
    -dontskipnonpubliclibraryclasses
    # 声明在处理过程中输出更多信息
    -verbose
    # 不优化
    -dontoptimize
    # 不预校验即将执行的类
    -dontpreverify
    # 不压缩输入文件
    -dontshrink
    # 输出所有找不到引用和一些其它错误的警告，但是继续执行处理过程
    #-ignorewarnings
    # 混淆采用的算法
    -optimizations !code/simplification/cast,!field/*,!class/merging/*

    #---------------------------------默认保留区---------------------------------
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.preference.Preference
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.support.v4.**
    -keep public class * extends android.support.annotation.**
    -keep public class * extends android.support.v7.**
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.view.View
    -keep class android.support.** {*;}


     # 指定受保护属性
    -keepattributes *Annotation*,SourceFile,LineNumberTable,Exceptions,InnerClasses,Signature

    -keep public class com.google.vending.licensing.ILicensingService
    -keep public class com.android.vending.licensing.ILicensingService

    # 保持native方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }

    # 保持自定义view的set和get以及构造方法不被混淆
    -keepclassmembers public class * extends android.view.View {
       void set*(***);
       *** get*();
       public <init>(android.content.Context);
       public <init>(android.content.Context, android.util.AttributeSet);
       public <init>(android.content.Context, android.util.AttributeSet, int);
    }

    # 保持Activity的public方法不被混淆，主要用于在xml中使用了onClick属性
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
       protected void onCreate(android.os.Bundle);
    }


    # 保持枚举不背混淆
    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }

    # 持 Parcelable 不被混淆
    -keepclassmembers class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator CREATOR;
    }

    # 保存序列化类和非private成员不被混淆
    -keepnames class * implements java.io.Serializable
    -keepclassmembers class * implements java.io.Serializable {
          static final long serialVersionUID;
          private static final java.io.ObjectStreamField[] serialPersistentFields;
          !static !transient <fields>;
          !private <fields>;
          !private <methods>;
          private void writeObject(java.io.ObjectOutputStream);
          private void readObject(java.io.ObjectInputStream);
          java.lang.Object writeReplace();
          java.lang.Object readResolve();
    }

    # 保持所有的R文件不被混淆
    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    # The support library contains references to newer platform versions.
    # Don't warn about those in case this app is linking against an older
    # platform version.  We know about them, and they are safe.
    -keep class android.support.**{*;}
    -dontwarn android.support.**

    # Understand the @Keep support annotation.
    -keep class android.support.annotation.Keep

    -keep @android.support.annotation.Keep class * {*;}

    -keepclasseswithmembers class * {
        @android.support.annotation.Keep <methods>;
    }

    -keepclasseswithmembers class * {
        @android.support.annotation.Keep <fields>;
    }

    -keepclasseswithmembers class * {
        @android.support.annotation.Keep <init>(...);
    }

    # volley
#    -keep class com.android.volley.**{*;}
    # fresco
#    -keep class com.facebook.fresco.** {*;}
    # eventbus
    -keepattributes *Annotation*
    -keepclassmembers class ** {
        @org.greenrobot.eventbus.Subscribe <methods>;
    }
    -keep enum org.greenrobot.eventbus.ThreadMode { *; }
    # Only required if you use AsyncExecutor
    -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
        <init>(java.lang.Throwable);
    }

    # bolts
    -keep class bolts.**{ *; }
    # butterknife
    -keep class butterknife.** { *; }
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }
    -keepclasseswithmembernames class * {
       @butterknife.* <fields>;
    }
    -keepclasseswithmembernames class * {
       @butterknife.* <methods>;
    }

    # pulltorefresh
    -keep class com.handmark.pulltorefresh.library.**{*;}


    # Gson
    -keep class sun.misc.Unsafe { *; }
    -keep class com.google.gson.stream.** { *; }
    # Application classes that will be serialized/deserialized over Gson
    -keep class com.google.gson.examples.android.model.** { *; }
    -keep class com.google.gson.** { *;}
    -keep class com.perfant.eyesir.plus.bean.** {*;}

    # java
    -keep public class javax.**
    # android
    -keep public class android.webkit.**
    -dontwarn android.webkit.WebView

    # okio
#    -keep class okio.**{*;}
#    -dontwarn okio.**
    # okhttp
#    -keep class okhttp3.**{*;}

    # umeng analytics
    -keepclassmembers class * {
       public <init>(org.json.JSONObject);
    }

    # 跳过对ShareSDK的混淆，因为它已经做了混淆处理，再次混淆会导致不可预期的错误
    -keep class cn.sharesdk.**{*;}
    -keep class com.sina.**{*;}
    -keep class **.R$* {*;}
    -keep class **.R{*;}
    -keep class com.mob.**{*;}
    -keep class m.framework.**{*;}
    -dontwarn cn.sharesdk.**
    -dontwarn com.sina.**
    -dontwarn com.mob.**
    -dontwarn **.R$*

    # 百度统计
    -keep class com.baidu.bottom.** { *; }
    -keep class com.baidu.mobstat.** { *; }

    #support-v4
    -dontwarn android.support.v4.**
    -keep class android.support.v4.app.** { *; }
    -keep interface android.support.v4.app.** { *; }
    -keep class android.support.v4.** { *; }

    #support-v7
    -dontwarn android.support.v7.**
    -keep class android.support.v7.internal.** { *; }
    -keep interface android.support.v7.internal.** { *; }
    -keep class android.support.v7.** { *; }

    #support design
    -dontwarn android.support.design.**
    -keep class android.support.design.** { *; }
    -keep interface android.support.design.** { *; }
    -keep public class android.support.design.R$* { *; }

    #关闭Log日志  注意：dontoptimize该配置会导致日志语句不会被优化掉
    -assumenosideeffects class android.util.Log {
        public static *** d(...);
        public static *** v(...);
        public static *** i(...);
        public static *** e(...);
        public static *** w(...);
    }

    #避免回调函数onXXEvent混淆
    -keepclassmembers class * {
        void *(*Event);
    }

    #WebView混淆配置
    -keepclassmembers class fqcn.of.javascript.interface.for.webview {
        public *;
    }
    -keepclassmembers class * extends android.webkit.webViewClient {
        public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
        public boolean *(android.webkit.WebView, java.lang.String);
    }
    -keepclassmembers class * extends android.webkit.webViewClient {
        public void *(android.webkit.webView, jav.lang.String);
    }

    # Retrofit
    -dontwarn okio.**
    -dontwarn javax.annotation.**
    -dontnote retrofit2.Platform
    -dontwarn retrofit2.Platform$Java8
    -keepattributes Signature
    -keepattributes Exceptions


    #okHttp
    -dontwarn com.squareup.okhttp3.**
    -keep class com.squareup.okhttp3.** { *;}
    -dontwarn okio.**

    #ALiyun OSS
    -keep class com.alibaba.sdk.android.oss.** { *; }
    -dontwarn okio.**
    -dontwarn org.apache.commons.codec.binary.**

    #LeanCloud
    -keepattributes Signature
    -dontwarn com.jcraft.jzlib.**
    -keep class com.jcraft.jzlib.**  { *;}

    -dontwarn sun.misc.**
    -keep class sun.misc.** { *;}

    -dontwarn com.alibaba.fastjson.**
    -keep class com.alibaba.fastjson.** { *;}

    -dontwarn sun.security.**
    -keep class sun.security.** { *; }

    -dontwarn com.google.**
    -keep class com.google.** { *;}

    -dontwarn com.avos.**
    -keep class com.avos.** { *;}

    -keep public class android.net.http.SslError
    -keep public class android.webkit.WebViewClient

    -dontwarn android.webkit.WebView
    -dontwarn android.net.http.SslError
    -dontwarn android.webkit.WebViewClient

    -dontwarn android.support.**

    -dontwarn org.apache.**
    -keep class org.apache.** { *;}

    -dontwarn org.jivesoftware.smack.**
    -keep class org.jivesoftware.smack.** { *;}

    -dontwarn com.loopj.**
    -keep class com.loopj.** { *;}

    -dontwarn com.squareup.okhttp.**
    -keep class com.squareup.okhttp.** { *;}
    -keep interface com.squareup.okhttp.** { *; }

    -dontwarn okio.**

    -dontwarn org.xbill.**
    -keep class org.xbill.** { *;}
