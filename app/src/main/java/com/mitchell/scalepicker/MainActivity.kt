/*TODO: Modes have been implemented
  TODO: Finish designing error checking. Deciding to use the version in MODES.
  TODO: maybe do a toast message along with it.
  TODO: somehow more clearly differentiate categories (modes, minor) from sub categories (lydian, harmonic minor)
  TODO: This could be through indentation or icons
 */
/*TODO: There is a bug in which the scale displayed on screen is lost
  TODO: when the screen rotates. Please fix.
 */
/*TODO: I would like to add a feature in the preferences screen where
  TODO: users can long press a pitch or mode and all other pitches besides that one
  TODO: will be disabled.
 */
/*TODO: Add "about" activity and preference. I believe I can use an intent directly with a preference.
  TODO: Should include Name, Email(android dev), GPL stuff, version
  TODO: Also add licensing header to files

  TODO: Also add a link to the github page.
 */
package com.mitchell.scalepicker
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.preference.PreferenceManager
import java.util.Vector

class MainActivity : AppCompatActivity() {
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

        //this block handles the randomButton being tapped
        randomButton.setOnClickListener{
            pitchClassText.text = notePicker(enabledNoteNames)
            modeText.text = modePicker(enabledModeNames)
        }
    }

    //Function to return a random note name
    private fun notePicker(noteNames: Vector<String>): String {
        //commented lines below are original function
        //val noteNames = arrayOf("C", "C♯/D♭", "D", "D♯/E♭", "E", "F", "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B")
        //return noteNames.getString((0..11).random())
        //val noteNames: Array<String> = resources.getStringArray(R.array.note_names)

       // return noteNames.elementAt((0..noteNames.count()).random() - 1)
        return noteNames.random()
    }

    //Function to return a random mode
    private fun modePicker(modeNames: Vector<String>): String {
        return modeNames.random()
/*
        when ((0..2).random()) { //logic to select scales at random.
            0 -> return "Major"
            1 -> {
                when((0..2).random()){
                    0 -> return "Natural Minor"
                    1 -> return "Harmonic Minor"
                    2 -> return "Melodic Minor"
                }
            }
            2 -> {
                when((0..3).random()){
                    0 -> return "Dorian"
                    1 -> return "Phrygian"
                    2 -> return "Lydian"
                    3 -> return "Mixolydian"
                }
            }
        }
    return "error" // this return statement should never be reached
*/
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

}
