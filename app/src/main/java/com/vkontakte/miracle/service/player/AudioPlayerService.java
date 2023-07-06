package com.vkontakte.miracle.service.player;

import static com.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.vkontakte.miracle.notification.AppNotificationChannels.AUDIO_CHANNEL_ID;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.ACTION_CHANGE_REPEAT;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.ACTION_NEXT;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.ACTION_PAUSE;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.ACTION_PLAY;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.ACTION_PREVIOUS;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.ACTION_SEEK_TO;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.ACTION_STOP;
import static com.vkontakte.miracle.service.player.AudioPlayerControls.playbackAction;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.Player.RepeatMode;
import androidx.media3.exoplayer.analytics.AnalyticsListener;

import com.miracle.engine.async.baseExecutors.SimpleTimer;
import com.miracle.engine.util.DimensionsUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.notification.AppNotificationChannels;

import java.util.LinkedHashSet;

public class AudioPlayerService extends Service implements ExoPlayer.Listener, AnalyticsListener {

    //public static final String LOG_TAG = "AudioPlayerService";

    //Service
    private final IBinder binder = new AudioPlayerServiceBinder(this);
    private boolean foregrounded = false;
    private boolean destroyed = false;

    //App
    private final AudioPlayerSettingsUtil settingsUtil = AudioPlayerSettingsUtil.get();

    //Player
    private ExoPlayer player;
    private final AudioPlayerState audioPlayerState = new AudioPlayerState();
    private AudioPlayerMedia audioPlayerMedia;
    private AudioPlayerMedia nextAudioPlayerMedia;

    //MediaSession
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;

    //Notification
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = -808;
    private Bitmap songImage, placeholderImage;
    private String songImageUrl = "";
    private int maxImageSize;
    private int placeholderSize;
    private Target target;

    //Callbacks
    @NonNull
    private final LinkedHashSet<AudioPlayerEventListener> audioPlayerEventListeners = new LinkedHashSet<>();
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(MainApp.getInstance().getThemeRecourseId());

        maxImageSize = getResources().getDimensionPixelSize(R.dimen.compat_notification_large_icon_max_width);
        placeholderSize = (int) DimensionsUtil.dpToPx(this, 84);

        placeholderImage = createPlaceHolderBitmap();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = AppNotificationChannels.getAudioChannel(this);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        handler = new Handler(Looper.getMainLooper());

        audioPlayerState.setRepeatMode(settingsUtil.getRepeatMode());

        initMediaSession();

        initializePlayer();

        updateProgressValue();

        registerBecomingNoisyReceiver();

