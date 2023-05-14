package com.sylvernet.game09

import android.content.Intent
import android.preference.PreferenceManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.
//import androidx.datastore.preferences.preferencesDataStore
import kotlinx.android.synthetic.main.activity_login.*
//import org.json.JSONObject
//import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    //private val dataStore: DataStore<String>=context.createDataStore(name="Login")
    private var lgn = ""
    private var psw = ""
   // private val sLgn = preferencesKey<String>("lgn")
  //  private val sPsw = preferencesKey<String>("psw")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("Game09","start 2" )
        val mPrefs = getPreferences(MODE_PRIVATE) ?: return

        val lgn = mPrefs.getString("Lgn","")
        val psw = mPrefs.getString("Psw","")
        Log.i("Game09 !!!","Lgn:"+lgn )
        if(!lgn.isNullOrEmpty() && !psw.isNullOrEmpty()){
            Log.i("Game09 ","startconnect!!:"+lgn+"=="+psw )
            connectToServer(lgn, psw)
        }else{ findViewById<Button>(R.id.lgnBtn).setOnClickListener {btnLoginClick()}
        }
    }

    //private fun testJson(){
    //    val jsonString = """{"type":"Foo", "data":[{"id":1,"title":"Hello"},{"id":2,"title":"World"}]}}"""
    //    val foos = Response(jsonString)
    //    val type: String? = foos.optString("type")
    //    Log.d("Game09", "This is my message type:" + type);
    //}

    private fun btnLoginClick(){
       // Log.i("Game09","btnLoginClick!" )
        val sp = getPreferences(MODE_PRIVATE) ?: return
        lgn = findViewById<EditText>(R.id.eLogin).text.toString()
        psw = findViewById<EditText>(R.id.ePassword).text.toString()
        with(sp.edit()) {
            putString("Lgn", lgn)
            putString("Psw", psw)
            apply()
            Log.i("Game09","apply!"+lgn+psw )
        }
        connectToServer(lgn, psw)
    }

    private fun connectToServer(lgn:String?, psw:String?){
        Log.i("Game09","connectToServer!" )
        val textLog:TextView = findViewById(R.id.LogText)
        if(lgn != "" && psw != ""){
            //val values = mapOf("name" to "John Doe", "occupation" to "gardener")
            //val objectMapper = ObjectMapper()
            var data = ""
            try {
                //
                val param = listOf("username" to lgn.toString(), "password" to psw.toString(), "login" to "1")
                Log.i("Game09", param.joinToString(" "))
                //var jStr = "{\"login\":\"1\",\"username\":\""+lgn+"\"}"
                //var jStr = "[\"login\"=>\"1\",\"username\"=>\""+lgn+"\"]"
                Fuel.post(getString(R.string.urlBase)+"login_user.php", parameters = param).responseJson() { request, response, result ->
                    Log.i("Game09 data", result.get().content)
                    /// {"status":"success","msg":"Wellcome to Game09!","credit":4200, "name":"testUser","bet_limit":60}
                    // {"status":"success","msg":"Wellcome to Game09!","credit":"4430","name":"testUser","bet_limit":"60"}
                    result.fold(success = {
                            try{
                                val sj =  JSONObject(result.get().content.replace("\uFEFF",""))
                                Log.i("Game09 msg", sj.optString("msg"))
                                val status = sj.optString("status")
                                Log.i("Game09 status:", status)
                                if (status=="success"){
                                    startMainActivity(sj)
                                }else{textLog.text = sj.optString("msg")}
                            }catch(e: Exception){Log.i("Game09", "error json!")}
                        }, failure = {Log.i("Game09", "server error!")})
                    }
                } catch (e: Exception) {
                    print(e.message)
                    textLog.text = "Server error!!"
                    Log.e("Game09","Error get post request!" )
                }

            textLog.text = data
        }else{
            Log.i("Game09","Login and password can not be empty!" )
            textLog.text = "Login and password can not be empty!"
        }
        }

    private fun startMainActivity(sj:JSONObject){
        var myIntent = Intent(this@LoginActivity, FullscreenActivity::class.java)
        myIntent.putExtra("id", sj.optString("id"))
        myIntent.putExtra("credit", sj.optString("credit"))
        myIntent.putExtra("sess_id", sj.optString("sess_id"))

        myIntent.putExtra("name", sj.optString("name"))
        myIntent.putExtra("bet_limit", sj.optString("bet_limit"))
        myIntent.putExtra("msg", sj.optString("msg"))
        Log.i("Game09 json","id-"+sj.optString("id")+"-"+sj.optInt("credit")+"-"+sj.optString("name")+"-"+sj.optString("bet_limmit")+"-" )
        startActivity(myIntent)
    }

  }

//class Response(json: String) : JSONObject(json) {
//    val type: String? = this.optString("type")
//    val data = this.optJSONArray("data")
//        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
//        ?.map { Foo(it.toString()) } // transforms each JSONObject of the array into Foo/
//}

//class Foo(json: String) : JSONObject(json) {
//    val id = this.optInt("id")
//    val title: String? = this.optString("title")
//}