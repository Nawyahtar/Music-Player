package com.example.musicplayer

import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class BackgroundService :MediaBrowserService(){
    private var mMediaSession: MediaSessionCompat? = null
    private lateinit var mStateBuilder: PlaybackStateCompat.Builder
    private var mExoPlayer: SimpleExoPlayer? = null
    private var oldUri: Uri? = null
    private lateinit var mExtractorFactory: ProgressiveMediaSource.Factory
    private var mAttrs: AudioAttributes? = null
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
       return BrowserRoot("",null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }
    private val mMediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            uri?.let {
                val mediaSource = extractMediaSourceFromUri(it)
                if (uri != oldUri)
                    play(mediaSource)
                else play() // this song was paused so we don't need to reload it
                oldUri = uri
            }
        }
    }
    private fun play(mediaSource: MediaSource) {
        if (mExoPlayer == null) initializePlayer()
        mExoPlayer?.apply {
            // AudioAttributes here from exoplayer package !!!
            mAttrs?.let { initializeAttributes() }
            // In 2.9.X you don't need to manually handle audio focus :D
            setAudioAttributes(mAttrs!!, true)
            prepare(mediaSource)
            play()
        }
    }
    private fun initializePlayer() {
        mExoPlayer = SimpleExoPlayer.Builder(this)
            .build()

    }
    private fun initializeAttributes() {
        mAttrs = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
    }
    private fun play() {
        mExoPlayer?.apply {
            mExoPlayer?.playWhenReady = true
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            mMediaSession?.isActive = true
        }
    }
    private fun updatePlaybackState(state: Int) {
        // You need to change the state because the action taken in the controller depends on the state !!!
        mMediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder().setState(
                state // this state is handled in the media controller
                , 0L
                , 1.0f // Speed playing
            ).build()
        )
    }
    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        initializeExtractor()
        initializeAttributes()
        mMediaSession = MediaSessionCompat(baseContext, "tag for debugging").apply {
            // Enable callbacks from MediaButtons and TransportControls
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            // Set initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            mStateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
            setPlaybackState(mStateBuilder.build())

            // methods that handle callbacks from a media controller
            setCallback(mMediaSessionCallback)

            // Set the session's token so that client activities can communicate with it
//            setSessionToken(sessionToken)
            isActive = true
        }
    }
    private fun initializeExtractor() {
        val userAgent = Util.getUserAgent(baseContext, "Application Name")
        mExtractorFactory = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, userAgent))
            .setExtractorsFactory(DefaultExtractorsFactory())
    }
    private fun extractMediaSourceFromUri(uri: Uri): MediaSource {

        return mExtractorFactory.createMediaSource(uri)
    }

}







