package com.example.sqliteexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_read_d_b.*

class ReadDBActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_d_b)

        val helper = DBHelper(this)
        val db = helper.writableDatabase
        val cursor = db.rawQuery("select title, content from tb_memo order by _id desc limit 1", null)
        while(cursor.moveToNext()){
            readTitleView.setText(cursor.getString(0))
            readContentView.setText(cursor.getString(1))
        }
        db.close()
    }
}