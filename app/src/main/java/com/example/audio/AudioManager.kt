package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundEffect {
    BUTTON_CLICK,
    COUNTDOWN_TICK,
    COUNTDOWN_GO,
    JUMP,
    DASH,
    IMPACT_BONK,
    EXPLOSION,
    EVENT_WARNING,
    LAVA_SIZZLE,
    ELIMINATION_SPLAT,
    CROWD_LAUGH,
    VICTORY_FANFARE,
    DEFEAT_TROMBONE,
    COIN_REWARD,
    SAFE_CHIME
}

class AudioManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var bgmJob: Job? = null
    
    var isSfxEnabled: Boolean = true
    var isBgmEnabled: Boolean = true
    var sfxVolume: Float = 0.8f
    var bgmVolume: Float = 0.5f

    fun playSound(effect: SoundEffect) {
        if (!isSfxEnabled) return
        scope.launch {
            try {
                generateAndPlayTone(effect)
            } catch (e: Exception) {
                // Audio synthesis error fallback
            }
        }
    }

    fun startBgm() {
        if (!isBgmEnabled || bgmJob?.isActive == true) return
        bgmJob = scope.launch {
            val sampleRate = 22050
            val noteDurationMs = 180L
            val chords = listOf(
                listOf(261.63f, 329.63f, 392.00f), // C
                listOf(293.66f, 349.23f, 440.00f), // Dm
                listOf(329.63f, 392.00f, 493.88f), // Em
                listOf(349.23f, 440.00f, 523.25f), // F
                listOf(392.00f, 493.88f, 587.33f), // G
                listOf(440.00f, 523.25f, 659.25f), // Am
                listOf(349.23f, 440.00f, 523.25f), // F
                listOf(392.00f, 493.88f, 587.33f)  // G
            )
            
            val bassNotes = listOf(130.81f, 146.83f, 164.81f, 174.61f, 196.00f, 220.00f, 174.61f, 196.00f)
            
            var step = 0
            while (isActive && isBgmEnabled) {
                val chordIndex = (step / 4) % chords.size
                val chord = chords[chordIndex]
                val bass = bassNotes[chordIndex]
                val note = chord[step % chord.size]
                
                playSimpleTone(note, 140, sampleRate, 0.15f * bgmVolume)
                if (step % 2 == 0) {
                    playSimpleTone(bass, 160, sampleRate, 0.25f * bgmVolume)
                }
                
                step++
                delay(noteDurationMs)
            }
        }
    }

    fun stopBgm() {
        bgmJob?.cancel()
        bgmJob = null
    }

    private fun playSimpleTone(freq: Float, durationMs: Int, sampleRate: Int = 22050, vol: Float = 0.2f) {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        val angularFreq = 2.0 * PI * freq / sampleRate
        
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = (1.0 - progress) * progress * 4.0 // Quick attack/decay
            val sample = sin(i * angularFreq) * envelope * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                delay(durationMs.toLong() + 50)
                audioTrack.release()
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun generateAndPlayTone(effect: SoundEffect) {
        val sampleRate = 22050
        val (samples, durationMs) = when (effect) {
            SoundEffect.BUTTON_CLICK -> generateChirp(600f, 900f, 60, sampleRate, 0.4f * sfxVolume)
            SoundEffect.COUNTDOWN_TICK -> generateChirp(440f, 440f, 80, sampleRate, 0.6f * sfxVolume)
            SoundEffect.COUNTDOWN_GO -> generateFanfare(listOf(523.25f, 659.25f, 783.99f, 1046.50f), 300, sampleRate, 0.8f * sfxVolume)
            SoundEffect.JUMP -> generateChirp(280f, 680f, 140, sampleRate, 0.7f * sfxVolume)
            SoundEffect.DASH -> generateNoiseWhoosh(160, sampleRate, 0.6f * sfxVolume)
            SoundEffect.IMPACT_BONK -> generateChirp(400f, 120f, 120, sampleRate, 0.8f * sfxVolume)
            SoundEffect.EXPLOSION -> generateExplosion(350, sampleRate, 0.9f * sfxVolume)
            SoundEffect.EVENT_WARNING -> generateSiren(300, sampleRate, 0.7f * sfxVolume)
            SoundEffect.LAVA_SIZZLE -> generateSizzle(250, sampleRate, 0.6f * sfxVolume)
            SoundEffect.ELIMINATION_SPLAT -> generateSplat(280, sampleRate, 0.85f * sfxVolume)
            SoundEffect.CROWD_LAUGH -> generateLaughter(400, sampleRate, 0.7f * sfxVolume)
            SoundEffect.VICTORY_FANFARE -> generateFanfare(listOf(523.25f, 659.25f, 783.99f, 1046.50f, 1318.51f), 550, sampleRate, 0.9f * sfxVolume)
            SoundEffect.DEFEAT_TROMBONE -> generateDefeatTrombone(500, sampleRate, 0.75f * sfxVolume)
            SoundEffect.COIN_REWARD -> generateCoinChime(sampleRate, 0.7f * sfxVolume)
            SoundEffect.SAFE_CHIME -> generateChirp(523f, 1046f, 180, sampleRate, 0.6f * sfxVolume)
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()
        scope.launch {
            delay(durationMs + 60L)
            audioTrack.release()
        }
    }

    private fun generateChirp(startFreq: Float, endFreq: Float, durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val phaseInc = 2.0 * PI * freq / sampleRate
            phase += phaseInc
            val envelope = (1.0 - progress) * (1.0 - exp(-progress * 15.0))
            val sample = sin(phase) * envelope * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateNoiseWhoosh(durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var last = 0f
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val white = (Math.random() * 2.0 - 1.0).toFloat()
            // Low-pass filter for whoosh
            last = last * 0.85f + white * 0.15f
            val envelope = sin(progress * PI)
            val sample = last * envelope * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateExplosion(durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var lowPass = 0f
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val noise = (Math.random() * 2.0 - 1.0).toFloat()
            lowPass = lowPass * 0.92f + noise * 0.08f
            val boom = sin(i * 2.0 * PI * 65.0 / sampleRate) * 0.4
            val envelope = exp(-progress * 6.0)
            val sample = (lowPass * 0.6 + boom) * envelope * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateSiren(durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 500.0 + sin(progress * PI * 6.0) * 250.0
            phase += 2.0 * PI * freq / sampleRate
            val sample = sin(phase) * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateSizzle(durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val noise = (Math.random() * 2.0 - 1.0).toFloat()
            val envelope = (1.0 - progress)
            val sample = noise * envelope * vol * 0.5f
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateSplat(durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 320.0 - progress * 240.0
            phase += 2.0 * PI * freq / sampleRate
            val noise = (Math.random() * 2.0 - 1.0) * 0.3
            val envelope = (1.0 - progress) * (1.0 - progress)
            val sample = (sin(phase) * 0.7 + noise) * envelope * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateLaughter(durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val pulse = (sin(progress * PI * 12.0) + 1.0) * 0.5
            val freq = 440.0 + sin(progress * PI * 4.0) * 80.0
            phase += 2.0 * PI * freq / sampleRate
            val sample = sin(phase) * pulse * (1.0 - progress * 0.5) * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateFanfare(notes: List<Float>, totalDurationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (totalDurationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        val samplesPerNote = numSamples / notes.size
        
        for (n in notes.indices) {
            val freq = notes[n]
            val offset = n * samplesPerNote
            val count = if (n == notes.size - 1) numSamples - offset else samplesPerNote
            var phase = 0.0
            for (i in 0 until count) {
                val progress = i.toDouble() / count
                phase += 2.0 * PI * freq / sampleRate
                val envelope = (1.0 - progress * 0.3) * (1.0 - exp(-progress * 20.0))
                val sample = (sin(phase) + 0.3 * sin(phase * 2.0)) * envelope * vol * 0.6
                buffer[offset + i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return Pair(buffer, totalDurationMs.toLong())
    }

    private fun generateDefeatTrombone(durationMs: Int, sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 220.0 - progress * 110.0 + sin(progress * PI * 18.0) * 10.0
            phase += 2.0 * PI * freq / sampleRate
            val envelope = (1.0 - progress * 0.2)
            val sample = (sin(phase) + 0.5 * sin(phase * 3.0)) * envelope * vol * 0.5
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return Pair(buffer, durationMs.toLong())
    }

    private fun generateCoinChime(sampleRate: Int, vol: Float): Pair<ShortArray, Long> {
        val durationMs = 240
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)
        val half = numSamples / 2
        
        var phase1 = 0.0
        for (i in 0 until half) {
            val progress = i.toDouble() / half
            phase1 += 2.0 * PI * 987.77 / sampleRate // B5
            val sample = sin(phase1) * (1.0 - progress) * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        
        var phase2 = 0.0
        for (i in half until numSamples) {
            val progress = (i - half).toDouble() / (numSamples - half)
            phase2 += 2.0 * PI * 1318.51 / sampleRate // E6
            val sample = sin(phase2) * (1.0 - progress) * vol
            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        
        return Pair(buffer, durationMs.toLong())
    }
}
