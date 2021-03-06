package org.ahomewithin.ahomewithin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.ahomewithin.ahomewithin.ParseClient;
import org.ahomewithin.ahomewithin.R;
import org.ahomewithin.ahomewithin.activities.VideoActivity;
import org.ahomewithin.ahomewithin.models.Item;
import org.ahomewithin.ahomewithin.util.BuyDialog;
import org.ahomewithin.ahomewithin.util.UserTools;
import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by chezlui on 15/03/16.
 */
public class DetailFragment extends Fragment {
    public static final String FRAGMENT_TAG = DetailFragment.class.getSimpleName();
    public static final String LOG_TAG = DetailFragment.class.getSimpleName();

    Item mItem;
    @Bind(R.id.btBuy) Button btBuy;
    @Bind(R.id.tvTitle) TextView tvTitle;
    @Bind(R.id.tvDescription) TextView tvDescription;

    @Bind(R.id.ivItemImage) ImageView ivItemImage;
    @Bind(R.id.tvPrice) TextView tvPrice;
    public static final int REQUEST_CODE = 22;


    public static DetailFragment newInstance(Item mItem) {

        Bundle args = new Bundle();
        args.putSerializable(mItem.SERIALIZABLE_TAG, mItem);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_detail, container, false);
        convertView.setFocusableInTouchMode(true);
        convertView.requestFocus();
        convertView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(DetailFragment.this).commit();
                    return false;
                }
                return false;
            }
        });

        ButterKnife.bind(this, convertView);
        mItem = (Item) getArguments().getSerializable(Item.SERIALIZABLE_TAG);


        return convertView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mItem.title);

        //Glide.with(getActivity()).load(mItem.imageUrl).into(ivItemImage);

        final int resourceId = getActivity().getResources().getIdentifier(
                mItem.imageUrl, "drawable", getActivity().getPackageName());
//        ((ItemViewHolder)holder).ivItemImage.setImageDrawable(mContext.getResources().getDrawable(resourceId));


        Glide.with(getActivity()).load(resourceId)
                .centerCrop()
                .into(ivItemImage);


        tvTitle.setText(mItem.title);
        tvDescription.setText(mItem.description);
        tvPrice.setText("Price: $" + mItem.price);

        // Calculate again to force update
        mItem.owned = UserTools.isItemPurchased(getActivity(), mItem.id);
        if (mItem.owned) {
            btBuy.setText("WATCH NOW");
            tvPrice.setVisibility(View.INVISIBLE);
        } else {
            btBuy.setText("BUY NOW");
        }

        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem.owned) {
                    onWatch();
                } else {
                    onBuy();
                }
            }
        });
    }

    public void onBuy() {
        FragmentManager fm = getFragmentManager();

        if (!ParseClient.newInstance(getActivity()).isUserLoggedIn()) {
            Log.d("DEBUG", "User is not logged in so go to Login");
            gotoLogin();
            return;
        }

        BuyDialog buyDialog = BuyDialog.newInstance(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // TODO Save bought item
                UserTools.purchaseItem(getActivity(), mItem.id);
                mItem.owned = true;
                btBuy.setText("WATCH NOW");
                final Snackbar snack = Snackbar.make(getView(), R.string.snackbar_purchased_text, Snackbar.LENGTH_INDEFINITE);
                snack.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snack.dismiss();
                    }
                });
                //snack.setActionTextColor(getResources().getColor(R.color.accent));
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
                snack.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(LOG_TAG, "Error on buying item, code " + statusCode);
            }
        }, mItem);
        buyDialog.show(fm, "");
    }


    public void onWatch() {

        if (mItem.type == Item.ITEM_TYPE.CONVERSATIONS) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContent, CardsPagerFragment.newInstance(mItem))
                    .addToBackStack(null)
                    .commit();
        } else if (mItem.type == Item.ITEM_TYPE.VIDEOS) {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            intent.putExtra(Item.SERIALIZABLE_TAG, mItem);
            startActivity(intent);
        }
//        ((AppCompatActivity) getActivity())
//                .getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.flContent, VideoActivity.newInstance(mItem))
//                .addToBackStack(null)
//                .commit();


    }

    public void gotoLogin() {
        LoginFragment loginFragment = LoginFragment.newInstance(REQUEST_CODE, Parcels.wrap(mItem));
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flContent, loginFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