        final long time = Build.VERSION.SDK_INT>=Build.VERSION_CODES.S?9500:4700;
        new SimpleTimer(time,time){
            @Override
            public void onExecute(Boolean object) {
                if(!foregrounded&&!destroyed){
                    if(audioPlayerMedia!=null) {
                        songImageUrl = "";
                        updateTarget(audioPlayerMedia.getCurrentAudioItem());
                        target.onBitmapLoaded(placeholderImage, null);
                    }else {
                        AudioPlayerService.this.stop();
                    }
                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIncomingAction(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleIncomingAction(Intent playbackAction){

        if (destroyed||playbackAction==null||playbackAction.getAction()==null){
            return;
        }

        String actionString = playbackAction.getAction();

        switch (actionString){
            case ACTION_PLAY:{
                play();
                break;
            }
            case ACTION_PAUSE:{
                pause();
                break;
            }
            case ACTION_STOP:{
                stop();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    ///////////////////////////////////////////////////////////////////////////

    public void playNewMedia(@Nullable AudioPlayerMedia audioPlayerMedia){

        if(audioPlayerMedia!=null){

            AudioItem audioItemOld = this.audioPlayerMedia==null?null:this.audioPlayerMedia.getCurrentAudioItem();
            AudioItem audioItemNew = audioPlayerMedia.getCurrentAudioItem();

            final boolean isNull = this.audioPlayerMedia==null;
            final boolean equals = !isNull&&this.audioPlayerMedia.equalsSource(audioPlayerMedia);

            this.audioPlayerMedia = audioPlayerMedia;

            if(isNull||!equals){
                notifyOnMediaChange();
            }

            if(audioItemNew.equals(audioItemOld)){
                if (player.getPlayWhenReady()) {
                    pause();
                } else {
                    play();
                }
            } else {
                playNewAudioItem(audioItemNew);
            }
        }

    }

    public void playNextMedia(@Nullable AudioPlayerMedia audioPlayerMedia){
        if(this.audioPlayerMedia==null){
            playNewMedia(audioPlayerMedia);
        } else {
            nextAudioPlayerMedia = audioPlayerMedia;
        }
    }

    private void initializePlayer(){
        if(player==null){
            player = new ExoPlayer.Builder(this).build();
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build();
            player.setAudioAttributes(audioAttributes,true);
            player.addListener(this);
            player.addAnalyticsListener(this);
        }
    }

    private void initMediaSession(){

        mediaSession = new MediaSessionCompat(getApplicationContext(),"AudioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                          PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SEEK_TO
                        | PlaybackStateCompat.ACTION_STOP);
        metadataBuilder = new MediaMetadataCompat.Builder();

        mediaSession.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPrepare() {
                super.onPrepare();
            }

            @Override
            public void onPlay() {
                super.onPlay();
                play();
            }

            @Override
            public void onPause() {
                super.onPause();
                pause();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                seekTo(pos);
            }

            @Override
            public void onStop() {
                super.onStop();
                stop();
            }
        });
    }

    private void playNewAudioItem(AudioItem audioItem){
        notifyOnMediaItemChange();
        updatePlayerStateAndNotify(AudioPlayerState.STATE_BUFFERING);
        updateMediaItem(audioItem);
        updateNotification(audioItem);
    }

    private void updateMediaItem(AudioItem audioItem){
        MediaItem mediaItem = createMediaItem(audioItem);
        player.setMediaItem(mediaItem);
    }

    private void updateNotification(AudioItem audioItem){

        updateTarget(audioItem);

        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                String url = album.getThumb().getOptimalSizeUrl(maxImageSize);
                if(url!=null){
                    if(!url.equals(songImageUrl)) {
                        Picasso.get().load(songImageUrl = url).into(target);
                    } else {
                        target.onBitmapLoaded(songImage, null);
                    }
                    return;
                }
            }
        }
        songImageUrl = "";
        target.onBitmapLoaded(placeholderImage, null);
    }

    private void updateTarget(AudioItem audioItem){

        if(target!=null){
            Picasso.get().cancelRequest(target);
        }

        target =  new ATarget() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if(!destroyed) {
                    songImage = bitmap;
                    player.prepare();
                    player.play();
                    sendNotification(audioItem);
                }
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                onBitmapLoaded(placeholderImage, null);
            }
        };
    }

    private void sendNotification(AudioItem audioItem){
        updateMetaData(audioItem);

        Notification notification = createNotification(audioItem, songImage, player.getPlayWhenReady());

        if(foregrounded){
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            foregrounded = true;
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void updateMetaData(AudioItem audioItem) {

        boolean changeWallpaper = settingsUtil.getChangeWallpapers();
        Bitmap wallpaperBitmap = changeWallpaper?songImage:null;

        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, wallpaperBitmap)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioItem.getDuration()*1000L)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioItem.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioItem.getTitle());

        mediaSession.setMetadata(metadataBuilder.build());
    }

    public void updateTheme(){
        setTheme(MainApp.getInstance().getThemeRecourseId());
        if(songImageUrl.isEmpty()){
            songImage = placeholderImage = createPlaceHolderBitmap();
            sendNotification(audioPlayerMedia.getCurrentAudioItem());
        } else {
            placeholderImage = createPlaceHolderBitmap();
        }
    }

    private void updatePlaybackState(int playbackState, int speed){
        if(player!=null) {
            stateBuilder.setState(playbackState, player.getCurrentPosition(), speed, SystemClock.elapsedRealtime());
            mediaSession.setPlaybackState(stateBuilder.build());
        }
    }

    private void updatePlayerStateAndNotify(int state){
        if(audioPlayerState.getState()!=state){
            audioPlayerState.setState(state);
            notifyOnStateChange();
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private MediaItem createMediaItem(AudioItem audioItem){
        if(audioItem.getDownloaded()==null){
            return MediaItem.fromUri(Uri.parse(audioItem.getUrl()));
        } else {
            return MediaItem.fromUri(Uri.parse(audioItem.getDownloaded().getPath()));
        }
    }

    private Notification createNotification(AudioItem audioItem, Bitmap largeIcon, boolean playWhenReady){

        int icon= R.drawable.ic_pause_28;
        int smallIcon=R.drawable.ic_play_28;
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

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("PlayerBottomSheetExpanded", true);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        ////////////////////////////////////////////
        ////////////////////////////////////////////

        NotificationCompat.Builder  notificationBuilder = new NotificationCompat.Builder(this, AUDIO_CHANNEL_ID)// Create a new Notification
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()// Set the Notification style
                        .setMediaSession(mediaSession.getSessionToken())// Attach our MediaSession token
                        .setShowActionsInCompactView(1, 2, 3))  // Show our playback controls in the compat view
                .setShowWhen(false) // Hide the timestamp
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentText(audioItem.getArtist()) // Set Notification content information
                .setContentTitle(audioItem.getTitle())
                .setContentInfo(audioItem.getTitle())
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_cancel_28,"Close",playbackAction(4, this))  // Add playback actions
                .addAction(R.drawable.ic_skip_previous_28, "play previous", playbackAction(3, this))
                .addAction(icon, "Pause", playPauseAction)
                .addAction(R.drawable.ic_skip_next_28, "Play next", playbackAction(2,this))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setChannelId(AUDIO_CHANNEL_ID);

        return notificationBuilder.build();
    }

