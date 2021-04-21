package com.example.flashlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val torch = Torch(this)

        flashSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            /*
             * buttonView -> 상태가 변경된 Switch 객체 자신
             * isChecked -> On/Off 상태
             */
            if(isChecked)
                startService(intentFor<TorchService>().setAction("on"))
            else
                startService(intentFor<TorchService>().setAction("off"))
        }
    }
}

