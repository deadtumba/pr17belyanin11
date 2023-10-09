package com.example.pr17belyanin;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

    final String LOG_TAG = "myLogs";
    Button btnAdd, btnRead, btnClear;
    EditText etName, etAnimal, etHeight, etSize;
    DBHelper dbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etAnimal = (EditText) findViewById(R.id.etAnimal);
        etHeight = findViewById(R.id.etHeight);
        etSize = findViewById(R.id.etSize);
        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View v) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода
        String name = etName.getText().toString();
        String animal = etAnimal.getText().toString();
        int height = Integer.parseInt(etHeight.getText().toString());
        int size = Integer.parseInt(etSize.getText().toString());
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (v.getId() == R.id.btnAdd)
        {
            Log.d(LOG_TAG, "--- Добавление в БД: ---");
            // подготовим данные для вставки в виде пар: наименование столбца - значение

            cv.put("name", name);
            cv.put("animal", animal);
            cv.put("height", height);
            cv.put("size", size);
            // вставляем запись и получаем ее ID
            long rowID = db.insert("mytable", null, cv);
            Log.d(LOG_TAG, "Rows id, ID = " + rowID);
        } else if (v.getId() == R.id.btnRead)
        {
            Log.d(LOG_TAG, "--- Записи в таблице: ---");
            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            // ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (c.moveToFirst()) {

                // определяем номера столбцов по имени в выборке
                int idColIndex = c.getColumnIndex("id");
                int nameColIndex = c.getColumnIndex("name");
                int animalColIndex = c.getColumnIndex("animal");
                int heightColIndex = c.getColumnIndex("height");
                int sizeColIndex = c.getColumnIndex("size");

                do {
                    // получаем значения по номерам столбцов и пишем все в лог
                    Log.d(LOG_TAG,
                            "ID = " + c.getInt(idColIndex) +
                                    ", name = " + c.getString(nameColIndex) +
                                    ", animal = " + c.getString(animalColIndex) +
                                    ", height = " + c.getString(heightColIndex) +
                                    ", weight = " + c.getString(sizeColIndex));
                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то false - выходим из цикла
                } while (c.moveToNext());
            } else
                Log.d(LOG_TAG, "0 rows ");
            c.close();
        } else if (v.getId() == R.id.btnClear) {
            Log.d(LOG_TAG, "--- Очистить таблицу: ---");
            // удаляем все записи
            int clearCount = db.delete("mytable", null, null);
            Log.d(LOG_TAG, "Count deleted rows = " + clearCount);
        }
        // закрываем подключение к БД
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "animal text,"
                    + "height text,"
                    + "size text" + ");"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}