    private Bitmap createPlaceHolderBitmap(){
        return bitmapFromLayerDrawable(this, R.drawable.audio_placeholder_image_colored, placeholderSize, placeholderSize);
    }

    private boolean hasMedia(){
        return audioPlayerMedia!=null;
    }

    ///////////////////////////////////////////////////////////////////////////

    public void play() {
        if (hasMedia() && player != null) {
            if (audioPlayerState.getState() == AudioPlayerState.STATE_ERROR) {
                AudioItem audioItem = audioPlayerMedia.getCurrentAudioItem();
                MediaItem mediaItem = createMediaItem(audioItem);
                player.setMediaItem(mediaItem, audioPlayerState.getPlaybackPosition());
                updatePlayerStateAndNotify(AudioPlayerState.STATE_BUFFERING);
                player.prepare();
                player.play();
            } else {
                player.setPlayWhenReady(true);
            }
        }
    }

    public void pause() {
        if(hasMedia()&&player!=null) {
            player.setPlayWhenReady(false);
        }
    }

    public void skipToNext() {
        if(hasMedia()&&player!=null){
            if(nextAudioPlayerMedia!=null){
                playNewMedia(nextAudioPlayerMedia);
                nextAudioPlayerMedia = null;
            } else {
                audioPlayerMedia.incrementIndex();
                playNewAudioItem(audioPlayerMedia.getCurrentAudioItem());
            }
        }
    }

    public void skipToPrevious() {
        if(hasMedia()&&player!=null){
            long returnPlaybackTime = settingsUtil.getReturnPlaybackTime()*1000L;
            if (returnPlaybackTime>0&&player.getCurrentPosition()>returnPlaybackTime) {
                player.seekTo(0);
            } else {
                audioPlayerMedia.decrementIndex();
                playNewAudioItem(audioPlayerMedia.getCurrentAudioItem());
            }
        }
    }

    public void skipTo(int pos) {
        if(hasMedia()&&player!=null){

            AudioItem audioItemOld = audioPlayerMedia.getCurrentAudioItem();
            AudioItem audioItemNew = audioPlayerMedia.getUnwrappedAudioItems().get(pos);

            if(audioItemOld.equals(audioItemNew)){
                if (player.getPlayWhenReady()) {
                    pause();
                } else {
                    play();
                }
            } else {
                audioPlayerMedia.setItemIndex(pos);
                playNewAudioItem(audioItemNew);
            }
        }
    }

    public void seekTo(long pos) {
        if(hasMedia()&&player!=null){
            player.seekTo(pos);
        }
    }

    public void seekToPercent(float percent) {
        seekTo((long)(audioPlayerState.getDuration()*percent));
    }

    public void stop() {
        notifyOnPlayerClose();
        stopSelf();
    }

