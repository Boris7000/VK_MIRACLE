package com.vkontakte.miracle.engine.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.longpoll.model.MessageReadUpdate;
import com.vkontakte.miracle.model.users.ProfileItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static com.vkontakte.miracle.engine.util.LogTags.STORAGE_TAG;

public class StorageUtil {

    public static final String USERS_NAME = "users.ser";
    public static final String SONGS_NAME = "songs.ser";
    public static final String PLAYLISTS_NAME = "playlists.ser";
    public static final String CACHES_NAME = "Caches";
    public static final String IMAGES_NAME = "Images";

    public static final String MESSAGE_ADDED_LONG_POLL_UPDATES_NAME = "messageAddedLongPollUpdates.ser";
    public static final String MESSAGE_READ_LONG_POLL_UPDATES_NAME = "messageReadLongPollUpdates.ser";


    public static void initializeDirectories(Context context){
        Context applicationContext = context.getApplicationContext();

        File internalStorageDir = applicationContext.getFilesDir();

        File cachesDir = createNewDirectory(CACHES_NAME, internalStorageDir);

        createNewFile(SONGS_NAME, cachesDir);

        createNewFile(PLAYLISTS_NAME, cachesDir);

        createNewFile(USERS_NAME, cachesDir);

        createNewDirectory(IMAGES_NAME, cachesDir);

        createNewFile(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME,cachesDir);

        createNewFile(MESSAGE_READ_LONG_POLL_UPDATES_NAME,cachesDir);
    }

    public static File createNewFile(String name, File parent){
        File file = new File(parent,name);
        if(!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Log.d(STORAGE_TAG, name+" created");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File createNewDirectory(String name, File parent){
        File file = new File(parent,name);
        if(!file.exists()) {
            if (file.mkdir()) {
                Log.d(STORAGE_TAG, name+" created");
            }
        }
        return file;
    }

    public static ObjectOutputStream getOutputStream(String filename, Context context) throws IOException {
        Context applicationContext = context.getApplicationContext();
        File internalStorageDir = applicationContext.getFilesDir();
        File cachesDir = new File(internalStorageDir, CACHES_NAME);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(cachesDir,filename));
        return new ObjectOutputStream(fileOutputStream);
    }

    public static ObjectInputStream getInputStream(String filename, Context context) throws IOException {
        Context applicationContext = context.getApplicationContext();
        File internalStorageDir = applicationContext.getFilesDir();
        File cachesDir = new File(internalStorageDir, CACHES_NAME);
        FileInputStream streamIn = new FileInputStream(new File(cachesDir,filename));
        return new ObjectInputStream(streamIn);
    }

    @NonNull
    public static ArrayList<ProfileItem> loadUsers(Context context){

        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = getInputStream(USERS_NAME, context);
            ArrayList<?> objects = (ArrayList<?>) objectInputStream.readObject();
            objectInputStream.close();
            if(objects==null) objects = new ArrayList<>();

            ArrayList<ProfileItem> profileItems = new ArrayList<>();
            for (Object o:objects){
                profileItems.add((ProfileItem) o);
            }
            return profileItems;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return new ArrayList<>();
        }

    }

