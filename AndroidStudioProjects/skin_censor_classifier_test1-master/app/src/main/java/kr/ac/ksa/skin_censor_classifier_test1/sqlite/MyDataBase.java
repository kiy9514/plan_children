package kr.ac.ksa.skin_censor_classifier_test1.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBase extends SQLiteOpenHelper {
    public MyDataBase(Context context) {
        super(context, "ResultDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ResultTB ( " +
                "idx INTEGER PRIMARY KEY," +
                "image BLOB, " +
                "result varchar(20), " +
                "date DATETIME default current_timestamp " +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ResultTB");
        onCreate(db);

    }
}