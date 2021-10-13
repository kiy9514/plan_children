package kr.ac.ksa.skin_censor_classifier_test1;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import kr.ac.ksa.skin_censor_classifier_test1.sqlite.MyDataBase;

public class ListViewActivity extends AppCompatActivity {

    MyDataBase myDataBase;
    SQLiteDatabase sqlDB;
    ImageView minus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        ActionBar actionBar = getSupportActionBar(); // 엑션바 선언
        actionBar.hide(); // 엑션바 숨기기


        ListView listview ;
        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        getIDXCount(adapter);


    }

    private void getIDXCount(ListViewAdapter adapter) {
        myDataBase = new MyDataBase(this);

        sqlDB = myDataBase.getReadableDatabase();

        // get Value
        Cursor cursor;
        cursor = sqlDB.rawQuery("select * from ResultTB",null);

        while (cursor.moveToNext()){
            byte[] bytes = cursor.getBlob(1);
            Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length ) ;
            adapter.addItem(cursor.getInt(0), new BitmapDrawable(getResources(), bitmap), cursor.getString(2), cursor.getString(3)) ;
        }
    }
}
