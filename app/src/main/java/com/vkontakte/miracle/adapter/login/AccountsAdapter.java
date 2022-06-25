package com.vkontakte.miracle.adapter.login;

import static com.vkontakte.miracle.engine.util.UserDataUtil.removeUserData;
import static com.vkontakte.miracle.network.Constants.fake_receipt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.login.AccountDialog;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.login.AuthState;
import com.vkontakte.miracle.login.LoginActivity;
import com.vkontakte.miracle.login.RegisterDevice;
import com.vkontakte.miracle.model.users.ProfileItem;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.UserViewHolder> {

    private final ArrayList<ProfileItem> accounts;
    private final LayoutInflater inflater;
    private final LoginActivity loginActivity;

    public AccountsAdapter(ArrayList<ProfileItem> accounts, LoginActivity loginActivity){
        this.accounts = accounts;
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
        if(accounts !=null) {
            return accounts.size();
        } else return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView imageView;
        private ProfileItem profileItem;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.photo);
            itemView.setOnLongClickListener(view -> {
                Context context = view.getContext();
                AccountDialog accountDialog = new AccountDialog(context,profileItem);
                accountDialog.setDialogActionListener(() -> {
                    removeUserData(profileItem);
                    int pos = accounts.indexOf(profileItem);
                    accounts.remove(pos);
                    notifyItemRemoved(pos);
                });
                accountDialog.show(context);
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
                                .fromAccount(profileItem.getAccessToken(),profileItem.getId(),receipt);
                        new RegisterDevice(authState, loginActivity).start();
                    });
                }
            });
        }

        public void bind(int position){
            profileItem = accounts.get(position);
            name.setText(profileItem.getFirstName());
            StorageUtil storageUtil = StorageUtil.get();
            imageView.setImageBitmap(storageUtil.loadBitmap("profileImage200.png",storageUtil.getUserCachesDir(profileItem)));
        }
    }

}
