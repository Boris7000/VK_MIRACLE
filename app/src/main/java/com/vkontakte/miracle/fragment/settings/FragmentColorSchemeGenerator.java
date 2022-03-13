package com.vkontakte.miracle.fragment.settings;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

public class FragmentColorSchemeGenerator  extends SimpleMiracleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();


        View rootView = inflater.inflate(R.layout.fragment_settings_color_scheme_generator, container, false);
        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));

        LinearLayout linearLayout = rootView.findViewById(R.id.layout);


        float[] hsv = new float[]{0,0,0};
        generateColors(hsv,linearLayout);

        EditText editText = (EditText) linearLayout.getChildAt(0);
        SeekBar hSeekBar = linearLayout.findViewById(R.id.HseekBar);
        SeekBar sSeekBar = linearLayout.findViewById(R.id.SseekBar);
        SeekBar vSeekBar = linearLayout.findViewById(R.id.VseekBar);

        SeekBar.OnSeekBarChangeListener hListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    hsv[0] = i;
                    editText.setText(Integer.toHexString(Color.HSVToColor(hsv)).substring(2));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        SeekBar.OnSeekBarChangeListener sListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    hsv[1] = i/100f;
                    editText.setText(Integer.toHexString(Color.HSVToColor(hsv)).substring(2));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        SeekBar.OnSeekBarChangeListener vListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    hsv[2] = i/100f;
                    editText.setText(Integer.toHexString(Color.HSVToColor(hsv)).substring(2));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==6){
                    try {
                        int color = Integer.parseInt(editText.getText().toString(), 16);
                        Color.colorToHSV(color,hsv);
                        hSeekBar.setProgress((int) hsv[0]);
                        sSeekBar.setProgress((int) (hsv[1]*100));
                        vSeekBar.setProgress((int) (hsv[2]*100));
                        generateColors(hsv,linearLayout);
                    }catch (Exception e){
                    }
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
        hSeekBar.setOnSeekBarChangeListener(hListener);
        sSeekBar.setOnSeekBarChangeListener(sListener);
        vSeekBar.setOnSeekBarChangeListener(vListener);

        return rootView;
    }



    public void generateColors(float[] hsv, LinearLayout linearLayout){

        float[][] sv = generatePrimary(hsv);

        float[] s = sv[0];
        float[] v = sv[1];

        TextView textView5 = linearLayout.findViewById(R.id.t5);
        float[] hsv5 = new float[]{hsv[0],s[4],v[4]};
        int color5 = Color.HSVToColor(hsv5);

        textView5.setText("<color name=\"_500\">#"+Integer.toHexString(color5).substring(2)+"</color>");
        textView5.getBackground().setColorFilter(new PorterDuffColorFilter(color5, PorterDuff.Mode.SRC_IN));

        TextView textview6 = linearLayout.findViewById(R.id.t6);
        float[] hsv6 = new float[]{hsv[0],s[5],v[5]};
        int color6 = Color.HSVToColor(hsv6);

        textview6.setText("<color name=\"_600\">#"+Integer.toHexString(color6).substring(2)+"</color>");
        textview6.getBackground().setColorFilter(new PorterDuffColorFilter(color6, PorterDuff.Mode.SRC_IN));

        TextView textview7 = linearLayout.findViewById(R.id.t7);
        float[] hsv7 = new float[]{hsv[0],s[6],v[6]};
        int color7 = Color.HSVToColor(hsv7);
        textview7.setText("<color name=\"_700\">#"+Integer.toHexString(color7).substring(2)+"</color>");
        textview7.getBackground().setColorFilter(new PorterDuffColorFilter(color7, PorterDuff.Mode.SRC_IN));

        TextView textview8 = linearLayout.findViewById(R.id.t8);
        float[] hsl8 = new float[]{hsv[0],s[7],v[7]};
        int color8 = Color.HSVToColor(hsl8);
        textview8.setText("<color name=\"_800\">#"+Integer.toHexString(color8).substring(2)+"</color>");
        textview8.getBackground().setColorFilter(new PorterDuffColorFilter(color8, PorterDuff.Mode.SRC_IN));


        TextView textview9 = linearLayout.findViewById(R.id.t9);
        float[] hsl9 = new float[]{hsv[0],s[8],v[8]};
        int color9 = Color.HSVToColor(hsl9);
        textview9.setText("<color name=\"_900\">#"+Integer.toHexString(color9).substring(2)+"</color>");
        textview9.getBackground().setColorFilter(new PorterDuffColorFilter(color9, PorterDuff.Mode.SRC_IN));


        TextView textView4 = linearLayout.findViewById(R.id.t4);
        float[] hsl4 = new float[]{hsv[0],s[3],v[3]};
        int color4 = Color.HSVToColor(hsl4);
        textView4.setText("<color name=\"_400\">#"+Integer.toHexString(color4).substring(2)+"</color>");
        textView4.getBackground().setColorFilter(new PorterDuffColorFilter(color4, PorterDuff.Mode.SRC_IN));

        TextView textView3 = linearLayout.findViewById(R.id.t3);
        float[] hsl3 = new float[]{hsv[0],s[2],v[2]};
        int color3 = Color.HSVToColor(hsl3);
        textView3.setText("<color name=\"_300\">#"+Integer.toHexString(color3).substring(2)+"</color>");
        textView3.getBackground().setColorFilter(new PorterDuffColorFilter(color3, PorterDuff.Mode.SRC_IN));

        TextView textView2 = linearLayout.findViewById(R.id.t2);
        float[] hsl2 = new float[]{hsv[0],s[1],v[1]};
        int color2 = Color.HSVToColor(hsl2);
        textView2.setText("<color name=\"_200\">#"+Integer.toHexString(color2).substring(2)+"</color>");
        textView2.getBackground().setColorFilter(new PorterDuffColorFilter(color2, PorterDuff.Mode.SRC_IN));


        TextView textview1 = linearLayout.findViewById(R.id.t1);
        float[] hsl1 = new float[]{hsv[0],s[0],v[0]};
        int color1 = Color.HSVToColor(hsl1);
        textview1.setText("<color name=\"_100\">#"+Integer.toHexString(color1).substring(2)+"</color>");
        textview1.getBackground().setColorFilter(new PorterDuffColorFilter(color1, PorterDuff.Mode.SRC_IN));

        sv = generateNeutral();

        s = sv[0];
        v = sv[1];

        textView5 = linearLayout.findViewById(R.id.t25);
        hsv5 = new float[]{hsv[0],s[4],v[4]};
        color5 = Color.HSVToColor(hsv5);
        textView5.setText("<color name=\"_neutral_500\">#"+Integer.toHexString(color5).substring(2)+"</color>");
        textView5.getBackground().setColorFilter(new PorterDuffColorFilter(color5, PorterDuff.Mode.SRC_IN));


        textview6 = linearLayout.findViewById(R.id.t26);
        hsv6 = new float[]{hsv[0],s[5],v[5]};
        color6 = Color.HSVToColor(hsv6);
        textview6.setText("<color name=\"_neutral_600\">#"+Integer.toHexString(color6).substring(2)+"</color>");
        textview6.getBackground().setColorFilter(new PorterDuffColorFilter(color6, PorterDuff.Mode.SRC_IN));

        textview7 = linearLayout.findViewById(R.id.t27);
        hsv7 = new float[]{hsv[0],s[6],v[6]};
        color7 = Color.HSVToColor(hsv7);
        textview7.setText("<color name=\"_neutral_700\">#"+Integer.toHexString(color7).substring(2)+"</color>");
        textview7.getBackground().setColorFilter(new PorterDuffColorFilter(color7, PorterDuff.Mode.SRC_IN));

        textview8 = linearLayout.findViewById(R.id.t28);
        hsl8 = new float[]{hsv[0],s[7],v[7]};
        color8 = Color.HSVToColor(hsl8);
        textview8.setText("<color name=\"_neutral_800\">#"+Integer.toHexString(color8).substring(2)+"</color>");
        textview8.getBackground().setColorFilter(new PorterDuffColorFilter(color8, PorterDuff.Mode.SRC_IN));

        textview9 = linearLayout.findViewById(R.id.t29);
        hsl9 = new float[]{hsv[0],s[8],v[8]};
        color9 = Color.HSVToColor(hsl9);
        textview9.setText("<color name=\"_neutral_900\">#"+Integer.toHexString(color9).substring(2)+"</color>");
        textview9.getBackground().setColorFilter(new PorterDuffColorFilter(color9, PorterDuff.Mode.SRC_IN));

        textView4 = linearLayout.findViewById(R.id.t24);
        hsl4 = new float[]{hsv[0],s[3],v[3]};
        color4 = Color.HSVToColor(hsl4);
        textView4.setText("<color name=\"_neutral_400\">#"+Integer.toHexString(color4).substring(2)+"</color>");
        textView4.getBackground().setColorFilter(new PorterDuffColorFilter(color4, PorterDuff.Mode.SRC_IN));

        textView3 = linearLayout.findViewById(R.id.t23);
        hsl3 = new float[]{hsv[0],s[2],v[2]};
        color3 = Color.HSVToColor(hsl3);
        textView3.setText("<color name=\"_neutral_300\">#"+Integer.toHexString(color3).substring(2)+"</color>");
        textView3.getBackground().setColorFilter(new PorterDuffColorFilter(color3, PorterDuff.Mode.SRC_IN));

        textView2 = linearLayout.findViewById(R.id.t22);
        hsl2 = new float[]{hsv[0],s[1],v[1]};
        color2 = Color.HSVToColor(hsl2);
        textView2.setText("<color name=\"_neutral_200\">#"+Integer.toHexString(color2).substring(2)+"</color>");
        textView2.getBackground().setColorFilter(new PorterDuffColorFilter(color2, PorterDuff.Mode.SRC_IN));

        textview1 = linearLayout.findViewById(R.id.t21);
        hsl1 = new float[]{hsv[0],s[0],v[0]};
        color1 = Color.HSVToColor(hsl1);
        textview1.setText("<color name=\"_neutral_100\">#"+Integer.toHexString(color1).substring(2)+"</color>");
        textview1.getBackground().setColorFilter(new PorterDuffColorFilter(color1, PorterDuff.Mode.SRC_IN));
    }

    public float[][] generatePrimary(float[] hsv){
        float[][] sv = new float[2][9];

        float[] s = new float[9];
        float[] v = new float[9];

        s[4] = hsv[1];
        v[4] = hsv[2];

        s[3] = s[4]-0.11f;
        v[3] = v[4]+0.1f;

        s[5] = s[4]+0.18f;
        v[5] = v[4]-0.1f;

        s[2] = s[3]-0.11f;
        v[2] = v[3]+0.1f;

        s[6] = s[5]+0.18f;
        v[6] = v[5]-0.1f;

        s[1] = s[2]-0.11f;
        v[1] = v[2]+0.1f;

        s[7] = s[6]+0.18f;
        v[7] = v[6]-0.12f;

        s[0] = s[1]-0.11f;
        v[0] = v[1]+0.1f;

        s[8] = s[7]+0.18f;
        v[8] = v[7]-0.15f;

        sv[0] = s;
        sv[1] = v;

        return sv;
    }

    public float[][] generateNeutral(){
        float[][] sv = new float[2][9];

        float[] s = new float[9];
        float[] v = new float[9];

        s[4] = 0.22f;
        v[4] = 0.54f;

        s[3] = 0.18f;
        v[3] = 0.64f;

        s[5] = 0.25f;
        v[5] = 0.44f;

        s[2] = 0.16f;
        v[2] = 0.75f;

        s[6] = 0.32f;
        v[6] = 0.35f;

        s[1] = 0.14f;
        v[1] = 0.86f;

        s[7] = 0.42f;
        v[7] = 0.25f;

        s[0] = 0.13f;
        v[0] = 0.97f;

        s[8] = 0.60f;
        v[8] = 0.17f;

        sv[0] = s;
        sv[1] = v;


        return sv;
    }
}
