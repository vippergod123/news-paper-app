package com.duyts.newspaper;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class MainApplication extends Application {

   @SuppressLint("StaticFieldLeak")
   private static Context context;
   @Override
   public void onCreate() {
      super.onCreate();
      context = this.getApplicationContext();
      Timber.plant(new Timber.DebugTree());
   }

   public static Context getAppContext() {
      return MainApplication.context.getApplicationContext();
   }
}
