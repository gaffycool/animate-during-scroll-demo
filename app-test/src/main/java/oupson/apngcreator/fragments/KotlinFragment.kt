package oupson.apngcreator.fragments


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import oupson.apng.decoder.ApngDecoder
import oupson.apng.drawable.ApngDrawable
import oupson.apngcreator.BuildConfig
import oupson.apngcreator.R


class KotlinFragment : Fragment() {
    companion object {
        private const val TAG = "KotlinFragment"

        @JvmStatic
        fun newInstance() =
            KotlinFragment()
    }

    private var apngImageView: ImageView? = null
    private var normalImageView: ImageView? = null

    private var pauseButton: Button? = null
    private var playButton: Button? = null

    private var speedSeekBar: SeekBar? = null

    //private var animator : ApngAnimator? = null
    private var animation: ApngDrawable? = null
    private var durations: IntArray? = null

    private var frameIndex = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (BuildConfig.DEBUG)
            Log.v(TAG, "onCreateView()")

        val view = inflater.inflate(R.layout.fragment_kotlin, container, false)

        apngImageView = view.findViewById(R.id.ApngImageView)
        pauseButton = view.findViewById(R.id.PauseButton)
        playButton = view.findViewById(R.id.PlayButton)

        speedSeekBar = view.findViewById(R.id.SpeedSeekBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (animation == null) {
            ApngDecoder.decodeApngAsyncInto(
                this.context!!,
                "https://metagif.files.wordpress.com/2015/01/bugbuckbunny.png",
                apngImageView!!,
                callback = object : ApngDecoder.Callback {
                    override fun onSuccess(drawable: Drawable) {
                        animation = (drawable as? ApngDrawable)
                        durations = IntArray(animation?.numberOfFrames ?: 0) { i ->
                            animation?.getDuration(i) ?: 0
                        }
                        animation?.stop()
                    }

                    override fun onError(error: Exception) {
                        Log.e(TAG, "Error when decoding apng", error)
                    }
                })
        }

        val scrollView: ScrollView = view.findViewById(R.id.scrollView)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY: Int = scrollView.scrollY/30
            val frames = animation?.numberOfFrames ?: 0
            Log.e("progress", scrollY.toString())
            if (scrollY in 0..frames)
                apngImageView?.setImageDrawable(animation?.getFrame(scrollY))
        }
    }

    override fun onPause() {
        super.onPause()
        if (BuildConfig.DEBUG)
            Log.v(TAG, "onPause()")

        animation = null
        normalImageView?.setImageDrawable(null)
        apngImageView?.setImageDrawable(null)

        playButton?.setOnClickListener(null)
        pauseButton?.setOnClickListener(null)
        speedSeekBar?.setOnSeekBarChangeListener(null)
    }
}