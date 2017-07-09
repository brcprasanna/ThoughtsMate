package ram.king.com.makebharathi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.util.AppConstants;
import ram.king.com.makebharathi.util.AppUtil;
import ram.king.com.makebharathi.util.SampleSlide;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackButtonVisibilityWithDone(true);

        addSlide(SampleSlide.newInstance(R.layout.intro_custom_layout1));
        addSlide(SampleSlide.newInstance(R.layout.intro_custom_layout2));
        addSlide(SampleSlide.newInstance(R.layout.intro_custom_layout3));

        setSlideOverAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goToHome();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        goToHome();
    }

    public void goToHome()
    {
        AppUtil.putBoolean(this, AppConstants.FIRST_TIME_LOGIN,false);
        startActivity(new Intent(AppIntroActivity.this, MainActivity.class));
        finish();
    }
}
