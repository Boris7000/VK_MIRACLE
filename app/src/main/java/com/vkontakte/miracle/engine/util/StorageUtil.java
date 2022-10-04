package com.vkontakte.miracle.engine.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.service.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.service.longpoll.model.MessageReadUpdate;
import com.vkontakte.miracle.model.users.ProfileItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class StorageUtil {

    public static final String CACHES_NAME = "Caches_%1$s";
    public static final String CACHES_NAME_PUBLIC = "Caches";
    public static final String USERS_NAME = "users.ser";
    public static final String SONGS_NAME = "songs.ser";
    public static final String MP3S_NAME = "MP3s";
    public static final String PLAYLISTS_NAME = "playlists.ser";
    public static final String IMAGES_NAME = "Images";

    public static final String MESSAGE_ADDED_LONG_POLL_UPDATES_NAME = "messageAddedLongPollUpdates.ser";
    public static final String MESSAGE_READ_LONG_POLL_UPDATES_NAME = "messageReadLongPollUpdates.ser";

    private final String LOG_TAG = "StorageUtil";

    private static StorageUtil instance;
    private ProfileItem currentUser;

    public StorageUtil(){
        instance = this;
        updateCurrentUser();
    }

    public static StorageUtil getInstance(){
        return new StorageUtil();
    }

    public static StorageUtil get(){
        StorageUtil localInstance = instance;
        if (localInstance == null) {
            synchronized (StorageUtil.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = getInstance();
                }
            }
        }
        return localInstance;
    }

    ////////////////////////////////////////////////////

    public void initializePublicDirectories(){
        File cachesDir = createNewDirectory(CACHES_NAME_PUBLIC,MiracleApp.getInstance().getFilesDir());
        createNewFile(USERS_NAME, cachesDir);
    }

    public void initializeDirectories(){
        File cachesDir = createNewDirectory(getCurrentUserCachesPath(),MiracleApp.getInstance().getFilesDir());

        createNewFile(SONGS_NAME, cachesDir);

        createNewFile(PLAYLISTS_NAME, cachesDir);

        createNewDirectory(IMAGES_NAME, cachesDir);

        createNewDirectory(MP3S_NAME, cachesDir);

        createNewFile(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME,cachesDir);

        createNewFile(MESSAGE_READ_LONG_POLL_UPDATES_NAME,cachesDir);
    }

    public void removeDirectory(@Nullable File file){
        if(file!=null) {
            File[] contents = file.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    removeDirectory(f);
                }
            }
            if(file.delete()){
                Log.d(LOG_TAG, "Deleted "+file.getAbsolutePath());
            } else {
                Log.d(LOG_TAG, "Unable to delete "+file.getAbsolutePath());
            }
        }
    }

    public void writeObject(String path, File parent, Object object){
        ObjectOutputStream fileOutputStream = null;
        try {
            fileOutputStream = getOutputStream(path, parent);
            if(fileOutputStream!=null) {
                fileOutputStream.writeObject(object);
                fileOutputStream.close();
            }
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

    public File createNewFile(String name, File parent){
        return createNewFile(new File(parent,name));
    }

    public File createNewFile(File file){
        if(!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Log.d(LOG_TAG, "Created file "+file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public boolean deleteFile(String name, File parent){
        return deleteFile(new File(parent,name));
    }

    public boolean deleteFile(File file){
        if(file.exists()) {
            if (file.delete()) {
                Log.d(LOG_TAG, "Deleted file "+file.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    public File createNewDirectory(String name, File parent){
        File file = new File(parent,name);
        if(!file.exists()) {
            if (file.mkdir()) {
                Log.d(LOG_TAG, "Created directory "+file.getAbsolutePath());
            }
        }
        return file;
    }

    ////////////////////////////////////////////////////

    @Nullable
    private String getCurrentUserCachesPath(){
        return getUserCachesPath(currentUser());
    }

    @Nullable
    public String getUserCachesPath(@Nullable ProfileItem profileItem){
        if(profileItem==null){
            return null;
        } else {
            return String.format(Locale.getDefault(), CACHES_NAME, profileItem.getId());
        }
    }

    @Nullable
    private File getCurrentUserCachesDir(){
        String currentUserCachesPath = getCurrentUserCachesPath();
        if(currentUserCachesPath==null){
            return null;
        } else {
            return new File(MiracleApp.getInstance().getFilesDir(), currentUserCachesPath);
        }
    }

    @Nullable
    public File getUserCachesDir(@Nullable ProfileItem profileItem){
        String userCachesPath = getUserCachesPath(profileItem);
        if(userCachesPath==null){
          return null;
        } else {
            return new File(MiracleApp.getInstance().getFilesDir(), userCachesPath);
        }
    }

    @NonNull
    private File getPublicCachesDir(){
        return new File(MiracleApp.getInstance().getFilesDir(), CACHES_NAME_PUBLIC);
    }

    ////////////////////////////////////////////////////

    @Nullable
    private ObjectOutputStream getOutputStream(String filename) throws IOException {
        File currentUserCachesDir = getCurrentUserCachesDir();
        if(currentUserCachesDir==null){
            return null;
        } else {
            return getOutputStream(filename, currentUserCachesDir);
        }
    }

    @Nullable
    private ObjectInputStream getInputStream(String filename) throws IOException {
        File currentUserCachesDir = getCurrentUserCachesDir();
        if(currentUserCachesDir==null){
            return null;
        } else {
            return getInputStream(filename,currentUserCachesDir);
        }
    }

    @Nullable
    private ObjectOutputStream getOutputStream(@Nullable String filename, @Nullable File parent) throws IOException {
        if(filename==null||parent==null){
            return null;
        } else {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(parent,filename));
            return new ObjectOutputStream(fileOutputStream);
        }
    }

    @Nullable
    private ObjectInputStream getInputStream(@Nullable String filename, @Nullable File parent) throws IOException {
        if(filename==null||parent==null){
            return null;
        } else {
            FileInputStream fileInputStream = new FileInputStream(new File(parent, filename));
            return new ObjectInputStream(fileInputStream);
        }
    }

    ////////////////////////////////////////////////////

    public void updateCurrentUser(){
        currentUser = loadCurrentUser();
    }

    @Nullable
    public ProfileItem loadCurrentUser(){
        ArrayList<ProfileItem> profileItems = loadUsers();
        if(profileItems.isEmpty()){
            return null;
        } else {
            return profileItems.get(0);
        }
    }

    @NonNull
    public ArrayList<ProfileItem> loadUsers(){
        return new ArrayListReader<ProfileItem>(this)
                .read(USERS_NAME, getPublicCachesDir(), object -> (ProfileItem) object);
    }

    public ProfileItem currentUser(){
        return currentUser;
    }

    public void saveUsers(ArrayList<ProfileItem> profileItems){
        writeObject(USERS_NAME, getPublicCachesDir(), profileItems);
        updateCurrentUser();
    }

    ////////////////////////////////////////////////////

    public static class ArrayListReader<T>{

        private final StorageUtil storageUtil;

        public ArrayListReader(StorageUtil storageUtil) {
            this.storageUtil = storageUtil;
        }

        public ArrayList<T> read(String path, File parent, AnonymousConverter<T> converter){
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = storageUtil.getInputStream(path, parent);
                if(objectInputStream!=null) {
                    ArrayList<?> objects = (ArrayList<?>) objectInputStream.readObject();
                    objectInputStream.close();
                    if (objects == null) {
                        objects = new ArrayList<>();
                    }
                    ArrayList<T> items = new ArrayList<>();
                    for (Object object : objects) {
                        if(object!=null){
                            items.add(converter.convert(object));
                        }
                    }
                    return items;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return new ArrayList<>();
        }
    }

    public interface AnonymousConverter<T>{
        T convert(Object object);
    }

    ////////////////////////////////////////////////////

    public void saveBitmap(Bitmap bitmap, @Nullable String name){

        if(name!=null) {
            File cachesDir = getCurrentUserCachesDir();
            File imagesDir = new File(cachesDir, IMAGES_NAME);
            File imageFile = new File(imagesDir, name);

            if (!imageFile.exists()) {
                try {
                    if (imageFile.createNewFile()) {
                        Log.d(LOG_TAG, "Created file "+imageFile.getAbsolutePath());
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

            } catch (IOException e) {
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

    }

    @Nullable
    public Bitmap loadBitmap(@Nullable String path){
        return loadBitmap(path, getCurrentUserCachesDir());
    }

    @Nullable
    public Bitmap loadBitmap(@Nullable String path, @Nullable File parent){
        if(path==null||parent==null) {
            return null;
        } else {
            File imagesDir = new File(parent, IMAGES_NAME);
            File imageFile = new File(imagesDir, path);
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }
    }

    public void removeBitmap(@Nullable String path){
        removeBitmap(path, getCurrentUserCachesDir());
    }

    public void removeBitmap(String path, File parent){
        if(path!=null&&parent!=null) {
            File imagesDir = new File(parent, IMAGES_NAME);
            File imageFile = new File(imagesDir, path);
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    Log.d(LOG_TAG, "Deleted file "+imageFile.getAbsolutePath());
                }
            }
        }
    }

    ////////////////////////////////////////////////////

    @NonNull
    public ArrayList<MessageAddedUpdate> loadMessageAddedLongPollUpdates(){
        return loadMessageAddedLongPollUpdates(getCurrentUserCachesDir());
    }

    @NonNull
    public ArrayList<MessageAddedUpdate> loadMessageAddedLongPollUpdates(File parent){
        return new ArrayListReader<MessageAddedUpdate>(this)
                .read(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME, parent, object -> (MessageAddedUpdate) object);
    }

    public void writeMessageAddedLongPollUpdates(ArrayList<MessageAddedUpdate> longPollUpdates, File parent){

        ArrayList<MessageAddedUpdate> previousLongPollUpdates = loadMessageAddedLongPollUpdates(parent);

        if(previousLongPollUpdates.size()>=250||previousLongPollUpdates.size()+longPollUpdates.size()>250){
            previousLongPollUpdates.subList(0, longPollUpdates.size()).clear();
        }

        previousLongPollUpdates.addAll(longPollUpdates);

        writeObject(MESSAGE_ADDED_LONG_POLL_UPDATES_NAME, parent, longPollUpdates);
    }

    @NonNull
    public ArrayList<MessageReadUpdate> loadMessageReadLongPollUpdates(){
        return loadMessageReadLongPollUpdates(getCurrentUserCachesDir());
    }

    @NonNull
    public ArrayList<MessageReadUpdate> loadMessageReadLongPollUpdates(File parent){
        return new ArrayListReader<MessageReadUpdate>(this)
                .read(MESSAGE_READ_LONG_POLL_UPDATES_NAME, parent, object -> (MessageReadUpdate) object);
    }

    public void writeMessageReadLongPollUpdates(ArrayList<MessageReadUpdate> longPollUpdates, File parent){

        ArrayList<MessageReadUpdate> previousLongPollUpdates = loadMessageReadLongPollUpdates(parent);

        if(previousLongPollUpdates.size()>=250||previousLongPollUpdates.size()+longPollUpdates.size()>250) {
            previousLongPollUpdates.subList(0, longPollUpdates.size()).clear();
        }

        previousLongPollUpdates.addAll(longPollUpdates);

        writeObject(MESSAGE_READ_LONG_POLL_UPDATES_NAME, parent, previousLongPollUpdates);

    }


}
