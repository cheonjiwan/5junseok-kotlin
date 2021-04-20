package com.example.mygallery

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // 권한이 허용되지 않음
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                // 이전에 이미 권한이 거부되었을 때
                alert("사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다", "권한이 필요한 이유"){
                    yesButton{
                        // 권한 요청
                        ActivityCompat.requestPermissions(this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    noButton{}
                }.show()
            }
            else{
                // 권한 요청
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE)
            }
        }else{
            getAllPhotos()
        }
    }

    private fun getAllPhotos(){
        val fragments = mutableListOf<Fragment>()
        // 모든 사진 정보 가져오기
        val query = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC")

        // Scoped Storage 대응
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)

                fragments.add(PhotoFragment.newInstance(contentUri))
            }
        }


        // 어댑터
        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter

        // 3초마다 자동 슬라이드
        timer(period = 3000){
            runOnUiThread {
                if(viewPager.currentItem < adapter.count - 1){ // 현재 페이지가 마지막 페이지가 아니라면
                    viewPager.currentItem = viewPager.currentItem+1 // 다음 페이지로 변경
                }
                else{ // 마지막 페이지라면
                    viewPager.currentItem = 0 // 첫 페이지로 변경
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    // 권한 허용됨
                    getAllPhotos()
                }
                else{
                    // 권한 거부
                    toast("권한 거부 됨")
                }
                return;
            }
        }
    }
}