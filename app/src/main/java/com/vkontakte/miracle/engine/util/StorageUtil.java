package com.vkontakte.miracle.engine.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleApp;
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
import java.util.Locale;

import static com.vkontakte.miracle.engine.util.LogTags.STORAGE_TAG;

public class StorageUtil {

    public static final String CACHES_NAME = "Caches_%1$s";
    public static final String CACHES_NAME_PUBLIC = "Caches";
    public static final String USERS_NAME = "users.ser";
    public static final String SONGS_NAME = "songs.ser";
    public static final String PLAYLISTS_NAME = "playlists.ser";
    public static final String IMAGES_NAME = "Images";

    public static final String MESSAGE_ADDED_LONG_POLL_UPDATES_NAME = "messageAddedLongPollUpdates.ser";
    public static final String MESSAGE_READ_LONG_POLL_UPDATES_NAME = "messageReadLongPollUpdates.ser";


    private static StorageUtil instance;

    public StorageUtil(){
        instance = this;
    }

    public static StorageUtil getInstance(){
        return new StorageUtil();
    }

    public void initializePublicDirectories(){
        File cachesDir = createNewDirectory(CACHES_NAME_PUBLIC,MiracleApp.getInstance().getFilesDir());
        createNewFile(USERS_NAME, cachesDir);
    }

    public void initializeDirectories(){

        File cachesDir = createNewDirectory(getCurrentUserCachesPath(),MiracleApp.getInstance().getFilesDir());

        createNewFile(SONGS_NAME, cachesDir);

        createNewFile(PLAYLISTS_NAME, cachesDir);

        createNewDirectory(IMAGES_NAME, cachesDir);

        createNewFile(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME,cachesDir);

        createNewFile(MESSAGE_READ_LONG_POLL_UPDATES_NAME,cachesDir);
    }

    private String getCurrentUserCachesPath(){
        return getUserCachesPath(currentUser());
    }

    public String getUserCachesPath(ProfileItem profileItem){
        return String.format(Locale.getDefault(), CACHES_NAME, profileItem.getId());
    }

    private File getCurrentUserCachesDir(){
        return new File(MiracleApp.getInstance().getFilesDir(), getCurrentUserCachesPath());
    }

    public File getUserCachesDir(ProfileItem profileItem){
        return new File(MiracleApp.getInstance().getFilesDir(), getUserCachesPath(profileItem));
    }

    private File getPublicCachesDir(){
        return new File(MiracleApp.getInstance().getFilesDir(), CACHES_NAME_PUBLIC);
    }

    private File createNewFile(String name, File parent){
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

    private File createNewDirectory(String name, File parent){
        File file = new File(parent,name);
        if(!file.exists()) {
            if (file.mkdir()) {
                Log.d(STORAGE_TAG, name+" created");
            }
        }
        return file;
    }

    private ObjectOutputStream getOutputStream(String filename) throws IOException {
        return getOutputStream(filename, getCurrentUserCachesDir());
    }

    private ObjectInputStream getInputStream(String filename) throws IOException {
        return getInputStream(filename,getCurrentUserCachesDir());
    }

    private ObjectOutputStream getOutputStream(String filename, File parent) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(parent,filename));
        return new ObjectOutputStream(fileOutputStream);
    }

    private ObjectInputStream getInputStream(String filename, File parent) throws IOException {
        FileInputStream streamIn = new FileInputStream(new File(parent,filename));
        return new ObjectInputStream(streamIn);
    }

    @NonNull
    public ArrayList<ProfileItem> loadUsers(){

        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = getInputStream(USERS_NAME, getPublicCachesDir());
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

    public ProfileItem currentUser(){
        return loadUsers().get(0);
    }

    public void saveUsers(ArrayList<ProfileItem> profileItems){
        ObjectOutputStream fileOutputStream = null;
        try {
            fileOutputStream = getOutputStream(USERS_NAME, getPublicCachesDir());
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

    public void saveBitmap(Bitmap bitmap, String name){
        File cachesDir = getCurrentUserCachesDir();
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

    public Bitmap loadBitmap(String path){
        return loadBitmap(path,getCurrentUserCachesDir());
    }

    public Bitmap loadBitmap(String path, File parent){
        File imagesDir = new File(parent, IMAGES_NAME);
        File imageFile = new File(imagesDir, path);
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public void removeBitmap(String path){
        removeBitmap(path, getCurrentUserCachesDir());
    }

    public void removeBitmap(String path, File parent){
        File imagesDir = new File(parent, IMAGES_NAME);
        File imageFile = new File(imagesDir, path);
        if(imageFile.exists()){
            if (imageFile.delete()) {
                Log.d(STORAGE_TAG, path+" deleted");
            }
        }
    }

    public boolean removeDirectory(File file){
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                removeDirectory(f);
            }
        }
        return file.delete();
    }

    @NonNull
    public ArrayList<MessageAddedUpdate> loadMessageAddedLongPollUpdates(){
        return loadMessageAddedLongPollUpdates(currentUser());
    }

    @NonNull
    public ArrayList<MessageAddedUpdate> loadMessageAddedLongPollUpdates(ProfileItem profileItem){

        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = getInputStream(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME, getUserCachesDir(profileItem));
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

    public void writeMessageAddedLongPollUpdates(ArrayList<MessageAddedUpdate> longPollUpdates, ProfileItem profileItem){
        ObjectOutputStream fileOutputStream = null;

        ArrayList<MessageAddedUpdate> previousLongPollUpdates = loadMessageAddedLongPollUpdates(profileItem);

        if(previousLongPollUpdates.size()>=250||previousLongPollUpdates.size()+longPollUpdates.size()>250){
            previousLongPollUpdates.subList(0, longPollUpdates.size()).clear();
        }

        previousLongPollUpdates.addAll(longPollUpdates);

        try {
            fileOutputStream = getOutputStream(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME);
            fileOutputStream.writeObject(previousLongPollUpdates);
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

    @NonNull
    public ArrayList<MessageReadUpdate> loadMessageReadLongPollUpdates(){
        return loadMessageReadLongPollUpdates(currentUser());
    }

    @NonNull
    public ArrayList<MessageReadUpdate> loadMessageReadLongPollUpdates(ProfileItem profileItem){

        ObjectInputStream objectInputStream = null;

        try {
            objectInputStream = getInputStream(MESSAGE_READ_LONG_POLL_UPDATES_NAME, getUserCachesDir(profileItem));
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

    public void writeMessageReadLongPollUpdates(ArrayList<MessageReadUpdate> longPollUpdates, ProfileItem profileItem){
        ObjectOutputStream fileOutputStream = null;

        ArrayList<MessageReadUpdate> previousLongPollUpdates = loadMessageReadLongPollUpdates(profileItem);

        if(previousLongPollUpdates.size()>=250||previousLongPollUpdates.size()+longPollUpdates.size()>250){
            previousLongPollUpdates.subList(0, longPollUpdates.size()).clear();
        }

        previousLongPollUpdates.addAll(longPollUpdates);

        try {
            fileOutputStream = getOutputStream(MESSAGE_READ_LONG_POLL_UPDATES_NAME);
            fileOutputStream.writeObject(previousLongPollUpdates);
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

    public static StorageUtil get(){
        if (null == instance){
            instance = StorageUtil.getInstance();
        }
        return instance;
    }

}
