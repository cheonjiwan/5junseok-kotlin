package com.example.mygallery

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_photo.*

private const val ARG_URI = "uri"

class PhotoFragment : Fragment() {
    private lateinit var uri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        프래그먼트가 생성되면 onCreate() 메서드가 호출되고
        ARG_URI 키에 저장된 uri값을 얻어서 변수에 저장합니다.
         */
        arguments?.getParcelable<Uri>(ARG_URI)?.let {
            uri = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        /*
        onCreateView() 메서드에서는 프래그먼트에 표시될 뷰를 생성합니다.
        액티비티가 아닌 곳에서 레이아웃 리소스를 가지고 오려면
        LayoutInflater 객체의 inflate() 메서드를 사용합니다.
        R.layout.fragmnet_photo 레이아웃 파일을 가지고와서 반환
         */
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    // 뷰가 완성된 직후에 호출되는 onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*
        프래그먼트가 생성되면서 imageView에 uri 경로에 있는 사진을 로딩하는 코드
         */
        super.onViewCreated(view, savedInstanceState)
        val descriptor = requireContext().contentResolver.openFileDescriptor(uri,"r")
        descriptor?.use {
            var bitmap = BitmapFactory.decodeFileDescriptor(descriptor.fileDescriptor)
            Glide.with(this).load(bitmap).into(imageView)
        }

    }
    /*
    newInstance() 메서드를 이용하여 프래그먼트를 생성할 수 있고 인자로 uri값을 전달합니다.
    이 값은 Bundle 객체에 ARG_URI 키로 저장되고 arguments 프로퍼티에 저장됩니다.
     */
    companion object {
        @JvmStatic
        fun newInstance(uri: Uri) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
            }
    }
}