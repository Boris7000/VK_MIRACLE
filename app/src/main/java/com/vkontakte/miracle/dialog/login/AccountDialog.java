package com.vkontakte.miracle.dialog.login;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.dialog.MiracleBottomDialog;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.view.MiracleButton;
import com.vkontakte.miracle.model.users.ProfileItem;

import static com.vkontakte.miracle.engine.util.UserDataUtil.removeUserData;

public class AccountDialog extends MiracleBottomDialog {

    private View rootView;
    private final ProfileItem profileItem;
    private AccountDialogActionListener dialogActionListener;

    public AccountDialog(@NonNull Context context, ProfileItem profileItem) {
        super(context);
        this.profileItem = profileItem;
    }

    @Override
    public void show(Context context) {
        setContentView(rootView =  View.inflate(context, R.layout.dialog_account, null));
        TextView name = rootView.findViewById(R.id.title);
        ImageView imageView = rootView.findViewById(R.id.photo);
        name.setText(profileItem.getFullName());
        StorageUtil storageUtil = StorageUtil.get();
        imageView.setImageBitmap(storageUtil.loadBitmap("profileImage200.png",storageUtil.getUserCachesDir(profileItem)));

        MiracleButton cancelButton = rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> cancel());

        MiracleButton deleteButton = rootView.findViewById(R.id.delete_button);
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
