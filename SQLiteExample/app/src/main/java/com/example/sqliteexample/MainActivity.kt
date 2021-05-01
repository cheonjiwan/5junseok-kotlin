package com.example.sqliteexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addBtnView.setOnClickListener {
            val title = addTitleView.getText().toString()
            val content = addContentView.getText().toString()
            val helper = DBHelper(this)
            val db = helper.writableDatabase
            db.execSQL("insert into tb_memo (title, content) values (?,?)", arrayOf<String>(title,content))
            db.close()

            val intent = Intent(this,ReadDBActivity::class.java)
            startActivity(intent)
        }
    }
}