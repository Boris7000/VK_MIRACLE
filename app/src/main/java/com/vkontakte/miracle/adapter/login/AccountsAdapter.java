package com.vkontakte.miracle.adapter.login;

import static com.vkontakte.miracle.engine.util.UserDataUtil.removeUserData;
import static com.vkontakte.miracle.network.Constants.fake_receipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.login.UserDialog;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.login.AuthState;
import com.vkontakte.miracle.login.LoginActivity;
import com.vkontakte.miracle.login.RegisterDevice;
import com.vkontakte.miracle.model.users.User;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.UserViewHolder> {

    private final ArrayList<User> users;
    private final LayoutInflater inflater;
    private final LoginActivity loginActivity;

    public AccountsAdapter(ArrayList<User> users, LoginActivity loginActivity){
        this.users = users;
        this.loginActivity = loginActivity;
        inflater = LayoutInflater.from(loginActivity);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(inflater.inflate(R.layout.view_account_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(users !=null) {
            return users.size();
        } else return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView imageView;
        private User user;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.photo);
            itemView.setOnLongClickListener(view -> {
                Context context = view.getContext();
                UserDialog userDialog = new UserDialog(context, user);
                userDialog.setDialogActionListener(() -> {
                    removeUserData(user);
                    int pos = users.indexOf(user);
                    users.remove(pos);
                    notifyItemRemoved(pos);
                });
                userDialog.show(context);
                return true;
            });

            itemView.setOnClickListener(view -> {
                if(loginActivity.canLogin()) {
                    loginActivity.setCanLogin(false);

                    loginActivity.getMiracleApp().getFCMToken(task -> {
                        String receipt;
                        if (task.isSuccessful()) {
                            receipt = task.getResult();
                        } else {
                            receipt = fake_receipt;
                        }

                        AuthState authState = AuthState
                                .fromAccount(user.getAccessToken(), user.getId(),receipt);
                        new RegisterDevice(authState, loginActivity).start();
                    });
                }
            });
        }

        public void bind(int position){
            user = users.get(position);
            name.setText(user.getFirstName());
            StorageUtil storageUtil = StorageUtil.get();
            imageView.setImageBitmap(storageUtil.loadBitmap("userImage200.png",storageUtil.getUserCachesDir(user)));
        }
    }

}