    public void changeRepeatMode(@RepeatMode int repeatMode) {
        if(hasMedia()){
            if(audioPlayerState.getRepeatMode()!=repeatMode) {
                audioPlayerState.setRepeatMode(repeatMode);
                settingsUtil.storeRepeatMode(repeatMode);
                notifyOnRepeatModeChange();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { pause(); }
    };

    private void registerBecomingNoisyReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    private void unregisterBecomingNoisyReceiver(){
        unregisterReceiver(becomingNoisyReceiver);
    }

    private void updateProgressValue(){

        long duration;
        long position;
        long bufferedPosition;
        long delay = 500;

        if(hasMedia()&&player!=null){
            duration = Math.max(1,player.getDuration());
            position = Math.min(duration,player.getCurrentPosition());
            bufferedPosition = player.getBufferedPosition();

            if(duration!=audioPlayerState.getDuration()) {
                audioPlayerState.setDuration(duration);
                notifyOnDurationChange();
            }

            if(position!=audioPlayerState.getPlaybackPosition()) {
                audioPlayerState.setPlaybackPosition(position);
                notifyOnPlaybackPositionChange();
            }

            if(bufferedPosition!=audioPlayerState.getBufferedPosition()) {
                audioPlayerState.setBufferedPosition(bufferedPosition);
                notifyOnBufferChange();
            }

            if(!player.getPlayWhenReady()){
                delay+=2000;
            }
        }

        if(!destroyed) {
            delay -= audioPlayerState.getPlaybackPosition() % 500;
            handler.postDelayed(this::updateProgressValue, delay);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onPlayWhenReadyChanged(@NonNull AnalyticsListener.EventTime eventTime, boolean playWhenReady, int reason) {
        AudioItem audioItem = audioPlayerMedia.getCurrentAudioItem();
        Notification notification = createNotification(audioItem, songImage, playWhenReady);
        notificationManager.notify(NOTIFICATION_ID, notification);
        audioPlayerState.setPlayWhenReady(playWhenReady);
        notifyPlayWhenReadyChange();
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {

        if(isPlaying){
            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, 1);
        }else {
            int state = player.getPlaybackState();
            if(state!=ExoPlayer.STATE_BUFFERING&&state!=ExoPlayer.STATE_ENDED){
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED, 0);
            }
        }
        notifyOnStateChange();
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onPlaybackStateChanged(@NonNull EventTime eventTime, int playbackState) {
        if(playbackState==ExoPlayer.STATE_READY){
            if(player.isPlaying()){
                updatePlayerStateAndNotify(AudioPlayerState.STATE_PLAYING);
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, 1);
            } else {
                updatePlayerStateAndNotify(AudioPlayerState.STATE_PAUSED);
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED, 0);
            }
            notifyOnStateChange();
            return;
        }

        if(playbackState==ExoPlayer.STATE_BUFFERING) {
            updatePlayerStateAndNotify(AudioPlayerState.STATE_BUFFERING);
            updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING, 0);
            notifyOnStateChange();
            return;
        }

        if(playbackState==ExoPlayer.STATE_ENDED) {
            switch (audioPlayerState.getRepeatMode()) {
                case Player.REPEAT_MODE_OFF: {
                    if (audioPlayerMedia.getItemIndex() + 1 >= audioPlayerMedia.getAudioItems().size()) {
                        seekTo(0);
                        pause();
                    } else {
                        skipToNext();
                    }
                    break;
                }
                case Player.REPEAT_MODE_ONE: {
                    seekTo(0);
                    break;
                }
                case Player.REPEAT_MODE_ALL: {
                    skipToNext();
                    break;
                }
            }
        }
    }

    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        if(error.errorCode==PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED||
                error.errorCode==PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT){
            updatePlayerStateAndNotify(AudioPlayerState.STATE_ERROR);
            transportControls.pause();
        } else {
            transportControls.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        destroyed = true;

        unregisterBecomingNoisyReceiver();

        if(player!=null) {
            player.release();
            player = null;
        }

        if (mediaSession !=null) {
            mediaSession.setActive(false);
            mediaSession.setCallback(null);
            mediaSession.release();
        }

        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    ///////////////////////////////////////////////////////////////////////////

    public void addAudioPlayerEventListener(@NonNull AudioPlayerEventListener listener) {
        audioPlayerEventListeners.add(listener);
        listener.onPlayerBind(audioPlayerState);
    }

    public void removeAudioPlayerEventListener(@NonNull AudioPlayerEventListener listener) {
        audioPlayerEventListeners.remove(listener);
    }

    public void clearAudioPlayerEventListeners() {
        audioPlayerEventListeners.clear();
    }

    ///////////////////////////////////////////////////////////////////////////

    private void notifyOnDurationChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onDurationChange(audioPlayerState);
        }
    }

    private void notifyOnPlaybackPositionChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onPlaybackPositionChange(audioPlayerState);
        }
    }

    private void notifyOnBufferChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onBufferChange(audioPlayerState);
        }
    }

    private void notifyOnStateChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onStateChange(audioPlayerState);
        }
    }

    private void notifyPlayWhenReadyChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onPlayWhenReadyChange(audioPlayerState);
        }
    }

    private void notifyOnRepeatModeChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onRepeatModeChange(audioPlayerState);
        }
    }

    private void notifyOnMediaItemChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onMediaItemChange(audioPlayerMedia);
        }
    }

    private void notifyOnMediaChange(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onMediaChange(audioPlayerMedia);
        }

    }

    private void notifyOnPlayerClose(){
        for (AudioPlayerEventListener listener : audioPlayerEventListeners) {
            listener.onPlayerClose();
        }
    }

}
