package com.example.android_open_cv

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import org.opencv.android.CameraActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.util.Collections

class MainActivity : CameraActivity() {

    private lateinit var camera: JavaCameraView
    private lateinit var caseFile: File
    private lateinit var faceDetector: CascadeClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()

        camera = findViewById(R.id.main_camera_view)

        camera.setCvCameraViewListener(object: CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) {
                //
            }

            override fun onCameraViewStopped() {
                //
            }

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                return inputFrame!!.rgba()
            }
        })

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV is successfully loaded")
            camera.enableView()
        } else {
            // Handle initialization error
            Log.d(TAG, "OpenCV initialization error")
        }
    }


    override fun getCameraViewList(): MutableList<out CameraBridgeViewBase> {
        return Collections.singletonList(camera)
    }

    private fun getPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPermission()
        }
    }

    private companion object {
        const val TAG = "GREDI"
    }
}