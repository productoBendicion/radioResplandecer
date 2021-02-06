package com.resplandecer.mediaPlayer;

/**
 * Every radio Player should implement Radio Player Listener.
 * */
public interface RadioPlayerListener {
    /**
     * Radio Player should notify when ready to play
     */
    void onRadioPlayerReady();

    /**
     * Radio Player should notify when there is an error.
     */
    void onRadioPlayerError();

    /**
     * Radio Player should notify when buffering.
     */
    void onRadioPlayerBuffering();

    /**
     * Radio Player should notify when released.
     */
    void onReleaseRadio();

    /**
     * Radio Player should notify when current media streaming ended.
     */
    void onRadioStateEnded();
}
