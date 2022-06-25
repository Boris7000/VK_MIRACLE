package com.vkontakte.miracle.dialog.login;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.button.TextViewButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.dialog.MiracleBottomDialog;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;

public class AccountDialog extends MiracleBottomDialog {

    private final ProfileItem profileItem;
    private AccountDialogActionListener dialogActionListener;

    public AccountDialog(@NonNull Context context, ProfileItem profileItem) {
        super(context);
        this.profileItem = profileItem;
    }

    @Override
    public void show(Context context) {
        View rootView;
        setContentView(rootView =  View.inflate(context, R.layout.dialog_account, null));
        TextView name = rootView.findViewById(R.id.title);
        ImageView imageView = rootView.findViewById(R.id.photo);
        name.setText(profileItem.getFullName());
        StorageUtil storageUtil = StorageUtil.get();
        imageView.setImageBitmap(storageUtil.loadBitmap("profileImage200.png",storageUtil.getUserCachesDir(profileItem)));

        TextViewButton cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> cancel());

        TextViewButton deleteButton = rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view -> {
            if(dialogActionListener!=null){
                dialogActionListener.remove();
            }
            cancel();
        });

        show();
        expand();
    }

    public void setDialogActionListener(AccountDialogActionListener dialogActionListener) {
        this.dialogActionListener = dialogActionListener;
    }
}
