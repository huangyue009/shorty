package com.example.fluttertest2.host;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

@Route(path = "/test2/main")
public class MainActivity extends FlutterActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
  }
}
