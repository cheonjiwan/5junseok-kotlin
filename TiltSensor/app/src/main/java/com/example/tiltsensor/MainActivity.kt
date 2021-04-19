package com.example.tiltsensor

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager

class MainActivity : AppCompatActivity(), SensorEventListener {

    // 지연된 초기화 사용 -> sensorManager 변수를 처음 사용할 때 getSystemService() 메서드로 객체 얻기.
    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private lateinit var tiltView: TiltView // 늦은 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        // 가로모드 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tiltView = TiltView(this)
        setContentView(tiltView)
    }

    // 액티비티가 동작할 때만 센서가 동작해야 배터리를 아낄 수 있다.
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 센서 정밀도 변경되면 호출
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // 센서값이 변경되면 호출
        // values[0] : x축 값 : 위로 기울이면 -10~0, 아래로 기울이면 0~10
        // values[1] : y축 값 : 왼쪽으로 기울이면 -10~0, 오른쪽으로 기울이면 0~10
        // values[2] : z축 값 : 미사용
        event?.let{
            Log.d("MainActivity","onSensorChanged: x" +
                "${event.values[0]}, y : ${event.values[1]}, z : ${event.values[2]}")

            tiltView.onSensorEvent(event)
        }
    }
}