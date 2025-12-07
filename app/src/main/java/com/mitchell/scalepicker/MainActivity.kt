/*TODO: I would like to add a feature in the preferences screen where
  TODO: users can long press a pitch or mode and all other pitches besides that one
  TODO: will be disabled.
 */
/*TODO FEATURE: I would like to add a sets feature (maybe accessible through the appbar)
  TODO: 	this way people can create and save a set in the preferences.
 */
package com.mitchell.scalepicker
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.Vector

class MainActivity : AppCompatActivity() {

    //Global strings used for passing things between TextViews and savedInstanceState
    private var pitchClassSavedText = ""
    private var modeSavedText = "" //set later as the startup message. Cannot be set here because it is not in scope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //shared preference and condition to set or unset "Screen Always On"
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(sharedPrefs.getBoolean("display_always_on", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //get enabled note names and store in a vector to be passed to notePicker
        val enabledNoteNames = Vector<String>(0)
        val noteNamesArr = resources.getStringArray(R.array.note_names)
        for (i in (0..11)) if(sharedPrefs.getBoolean("pitch_$i", true)) enabledNoteNames.add(noteNamesArr[i])

        //get enabled modes and store in vector to be passed to modePicker
        val enabledModeNames = Vector<String>(0)
        if (sharedPrefs.getBoolean("major", true)) enabledModeNames.add("Major") //if major is enabled, add major. (modeNamesArr[0] is major)
        if (sharedPrefs.getBoolean("minor", true)){
            if(sharedPrefs.getBoolean("minor_natural", true)) enabledModeNames.add("Natural Minor")
            if(sharedPrefs.getBoolean("minor_harmonic", true)) enabledModeNames.add("Harmonic Minor")
            if(sharedPrefs.getBoolean("minor_melodic", true)) enabledModeNames.add("Melodic Minor")
        }
        if (sharedPrefs.getBoolean("modes", false)){
            if(sharedPrefs.getBoolean("modes_dorian", true)) enabledModeNames.add("Dorian")
            if(sharedPrefs.getBoolean("modes_phrygian", true)) enabledModeNames.add("Phrygian")
            if(sharedPrefs.getBoolean("modes_lydian", true)) enabledModeNames.add("Lydian")
            if(sharedPrefs.getBoolean("modes_mixolydian", true)) enabledModeNames.add("Mixolydian")
            if(sharedPrefs.getBoolean("modes_locrian", false)) enabledModeNames.add("Locrian")
        }

        //defining TextView objects to be updated by RNG
        val pitchClassText: TextView = findViewById(R.id.scale_pitch) //Text for letter name to display. (A, C#, Bb etc)
        val modeText: TextView = findViewById(R.id.scale_mode)  //Text for mode. (Major, Harmonic Minor, Phrygian, etc)
        val randomButton: Button = findViewById(R.id.button)   //Button to generate scale

        //sets the string for savedInstanceState to the default startup message.
        //This is to prevent the screen from clearing and looking silly if the user rotates the screen before picking a scale
        modeSavedText = resources.getString(R.string.start_message)

        //restore scale pitch and mode from savedInstanceState
        //check if there is a saved instance state in the bundle
        if (savedInstanceState != null) {
            pitchClassText.text = savedInstanceState.getString("pitch_class_tag")
            modeText.text = savedInstanceState.getString("mode_text_tag")
            setNoteAndMode(pitchClassText, modeText)
        }

        //this block handles the randomButton being tapped
        randomButton.setOnClickListener{
            pitchClassText.text = notePicker(enabledNoteNames)
            modeText.text = modePicker(enabledModeNames)
            setNoteAndMode(pitchClassText, modeText)
        }
    }

    //Function to return a random note name
    private fun notePicker(noteNames: Vector<String>): String {
        return noteNames.random()
    }

    //Function to save values to global strings to be used by savedInstanceState.
    private fun setNoteAndMode(pitchClassText: TextView, modeText: TextView){
        pitchClassSavedText = pitchClassText.text.toString()
        modeSavedText = modeText.text.toString()
    }

    //Function to return a random mode
    private fun modePicker(modeNames: Vector<String>): String {
        return modeNames.random()
    }

    //function to create the AppBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    //function to handle button in AppBar being clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)


    }

    //This function makes app switch to home screen when back button is pressed in main activity
    //Without this block, the app would switch back and forth between MainActivity and SettingsActivity
    //in a loop if SettingsActivity had been visited (Because of Settings onkeydown being required).
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { //TODO Deprecating soon. Find an alternative
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    //Used to save UI TextViews to preserve them during rotation/multi-window type events.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("pitch_class_tag", pitchClassSavedText)
        outState.putString("mode_text_tag", modeSavedText)
        super.onSaveInstanceState(outState)
    }
}
