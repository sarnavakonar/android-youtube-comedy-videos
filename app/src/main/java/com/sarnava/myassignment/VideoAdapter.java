package com.sarnava.myassignment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Video> video_List;
    public MyAdapterListener onClickListener;

    public VideoAdapter(List<Video> video_List, MyAdapterListener onClickListener) {
        this.video_List = video_List;
        this.onClickListener = onClickListener;
    }


    private class CellViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll,ll2;
        private TextView title;
        private WebView webView;

        public CellViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.video_title);
            ll = (LinearLayout) itemView.findViewById(R.id.ll);
            ll2 = (LinearLayout) itemView.findViewById(R.id.ll2);
            webView = (WebView) itemView.findViewById(R.id.videoWebView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {
            } );

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.ll_OnClick(v, getAdapterPosition());
                }
            });
            ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.ll2_OnClick(v, getAdapterPosition());
                }
            });

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default: {
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new CellViewHolder(v1);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            default: {
                final Video video = video_List.get(position);
                final CellViewHolder holder = (CellViewHolder) viewHolder;

                holder.title.setText(video.getName());
                holder.webView.loadData( video.getVideo_id(), "text/html" , "utf-8" );

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return video_List.size();
    }

    public interface MyAdapterListener {
        void ll_OnClick(View v, int position);
        void ll2_OnClick(View v, int position);
    }
}
