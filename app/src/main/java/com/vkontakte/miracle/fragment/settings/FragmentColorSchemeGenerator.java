package com.vkontakte.miracle.fragment.settings;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideListFragment;

public class FragmentColorSchemeGenerator extends SideListFragment {

    private View rootView;
    private float maxS = 100;
    private float minS = 30;
    private float h = 0;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout linearLayout = rootView.findViewById(R.id.layout);

        EditText hEditText = linearLayout.findViewById(R.id.hEditText);
        SeekBar hSeekBar = linearLayout.findViewById(R.id.hSeekBar);
        EditText maxSEditText = linearLayout.findViewById(R.id.maxSEditText);
        SeekBar maxSSeekBar = linearLayout.findViewById(R.id.maxSSeekBar);
        EditText minSEditText = linearLayout.findViewById(R.id.minSEditText);
        SeekBar minSSeekBar = linearLayout.findViewById(R.id.minSSeekBar);

        SeekBar.OnSeekBarChangeListener hListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    hEditText.setText(String.valueOf(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        SeekBar.OnSeekBarChangeListener maxSListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    maxSEditText.setText(String.valueOf(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        SeekBar.OnSeekBarChangeListener minSListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    minSEditText.setText(String.valueOf(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        hSeekBar.setOnSeekBarChangeListener(hListener);
        maxSSeekBar.setOnSeekBarChangeListener(maxSListener);
        minSSeekBar.setOnSeekBarChangeListener(minSListener);

        TextWatcher hTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0&&editable.length()<=3){
                    try {
                        h = Float.parseFloat(editable.toString());
                        generateColors();
                    }catch (Exception ignored){ }
                }
            }
        };
        TextWatcher maxSTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0&&editable.length()<=3){
                    try {
                        maxS = Float.parseFloat(editable.toString());
                        generateColors();
                    }catch (Exception ignored){ }
                }
            }
        };
        TextWatcher minSTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0&&editable.length()<=3){
                    try {
                        minS = Float.parseFloat(editable.toString());
                        generateColors();
                    }catch (Exception ignored){ }
                }
            }
        };
        hEditText.addTextChangedListener(hTextWatcher);
        maxSEditText.addTextChangedListener(maxSTextWatcher);
        minSEditText.addTextChangedListener(minSTextWatcher);

        hEditText.setText("0");

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings_color_scheme_generator, container, false);
    }



    public void generateColors(){

        float correctionL = getCorrectionForL();

        Log.d("eokfokefo", String.valueOf(correctionL));

        correctionL*=(minS/100);

        float minL = Math.max(10,10f+correctionL*0.085f) ;
        float maxL = Math.min(98,98f+correctionL*0.085f);

        int gradCount = 5;
        int colorsCount = (gradCount*2)-1;

        float dL = 100f;
        float wL = dL-(minL+(dL-maxL));
        float hL = wL/(float) colorsCount;

        float dS = 100f;
        float wS = dS-(minS+(dS-maxS));
        float hS = wS/(float) colorsCount;


        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout linearLayout = rootView.findViewById(R.id.container);
        linearLayout.removeAllViews();

        for(int i = 0; i < colorsCount; i++){
            float lValue = minL+((float)i)*hL;
            float sValue = minS+((float)i)*hS;
            int color = ColorUtils.HSLToColor(new float[]{h,sValue*0.01f,lValue*0.01f});
            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(color);
            TextView textView =  (TextView) inflater.inflate(R.layout.text_view, linearLayout, false);
            textView.setTextColor(Color.BLACK);
            textView.setWidth(linearLayout.getWidth());
            textView.setHeight(75);
            textView.setTextSize(17);
            textView.setText("<color name=\"_"+(colorsCount-i)+"00\">#"+Integer.toHexString(color).substring(2)+"</color>");
            textView.setBackground(colorDrawable);
            linearLayout.addView(textView);
        }

        float minS = Math.min(this.minS,10f);
        float maxS = Math.min(this.maxS,30f);

        wS = dS-(minS+(dS-maxS));
        hS = wS/(float) colorsCount;

        for(int i = 0; i < colorsCount; i++){
            float lValue = minL+((float)i)*hL;
            float sValue = minS+((float)i)*hS;
            int color = ColorUtils.HSLToColor(new float[]{h,sValue*0.01f,lValue*0.01f});
            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(color);
            TextView textView =  (TextView) inflater.inflate(R.layout.text_view, linearLayout, false);
            textView.setTextColor(Color.BLACK);
            textView.setWidth(linearLayout.getWidth());
            textView.setHeight(75);
            textView.setTextSize(17);
            textView.setText("<color name=\"_neutral_"+(colorsCount-i)+"00\">#"+Integer.toHexString(color).substring(2)+"</color>");
            textView.setBackground(colorDrawable);
            linearLayout.addView(textView);
        }
    }

    private float getCorrectionForL(){
        if(h>=0&&h<=10){
            //0 = 75%
            //10 = 100%
            return 25f*getPercent(0, 10, h);
        } else {
            if(h>=10&&h<=60){
                //10 = 100%
                //60 = 150%
                return -50f*(1f-getPercent(10, 60, h));
            } else {
                if(h>=60&&h<=114){
                    //60 = 150%
                    //114 = 100%
                    return -50f*getPercent(60, 114, h);
                } else {
                    if(h>=114&&h<=120){
                        //114 = 100%
                        //120 = 91%
                        return 9f*(1f-getPercent(114, 120, h));
                    } else {
                        if(h>=120&&h<=136){
                            //120 = 91%
                            //136 = 100%
                            return 9f*getPercent(120, 136, h);
                        } else {
                            if(h>=136&&h<=180){
                                //136 = 100%
                                //180 = 123%
                                return -23f*(1f-getPercent(136, 180, h));
                            } else {
                                if(h>=180&&h<=189){
                                    //180 = 123%
                                    //189 = 100%
                                    return -23f*getPercent(180, 189, h);
                                } else {
                                    if (h >= 189 && h <= 240) {
                                        //189 = 100%
                                        //240 = 5%
                                        return 95f*(1f-getPercent(189, 240, h));
                                    } else {
                                        if (h >= 240 && h <= 300) {
                                            //240 = 5%
                                            //300 = 45%
                                            float p = getPercent(240, 300, h);
                                            return (95f * p) + (55f * (1f-p));
                                        } else {
                                            if (h >= 300 && h <= 360) {
                                                //300 = 45%
                                                //360 = 75%
                                                float p = getPercent(300, 360, h);
                                                return (55f * p) + (25f * (1f-p));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    private float getPercent(float a, float b, float h){
        return Math.abs(b-h)/(b-a);
    }

}
