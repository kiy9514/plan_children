package kr.ac.ksa.skin_censor_classifier_test1;

import static android.graphics.Typeface.BOLD;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.ByteArrayOutputStream;
import java.util.List;

import kr.ac.ksa.skin_censor_classifier_test1.sqlite.MyDataBase;
// import kr.ac.ksa.skin_censor_classifier_test1.databinding.ActivityPictureStateBinding;

public class picture_state extends AppCompatActivity {


    TextView main_page_go, save_results, restart;
    ImageView beforeImage;
    MyDataBase myDataBase;
    SQLiteDatabase sqlDB;
    Bitmap mybitmap;
    int idxCount;

    final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_state);

        //로딩화면 실행
        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
        startActivity(intent);

        //결과를 받아오는 공간입니다.
        TextView txt_color_red = (TextView) findViewById(R.id.result);
        fetchResult(txt_color_red);



        Spannable span = (Spannable) txt_color_red.getText();

        String str = txt_color_red.getText().toString();
        int probability = str.indexOf("의");
        int diseaseStart = str.indexOf("로") + 1 ;
        int diseaseEnd = str.indexOf("입");


        span.setSpan(new ForegroundColorSpan(Color.rgb(170,0,7)), 0, probability, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.rgb(0,77,165)), diseaseStart, diseaseEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int maxByteArrCount = 100;

        // use bundle */
        Bundle extras = getIntent().getExtras();
        int count = extras.getInt("count");
        int length = extras.getInt("length");

        byte[] byteArray = new byte[length];

        for (int i = 0; i < count; i++) {
            byte[] byteArr = extras.getByteArray("image_" + i);
            for (int j = 0; j < maxByteArrCount; j++) {
                int idx = (i * maxByteArrCount) + j;
                byteArray[idx] = byteArr[j];
                if (idx > length) break;
            }
        }

//        byte[] byteArray = getIntent().getByteArrayExtra("image");
        mybitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        beforeImage = (ImageView) findViewById(R.id.imageView3);
        beforeImage.setImageBitmap(mybitmap);
        beforeImage.setMinimumHeight(800);
        beforeImage.setMinimumWidth(800);

        main_page_go = (TextView) findViewById(R.id.main_page_go);
        save_results = (TextView)findViewById(R.id.save_results);
        restart = (TextView)findViewById(R.id.restart);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsCameraAvailable()){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        });

        main_page_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main_intent);
            }
        });

        save_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialog alert = new ViewDialog();
                if (myDBLoad() >= 0) {
                    alert.showDialog(picture_state.this, "성공적으로 저장하였습니다.", Color.rgb(34,193,195));
                } else {
                    alert.showDialog(picture_state.this, "이미 저장된 결과입니다.", Color.rgb(255,68,113));
                }
            }
        });
        getIDXCount();
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        return data;
    }

    private void getIDXCount() {
        myDataBase = new MyDataBase(this);

        sqlDB = myDataBase.getReadableDatabase();

        // get Value
        Cursor cursor;
        cursor = sqlDB.rawQuery("select idx\n" +
                "from ResultTB\n" +
                "order by idx DESC limit 1;",null);

        idxCount = 0;
        while (cursor.moveToNext()){
            idxCount = cursor.getInt(0);
        }

        Log.d("jihee", "value : " + idxCount++);
    }

    private int myDBLoad() {
        try {

            sqlDB = myDataBase.getWritableDatabase();

            SQLiteStatement statement = sqlDB.compileStatement("INSERT INTO ResultTB VALUES(?, ?, ?, datetime('now'))");
            statement.bindLong(1, idxCount);
            statement.bindBlob(2, bitmapToByte(mybitmap));
            statement.bindString(3, "테스트입니다.");
            statement.executeInsert();
            sqlDB.close();

            return 0;
        } catch (Exception e) {
            return -1;
        }

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

    public void fetchResult (TextView textView) {
        // restapi 서버 주소를 통해 결과를 textView 에 저장합니다.
        // textView.setText(restapi + "의 확률로 " + restapi + "입니다.");
    }

//    public void mainPageClick(View view) {
//        view.getId() == R.id.main_page_go {
//
//        }
//    }


//    apiInteface.getTest(body, xList, yList).enqueue(object : Callback<ResponseBody> {
//        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//            Log.d("AAA", "FAIL REQUEST ==> " + t.localizedMessage)
//            drawImageView.clear()
//        }
//
//        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//            Log.d("AAA", "REQUEST SUCCESS ==> ")
//            val file = response.body()?.byteStream()
//            val bitmap = BitmapFactory.decodeStream(file)
//            drawImageView.clear()
//        }
//    })
}