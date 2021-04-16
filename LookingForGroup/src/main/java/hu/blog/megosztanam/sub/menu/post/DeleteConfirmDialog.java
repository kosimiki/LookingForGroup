package hu.blog.megosztanam.sub.menu.post;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import hu.blog.megosztanam.R;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;

/**
 * Created by Miklós on 2017. 06. 04..
 */
public class DeleteConfirmDialog {
    private NoticeBoardFragment noticeBoardFragment;

    public DeleteConfirmDialog(NoticeBoardFragment fragment){
        this.noticeBoardFragment = fragment;
    }

    public Dialog createDialog(Activity activity, final Integer userId, final Post post, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Log.i("ApplicationConfirm", "AlertDialog.Builder");

        builder.setMessage(R.string.delete_post)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        noticeBoardFragment.deletePost(userId, post, position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }


}
