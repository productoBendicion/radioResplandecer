package com.radio.resplandecer.mediaPlayer;

/**
 * Radio functionality for APIs above 23.
 */

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * Player class to control the flow of the ExoPlayer library.
 */
public class SimplePlayer
        implements RadioPlayer,
        ExoPlayer.EventListener {

    private static Activity mActivity;
    private static SimpleExoPlayer mSimpleExoPlayer;
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
            mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(mActivity),
                    new DefaultTrackSelector(), new DefaultLoadControl());


            mSimpleExoPlayer.setPlayWhenReady(true);
        }
        mRadioStationUrls = RadioStationUrls.initRadioStationUrl();
        mSimpleExoPlayer.addListener(this);

        Uri uri = Uri.parse(mRadioStationUrls.getCurrentSong().getAudioUrl());
        MediaSource mediasource = buildMediaSource(uri);
        mSimpleExoPlayer.prepare(mediasource, true, false);
    }

    @Override
    public void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mIsInitialized = false;
            mSimpleExoPlayer.removeListener(this);
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;

            mRadioPlayerListener.onReleaseRadio();
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) { }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) { }

    @Override
    public void onLoadingChanged(boolean isLoading) { }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        String stateString;

        switch (playbackState) {
            case ExoPlayer.STATE_IDLE:
                stateString = "ExoPlayer.STATE_IDLE      -";
                initPlayer();
                break;
            case ExoPlayer.STATE_BUFFERING:
                stateString = "ExoPlayer.STATE_BUFFERING -";
                mRadioPlayerListener.onRadioPlayerBuffering();
                break;
            case ExoPlayer.STATE_READY:
                stateString = "ExoPlayer.STATE_READY     -";
                playedOnce = true;
                mIsInitialized = true;
                mRadioPlayerListener.onRadioPlayerReady();
                break;
            case ExoPlayer.STATE_ENDED:
                stateString = "ExoPlayer.STATE_ENDED     -";
                mRadioStationUrls.nextSong();
                if (mRadioStationUrls.getCurrentSongTracker() != -1) {
                    initPlayer();
                }
                mRadioPlayerListener.onRadioStateEnded();
                break;
            default:
                stateString = "UNKNOWN_STATE             -";
                break;
        }

        Log.d("PlayerStateChanged", "changed state to " + stateString
                + " playWhenReady: " + playWhenReady);

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (!playedOnce) {
            releasePlayer();
            mRadioPlayerListener.onRadioPlayerError();
            mIsInitialized = false;
        }
    }

    @Override
    public void onPositionDiscontinuity() { }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }
}
