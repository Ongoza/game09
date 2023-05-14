package com.sylvernet.game09

//import com.sylvernet.game09.LoginActivity
import android.R.array
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.json.JSONArray
import org.json.JSONObject


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    private lateinit var fullscreenContent: TextView

    //val Credit_data:TextView = findViewById(R.id.Credit_data)
    var userId:Int? = 0
    var userName:String? = ""
    var userMsg:String? = ""
    //var userCredit:String? = ""
    var sess_id:String? = "0"
    var userBetLimit:Int = 0
    private var textures = arrayOfNulls<Drawable?>(8)
    private var bet = 10
    private var screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val winTable = arrayOf(
        intArrayOf(0, 20, 100, 400),
        intArrayOf(1, 20, 40, 200),
        intArrayOf(2, 10, 20, 100),
        intArrayOf(3, 20, 40, 200),
        intArrayOf(4, 8, 16, 80),
        intArrayOf(5, 10, 24, 120),
        intArrayOf(6, 5, 25, 500),
        intArrayOf(7, 40, 400, 1200)
    )
    private var testCodes_i = -1;
    private val testCodes = arrayOf(
        intArrayOf(
            0, 1, 2, 3, 4,
            7, 6, 5, 4, 3,
            0, 1, 2, 3, 4
        ),
        intArrayOf(
            0, 1, 0, 1, 0,
            7, 4, 4, 4, 7,
            7, 6, 5, 4, 3
        ),
        intArrayOf(
            1, 1, 1, 1, 0,
            6, 1, 0, 1, 6,
            3, 7, 7, 7, 3
        )
    )
    //private var propTable = intArrayOf(10, 10, 10, 10, 10, 10, 10, 5)
    private var lines = arrayOfNulls<ImageView?>(3)
    private var icons = arrayOfNulls<ImageView?>(15)

    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)
        val bundle: Bundle? = intent.extras
        val Credit_data:TextView =  findViewById(R.id.Credit_data);
        Credit_data.text = bundle?.getString("credit", "0")
        userMsg = bundle?.getString("msg", "")
        userId = bundle?.getString("id", "-1")?.toIntOrNull()!!
        userBetLimit = bundle?.getString("bet_limit", "0")?.toIntOrNull()!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sess_id = bundle?.getString("sess_id", "0")
        //checkLogin()
        Log.i("Game09 intend:", "!!! userId " + userId + "-" + userBetLimit + "-" + userMsg)
        if (screenWidth == 0) screenWidth = 1280
        isFullscreen = true
        // DecimalFormat("#,###.##")

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = findViewById(R.id.fullscreen_content)
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = findViewById(R.id.fullscreen_content_controls)

        // init icons
        icons[0] = findViewById(R.id.icon_0_0)
        icons[1] = findViewById(R.id.icon_0_1)
        icons[2] = findViewById(R.id.icon_0_2)
        icons[3] = findViewById(R.id.icon_0_3)
        icons[4] = findViewById(R.id.icon_0_4)

        icons[5] = findViewById(R.id.icon_1_0)
        icons[6] = findViewById(R.id.icon_1_1)
        icons[7] = findViewById(R.id.icon_1_2)
        icons[8] = findViewById(R.id.icon_1_3)
        icons[9] = findViewById(R.id.icon_1_4)

        icons[10] = findViewById(R.id.icon_2_0)
        icons[11] = findViewById(R.id.icon_2_1)
        icons[12] = findViewById(R.id.icon_2_2)
        icons[13] = findViewById(R.id.icon_2_3)
        icons[14] = findViewById(R.id.icon_2_4)
        icons.forEach { icon ->
                        icon?.layoutParams?.width = screenWidth/9
                        icon?.layoutParams?.height = screenWidth/9
            }

        // init textures array
        textures[0] = resources.getDrawable(R.drawable.s2_0)
        textures[1] = resources.getDrawable(R.drawable.s2_1)
        textures[2] = resources.getDrawable(R.drawable.s2_2)
        textures[3] = resources.getDrawable(R.drawable.s2_3)
        textures[4] = resources.getDrawable(R.drawable.s2_6)
        textures[5] = resources.getDrawable(R.drawable.s2_6_bell)
        textures[6] = resources.getDrawable(R.drawable.s2_7_7)
        textures[7] = resources.getDrawable(R.drawable.s2_10)

        lines[0] = findViewById(R.id.line_0)
        lines[1] = findViewById(R.id.line_1)
        lines[2] = findViewById(R.id.line_2)
        lines.forEach { line->
            line?.layoutParams?.width =  screenWidth/9*5;
            line?.layoutParams?.height =  screenWidth/9;
        }
        //initIcons()
        initDecks()
        fullscreenContentControls = findViewById(R.id.fullscreen_content_controls)

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById<Button>(R.id.exit_button).setOnTouchListener(delayHideTouchListener)

        //Log.i("AAA", "Width"+Resources.getSystem().displayMetrics.widthPixels.toString())
        //Log.i("AAA", "Height"+Resources.getSystem().displayMetrics.heightPixels.toString())

        findViewById<Button>(R.id.Spin_btn).setOnClickListener {newSpin()}}


    public fun newSpin2(){
        Log.i("AAA", "!!! new SPIN !!!!")
        //val codes  = getTestCodes()
        //getCodes()

       // Log.i("Game09 json codes=", codes.joinToString { "," })
    }


    fun initDecks(){
        //lines.forEach { it?.visibility = View.VISIBLE }
        lines.forEach{it?.visibility = View.GONE }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) { hide()
        } else { show() }
    }

    private fun showLine(i:Int) {
        lines[i]?.visibility = View.VISIBLE
        Log.i("Game09 2 show line", i.toString()+" "+lines[i]?.visibility.toString())
    }
    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    private fun newSpin(){
        var textCredit:TextView = findViewById(R.id.Credit_data)
        var textWin:TextView = findViewById(R.id.lastWin_data)
        val param = listOf("id" to userId, "bet" to bet.toString(), "sess_id" to sess_id)
        Fuel.post(getString(R.string.urlBase) + "getCodes.php", parameters = param).responseJson() { request, response, result ->
            Log.i("Game09 data", result.get().content)
            /// {"status":"success","msg":"Wellcome to Game09!","credit":4200, "name":"testUser","bet_limit":60}
            result.fold(success = {
                try {
                    val sj = JSONObject(result.get().content.replace("\uFEFF", ""))
                    //Log.i("Game09",sj.optString("msg") + sj.optString("credit") + "=" + sj.optString("win"))
                    val status = sj.optString("status")
                    if (status == "success") {
                        textCredit.text = sj.optString("credit")
                        textWin.text = sj.optString("win")
                        //val msg = sj.optString("msg")
                        val c = sj.optJSONArray("codes")
                        //showCodes(c)
                        icons.forEachIndexed() { i, icon -> icon?.setImageDrawable(textures[c.getInt(i)]) }
                        val wins = sj.optJSONArray("wins")
                        for (i in 0 until lines.size) {
                            val line = wins.optJSONArray(i)
                            val tr = line.optInt(2,0)
                            Log.i("Game09 line", i.toString()+"-"+ lines[i]?.visibility.toString())
                            lines[i]?.visibility = if(tr>0){View.VISIBLE
                            }else{ View.INVISIBLE }
                            //Log.i("Game09 show line", i.toString()+"-"+ lines[i]?.visibility.toString())
                            lines[i]?.systemUiVisibility = 4
                            //it?.visibility = if (it?.visibility){ View.VISIBLE}else{ View.INVISIBLE}
                        }
                        //wins.forEachIndexed { i, it -> if (it[2]==1) lines[i]?.visibility = View.VISIBLE }

                    } else {
                        //textLog.text = sj.optString("msg")
                        Log.i("Game09", "json error!")
                    }
                } catch (e: Exception) {
                    Log.i("Game09", e.message.toString())
                }
            }, failure = { Log.i("Game09", "server error!") })
        }
        //return outCodes
    }
    private fun getTestCodes(): IntArray {
        testCodes_i++
        if (testCodes_i > testCodes.lastIndex){testCodes_i = 0 }
        return testCodes[testCodes_i]
    }

    private fun getWinCodes(codes: IntArray): Array<IntArray> {
        var arr = arrayOf(IntArray(3), IntArray(3), IntArray(3))
        var j = 0
        var m = 0
        var winCnt = IntArray(size = winTable.size)
        for (i in 0..codes.lastIndex){
            if(j >= 5){ j = 0; m++; winCnt = IntArray(size = winTable.size)
            } else { j++ }
            for (tbr in winTable){
                if (codes[i] == tbr[0]){
                    winCnt[codes[i]] = winCnt[codes[i]] + 1
                    if (winCnt[codes[i]]>=3){
                        arr[m][0] = codes[i]
                        arr[m][1] = winCnt[codes[i]]
                        arr[m][2] = 1
                        //var code = codes[i]
                        //var num = winCnt[codes[i]]
                        //Log.i("AAA", "WIN = $code num $num")
                    }
                }
            }
        }
        return arr
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}