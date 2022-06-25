package com.vkontakte.miracle.engine.util;

import android.content.Context;
import android.widget.EditText;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.Owner;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StringsUtil {

    public static String getTrimmed(EditText editText){
        return editText.getText().toString().trim();
    }

    public static String stringFromArrayList(ArrayList<String> strings,String divider){
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0; i<strings.size();i++){
            stringBuilder.append(strings.get(i));
            if(i+1<strings.size()){
                stringBuilder.append(divider);
            }
        }
        return stringBuilder.toString();
    }

    public static String getWordFirstChar(String string){
        StringBuilder stringBuilder = new StringBuilder(string);
        StringBuilder stringBuilder1 = new StringBuilder();

        for(int i=0;i<stringBuilder.length();i++) {
            if ((i == 0 && (stringBuilder.charAt(i) != ' ')) || stringBuilder.charAt(i - 1) == ' ') {
                stringBuilder1.append(stringBuilder.charAt(i));
                if(stringBuilder1.length()==2){
                    break;
                }
            }
        }

        return stringBuilder1.toString();
    }

    public static String getOnlineSexDeclensions(Context context, int sex){
        switch (sex){
            default:
            case 0:{
                return context.getString(R.string.last_seen_sex_unknown);
            }
            case 1:{
                return context.getString(R.string.last_seen_sex_female);
            }
            case 2:{
                return context.getString(R.string.last_seen_sex_male);
            }
        }
    }

    public static String getPhotosDeclensions(Context context, int count){

        if(count==0||count>1000){
            return context.getString(R.string.formatted_photo4, reduceTheNumber(count));
        }

        if(count<20 && count>10) {
            return context.getString(R.string.formatted_photo2, count);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.formatted_photo2, count);
            }
            case 1:{
                return context.getString(R.string.formatted_photo3, count);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.formatted_photo1, count);
            }
        }
    }

    public static String getPlaysDeclensions(Context context, int count){

        if(count==0||count>1000){
            return context.getString(R.string.formatted_plays4, reduceTheNumber(count));
        }

        if(count<20 && count>10){
            return context.getString(R.string.formatted_plays2, count);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.formatted_plays2, count);
            }
            case 1:{
                return context.getString(R.string.formatted_plays3, count);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.formatted_plays1, count);
            }
        }
    }

    public static String getAudiosDeclensions(Context context, int count){

        if(count==0||count>1000){
            return context.getString(R.string.formatted_audios4, reduceTheNumber(count));
        }

        if(count<20 && count>10){
            return context.getString(R.string.formatted_audios2, count);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.formatted_audios2, count);
            }
            case 1:{
                return context.getString(R.string.formatted_audios3, count);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.formatted_audios1, count);
            }
        }
    }

    public static String getMembersDeclensions(Context context, int count){

        if(count==0||count>1000){
            return context.getString(R.string.formatted_members4, reduceTheNumber(count));
        }

        if(count<20 && count>10){
            return context.getString(R.string.formatted_members2, count);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.formatted_members2, count);
            }
            case 1:{
                return context.getString(R.string.formatted_members3, count);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.formatted_members1, count);
            }
        }
    }

    public static String getMessageTypingDeclensions(Context context, Owner owner, boolean isText, int count){
        if(count==0){
            if(isText) {
                return context.getString(R.string.formatted_typing4, owner.getNameWithInitials());
            } else {
                return context.getString(R.string.formatted_typing5, owner.getNameWithInitials());
            }
        } else {
            switch (count%10){
                default:
                case 9:
                case 8:
                case 7:
                case 6:
                case 5:
                case 1:
                case 0:{
                    return context.getString(R.string.formatted_typing3,
                            owner.getNameWithInitials(),context.getString(R.string.formatted_typing2, count));
                }
                case 3:
                case 4:
                case 2:{
                    return context.getString(R.string.formatted_typing3,
                            owner.getNameWithInitials(),context.getString(R.string.formatted_typing1, count));
                }
            }
        }
    }

    public static String reduceTheNumber(int number){
        if(number>=1000000){
            return new DecimalFormat("#.#M").format(number/1000000f);
        }
        if(number>=1000){
            return new DecimalFormat("#.#K").format(number/1000f);
        }
        return number>0?String.valueOf(number):"Нет";
    }

}
