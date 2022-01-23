package com.example.fullvolume

import android.database.ContentObserver
import android.media.AudioManager
import android.media.AudioManager.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.System.CONTENT_URI
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var ringerVolumeText: TextView
    private lateinit var callVolumeText: TextView
    private lateinit var ringerMaxVolumeButton: Button
    private lateinit var callMaxVolumeButton: Button


    private lateinit var audioManager: AudioManager
    private val volumeSettingObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            onVolumeChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentResolver.registerContentObserver(CONTENT_URI, true, volumeSettingObserver)
        ringerVolumeText = findViewById(R.id.ringerVolumePercentage_text)
        callVolumeText = findViewById(R.id.callVolumePercentage_text)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        ringerMaxVolumeButton = findViewById<Button>(R.id.ringerMaxVolume_button).apply {
            setOnClickListener {
                setMaxVolume(STREAM_RING)
            }
        }
        callMaxVolumeButton = findViewById<Button>(R.id.callMaxVolume_button).apply {
            setOnClickListener {
                setMaxVolume(STREAM_VOICE_CALL)
            }
        }
        onVolumeChanged()
    }

    override fun onDestroy() {
        contentResolver.unregisterContentObserver(volumeSettingObserver)
        super.onDestroy()
    }

    private fun setMaxVolume(streamType: Int) {
        audioManager.setStreamVolume(
            streamType,
            audioManager.getStreamMaxVolume(streamType),
            FLAG_PLAY_SOUND
        )
    }

    private fun onVolumeChanged() {
        val ringerVolume = getStreamVolume(STREAM_RING)
        ringerVolumeText.text = getString(R.string.ringer_max_volume_percentage_text, ringerVolume)
        ringerMaxVolumeButton.setColorForPercentage(ringerVolume)

        val callVolume = getStreamVolume(STREAM_VOICE_CALL)
        callVolumeText.text = getString(R.string.call_max_volume_percentage_text, callVolume)
        callMaxVolumeButton.setColorForPercentage(callVolume)
    }

    private fun getStreamVolume(streamType: Int): Int {
        val maxVolume = audioManager.getStreamMaxVolume(streamType).toDouble()
        val currentVolume = audioManager.getStreamVolume(streamType)
        return (currentVolume / maxVolume * 100).toInt()
    }

    private fun View.setColorForPercentage(percentage: Int) {
        val color = if (percentage == 100) R.color.green else R.color.red
        setBackgroundColor(getColor(color))
    }
}