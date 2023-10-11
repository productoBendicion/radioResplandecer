package com.resplandecer.mediaPlayer;

/**
 * Radio functionality for APIs above 23.
 */

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;


/**
 * Player class to control the flow of the ExoPlayer library.
 */
public class SimplePlayer
        implements RadioPlayer {

    private static Activity mActivity;
    private static ExoPlayer mSimpleExoPlayer;
    private static SimplePlayer mSimplePlayerInstance;
    private static RadioPlayerListener mRadioPlayerListener;
    private static RadioStationUrls mRadioStationUrls;

    private boolean playedOnce;
    private boolean mIsInitialized;

    private SimplePlayer() { }

    public static SimplePlayer initializeSimplePlayer(
            Activity activity,
            RadioPlayerListener radioPlayerListener) {

        if (mSimplePlayerInstance == null) {
            mSimplePlayerInstance = new SimplePlayer();
        }
        mActivity = activity;
        mRadioPlayerListener = radioPlayerListener;

        return mSimplePlayerInstance;
    }

    public static SimplePlayer getSimplePlayer() {
        return mSimplePlayerInstance;
    }

    public boolean isInitialized() { return mIsInitialized; }

    @Override
    public void initPlayer() {
        if (mSimpleExoPlayer == null) {

            playedOnce = false;
            mIsInitialized = false;
            mSimpleExoPlayer = new ExoPlayer.Builder(mActivity).build();


            mSimpleExoPlayer.setPlayWhenReady(true);
        }
        mRadioStationUrls = RadioStationUrls.initRadioStationUrl();
//        mSimpleExoPlayer.addListener(this);

        Uri uri = Uri.parse(mRadioStationUrls.getCurrentSong().getAudioUrl());
        MediaItem mediasource = buildMediaSource(uri);
        mSimpleExoPlayer.addMediaItem(mediasource);
        mSimpleExoPlayer.prepare();
        mSimpleExoPlayer.play();
    }

    @Override
    public void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mIsInitialized = false;
//            mSimpleExoPlayer.removeListener(this);
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;

            mRadioPlayerListener.onReleaseRadio();
        }
    }

    private MediaItem buildMediaSource(Uri uri) {
        return MediaItem.fromUri(uri);
    }

//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        String stateString;
//
//        switch (playbackState) {
//            case ExoPlayer.STATE_IDLE:
//                stateString = "ExoPlayer.STATE_IDLE      -";
//                initPlayer();
//                break;
//            case ExoPlayer.STATE_BUFFERING:
//                stateString = "ExoPlayer.STATE_BUFFERING -";
//                mRadioPlayerListener.onRadioPlayerBuffering();
//                break;
//            case ExoPlayer.STATE_READY:
//                stateString = "ExoPlayer.STATE_READY     -";
//                playedOnce = true;
//                mIsInitialized = true;
//                mRadioPlayerListener.onRadioPlayerReady();
//                break;
//            case ExoPlayer.STATE_ENDED:
//                stateString = "ExoPlayer.STATE_ENDED     -";
//                mRadioStationUrls.nextSong();
//                if (mRadioStationUrls.getCurrentSongTracker() != -1) {
//                    initPlayer();
//                }
//                mRadioPlayerListener.onRadioStateEnded();
//                break;
//            default:
//                stateString = "UNKNOWN_STATE             -";
//                break;
//        }
//
//        Log.d("PlayerStateChanged", "changed state to " + stateString
//                + " playWhenReady: " + playWhenReady);
//
//    }


//
//    @Override
//    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }
}
