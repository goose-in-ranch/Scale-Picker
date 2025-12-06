package com.mitchell.scalepicker

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class SettingsActivity : AppCompatActivity(){

    //These are global so they can be accessed and defined throughout ALL functions and subclasses
    private lateinit var preferences: SharedPreferences
    private lateinit var listener: OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        //defining here so registering onSharedPreferenceChangeListener doesnt get pissy
        preferences = getDefaultSharedPreferences(this)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        prefsManager(preferences) //call function to handle "Enable All Pitches" button
        preferences.registerOnSharedPreferenceChangeListener(listener)


    }

    //Function to prevent user from deselecting ALL pitches or modes. At least ONE needs to be selected.
    //Keeping this code in a separate function instead of onCreate seems to be better practice
    //And allows for smoother UI regeneration with recreate()
    private fun prefsManager(preferences: SharedPreferences){
       // preferences = PreferenceManager.getDefaultSharedPreferences(this)
        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            //Defining prefsEditor here so it can be defined once and used many times by function
            val prefsEditor = sharedPreferences.edit()

            //Error checking for pitches
            var numOfSelectedPitches = 0
            for(i in 0..11) if(preferences.getBoolean("pitch_$i", true)) numOfSelectedPitches++
            if (numOfSelectedPitches == 0) {
                Toast.makeText(this,"At least one pitch must be selected", Toast.LENGTH_SHORT).show()
                prefsEditor.putBoolean(key,true)
                prefsEditor.commit()
                recreate()
            }

                //Error checking for scale types
            //First ensure that at least one scale type is selected
            if(!preferences.getBoolean("major", true) &&
                !preferences.getBoolean("minor", true) &&
                !preferences.getBoolean("modes", false)){

                Toast.makeText(this, "At least one scale type must be selected", Toast.LENGTH_SHORT).show()
                prefsEditor.putBoolean(key, true)
                prefsEditor.commit()
                recreate()
            }

            //If that check passes, now ensure at least one of each sub-category is selected
            //No need to worry about major here because that is dealt with in above code
            else if (!preferences.getBoolean("minor_natural", true) &&
                     !preferences.getBoolean("minor_harmonic", true) &&
                     !preferences.getBoolean("minor_melodic", true) ||
                     !preferences.getBoolean("modes_dorian", true) &&
                     !preferences.getBoolean("modes_phrygian", true) &&
                     !preferences.getBoolean("modes_lydian", true) &&
                     !preferences.getBoolean("modes_mixolydian", true) &&
                     !preferences.getBoolean("modes_locrian", false)){

                //Doing something different in the minor and modal sections so I can survey people
                //about which is less confusing

                //this one will be like pitch behavior
                //If all minor scales are deselected
                if (key?.substring(0,5) == "minor"){
                    prefsEditor.putBoolean(key, true)
                    prefsEditor.putBoolean("minor", false)
                    prefsEditor.commit()
                    recreate()
                    Toast.makeText(this, "Minor Scales Disabled", Toast.LENGTH_SHORT).show()
                }

                //this one will disable the category if the user attempts to deselect everything
                //if all modal scales are deselected
                else {
                    prefsEditor.putBoolean(key, true)
                    prefsEditor.putBoolean("modes", false)
                    prefsEditor.commit()
                    recreate()
                    Toast.makeText(this, "Modal Scales Disabled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    class SettingsFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            //block of code to handle the "Enable All Pitches button"
            //requireActivity() is the magic function for context in this class
            //TODO: when adding an "Enable all modes" button, this may be a great place to add it
            val noValuePreference: Preference? = findPreference("enable_all_pitches") //this may be better as a when (switch) statement when adding modal code
            noValuePreference?.setOnPreferenceClickListener {
                val preferences = getDefaultSharedPreferences(requireActivity())
                val prefsEditor = preferences.edit()
                for (i in (0..11)){
                    prefsEditor.putBoolean("pitch_$i", true)
                }
                prefsEditor.commit()
                Toast.makeText(requireActivity(), "All Pitches Enabled", Toast.LENGTH_SHORT).show()
                requireActivity().recreate()
                true
            }
        }
    }

    //makes the OS back button act like the application's UP button
    //This should allow preferences to work as expected when using back button
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK){
//            val mainIntent = Intent(this, MainActivity::class.java)
//            startActivity(mainIntent)
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//
//    }

    //these overrides are here as good practice, keeps listeners from hanging when activity isnt loaded.
    //Re-registers when activity resumes
    override fun onPause() {
        super.onPause()
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

}