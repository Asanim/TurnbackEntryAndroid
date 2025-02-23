package com.example.turnbackentry

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T





class MainActivity : FragmentActivity(), cameraview.OnBarcodeResultListener, CommFragment.TextClickedListener {
    var TAG = "Main Activity"

    override fun onBarcodeResult(barcode: String) {
        Log.d(TAG, "text sent: " + barcode)
        Toast.makeText(this, "barcode main activity" + barcode, Toast.LENGTH_LONG)

        val args = Bundle()
        args.putString("barcode", barcode);

        val newFragment = DataEntryFragment()
        newFragment.arguments = args

        val transaction = supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_content, newFragment)
            addToBackStack(null)
        }
        transaction.commit();
        //}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Main started...")
        var next = findViewById<Button>(R.id.bnext)

//        var contentview = findViewById<LinearLayout>(R.id.main_content);

        if (findViewById<LinearLayout>(R.id.main_content) != null) {
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            val firstFragment = DataEntryFragment()

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.arguments = intent.extras

            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager.beginTransaction()
                .add(R.id.main_content, firstFragment).commit()
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Scan License Tag", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            val newFragment = cameraview()
            //Bundle args = Bundle()
            //args.putInt(cameraview.ARG_POSITION, position)
            //newFragment.arguments = args

            val transaction = supportFragmentManager.beginTransaction().apply {
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                replace(R.id.main_content, newFragment)
                addToBackStack(null)
            }
            transaction.commit();
        }

        next.setOnClickListener { view ->

            Snackbar.make(view, "Opening webpage", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            val newFragment = WebViewFragment(); //cameraview()
            //Bundle args = Bundle()
            //args.putInt(cameraview.ARG_POSITION, position)
            //newFragment.arguments = args

            val transaction = supportFragmentManager.beginTransaction().apply {
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                replace(R.id.main_content, newFragment)
                addToBackStack(null)
            }
            transaction.commit();
        }

        findViewById<Button>(R.id.btest).setOnClickListener { view ->

            Snackbar.make(view, "Opening test page", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            val newFragment = CommFragment(); //cameraview()

            val transaction = supportFragmentManager.beginTransaction().apply {
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                replace(R.id.main_content, newFragment)
                addToBackStack(null)
            }
            transaction.commit();
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }





    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment is CommFragment) {
            //fragment.setOnTextClickedListener(this)
            //fragment.setOnTextClickedListener() // OnBarcodeResultListener(this)
        }
        if (fragment is cameraview) {

        }
    }

    override fun sendText(text: String) {
        // Get FragmentB
        //val callingFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_b) as FragmentB
        //calling the updateText method of the FragmentB
        //callingFragment.updateText(text)

        Log.d(TAG, "text sent: " + text)
        Toast.makeText(this, "barcode main activity" + text, Toast.LENGTH_LONG)
    }
}
