package com.resplandecer.mediaPlayer;

/**
 * Provides functionality for a Radio Player.
 */
public interface RadioPlayer {

    /**
     * A Radio Player should be able to initialize itself.
     * */
    void initPlayer();

    /**
     * Be able to release a player.
     */
    void releasePlayer();
}
