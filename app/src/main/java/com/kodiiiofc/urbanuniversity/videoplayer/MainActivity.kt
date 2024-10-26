package com.kodiiiofc.urbanuniversity.videoplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kodiiiofc.urbanuniversity.videoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val videoStorageUriLiveData = MutableLiveData<Uri>()

    private lateinit var list: List<Uri?>

    var currentVideo = 0

    private val activityVideoPicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                videoStorageUriLiveData.value = data?.data
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoStorageUriLiveData.observe(this, Observer {
            binding.videoView.setVideoURI(it)
            binding.videoView.requestFocus()
            binding.videoView.start()
        })

        list = listOf(
            Uri.parse("https://videocdn.cdnpk.net/videos/307fe935-a63c-4154-9737-27ff09909afa/horizontal/previews/watermarked/large.mp4"),
            Uri.parse("android.resource://$packageName/${R.raw.first}"),
            Uri.parse("android.resource://$packageName/${R.raw.second}"),
            videoStorageUriLiveData.value
        )

        val mediaController = MediaController(this)
        mediaController.setAnchorView(mediaController)

        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(list[currentVideo])
        binding.videoView.requestFocus()
        binding.videoView.start()
        mediaController.setPrevNextListeners({
            ++currentVideo
            changeVideo()
        }, {
            --currentVideo
            changeVideo()
        })

    }

    private fun changeVideo() {
        when {
            currentVideo < 0 -> currentVideo += list.size
            currentVideo > list.lastIndex -> {
                currentVideo %= list.size
            }
        }
        if (currentVideo == list.lastIndex) {
            val videoPickerIntent = Intent(Intent.ACTION_PICK)
            videoPickerIntent.type = "video/*"
            activityVideoPicker.launch(videoPickerIntent)
        } else {
            binding.videoView.setVideoURI(list[currentVideo])
        }
    }
}