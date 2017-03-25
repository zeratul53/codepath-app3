package com.david.simpletweets.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.david.simpletweets.R;
import com.david.simpletweets.TwitterApplication;
import com.david.simpletweets.databinding.FragmentComposeBinding;
import com.david.simpletweets.models.Tweet;
import com.david.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by David on 3/24/2017.
 */

public class ComposeTweetFragment extends DialogFragment {
    Button btnCancel;
    Button btnTweet;
    ImageView ivProfileImage;
    TextView tvUserName;
    EditText etBody;

    User user;
    FragmentComposeBinding binding;

    public ComposeTweetFragment() {
        //needs to be empty
    }

    public static ComposeTweetFragment newInstance(User user) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        frag.setArguments(args);
        return frag;
    }

    public interface ComposeTweetListener {
        void onTweet(Tweet tweet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose, container, true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnCancel = binding.btnCancel;
        btnTweet = binding.btnTweet;
        ivProfileImage = binding.ivProfileImage;
        tvUserName = binding.tvUserName;
        etBody = binding.etBody;

        user = getArguments().getParcelable("user");

        setupViews();
    }

    private void setupViews() {
        binding.setUser(user);
        binding.executePendingBindings();

        Picasso.with(getContext()).load(user.getProfileImageUrl())
                .into(ivProfileImage);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make network call to post a tweet
                String tweetBody = etBody.getText().toString();

                TwitterApplication.getRestClient().postStatusUpdate(tweetBody, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tweet newTweet = Tweet.fromJSON(response);
                        ComposeTweetListener listener = (ComposeTweetListener) getActivity();
                        listener.onTweet(newTweet);

                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", "Tweeting failed " + errorResponse.toString());
                    }
                });
            }
        });
    }
}