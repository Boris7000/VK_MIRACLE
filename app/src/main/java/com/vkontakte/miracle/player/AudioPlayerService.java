package com.vkontakte.miracle.player;

import static com.vkontakte.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.vkontakte.miracle.notification.AppNotificationChannels.AUDIO_CHANNEL_ID;
import static com.vkontakte.miracle.player.AudioPlayerControls.ACTION_CHANGE_REPEAT;
import static com.vkontakte.miracle.player.AudioPlayerControls.ACTION_NEXT;
import static com.vkontakte.miracle.player.AudioPlayerControls.ACTION_PAUSE;
import static com.vkontakte.miracle.player.AudioPlayerControls.ACTION_PLAY;
import static com.vkontakte.miracle.player.AudioPlayerControls.ACTION_PREVIOUS;
import static com.vkontakte.miracle.player.AudioPlayerControls.ACTION_SEEK_TO;
import static com.vkontakte.miracle.player.AudioPlayerControls.ACTION_STOP;
import static com.vkontakte.miracle.player.AudioPlayerControls.playbackAction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.async.ExecutorConveyor;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Audio;
import com.vkontakte.miracle.notification.AppNotificationChannels;

public class AudioPlayerService extends Service implements ExoPlayer.Listener, AnalyticsListener {

    private final MiracleApp miracleApp = MiracleApp.getInstance();
    private final IBinder iBinder = new PlayerServiceBinder(this);
    private final PlayerServiceController playerServiceController = PlayerServiceController.get();
    private final ProfileItem profileItem = StorageUtil.get().currentUser();
    private final SettingsUtil settingsUtil = SettingsUtil.get();
    private AudioPlayerData playerData;
    private AudioPlayerData nextPlayerData;
    private ExoPlayer player;
    private Handler handler;
    private boolean destroyed = false;
    private boolean repeatOnePlayed = false;

    //Notification
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = -808;
    private Bitmap songImage, placeholderImage;
    private String previousImageUrl = "";

    //MediaSession
    private MediaSessionCompat mediaSessionCompat;
    private MediaControllerCompat.TransportControls transportControls;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;

    //Threads
    private final ExecutorConveyor<Boolean> trackEvents = new ExecutorConveyor<>();

    private final OnPlayerServiceEventListener onPlayerEventListener = new OnPlayerServiceEventListener() {
        @Override
        public void onBufferChange(long bufferPosition) {
            if(playerData!=null) {
                playerData.setBufferedPosition(bufferPosition);
                playerServiceController.getOnPlayerEventListener().onBufferChange(playerData);
            }
        }

        @Override
        public void onPlaybackPositionChange(long playbackPosition, long duration) {
            if(playerData!=null) {
                playerData.setCurrentPosition(playbackPosition);
                if(duration>0) {
                    playerData.setDuration(duration);
                }
                playerServiceController.getOnPlayerEventListener().onPlaybackPositionChange(playerData);
            }
        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            if(playerData.getRepeatMode()==Player.REPEAT_MODE_ONE) {
                changeRepeatMode(Player.REPEAT_MODE_OFF);
            }
            AudioPlayerService.this.playerData = playerData;
            playerServiceController.getOnPlayerEventListener().onSongChange(playerData, animate);
        }

        @Override
        public void onPlayWhenReadyChange(boolean playWhenReady, boolean animate) {
            if(playerData!=null) {
                playerData.setPlayWhenReady(playWhenReady);
                playerServiceController.getOnPlayerEventListener().onPlayWhenReadyChange(playerData, animate);
            }
        }

        @Override
        public void onRepeatModeChange(int repeatMode) {
            if(playerData!=null) {
                if(repeatMode == Player.REPEAT_MODE_ONE){
                    repeatOnePlayed = false;
                }
                playerData.setRepeatMode(repeatMode);
                playerServiceController.getOnPlayerEventListener().onRepeatModeChange(playerData);
            }
        }

        @Override
        public void onPlayerClose() {
            playerServiceController.getOnPlayerEventListener().onPlayerClose();
        }
    };

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            songImage = bitmap;

