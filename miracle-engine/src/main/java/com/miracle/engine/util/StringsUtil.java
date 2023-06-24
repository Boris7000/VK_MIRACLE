package com.miracle.engine.util;

import android.widget.EditText;

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