    public static void saveUsers(ArrayList<ProfileItem> profileItems, Context context){
        ObjectOutputStream fileOutputStream = null;
        try {
            fileOutputStream = getOutputStream(USERS_NAME, context);
            fileOutputStream.writeObject(profileItems);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void saveBitmap(Bitmap bitmap, String name, Context context){
        Context applicationContext = context.getApplicationContext();
        File internalStorageDir = applicationContext.getFilesDir();
        File cachesDir = new File(internalStorageDir, CACHES_NAME);
        File imagesDir = new File(cachesDir, IMAGES_NAME);
        File imageFile = new File(imagesDir, name);

        if(!imageFile.exists()) {
            try {
                if (imageFile.createNewFile()) {
                    Log.d(STORAGE_TAG, name+" created");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.PNG, Bitmap.CompressFormat.PNG.ordinal(), fos);

            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public static Bitmap loadBitmap(String name, Context context){
        Context applicationContext = context.getApplicationContext();
        File internalStorageDir = applicationContext.getFilesDir();
        File cachesDir = new File(internalStorageDir, CACHES_NAME);
        File imagesDir = new File(cachesDir, IMAGES_NAME);
        File imageFile = new File(imagesDir, name);
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public static void removeBitmap(String name, Context context){
        Context applicationContext = context.getApplicationContext();
        File internalStorageDir = applicationContext.getFilesDir();
        File cachesDir = new File(internalStorageDir, CACHES_NAME);
        File imagesDir = new File(cachesDir, IMAGES_NAME);
        File imageFile = new File(imagesDir, name);
        if(imageFile.exists()){
            if (imageFile.delete()) {
                Log.d(STORAGE_TAG, name+" deleted");
            }
        }
    }


    @NonNull
    public static ArrayList<MessageAddedUpdate> loadMessageAddedLongPollUpdates(Context context){

        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = getInputStream(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME, context);
            ArrayList<?> objects = (ArrayList<?>) objectInputStream.readObject();
            objectInputStream.close();
            if(objects==null) objects = new ArrayList<>();

            ArrayList<MessageAddedUpdate> longPollUpdates = new ArrayList<>();
            for (Object o:objects){
                longPollUpdates.add((MessageAddedUpdate) o);
            }
            return longPollUpdates;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return new ArrayList<>();
        }

    }

    public static void writeMessageAddedLongPollUpdates(ArrayList<MessageAddedUpdate> longPollUpdates, Context context){
        ObjectOutputStream fileOutputStream = null;

        ArrayList<MessageAddedUpdate> previousLongPollUpdates = loadMessageAddedLongPollUpdates(context);

        if(previousLongPollUpdates.size()>=250||previousLongPollUpdates.size()+longPollUpdates.size()>250){
            previousLongPollUpdates.subList(0, longPollUpdates.size()).clear();
        }

        previousLongPollUpdates.addAll(longPollUpdates);

        try {
            fileOutputStream = getOutputStream(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME, context);
            fileOutputStream.writeObject(previousLongPollUpdates);
            fileOutputStream.close();
        } catch (IOException e) {
            Log.d("eifiejfiejfi", e.getMessage());
            e.printStackTrace();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @NonNull
    public static ArrayList<MessageReadUpdate> loadMessageReadLongPollUpdates(Context context){

        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = getInputStream(MESSAGE_READ_LONG_POLL_UPDATES_NAME, context);
            ArrayList<?> objects = (ArrayList<?>) objectInputStream.readObject();
            objectInputStream.close();
            if(objects==null) objects = new ArrayList<>();

            ArrayList<MessageReadUpdate> longPollUpdates = new ArrayList<>();
            for (Object o:objects){
                longPollUpdates.add((MessageReadUpdate) o);
            }
            return longPollUpdates;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return new ArrayList<>();
        }

    }

    public static void writeMessageReadLongPollUpdates(ArrayList<MessageReadUpdate> longPollUpdates, Context context){
        ObjectOutputStream fileOutputStream = null;

        ArrayList<MessageReadUpdate> previousLongPollUpdates = loadMessageReadLongPollUpdates(context);

        if(previousLongPollUpdates.size()>=250||previousLongPollUpdates.size()+longPollUpdates.size()>250){
            previousLongPollUpdates.subList(0, longPollUpdates.size()).clear();
        }

        previousLongPollUpdates.addAll(longPollUpdates);

        try {
            fileOutputStream = getOutputStream(MESSAGE_READ_LONG_POLL_UPDATES_NAME, context);
            fileOutputStream.writeObject(previousLongPollUpdates);
            fileOutputStream.close();
        } catch (IOException e) {
            Log.d("eifiejfiejfi", e.getMessage());
            e.printStackTrace();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
