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
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import eu.livotov.zxscan.R;
import eu.livotov.zxscan.ZXScanHelper;

/**
 * Manages beeps and vibrations for {@link CaptureActivity}.
 */
final class BeepManager
{

    private static final long VIBRATE_DURATION = 200L;

    private final Activity activity;
    private int beepSoundId;
    SoundPool soundPool;

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

        activity.setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);
    }

    void vibrate()
    {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_DURATION);
    }

    void playBeep()
    {
        if (soundPool != null)
        {
            soundPool.play(beepSoundId, 1.0f, 1.0f, 5, 0, 1.0f);
        }
    }

}
