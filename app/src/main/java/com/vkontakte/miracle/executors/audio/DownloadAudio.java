package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.StorageUtil.MP3S_NAME;
import static com.vkontakte.miracle.engine.util.StorageUtil.SONGS_NAME;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Downloaded;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Audio;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class DownloadAudio extends AsyncExecutor<Boolean> {

    private final AudioItem audioItem;
    private final User user;
    private OnProgressListener onProgressListener;

    public DownloadAudio(AudioItem audioItem) {
        this.audioItem = audioItem;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {

        try {

            Call<JSONObject> call = Audio.getByID(
                    audioItem.getOwnerId(),
                    audioItem.getId(),
                    "5.87",
                    user.getAccessToken());

            Response<JSONObject> response = call.execute();

            JSONObject jo_response = validateBody(response);

            JSONArray items = jo_response.getJSONArray("response");

            JSONObject jo_audio = items.getJSONObject(0);

            String mp3_url = jo_audio.getString("url");

            StorageUtil storageUtil = StorageUtil.get();

            File cachesDir = storageUtil.getUserCachesDir(user);

            File mp3sDir = new File(cachesDir, MP3S_NAME);
            File mp3File = new File(mp3sDir, audioItem.getOwnerId()+"_"+audioItem.getId()+".mp3");
            File temporaryMp3File = new File(mp3sDir, audioItem.getOwnerId()+"_"+audioItem.getId()+"_cashing.mp3");

            storageUtil.createNewFile(temporaryMp3File);

            FileOutputStream fos = null;
            InputStream fis = null;

            try {

                fos = new FileOutputStream(temporaryMp3File);

                URLConnection uc = new URL(mp3_url).openConnection();
                uc.connect();


                fis = new BufferedInputStream(uc.getInputStream());

                byte[] data = new byte[1024];
                int count;
                int total = uc.getContentLength()/1024;
                int current=0;

                while ((count = fis.read(data)) != -1) {

                    current++;

                    if(onProgressListener!=null){
                        onProgressListener.onProgress((int)(((current*100)/total)));
                    }

                    fos.write(data, 0, count);
                }

                fos.close();
                fis.close();

                if(temporaryMp3File.renameTo(mp3File)){
                    ArrayList<ItemDataHolder> currentAudios =
                            new StorageUtil.ArrayListReader<ItemDataHolder>(storageUtil).
                                    read(SONGS_NAME, cachesDir, object -> (ItemDataHolder) object);

                    audioItem.setDownloaded(new Downloaded(mp3File.getAbsolutePath()));

                    currentAudios.add(0, audioItem);

                    storageUtil.writeObject(SONGS_NAME, cachesDir, currentAudios);
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                storageUtil.deleteFile(temporaryMp3File);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public AudioItem getAudioItem() {
        return audioItem;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public OnProgressListener getOnProgressListener() {
        return onProgressListener;
    }

    public interface OnProgressListener {
        void onProgress(int progress);
    }

}
