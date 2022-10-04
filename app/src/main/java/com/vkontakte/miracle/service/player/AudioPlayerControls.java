package com.vkontakte.miracle.service.player;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AudioPlayerControls {
    public static final String ACTION_PLAY = "VKM_PLAYER_ACTION_PLAY";
    public static final String ACTION_PAUSE = "VKM_PLAYER_ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "VKM_PLAYER_ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "VKM_PLAYER_music.ACTION_NEXT";
    public static final String ACTION_STOP = "VKM_PLAYER_music.ACTION_STOP";
    public static final String ACTION_SEEK_TO = "VKM_PLAYER_ACTION_SEEK_TO";
    public static final String ACTION_CHANGE_REPEAT = "VKM_PLAYER_ACTION_CHANGE_REPEAT";


    public static PendingIntent playbackAction(int actionNumber, Context context) {
        Intent playbackAction = new Intent(context, AudioPlayerService.class);
        switch (actionNumber) {
            case 0:// Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(context, actionNumber, playbackAction, PendingIntent.FLAG_IMMUTABLE);
            case 1:// Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(context, actionNumber, playbackAction, PendingIntent.FLAG_IMMUTABLE);
            case 2:// Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(context, actionNumber, playbackAction, PendingIntent.FLAG_IMMUTABLE);
            case 3:// Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(context, actionNumber, playbackAction, PendingIntent.FLAG_IMMUTABLE);
            case 4:// Stop service
                playbackAction.setAction(ACTION_STOP);
                return PendingIntent.getService(context, actionNumber, playbackAction, PendingIntent.FLAG_IMMUTABLE);
            case 5:// Seek
                playbackAction.setAction(ACTION_SEEK_TO);
                return PendingIntent.getService(context, actionNumber, playbackAction, PendingIntent.FLAG_IMMUTABLE);
            default: return null;
        }
    }

}
