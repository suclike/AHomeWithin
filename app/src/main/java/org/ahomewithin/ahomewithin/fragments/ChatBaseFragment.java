package org.ahomewithin.ahomewithin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import org.ahomewithin.ahomewithin.ParseClient;
import org.ahomewithin.ahomewithin.ParseClientAsyncHandler;
import org.ahomewithin.ahomewithin.R;
import org.ahomewithin.ahomewithin.adapters.ChatUsersRecyclerViewAdapter;
import org.ahomewithin.ahomewithin.parseModel.ParseObjectUser;
import org.ahomewithin.ahomewithin.util.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by xiangyang_xiao on 3/20/16.
 */
public abstract class ChatBaseFragment extends Fragment {

    @Bind(R.id.rvUsers)
    RecyclerView rvUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chatBaseView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, chatBaseView);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.chatRoom);
        return chatBaseView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final ParseClient client = ParseClient.newInstance(getContext());
        ParseClientAsyncHandler handler = new ParseClientAsyncHandler() {
            @Override
            public void onSuccess(Object obj) {
                final List<ParseObjectUser> users = (List<ParseObjectUser>)obj;

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                rvUsers.setLayoutManager(layoutManager);
                rvUsers.setHasFixedSize(true);
                ChatUsersRecyclerViewAdapter rcAdapter =
                    new ChatUsersRecyclerViewAdapter(
                        users,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                String otherEmail = users.get(position).getEmail();
                                ChatFragment chatFragment = ChatFragment.newIntance(otherEmail);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.flContent, chatFragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        },
                        getFragmentCode()
                    );
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(rcAdapter);
                alphaAdapter.setDuration(1000);
                alphaAdapter.setInterpolator(new OvershootInterpolator());
                alphaAdapter.setFirstOnly(false);
                rvUsers.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

                ParseObjectUser currentUser = client.getCurParseObjectUser();
                if (currentUser != null) {
                    int curUsrIdx = -1;
                    for (int idx = 0; idx < users.size(); idx++) {
                        if (client.getCurParseObjectUser().getEmail().equals(
                            users.get(idx).getEmail())) {
                            curUsrIdx = idx;
                        }
                    }
                    if (curUsrIdx != -1) {
                        users.remove(curUsrIdx);
                        rcAdapter.notifyItemRemoved(curUsrIdx);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(
                    getContext(),
                    String.format(
                        "Failed to fetch users due to ",
                        error
                    ),
                    Toast.LENGTH_SHORT
                ).show();
            }
        };
        populateUsers(handler);
    }

    public abstract void populateUsers(ParseClientAsyncHandler handler);

    public abstract int getFragmentCode();

    //The subclass can not use the same framgent, otherwise one of them
    //will not be able to bind
    public abstract int getLayoutId();
}