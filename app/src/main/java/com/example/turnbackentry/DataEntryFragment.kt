package com.example.turnbackentry


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.os.AsyncTask
import java.io.*
//import javax.swing.UIManager.put
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "DATA ENTRY: "
public var sourceurl = "http://192.168.20.19:8000/forms/turnback"
public lateinit var order: TextView
public val ARG_ORDER = ""
/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DataEntryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DataEntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DataEntryFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var barcode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            barcode = it.getString("barcode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootview = inflater.inflate(R.layout.fragment_data_entry, container, false)

        var submit = rootview.findViewById<Button>(R.id.xsubmit)
        var department = rootview.findViewById<TextView>(R.id.xdepartment)
        var order = rootview.findViewById<TextView>(R.id.xproductionorder)
        var occur = rootview.findViewById<TextView>(R.id.xoccurence)
        var description = rootview.findViewById<TextView>(R.id.xdesciption)
        var myurl = rootview.findViewById<TextView>(R.id.xurl)
        var test = rootview.findViewById<Button>(R.id.test)

        Log.d(TAG, "barcode: "+barcode)
        if (barcode !=null) {
            order.text = barcode
        }
        var url = URL("https://www.google.com")
        var digitalqcpcurl = "192.168.0.2:80/forms/turnback"
        //"192.168.122.1:8000/dashboard/home"
        val jsonObj = JSONObject()

        //var json = new JSONObject()

        //JSONObject reader = new JSONObject();
        //order.text = com.example.turnbackentry.orderval



        //todo: bodyparser
        submit.setOnClickListener {
            jsonObj.put("department", department.text.toString()) // Set the first name/pair
            jsonObj.put("order", order.text.toString())
            jsonObj.put("occurence", occur.text.toString())
            jsonObj.put("description", description.text.toString())
            Log.d("main onclick", jsonObj.toString())

            var inputurl = myurl.text.toString()
            if (inputurl != "") {
                 sourceurl = inputurl
            }
            Log.d(TAG, "url: " + sourceurl)


            val postRequest = object : StringRequest(Request.Method.POST,
                sourceurl,
                Response.Listener { response ->
                    try {
                        Log.d(TAG, response.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error -> error.printStackTrace()
            }) {
                override fun getBody(): ByteArray {
                    Log.d(TAG, jsonObj.toString())
                    return  jsonObj.toString().toByteArray(Charsets.UTF_8);
                }

            }

            Volley.newRequestQueue(context).add(postRequest)

            /*

            val postRequest = object : StringRequest(Request.Method.POST, sourceurl,

                Response.Listener { response ->
                    try {
                        val jsonResponse = JSONObject(response).getJSONObject("form")
                        val site = jsonResponse.getString("site")
                        val network = jsonResponse.getString("network")
                        println("Site: $site\nNetwork: $network")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error -> error.printStackTrace() }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    // the POST parameters:
                    params.put("site", "code")
                    params.put("network", "tutsplus")
                    return params
                }
            }
             */
        }


        test.setOnClickListener {
        }




        // Inflate the layout for this fragment
        return rootview
    }

    private fun testVolley (url : URL) {

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, url.toString(),
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Toast.makeText(
                    context,
                    "Response is: ${response.substring(0, 500)}",
                    Toast.LENGTH_SHORT
                )
            },
            Response.ErrorListener { Toast.makeText(context, "No Response", Toast.LENGTH_SHORT) })
        queue.add(stringRequest)
    }


    //set context variables if required
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
         * @return A new instance of fragment DataEntryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DataEntryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}