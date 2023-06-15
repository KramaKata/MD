package com.c23pc657.kramakata.ui.machinelearning

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.c23pc657.kramakata.R
import com.c23pc657.kramakata.R.id
import com.c23pc657.kramakata.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MLActivity : AppCompatActivity() {private var result: TextView? = null
    private var confidence: TextView? = null
    private var imageView: ImageView? = null
    private var picture: Button? = null
    private var imageSize = 224
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mlactivity)
        result = findViewById(id.result)
        confidence = findViewById(id.confidence)
        imageView = findViewById(id.imageView)
        picture = findViewById(id.button)
        picture?.setOnClickListener(View.OnClickListener {
            // Launch camera if we have permission
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 1)
            } else {
                //Request camera permission if we don't have it.
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        })
    }

    private fun classifyImage(image: Bitmap?) {
        try {
            val model = Model.newInstance(applicationContext)

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)
            image!!.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            val classes = arrayOf("ha", "na", "ca")
            result!!.text = classes[maxPos]
            var s = ""
            for (i in classes.indices) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100)
            }
            confidence!!.text = s

            // Releases model resources if no longer used.
            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            var image = data!!.extras!!["data"] as Bitmap? // START OF IMAGE
            val dimension = Math.min(image!!.width, image.height)
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            imageView!!.setImageBitmap(image)
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
            classifyImage(image)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}