package com.vkontakte.miracle.dialog.login;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.dialog.MiracleBottomDialog;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;

public class UserDialog extends MiracleBottomDialog {

    private final User user;
    private AccountDialogActionListener dialogActionListener;

    public UserDialog(@NonNull Context context, User user) {
        super(context);
        this.user = user;
    }

    @Override
    public void show(Context context) {
        View rootView;
        setContentView(rootView =  View.inflate(context, R.layout.dialog_account, null));
        TextView name = rootView.findViewById(R.id.title);
        ImageView imageView = rootView.findViewById(R.id.photo);
        name.setText(user.getFullName());
        StorageUtil storageUtil = StorageUtil.get();
        imageView.setImageBitmap(storageUtil.loadBitmap("userImage200.png",storageUtil.getUserCachesDir(user)));

        Button cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> cancel());

        Button deleteButton = rootView.findViewById(R.id.deleteButton);
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
