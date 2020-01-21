package com.example.customer;

import android.content.Intent;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class Splash extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {
//        ActionBar actionB


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        configSplash.setBackgroundColor(R.color.colorAccent);
        configSplash.setAnimCircularRevealDuration(1500);
        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);
        configSplash.setRevealFlagX(Flags.REVEAL_BOTTOM);

        configSplash.setLogoSplash(R.drawable.splash_icon);
        configSplash.setAnimLogoSplashDuration(1500);
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce);

        configSplash.setTitleSplash("Junk O' Scrap");
        configSplash.setTitleTextColor(R.color.colorPrimary);
        configSplash.setTitleTextSize(50f);
        configSplash.setAnimTitleDuration(500);
        configSplash.setAnimTitleTechnique(Techniques.BounceIn);
    }

    @Override
    public void animationsFinished() {
        startActivity(new Intent(Splash.this,Login.class));
        finish();
    }
}
