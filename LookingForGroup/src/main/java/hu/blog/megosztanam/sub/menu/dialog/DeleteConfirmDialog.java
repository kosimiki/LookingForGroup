package hu.blog.megosztanam.sub.menu.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import hu.blog.megosztanam.R;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;

/**
 * Created by Miklós on 2017. 06. 04..
 */
public class DeleteConfirmDialog {
    private final NoticeBoardFragment noticeBoardFragment;

    public DeleteConfirmDialog(NoticeBoardFragment fragment) {
        this.noticeBoardFragment = fragment;
    }

    public Dialog createDialog(Activity activity, final Integer userId, final Post post, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(R.string.delete_post_question)
                .setPositiveButton(R.string.delete, (dialog, id) -> noticeBoardFragment.deletePost(userId, post, position))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                });
        return builder.create();
    }


}
