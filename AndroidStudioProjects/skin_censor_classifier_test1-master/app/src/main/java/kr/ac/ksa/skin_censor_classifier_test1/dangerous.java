package kr.ac.ksa.skin_censor_classifier_test1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static android.graphics.Typeface.BOLD;


public class  dangerous extends AppCompatActivity {
    private TextView txt_bold;
    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangerous);

        ActionBar actionBar = getSupportActionBar(); // 엑션바 선언
        actionBar.hide(); // 엑션바 숨기기

        txt_bold = (TextView) findViewById(R.id.textView7);

        link = (TextView) findViewById(R.id.link);
        String url ="https://post.naver.com/viewer/postView.nhn?volumeNo=29211479&memberNo=15460571&vType=VERTICAL";

        Spannable span = (Spannable) txt_bold.getText();
        span.setSpan(new StyleSpan(BOLD), 155, 167, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(BOLD), 225, 235, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(BOLD), 274, 282, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(BOLD), 330, 341, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(BOLD), 377, 390, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable spen = (Spannable) link.getText();
        spen.setSpan(new URLSpan(url),0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setMovementMethod(LinkMovementMethod.getInstance());


    }
}