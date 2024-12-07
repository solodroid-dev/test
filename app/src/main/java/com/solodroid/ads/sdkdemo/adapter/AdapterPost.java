package com.solodroid.ads.sdkdemo.adapter;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_DISCOVERY;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.FACEBOOK;
import static com.solodroid.ads.sdk.util.Constant.FAN;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.HUAWEI;
import static com.solodroid.ads.sdk.util.Constant.PANGLE;
import static com.solodroid.ads.sdk.util.Constant.STARTAPP;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.solodroid.ads.sdk.format.NativeAdViewHolder;
import com.solodroid.ads.sdkdemo.R;
import com.solodroid.ads.sdkdemo.data.Constant;
import com.solodroid.ads.sdkdemo.database.SharedPref;
import com.solodroid.ads.sdkdemo.model.Post;

import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class AdapterPost extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Post> posts;
    private OnItemClickListener onItemClickListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_AD = 2;
    SharedPref sharedPref;

    public interface OnItemClickListener {
        void onItemClick(View view, Post obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.onItemClickListener = mItemClickListener;
    }

    public AdapterPost(Context context, List<Post> posts) {
        this.posts = posts;
        this.context = context;
        this.sharedPref = new SharedPref(context);
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public LinearLayout lytParent;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            image = v.findViewById(R.id.image);
            lytParent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            vh = new OriginalViewHolder(v);
        } else if (viewType == VIEW_AD) {
            vh = new NativeAdViewHolder(NativeAdViewHolder.setLayoutInflater(parent, Constant.NATIVE_STYLE))
                    .setAdStatus(Constant.AD_STATUS)
                    .setAdNetwork(Constant.AD_NETWORK)
                    .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                    .setAdMobNativeId(Constant.ADMOB_NATIVE_ID)
                    .setAdManagerNativeId(Constant.GOOGLE_AD_MANAGER_NATIVE_ID)
                    .setFanNativeId(Constant.FAN_NATIVE_ID)
                    .setAppLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                    .setAppLovinDiscoveryMrecZoneId(Constant.APPLOVIN_BANNER_MREC_ZONE_ID)
                    .setWortiseNativeId(Constant.WORTISE_NATIVE_ID)
                    .setPangleNativeId(Constant.PANGLE_NATIVE_ID)
                    .setHuaweiNativeId(Constant.HUAWEI_NATIVE_ID)
                    .setNativeAdStyle(Constant.NATIVE_STYLE)
                    .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                    .setDarkTheme(sharedPref.getIsDarkTheme());
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            vh = new OriginalViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Post p = posts.get(position);
            OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.name.setText(p.name);

            Glide.with(context)
                    .load(p.image.replace(" ", "%20"))
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(vItem.image);

            vItem.lytParent.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, p, position);
                }
            });

        } else if (holder instanceof NativeAdViewHolder) {
            final NativeAdViewHolder vItem = (NativeAdViewHolder) holder;
            vItem.loadNativeAd(context);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        if (post != null) {
            if (post.name == null || post.name.equals("")) {
                return VIEW_AD;
            } else {
                return VIEW_ITEM;
            }
        } else {
            return VIEW_ITEM;
        }
    }

    public void setListData(List<Post> posts, int totalPosts) {
        this.posts = posts;
        insertNativeAd(posts, totalPosts);
        notifyDataSetChanged();
    }

    private void insertNativeAd(List<Post> posts, int totalPosts) {
        switch (Constant.AD_NETWORK) {
            case ADMOB:
            case GOOGLE_AD_MANAGER:
            case FAN:
            case FACEBOOK:
            case APPLOVIN:
            case APPLOVIN_MAX:
            case APPLOVIN_DISCOVERY:
            case STARTAPP:
            case WORTISE:
            case PANGLE:
            case HUAWEI:
                int maxNumberNativeAd;
                if (totalPosts >= Constant.NATIVE_AD_INTERVAL) {
                    maxNumberNativeAd = (totalPosts / Constant.NATIVE_AD_INTERVAL);
                } else {
                    maxNumberNativeAd = 1;
                }
                int limitNativeAd = (maxNumberNativeAd * Constant.NATIVE_AD_INTERVAL) + Constant.NATIVE_AD_INDEX;
                if (posts.size() >= Constant.NATIVE_AD_INDEX) {
                    for (int i = Constant.NATIVE_AD_INDEX; i < limitNativeAd; i += Constant.NATIVE_AD_INTERVAL) {
                        posts.add(i, new Post());
                    }
                }
                break;
            default:
                //none
                break;
        }
    }

    public void resetListData() {
        this.posts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}