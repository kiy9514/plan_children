package kr.ac.ksa.skin_censor_classifier_test1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button camera, myreport, dangerous_criteria;
    ImageView img;

    final int CAMERA_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태창 없애기
        setContentView(R.layout.activity_main);
        Log.d("Hello ", "hello :  ");
        ActionBar actionBar = getSupportActionBar(); // 엑션바 선언
        actionBar.hide(); // 엑션바 숨기기

        camera = (Button)findViewById(R.id.pictures);
        myreport = (Button)findViewById(R.id.myreport);
        dangerous_criteria = (Button)findViewById(R.id.dangerous_criteria);
        img = (ImageView)findViewById(R.id.classification_picture);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsCameraAvailable()){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, CAMERA_REQUEST_CODE);

                }
            }
        });

        myreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("죄송합니다.");
//                builder.setMessage("아직 구현하지 못했습니다.");
//                builder.setNegativeButton("확인",null);
//                builder.create().show();
                Intent list_intent = new Intent(getApplicationContext(), ListViewActivity.class);
                startActivity(list_intent);
            }
        });

        dangerous_criteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dger_intent = new Intent(getApplicationContext(), dangerous.class);
                startActivity(dger_intent);
            }
        });


    }

    // 사진 찍은 후 사진 저장, 취소 할 경우
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                // 찍은 사진 전송 파트
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                Bitmap bitmap = (Bitmap) bundle.get("data");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                int maxByteArrCount = 100;

                Integer sendCount = (int) Math.ceil(byteArray.length / maxByteArrCount);
                Log.d("send Count ", "value :  " + sendCount);

                Intent pic_af_iten = new Intent(this, picture_state.class);

                for (int i = 0; i < sendCount; i++) {
                    byte[] bytes = new byte[maxByteArrCount];
                    for (int j = 0; j < maxByteArrCount; j++) {
                        int idx = (i * maxByteArrCount) + j;

                        bytes[j] = byteArray[idx];
                    }
                    pic_af_iten.putExtra("image_" + i, bytes);
                }

                pic_af_iten.putExtra("count", sendCount);
                pic_af_iten.putExtra("length", byteArray.length);
                startActivity(pic_af_iten);
            }
        }catch(Exception e){
            onStop(); // 카메라만 꺼짐
            // finish(); // 어플이 꺼짐
        }
    }

    // 카메라 활성화
    public boolean IsCameraAvailable(){
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


}