package com.example.qnsolutions.activity.main

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qnsolutions.activity.main.fragment.BenvenutoFragment
import com.example.qnsolutions.util.NavigationManager
import com.example.qnsolutions.R
import com.example.qnsolutions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    val frame = R.id.MainFrameLayout
    val root_tag = "benvenuto"

    //evito di poppare l'ultimo fragment
    override fun onBackPressed()
    {
        if(supportFragmentManager.backStackEntryCount != 1)
        {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //animazione dello sfondo
        animazione()

        if(savedInstanceState == null)
        {
            NavigationManager().apriFragment(this, frame, BenvenutoFragment(), "benvenuto")
        }
    }

    fun blendColors(startColor: Int, endColor: Int, fraction: Float): Int
    {
        val startA = Color.alpha(startColor)
        val startR = Color.red(startColor)
        val startG = Color.green(startColor)
        val startB = Color.blue(startColor)

        val endA = Color.alpha(endColor)
        val endR = Color.red(endColor)
        val endG = Color.green(endColor)
        val endB = Color.blue(endColor)

        val blendedA = (startA + fraction * (endA - startA)).toInt()
        val blendedR = (startR + fraction * (endR - startR)).toInt()
        val blendedG = (startG + fraction * (endG - startG)).toInt()
        val blendedB = (startB + fraction * (endB - startB)).toInt()

        return Color.argb(blendedA, blendedR, blendedG, blendedB)
    }

    fun animazione()
    {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(resources.getColor(R.color.dark_purple, null), resources.getColor(R.color.light_purple, null)) // Colori del gradient
        )

        val animator = ValueAnimator()
        animator.duration = 3500 // Durata dell'animazione in millisecondi
        animator.repeatCount = ValueAnimator.INFINITE // Ripeti l'animazione all'infinito

        animator.setFloatValues(0f, 1f)
        animator.setEvaluator(FloatEvaluator())

        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float

            // Modifica i colori del gradient in base alla frazione di completamento
            val colors = intArrayOf(
                blendColors(resources.getColor(R.color.dark_purple, null), resources.getColor(R.color.light_purple, null), fraction),
                blendColors(resources.getColor(R.color.light_purple, null), resources.getColor(R.color.dark_purple, null), fraction),
            )
            gradientDrawable.colors = colors

            // Imposta il gradient come sfondo del FrameLayout
            binding.MainFrameLayout.background = gradientDrawable
        }
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
    }
}