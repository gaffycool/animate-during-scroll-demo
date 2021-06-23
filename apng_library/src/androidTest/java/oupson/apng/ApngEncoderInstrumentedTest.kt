package oupson.apng

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import oupson.apng.Utils.Companion.areBitmapSimilar
import oupson.apng.Utils.Companion.getFrame
import oupson.apng.Utils.Companion.isSimilar
import oupson.apng.decoder.ApngDecoder
import oupson.apng.utils.Utils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class ApngEncoderInstrumentedTest {
    @Test
    fun testDiffBunny() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val bunnyFrame1 = getFrame(context, "bunny/frame_apngframe01.png")
        val bunnyFrame2 = getFrame(context, "bunny/frame_apngframe02.png")

        val diffBunny1to2 = Utils.getDiffBitmap(bunnyFrame1, bunnyFrame2)

        assertTrue(isSimilar(bunnyFrame1, bunnyFrame2, diffBunny1to2))
    }

    @Test
    fun testDiffBall() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val ballFrame1 = getFrame(context, "ball/apngframe01.png")
        val ballFrame2 = getFrame(context, "ball/apngframe02.png")

        val diffBall1to2 = Utils.getDiffBitmap(ballFrame1, ballFrame2)

        assertTrue(isSimilar(ballFrame1, ballFrame2, diffBall1to2))
    }

    @Test
    fun testContainTransparency() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val bunnyFrame1 = getFrame(context, "bunny/frame_apngframe01.png")
        assertFalse(Utils.containTransparency(bunnyFrame1))

        val ballFrame1 = getFrame(context, "ball/apngframe01.png")
        assertTrue(Utils.containTransparency(ballFrame1))
    }

    @Test
    fun testOptimiseBall() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val list = context.assets.list("ball")?.map { getFrame(context, "ball/$it") }!!

        val optimisedOutputStream = ByteArrayOutputStream()
        val optimisedEncoder =
            ApngEncoder(optimisedOutputStream, list[0].width, list[0].height, list.size)
                .setOptimiseApng(true)

        list.forEach {
            optimisedEncoder.writeFrame(it)
        }

        optimisedEncoder.writeEnd()
        optimisedOutputStream.close()

        val bytes = optimisedOutputStream.toByteArray()
        val optimisedInputStream = ByteArrayInputStream(bytes)


        val optimisedApng =
            runBlocking {
                ApngDecoder.decodeApng(
                    context,
                    optimisedInputStream
                ) as AnimationDrawable
            }

        optimisedInputStream.close()

        for (i in 0 until optimisedApng.numberOfFrames) {
            assertTrue(
                areBitmapSimilar(
                    list[i],
                    (optimisedApng.getFrame(i) as BitmapDrawable).bitmap
                )
            )
        }

        val nonOptimisedOutputStream = ByteArrayOutputStream()

        val nonOptimisedEncoder =
            ApngEncoder(nonOptimisedOutputStream, list[0].width, list[0].height, list.size)
                .setOptimiseApng(false)

        list.forEach {
            nonOptimisedEncoder.writeFrame(it)
        }

        nonOptimisedEncoder.writeEnd()

        nonOptimisedOutputStream.close()

        val nonOptimisedBytes = nonOptimisedOutputStream.toByteArray()
        val nonOptimisedInputStream = ByteArrayInputStream(nonOptimisedBytes)

        val nonOptimisedApng =
            runBlocking {
                ApngDecoder.decodeApng(
                    context,
                    nonOptimisedInputStream
                ) as AnimationDrawable
            }
        nonOptimisedInputStream.close()

        for (i in 0 until optimisedApng.numberOfFrames) {
            assertTrue(
                areBitmapSimilar(
                    (optimisedApng.getFrame(i) as BitmapDrawable).bitmap,
                    (nonOptimisedApng.getFrame(i) as BitmapDrawable).bitmap
                )
            )
        }
    }

    @Test
    fun testOptimiseBunny() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val list = context.assets.list("bunny")?.map { getFrame(context, "bunny/$it") }!!

        val optimisedOutputStream = ByteArrayOutputStream()
        val optimisedEncoder =
            ApngEncoder(optimisedOutputStream, list[0].width, list[0].height, list.size)
                .setOptimiseApng(true)

        list.forEach {
            optimisedEncoder.writeFrame(it)
        }

        optimisedEncoder.writeEnd()
        optimisedOutputStream.close()

        val bytes = optimisedOutputStream.toByteArray()
        val optimisedInputStream = ByteArrayInputStream(bytes)

        val optimisedApng =
            runBlocking {
                ApngDecoder.decodeApng(
                    context,
                    optimisedInputStream
                ) as AnimationDrawable
            }

        optimisedInputStream.close()

        for (i in 0 until optimisedApng.numberOfFrames) {
            assertTrue(
                areBitmapSimilar(
                    list[i],
                    (optimisedApng.getFrame(i) as BitmapDrawable).bitmap
                )
            )
        }

        val nonOptimisedOutputStream = ByteArrayOutputStream()

        val nonOptimisedEncoder =
            ApngEncoder(nonOptimisedOutputStream, list[0].width, list[0].height, list.size)
                .setOptimiseApng(false)

        list.forEach {
            nonOptimisedEncoder.writeFrame(it)
        }

        nonOptimisedEncoder.writeEnd()

        nonOptimisedOutputStream.close()

        val nonOptimisedBytes = nonOptimisedOutputStream.toByteArray()
        val nonOptimisedInputStream = ByteArrayInputStream(nonOptimisedBytes)

        val nonOptimisedApng =
            runBlocking {
                ApngDecoder.decodeApng(
                    context,
                    nonOptimisedInputStream
                ) as AnimationDrawable
            }
        nonOptimisedInputStream.close()

        for (i in 0 until optimisedApng.numberOfFrames) {
            assertTrue(
                areBitmapSimilar(
                    (optimisedApng.getFrame(i) as BitmapDrawable).bitmap,
                    (nonOptimisedApng.getFrame(i) as BitmapDrawable).bitmap
                )
            )

        }
    }
}