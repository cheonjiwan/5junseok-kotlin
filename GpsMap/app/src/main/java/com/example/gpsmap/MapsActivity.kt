package com.example.gpsmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var REQUEST_ACCESS_FINE_LOCATION = 1000

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : MyLocationCallBack

    private var polylineOptions = PolylineOptions().width(5f).color(Color.RED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 화면 꺼지지 않게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 세로 모드로 화면 고정
        setContentView(R.layout.activity_maps)

        // SupportMapFragment를 가져와서 지도가 준비되면 알림을 받습니다.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationInit()
    }

    /*
     * 이 함수는 함수 인자 두 개를 받는다.
     * 두 함수는 모두 인자가 없고 반환값도 없다.
     * 이전에 사용자가 권한 요청을 거부한 적이 있다면 cancel() 호출, 수락되었다면 ok() 호출
     */
    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit){
        // 위치 권환이 있는지 검사
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // 권한이 허용되지 않음
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                // 이전에 권한을 한 번 거부한 적이 있는 경우에 실행
                cancel()
            }
            else{
                // 권한 요청
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION)
            }
        }
        else{
            // 권한을 수락했을 때 실행할 함수
            ok()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_ACCESS_FINE_LOCATION -> {
                if((grantResults.isNotEmpty()
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    // 권한 허용됨
                    addLocationListener()
                }
                else
                    toast("권한 거부됨" )
                return;
            }
        }
    }
    private fun showPermissionInfoDialog(){
        alert("현재 위치 정보를 얻으려면 위치 권한이 필요합니다","권한이 필요한 이유"){
            yesButton {
                // 권한 요청
                ActivityCompat.requestPermissions(this@MapsActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_ACCESS_FINE_LOCATION)
            }
            noButton { }
        }.show()
    }

    override fun onResume() {
        super.onResume()

        // 권한 요청
        permissionCheck(cancel = {
            // 위치 정보가 필요한 이유 다이얼로그 표시
            showPermissionInfoDialog()
        }, ok = {
            // 현재 위치를 주기적으로 요청 (권한이 필요한 부분)
            addLocationListener()
        })
    }

    override fun onPause() {
        super.onPause()
        removeLocationListener()
    }

    private fun removeLocationListener(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun locationInit(){
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        locationCallback = MyLocationCallBack()
        locationRequest = LocationRequest()

        // GPS 우선
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // 업데이트 인터벌
        // 위치 정보가 없을 때는 업데이트 안함
        // 상황에 따라 짧아질 수 있음, 정확하지 않음
        // 다른 앱에서 짧은 인터벌로 위치 정보를 요청하면 짧아질 수 있음
        locationRequest.interval = 10000
        // 정확함. 이것보다 짧은 업데이트는 하지 않음.
        locationRequest.fastestInterval = 5000
    }

    /**
     * 사용 가능한 맵을 조작합니다.
     * 지도를 사용할 준비가 되면 이 콜백이 호출됩니다.
     * 여기서 마커나 선, 청취자를 추가하거나 카메라를 이동할 수 있습니다.
     * 호주 시드니 근처에 마커를 추가하고 있습니다.
     * Google Play 서비스가 기기에 설치되어 있지 않은 경우 사용자에게
     * SupportMapFragment 안에 Google Play 서비스를 설치하라는 메세지가
     * 표시됩니다. 이 메서드는 사용자가 Google Play 서비스를 설치하고 앱으로
     * 돌아온 후에만 호출됩니다.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 시드니에 마커를 추가하고 카메라를 이동합니다.
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    /*
     * 위치 정보를 주기적으로 요청하는 코드는 액티비티가 화면에 보일 때만 수행하는 것이 좋다.
     * onResume() 콜백 메서드에서 위치 정보 요청을 수행하고,
     * onPaush() 콜백 메서드에서 위치 정보 요청을 삭제하는 것이 일반적인 방법.
     */

    // 권한을 요청하는 코드를 작성했지만 별도의 메서드로 지정했기 때문
    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
    }

    /*
     * requestLocationUpdates() 메서드에 전달되는 인자 중 LocationCallBack을 구현한 내부클래스
     * LocationResult 객체 반환, lastLocation 프로퍼티로 Location 객체를 얻는다.
     */
    inner class MyLocationCallBack : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            val location = p0?.lastLocation
            location?.run {
                // 14 level로 확대하고 현재 위치로 카메라 이동
                val latLng = LatLng(latitude,longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))

                Log.d("MapsActivity","위도: ${latitude}, 경도 : ${longitude}")

                // PolyLine에 좌표 추가
                polylineOptions.add(latLng)

                // 선그리기
                mMap.addPolyline(polylineOptions)
            }
        }
    }
}