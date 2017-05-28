package hu.blog.megosztanam.sub.menu.post;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import com.example.lookingforgroup.R;
import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.rest.LFGServicesImpl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ApplicationConfirmDialog {
    private static final String ROLES = "hu.blog.megosztanam.sub.menu.post.ApplicationConfirmDialog.ROLES";
    private static final String USER_ID = "hu.blog.megosztanam.sub.menu.post.ApplicationConfirmDialog.USER_ID";
    private static final String POST_ID = "hu.blog.megosztanam.sub.menu.post.ApplicationConfirmDialog.POST_ID";

    private List<String> selectedRoles;
    private List<String> selectableRoles;


    public Dialog createDialog(ArrayList<Role> roles,final int userId,final int postId, Activity activity) {
        selectedRoles = new ArrayList<>();
        selectableRoles = new ArrayList<>();
        for (Role role: roles){
            if(!selectableRoles.contains(role.getValue())){
                selectableRoles.add(role.getValue());
            }
        }
        Log.i("ApplicationConfirm", "createDialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Log.i("ApplicationConfirm", "AlertDialog.Builder");

        builder.setTitle(R.string.pick_your_role)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(selectableRoles.toArray(new CharSequence[selectableRoles.size()]), null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    selectedRoles.add(selectableRoles.get(which));
                                } else {
                                    selectedRoles.remove(selectableRoles.get(which));
                                }
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        PostApplyRequest request = new PostApplyRequest();
                        List<Role> roles = new ArrayList<>();
                        for (String role: selectedRoles){
                            roles.add(Role.valueOf(role));
                        }
                        request.setRoles(roles);
                        request.setPostId(postId);
                        request.setUserId(userId);
                        applyForPost(request);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private void applyForPost(PostApplyRequest request) {
        LFGServicesImpl lfgServices = new LFGServicesImpl();
        Call<Boolean> response = lfgServices.applyForPost(request);
        response.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.i(PostActivity.class.getName(), "Response: " + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i(PostActivity.class.getName(), "Failure: " + t.toString());
            }
        });
    }
}