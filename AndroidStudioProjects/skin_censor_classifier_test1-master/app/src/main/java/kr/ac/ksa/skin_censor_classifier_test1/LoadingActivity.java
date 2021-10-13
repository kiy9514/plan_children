package kr.ac.ksa.skin_censor_classifier_test1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ActionBar actionBar = getSupportActionBar(); // 엑션바 선언
        actionBar.hide(); // 엑션바 숨기기

        startAnimation();
        startLoading();
    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void startAnimation() {
        Animation anim_load = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ani_load);
        ImageView imageView = findViewById(R.id.loader);
        imageView.startAnimation(anim_load);
    }
}