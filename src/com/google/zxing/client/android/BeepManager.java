/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import eu.livotov.zxscan.R;
import eu.livotov.zxscan.ZXScanHelper;

import java.io.IOException;

/**
 * Manages beeps and vibrations for {@link CaptureActivity}.
 */
final class BeepManager
{

    private static final String TAG = BeepManager.class.getSimpleName();

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;

    private final Activity activity;
    private int beepSoundId;
    SoundPool soundPool;
    private boolean playBeep;
    private boolean vibrate;

    BeepManager(Activity activity)
    {
        this.activity = activity;
        soundPool = new SoundPool(ZXScanHelper.getCustomScanSound() > 0 ? 2 : 1, AudioManager.STREAM_NOTIFICATION, 0);

        if (ZXScanHelper.getCustomScanSound() > 0)
        {
            beepSoundId = soundPool.load(activity, ZXScanHelper.getCustomScanSound(), 1);
        } else
        {
            beepSoundId = soundPool.load(activity, R.raw.beep, 1);
        }

        updatePrefs();
    }

    void updatePrefs()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        playBeep = shouldBeep(prefs, activity);
        vibrate = false; //todo control vibrate or not
        if (playBeep)
        {
            activity.setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);
        }
    }

    void playBeepSoundAndVibrate()
    {
        if (playBeep && soundPool != null)
        {
            soundPool.play(beepSoundId, 1.0f, 1.0f, 5, 0, 1.0f);
        }

        if (vibrate)
        {
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private static boolean shouldBeep(SharedPreferences prefs, Context activity)
    {
        boolean shouldPlayBeep = true; //todo: control beep or no beep
        if (shouldPlayBeep)
        {
            // See if sound settings overrides this
            AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
            {
                shouldPlayBeep = false;
            }
        }
        return shouldPlayBeep;
    }

}
