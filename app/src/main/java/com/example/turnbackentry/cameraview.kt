package com.example.turnbackentry

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.util.SparseArray
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.vision.Frame
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [cameraview.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [cameraview.newInstance] factory method to
 * create an instance of this fragment.
 */
class cameraview : Fragment() {
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
        // Inflate the layout for this fragment
        var rootview = inflater.inflate(R.layout.fragment_cameraview, container, false)

        val txtView = rootview.findViewById(R.id.txtContent) as TextView
        val myImageView = rootview.findViewById<ImageView>(R.id.imgview)
        val myBitmap = BitmapFactory.decodeResource(
            this.resources, R.drawable.index)

        val detector = BarcodeDetector.Builder(activity?.applicationContext)
            .setBarcodeFormats(Barcode.UPC_A) //Barcode.CODE_128 or Barcode.EAN_13 or Barcode.DATA_MATRIX or Barcode.QR_CODE or
            .build()
        if (!detector.isOperational) {
            txtView.setText("Could not set up the detector!")
        }

        myImageView.setImageBitmap(myBitmap)

        val bprocess = rootview.findViewById<Button>(R.id.bprocess);

        bprocess.setOnClickListener { view ->
            val frame = Frame.Builder().setBitmap(myBitmap).build()
            val barcodes = detector.detect(frame)

            val thisCode = barcodes.size() //.valueAt(0)
            //txtView.text = "scanning..."
            Snackbar.make(view , "scanning...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            txtView.text = thisCode.toString() //.rawValue

        }
        return rootview
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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