            player.prepare();
            player.play();

            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,playerData.getCurrentPosition(), 0, SystemClock.elapsedRealtime());
            mediaSessionCompat.setPlaybackState(stateBuilder.build());

            updateMetaData();

            Notification notification = createNotification(songImage, player.getPlayWhenReady());

            notificationManager.notify(NOTIFICATION_ID, notification);

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            onBitmapLoaded(placeholderImage, null);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private Notification createNotification(Bitmap largeIcon, boolean playWhenReady){

        int icon= R.drawable.ic_pause_28, smallIcon=R.drawable.ic_play_28;
        PendingIntent playPauseAction;

        ////////////////////////////////////////////
        ////////////////////////////////////////////

        if(playWhenReady) {
            playPauseAction = playbackAction(1, this);
        }else {
            icon = R.drawable.ic_play_28;
            smallIcon = R.drawable.ic_pause_28;
            playPauseAction = playbackAction(0, this);
        }

        ////////////////////////////////////////////
        ////////////////////////////////////////////

        Intent intent = new Intent(miracleApp, MiracleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("PlayerBottomSheetExpanded", true);
        PendingIntent contentIntent = PendingIntent.getActivity(miracleApp, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        ////////////////////////////////////////////
        ////////////////////////////////////////////

        NotificationCompat.Builder  notificationBuilder = new NotificationCompat.Builder(this, AUDIO_CHANNEL_ID)// Create a new Notification
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()// Set the Notification style
                        .setMediaSession(mediaSessionCompat.getSessionToken())// Attach our MediaSession token
                        .setShowActionsInCompactView(1, 2, 3))  // Show our playback controls in the compat view
                .setShowWhen(false) // Hide the timestamp
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentText(playerData.getCurrentItem().getArtist()) // Set Notification content information
                .setContentTitle(playerData.getCurrentItem().getTitle())
                .setContentInfo(playerData.getCurrentItem().getTitle())
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_cancel_28,"close",playbackAction(4, this))  // Add playback actions
                .addAction(R.drawable.ic_skip_previous_28, "previous", playbackAction(3, this))
                .addAction(icon, "pause", playPauseAction)
                .addAction(R.drawable.ic_skip_next_28, "next", playbackAction(2,this))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setChannelId(AUDIO_CHANNEL_ID);

        return notificationBuilder.build();
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(miracleApp.getThemeRecourseId());

        handler = new Handler(Looper.getMainLooper());

        notificationManager = (NotificationManager) miracleApp.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = AppNotificationChannels.getAudioChannel(this);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int size = (int) DimensionsUtil.dpToPx(84,this);
        placeholderImage = bitmapFromLayerDrawable(R.drawable.audio_placeholder_image, this,size,size);

        updateProgressValue();

        registerBecomingNoisyReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIncomingAction(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleIncomingAction(Intent playbackAction){

        if (destroyed||player==null||playerData==null||transportControls==null||
                playbackAction==null||playbackAction.getAction()==null){
            return;
        }

        String actionString = playbackAction.getAction();

        switch (actionString){
            case ACTION_PLAY:{
                transportControls.play();
                break;
            }
            case ACTION_PAUSE:{
                transportControls.pause();
                break;
            }
            case ACTION_STOP:{
                transportControls.stop();
                break;
            }
            case ACTION_NEXT:{
                skipToNext();
                break;
            }
            case ACTION_PREVIOUS:{
                skipToPrevious();
                break;
            }
            case ACTION_SEEK_TO:{
                seekTo(playbackAction.getLongExtra("position",0));
                break;
            }
            case ACTION_CHANGE_REPEAT:{
                changeRepeatMode(playbackAction.getIntExtra("repeatMode", Player.REPEAT_MODE_OFF));
                break;
            }
        }
    }

    private void initMediaSession(){

        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(),"AudioPlayer");
        transportControls = mediaSessionCompat.getController().getTransportControls();
        mediaSessionCompat.setActive(true);
        stateBuilder = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS|PlaybackStateCompat.ACTION_SEEK_TO);
        metadataBuilder = new MediaMetadataCompat.Builder();

        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPrepare() {
                super.onPrepare();
            }

            @Override
            public void onPlay() {
                super.onPlay();
                if(playerData.hasError()){
                    startPlaying();
                } else {
                    player.setPlayWhenReady(true);
                }
            }

            @Override
            public void onPause() {
                super.onPause();
                player.setPlayWhenReady(false);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                if(nextPlayerData!=null){
                    playerData = nextPlayerData;
                    nextPlayerData = null;
                }else {
                    if(playerData.getCurrentItemIndex()==playerData.getItems().size()-1){
                        playerData.reset(0);
                    } else {
                        playerData.reset(playerData.getCurrentItemIndex()+1);
                    }
                }
                startPlaying();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                if (player.getCurrentPosition() < 5000) {
                    if(playerData.getCurrentItemIndex()==0){
                        seekTo(0);
                    } else {
                        playerData.reset(playerData.getCurrentItemIndex()-1);
                    }
                    startPlaying();
                } else {
                    seekTo(0);
                }
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                player.seekTo(pos);
                onPlayerEventListener.onPlaybackPositionChange(pos, playerData.getDuration());
            }

            @Override
            public void onStop() {
                super.onStop();
                onPlayerEventListener.onPlayerClose();
                stopSelf();
            }
        });
    }

    private void initializePlayer(){
        if(player==null){
            player = new ExoPlayer.Builder(this).build();
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MUSIC).build();
            player.setAudioAttributes(audioAttributes,true);
            player.addListener(this);
            player.addAnalyticsListener(this);
        }
    }

    private void startPlaying(){

        player.stop();

        onPlayerEventListener.onSongChange(playerData, playerData.hasError());
        onPlayerEventListener.onPlayWhenReadyChange(playerData.getPlayWhenReady(), true);
        if(!playerData.hasError()) {
            onPlayerEventListener.onBufferChange(playerData.getBufferedPosition());
            onPlayerEventListener.onPlaybackPositionChange(playerData.getCurrentPosition(), playerData.getDuration());
        }
        ////////////////////////////////////////////

        AudioItem audioItem = playerData.getCurrentItem();

          /*
         else {
            asMP3 = false;
            Uri uri = Uri.parse("file://"+ getApplicationContext().getFilesDir() +"/"+ audioItem.getOwner_id() + "-" + audioItem.getId() + ".mp3");
            player.setMediaItem(MediaItem.fromUri(uri));
        }
         */


        if(playerData.hasError()){
            playerData.setHasError(false);
            player.setMediaItem(MediaItem.fromUri(Uri.parse(audioItem.getUrl())),playerData.getCurrentPosition());
        } else {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(audioItem.getUrl())));
        }

        trackEvents.addAsyncExecutor(new AsyncExecutor<Boolean>() {
            @Override
            public Boolean inBackground() {
                try {
                    Audio.trackEventsAudioPlay(audioItem.getId(),audioItem.getOwnerId(),
                            audioItem.getDuration(),profileItem.getAccessToken()).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            @Override
            public void onExecute(Boolean object) { }
        });

        createNotificationAndPlay(audioItem);
    }

    private void createNotificationAndPlay(AudioItem audioItem){
        Picasso.get().cancelRequest(target);
        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                Photo thumb = album.getThumb();
                if(thumb.getPhoto270()!=null){
                    if(!thumb.getPhoto270().equals(previousImageUrl)) {
                        previousImageUrl = thumb.getPhoto270();
                        Picasso.get().load(thumb.getPhoto270()).into(target);
                    } else {
                        target.onBitmapLoaded(songImage, null);
                    }
                    return;
                }
            }
        }
        previousImageUrl = "";
        target.onBitmapLoaded(placeholderImage, null);
    }

    private void updatePlaybackState(int playbackState, int speed){
        stateBuilder.setState(playbackState, playerData.getCurrentPosition(), speed, SystemClock.elapsedRealtime());
        mediaSessionCompat.setPlaybackState(stateBuilder.build());
    }

    private void updateMetaData() {
        AudioItem audioItem = playerData.getCurrentItem();
        int changeWallpaper =  settingsUtil.playerChangeWallpapers();
        Bitmap wallpaperBitmap = changeWallpaper==0?songImage:null;

        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, wallpaperBitmap)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, playerData.getDuration())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioItem.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioItem.getTitle());

        mediaSessionCompat.setMetadata(metadataBuilder.build());
    }

    /////////////////////////////////////////////////////////////////////////

    public void resume() {
        transportControls.play();
    }

    public void pause() {
        transportControls.pause();
    }

    public void skipToNext() {
        transportControls.skipToNext();
    }

    public void skipToPrevious() {
        transportControls.skipToPrevious();
    }

    public void seekTo(long pos) {
        transportControls.seekTo(pos);
    }

    public void seekToPercent(float seekPercent) {
        transportControls.seekTo((long)(playerData.getDuration()*seekPercent));
    }

    public void changeRepeatMode(int repeatMode) {
        onPlayerEventListener.onRepeatModeChange(repeatMode);
        settingsUtil.storePlayerRepeatMode(repeatMode);
    }

    public void stopService() {
        transportControls.stop();
    }

    public void playNewAudio(AudioPlayerData newPlayerData){
        if(playerData==null){
            playerData = newPlayerData;
            playerData.setRepeatMode(settingsUtil.playerRepeatMode());
            initMediaSession();
            initializePlayer();
            startForeground(NOTIFICATION_ID, createNotification(placeholderImage,true));
            startPlaying();
        } else {
            if (playerData.equals(newPlayerData)) {
                if (playerData.getCurrentItem().equals(newPlayerData.getCurrentItem())) {
                    playerData = newPlayerData;
                    if (player.getPlayWhenReady()) {
                        pause();
                    } else {
                        resume();
                    }
                } else {
                    playerData = newPlayerData;
                    startPlaying();
                }
            } else {
                playerData = newPlayerData;
                startPlaying();
            }
        }
    }

    public void setPlayNext(AudioPlayerData newPlayerData){
        nextPlayerData = newPlayerData;
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayWhenReadyChanged(@NonNull EventTime eventTime, boolean playWhenReady, int reason) {
        if (playWhenReady) {
            onPlayerEventListener.onPlayWhenReadyChange(true, true);
            Notification notification = createNotification(songImage, true);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            onPlayerEventListener.onPlayWhenReadyChange(false, true);
            Notification notification = createNotification(songImage, false);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if(isPlaying){
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, 1);
        }else {
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED, 0);
        }
    }

    @Override
    public void onPlaybackStateChanged(@NonNull EventTime eventTime, int playbackState) {
        if(playbackState==ExoPlayer.STATE_BUFFERING) {
            updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING, 0);
            return;
        }
        if(playbackState==ExoPlayer.STATE_READY && player.getPlayWhenReady()) {
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, 1);
            return;
        }
        if(playbackState==ExoPlayer.STATE_ENDED) {

            switch (playerData.getRepeatMode()){
                case Player.REPEAT_MODE_OFF:{
                    skipToNext();
                    break;
                }
                case Player.REPEAT_MODE_ONE:{
                    if(!repeatOnePlayed){
                        repeatOnePlayed =true;
                        seekTo(0);
                    } else {
                        skipToNext();
                    }
                    break;
                }
                case Player.REPEAT_MODE_ALL:{
                    seekTo(0);
                    break;
                }
            }


        }
    }


    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        Log.d("MIRACLE_PLAYER","error: "+error.getErrorCodeName());

        if(error.errorCode==PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED||
                error.errorCode==PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT){
            playerData.setHasError(true);
            transportControls.pause();
        } else {
            transportControls.stop();
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();

        destroyed = true;

        unregisterBecomingNoisyReceiver();

        if(player!=null) {
            player.release();
            player = null;
        }

        if (mediaSessionCompat!=null) {
            mediaSessionCompat.setActive(false);
            mediaSessionCompat.setCallback(null);
            mediaSessionCompat.release();
        }

        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void updateProgressValue(){
        long duration;
        long position;
        long bufferedPosition;
        long delay = 500;

        if(player==null||playerData==null){
            position = 0;
            delay+=500;
        } else {
            position = player.getCurrentPosition();
            duration = player.getDuration();
            bufferedPosition = player.getBufferedPosition();
            if(position!=playerData.getCurrentPosition()||duration!=playerData.getDuration()){
                onPlayerEventListener.onPlaybackPositionChange(position,duration);
            }

            if(bufferedPosition!=playerData.getBufferedPosition()){
                onPlayerEventListener.onBufferChange(bufferedPosition);
            }

            if(!playerData.getPlayWhenReady()){
                delay+=2000;
            }
        }

        if(!destroyed) {
            delay -= position % 500;
            handler.postDelayed(this::updateProgressValue, delay);
        }
    }

    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if(player!=null) {
                pause();
            }
        }
    };

    private void registerBecomingNoisyReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    private void unregisterBecomingNoisyReceiver(){
        unregisterReceiver(becomingNoisyReceiver);
    }

}
