package com.ourlauncher.app.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun LiquidGlassDock(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val context = LocalContext.current
    var roll by remember { mutableFloatStateOf(0f) }
    var pitch by remember { mutableFloatStateOf(0f) }

    // জাইরোস্কোপ ও রোটেশন সেন্সর লিসেনার (১২০ FPS অপটিমাইজড)
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    
                    // মসৃণ টিল্ট রেঞ্জ ম্যাপিং
                    roll = (orientation[2] * 35f).coerceIn(-40f, 40f)
                    pitch = (orientation[1] * 20f).coerceIn(-25f, 25f)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    val animatedRoll by animateFloatAsState(targetValue = roll, animationSpec = tween(80), label = "roll")
    val animatedPitch by animateFloatAsState(targetValue = pitch, animationSpec = tween(80), label = "pitch")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(84.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(38.dp),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(38.dp))
            // ব্যাকড্রপ বেস ফ্রস্টেড গ্লাস
            .background(Color.White.copy(alpha = 0.12f))
            // কাঁচের চারপাশের স্পেকুলার ক্রিস্টাল বর্ডার
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.65f),
                        Color.White.copy(alpha = 0.10f),
                        Color.White.copy(alpha = 0.45f)
                    )
                ),
                shape = RoundedCornerShape(38.dp)
            )
    ) {
        // সেন্সরের সাথে নড়াচড়া করা লাইভ রিফ্লেকশন লেয়ার
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = animatedRoll.dp, y = animatedPitch.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.Transparent
                        ),
                        radius = 220f
                    )
                )
        )

        // ডক আইকন কন্টেইনার
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
