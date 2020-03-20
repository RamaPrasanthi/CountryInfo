package com.quest.countryinfo

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import model.CountryData
import model.DataPoint
import java.io.UnsupportedEncodingException
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog


class DataMainActivity : AppCompatActivity() {

    private val countryDataList: MutableList<DataPoint> = mutableListOf()
    private lateinit var myAdapter: ListDataAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var recycler_view: RecyclerView
    lateinit var refresh: SwipeRefreshLayout
    private val TAG = "DataMainActivity"
    val apiUrl: String = "https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/facts.json"
    lateinit var sharedprefrences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_view = findViewById<RecyclerView>(R.id.list_view)
        refresh = findViewById(R.id.refresh)
        sharedprefrences=PreferenceManager.getDefaultSharedPreferences(this)
        sharedprefrences.edit()
        val isFirstRun:Boolean=sharedprefrences.getBoolean("IS_FIRST_RUN", true)
        if (isFirstRun)
        {
            if (!isNetworkConnected()) {
                showAlertDialog()
            }
        }
        setAdapter()
        /**
         * Refreshing the data
         **/
        refresh.setOnRefreshListener {
            countryDataList.clear()
            setAdapter()
            refresh.isRefreshing = false
        }

    }

    private fun showAlertDialog() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Please Turn on Internet and refresh the page")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })
            // negative button text and action
            .setNegativeButton("Close", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }


    /**
     * Creation of RecyclerViewAdapter
     */
    fun setAdapter() {
        myAdapter = ListDataAdapter(countryDataList, this)
        // Creates a vertical Layout Manager
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = myAdapter
        getDataList()
    }

    /**
     * Method to retrieve the data from API.
     */
    fun getDataList() {
        val actionBar = supportActionBar
        progressDialog = ProgressDialog(this)
        showDialog()

        val cache = CountryInfoController.instance?.countryInfoRequestQueue?.cache
        val entry = cache?.get(apiUrl)
        if (entry != null) {
            try {
                val data = String(entry.data, Charsets.UTF_8)
                setData(data, true)
            } catch (e: UnsupportedEncodingException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

        } else {

                callJsonObjectRequest()
            }


    }

    /**
     * Instantiate JsonObjectRequest
     */
    private fun callJsonObjectRequest() {
        showDialog()
        val strReq = JsonObjectRequest(apiUrl, null, Listener { response ->
            Log.d(TAG, response.toString())
            setData(response.toString(), false)
            dismissDialog()
        }, Response.ErrorListener {errorResponse ->
            Toast.makeText(applicationContext, "Fail load Data"+errorResponse.message, Toast.LENGTH_SHORT).show()
            dismissDialog()
        })
        // Adding request to request queue
        CountryInfoController.instance?.addToRequestQueue(strReq)
    }

    /**
     * Fecthing the response from Gson library
     */

    private fun setData(response: String, isCache: Boolean) {
        //Log.d(TAG, response.toString());
        val countryInfoList = Gson().fromJson(response, CountryData::class.java)
        countryDataList.clear()
        countryInfoList?.rows?.let { countryDataList.addAll(it) }
        actionBar?.title = countryInfoList?.title
        myAdapter.notifyDataSetChanged()
        if (isCache) {
            Toast.makeText(applicationContext, "Loading from Volley Cache", Toast.LENGTH_SHORT).show()
            dismissDialog()
        }
    }

    private fun dismissDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun showDialog() {
        if (!progressDialog.isShowing) {
            progressDialog.show()
        }
    }
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
    }


    override fun onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        val editor: SharedPreferences.Editor  = sharedprefrences.edit();
        // Update
        editor.putBoolean("IS_FIRST_RUN", false);
        editor.commit();
    }
    override fun onBackPressed() {
        super.onBackPressed()
        onDestroy()
    }

}
