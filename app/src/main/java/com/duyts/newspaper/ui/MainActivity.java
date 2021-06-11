package com.duyts.newspaper.ui;

import com.duyts.newspaper.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity{

   private ActivityMainBinding binding;

   @Override
   void initView() {
      super.initView();
      binding = ActivityMainBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());
   }
}