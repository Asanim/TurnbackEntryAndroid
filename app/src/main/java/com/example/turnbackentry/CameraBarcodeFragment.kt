package com.example.turnbackentry

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.util.Log
import android.util.Size
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.util.SparseArray
import android.view.*
import android.widget.*
import com.google.android.gms.vision.Frame
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.BufferedOutputStream
import java.io.File
import java.util.concurrent.Executors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val REQUEST_CODE_PERMISSIONS = 10
private const val TAG = "Camera View"
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
public var result = "";
/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [cameraview.OnFragmentInteractionListener] interfvar ace
 * to handle interaction events.
 * Use the [cameraview.newInstance] factory method to
 * create an instance of this fragment.
 */
class cameraview : Fragment() {
    /*
    internal var callback: OnBarcodeResult

    fun setBarcodeResultListener(callback: OnBarcodeResult) {
        this.callback = callback
    }
    */

    var orderlistener : OnBarcodeResultListener? = null

    interface OnBarcodeResultListener {
        fun onBarcodeResult(barcode: String)
    }


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //todo: get from camera
        Log.d("camera frag", "sending result...")

        //
        // ("barcode result test")

        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_cameraview, container, false)
        viewFinder = rootview.findViewById(R.id.view_finder)

        if (allPermissionsGranted()) {
            viewFinder.post{startCamera()}
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }


        rootview.findViewById<Button>(R.id.bprocess).setOnClickListener {
            //Toast.makeText(context, TAG, "process clicked", Toast.LENGTH_LONG).show()/
            Toast.makeText(viewFinder.context, "process clicked", Toast.LENGTH_SHORT).show()
             simplebarcodedetect(viewFinder.bitmap);
        }
        return rootview
    }

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView
    private lateinit var rootview: View

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCamera () {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0);

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        val imageCapture = ImageCapture(imageCaptureConfig)

        rootview.findViewById<ImageButton>(R.id.capture_button).setOnClickListener {

            imageCapture
                .takePicture(executor,
                object : ImageCapture.OnImageCapturedListener() {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            Toast.makeText(viewFinder.context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCaptureSuccess(image: ImageProxy?, rotationDegrees: Int) {
                        Log.e("CameraXApp", "Capture success")
                        if (image != null) {
                            barcodeDetect(image.image!!, rotationDegrees)
                        } else {
                            Toast.makeText(viewFinder.context, "image not found", Toast.LENGTH_SHORT).show()
                        }
                        super.onCaptureSuccess(image, rotationDegrees)
                    }
                })
        }




        CameraX.bindToLifecycle(this, preview)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun updateTransform() {
        val matrix = Matrix();
        val centreX = viewFinder.width / 2f
        val centrey = viewFinder.height / 2f

        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centreX, centrey )

        viewFinder.setTransform(matrix)

    }

    private fun simplebarcodedetect (myBitmap: Bitmap) {
        val txtView = rootview.findViewById(R.id.txtContent) as TextView
        //val myImageView = rootview.findViewById<ImageView>(R.id.imgview)
        //val myBitmap = BitmapFactory.decodeResource( this.resources, R.drawable.index)
        //myImageView.setImageBitmap(myBitmap)
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
            .build()
        //val imgrotation = FirebaseVisionImageMetadata.ROTATION_0;
        //image must be upright!
        val image = FirebaseVisionImage.fromBitmap(myBitmap); //fromMediaImage(myBitmap, imgrotation)
        val detector = FirebaseVision.getInstance().visionBarcodeDetector;
        val result = detector.detectInImage(image)
            .addOnSuccessListener { barcodes ->
                var stringvalue = "" //= emptyArray<String>();

                var index = 0;
                for (barcode in barcodes) {
                    stringvalue = stringvalue + " " + barcode.rawValue.toString()
                    index++
                }
                //val rawValue = barcode.rawValue.
                txtView.text = stringvalue.toString()
                result = stringvalue.toString()
                //orderval = stringvalue.toString()

                orderlistener?.onBarcodeResult(result)
                //todo: input field with value

            }
            .addOnFailureListener { exception ->
            }
    }


    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    private fun barcodeDetect (image :Image, rotation :Int) {

        val txtView = rootview.findViewById(R.id.txtContent) as TextView

        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
            .build()

        val imgrotation = FirebaseVisionImageMetadata.ROTATION_0;

        val image = FirebaseVisionImage.fromMediaImage(image, degreesToFirebaseRotation(rotation))

        val detector = FirebaseVision.getInstance().visionBarcodeDetector;
        val result = detector.detectInImage(image)
            .addOnSuccessListener { barcodes ->
                var stringvalue = "" //= emptyArray<String>();

                var index = 0;
                if (barcodes != null) {
                    for (barcode in barcodes) {
                        stringvalue = stringvalue + " " + barcode.rawValue.toString()
                        index++
                    }
                } else {
                    Toast.makeText(context, "Barcode not detected", Toast.LENGTH_LONG)
                }
                //val rawValue = barcode.rawValue.
                txtView.text = stringvalue.toString();
                //todo: input field with value

            }
            .addOnFailureListener { exception ->
            }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    //used viewfinder context...
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            viewFinder.context , it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnBarcodeResultListener) {
            orderlistener = context

        } else {
            throw RuntimeException(context.toString() + " must implement OnBarcodeResultListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment cameraview.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            cameraview().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
