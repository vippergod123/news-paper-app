package com.duyts.newspaper;

import android.app.Application;
import android.content.Context;

public class MainAppilication extends Application {

   private Context context;
   @Override
   public void onCreate() {
      super.onCreate();
      context = this.getApplicationContext();
   }
}
