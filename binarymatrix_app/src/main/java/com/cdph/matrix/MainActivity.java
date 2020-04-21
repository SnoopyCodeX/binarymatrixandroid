package com.cdph.matrix;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		setTheme(android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
