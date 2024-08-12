package com.example.android_open_cv

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import org.opencv.android.CameraActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.util.Collections

class MainActivity : CameraActivity() {

    private lateinit var camera: JavaCameraView
    private var cascadeClassifier: CascadeClassifier? = null
    private lateinit var mGray: Mat
    private lateinit var mRGB: Mat
    private lateinit var rects: MatOfRect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()

        camera = findViewById(R.id.main_camera_view)

        camera.setCvCameraViewListener(object: CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) {
                mGray = Mat()
                mRGB = Mat()
                rects = MatOfRect()
            }

            override fun onCameraViewStopped() {
                mGray.release()
                mRGB.release()
            }

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                inputFrame?.let {
                    mGray = it.gray()
                    mRGB = it.rgba()
                    rects.release()
                }

                cascadeClassifier?.detectMultiScale(mGray, rects, 1.1, 2)

                for (rect in rects.toArray()) {
                    Imgproc.rectangle(mRGB, rect, Scalar(0.0, 255.0, 0.0), 5)
                }

                return mRGB
            }
        })

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV is successfully loaded")
            camera.enableView()
            val stream = resources.openRawResource(R.raw.haarcascade_frontalface_alt)
            val file: File = File(getDir("cascade", MODE_PRIVATE), "haarcascade_frontalface_alt.xml")
            val outputStream = FileOutputStream(file)

            val data = ByteArray(4096)
            var readBytes: Int = stream.read(data)

            while (readBytes != -1) {
                outputStream.write(data, 0, readBytes)
                readBytes = stream.read(data)
            }

            cascadeClassifier = CascadeClassifier(file.absolutePath)
            if (cascadeClassifier!!.empty()) cascadeClassifier = null

            stream.close()
            outputStream.close()
            file.delete()
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