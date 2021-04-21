package com.example.flashlight

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

// 플래시를 켜려면 CameraManager 객체가 필요하고 이를 얻으려면 Context 객체가 필요하기 때문에
// 생성자로  Context를 받는다.
class Torch(context: Context) {
    private var cameraId: String? = null
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    init {
        cameraId = getCameraId()
    }

    // 플래시 켜기
    fun flashOn(){
        cameraManager.setTorchMode(cameraId!!, true)
    }

    // 플래시 끄기
    fun flashOff(){
        cameraManager.setTorchMode(cameraId!!, false)
    }

    private fun getCameraId(): String? {
        val cameraIds = cameraManager.cameraIdList
        for(id in cameraIds){
            val info = cameraManager.getCameraCharacteristics(id)
            val flashAvailable = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
            val lensFacing = info.get(CameraCharacteristics.LENS_FACING)
            if( flashAvailable != null
                && flashAvailable
                && lensFacing != null
                && lensFacing == CameraCharacteristics.LENS_FACING_BACK){
                return id
            }
        }
        return null
    }
}