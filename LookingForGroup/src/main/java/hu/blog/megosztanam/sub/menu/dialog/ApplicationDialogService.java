package hu.blog.megosztanam.sub.menu.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.blog.megosztanam.R;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Role;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;
import hu.blog.megosztanam.sub.menu.post.PostActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicationDialogService {
    private final ILFGService lfgService;

    private List<String> selectedRoles;
    private List<String> selectableRoles;
    private Activity activity;
    private NoticeBoardFragment noticeBoardFragment;

    public ApplicationDialogService(NoticeBoardFragment noticeBoardFragment, ILFGService lfgService) {
        this.noticeBoardFragment = noticeBoardFragment;
        this.lfgService = lfgService;

    }

    public Dialog createApplicationDialog(ArrayList<Role> roles, final int userId, final int postId, Activity activity) {
        selectedRoles = new ArrayList<>();
        selectableRoles = new ArrayList<>();
        for (Role role : roles) {
            if (!selectableRoles.contains(role.getValue())) {
                selectableRoles.add(role.getValue());
            }
        }
        this.activity = activity;
        Log.i("ApplicationConfirm", "createDialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Log.i("ApplicationConfirm", "AlertDialog.Builder");

        builder.setTitle(R.string.pick_your_role)
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
                        for (String role : selectedRoles) {
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

    public Dialog managementDialog(Activity activity, final Integer userId, final Post post, final boolean accepted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(R.string.confirm_application_question)
                .setPositiveButton(R.string.revoke_application, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        noticeBoardFragment.revokeApplication(userId, post);
                    }
                });
        if(accepted) {
            builder.setNegativeButton(R.string.confirm_application, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    noticeBoardFragment.confirmApplication(userId, post);
                }
            });
        }
        return builder.create();
    }

    private void applyForPost(PostApplyRequest request) {
        Call<Void> response = lfgService.applyForPost(request);
        response.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(PostActivity.class.getName(), "Response: " + response.isSuccessful());
                noticeBoardFragment.loadPosts("applied for post");
                Toast toast = Toast.makeText(activity, R.string.applied_for_post, Toast.LENGTH_SHORT);
                toast.show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(PostActivity.class.getName(), "Failure: " + t.toString());
            }
        });
    }
}