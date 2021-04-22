package hu.blog.megosztanam.sub.menu;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.blog.megosztanam.MainMenuActivity;
import hu.blog.megosztanam.R;
import hu.blog.megosztanam.login.LoginActivity;
import hu.blog.megosztanam.messaging.MessagingService;
import hu.blog.megosztanam.model.parcelable.ParcelableLoginResponse;
import hu.blog.megosztanam.model.shared.messaging.MessageType;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.sub.menu.adapter.ApplicationAdapter;
import hu.blog.megosztanam.sub.menu.post.RecyclerItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Miklós on 2017. 04. 19..
 */
public class ApplicationsFragment extends Fragment {

    private ParcelableLoginResponse userDetails;
    private RecyclerView recyclerView;
    private ILFGService ilfgService;
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadApplications();
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        this.ilfgService = activity.getLfgService();
        loadApplications();
        IntentFilter filter = new IntentFilter(MessageType.NEW_APPLICATION);
        filter.addAction(MessageType.APPLICATION_CONFIRMED);
        filter.addAction(MessageType.APPLICATION_REVOKED);
        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                filter
        );
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.applications_sub_menu, container, false);
        userDetails = getArguments().getParcelable(LoginActivity.USER_DETAILS_EXTRA);
        recyclerView = rootView.findViewById(R.id.applications_list);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(llm);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public void loadApplications() {
        Call<List<PostApplyResponse>> applicationResponse = ilfgService.getApplications(userDetails.getUser().getUserId());
        applicationResponse.enqueue(new Callback<List<PostApplyResponse>>() {
            @Override
            public void onResponse(Call<List<PostApplyResponse>> call, Response<List<PostApplyResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ApplicationAdapter adapter = new ApplicationAdapter(response.body(), getActivity().getBaseContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, final int position) {
                            final ApplicationAdapter adapter = (ApplicationAdapter) recyclerView.getAdapter();
                            PostApplyResponse application = adapter.getApplication(position);
                            final int postId = application.getPost().getPostId();
                            final int applicantUserId = application.getUserId();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            builder.setMessage(R.string.appcept_or_reject_application)
                                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            ilfgService.acceptApplication(postId, applicantUserId).enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    Toast toast = Toast.makeText(getActivity(), R.string.accpted_application, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    adapter.remove(position);
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast toast = Toast.makeText(getActivity(), R.string.failed_to_accep_application, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            ilfgService.rejectApplication(postId, applicantUserId).enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    Toast toast = Toast.makeText(getActivity(), R.string.rejected_application, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    adapter.remove(position);
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast toast = Toast.makeText(getActivity(), R.string.failed_to_reject_application, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            });
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    }));

                } else {
                    Log.i(this.getClass().getName(), "Is successful " + response.isSuccessful());
                }
                Log.i(this.getClass().getName(), "Response body " + response.body().size());
            }

            @Override
            public void onFailure(Call<List<PostApplyResponse>> call, Throwable t) {
                Log.i(this.getClass().getName(), "Failure: " + t.toString());
            }
        });
    }
}
