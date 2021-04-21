package com.example.flashlight

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TorchService : Service() {

    /*
     * Torch 클래스의 인스턴스를 얻는 방법
     * 1. onCreate() 콜백 메서드를 사용하는 방법
     * 2. by lazy를 사용하는 방법 -> 객체를 처음 사용할 때 초기화 된다.
     */
    private val torch : Torch by lazy {
        Torch(this)
    }

    private var isRunning = false

    // 외부에서 startService() 메서드로 TorchService 서비스를 호출하면
    // onStartCommand() 콜백 메서드 호출
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            // 앱에서 실행할 경우
            "on" -> {
                torch.flashOn()
                isRunning = true
            }
            "off" -> {
                torch.flashOff()
                isRunning = false
            }
            // 서비스에서 실행할 경우
            else -> {
                isRunning = !isRunning
                if(isRunning){
                    torch.flashOn()
                }
                else
                    torch.flashOff()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}