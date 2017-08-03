package me.imzack.app.end.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.imzack.app.end.R;
import me.imzack.app.end.model.bean.Library;
import me.imzack.app.end.util.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LibraryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private static final Library[] LIBRARIES = {
            new Library("Android Support Libraries", "Android", "https://developer.android.com/topic/libraries/support-library/"),
            new Library("Material Design Icons", "Google", "https://material.io/icons/"),
            new Library("ButterKnife", "JakeWharton", "http://jakewharton.github.io/butterknife/"),
            new Library("EventBus", "greenrobot", "http://greenrobot.org/eventbus/"),
            new Library("Dagger2", "Google", "https://google.github.io/dagger/"),
            new Library("RxJava", "ReactiveX", "https://github.com/ReactiveX/RxJava"),
            new Library("Plaid", "nickbutcher", "https://github.com/nickbutcher/plaid"),
            new Library("Kotlin", "JetBrains", "https://kotlinlang.org"),
            new Library("Realm Mobile Database", "Realm", "https://realm.io/products/realm-mobile-database/"),
    };

    private Activity mActivity;

    public LibraryListAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_list_library, parent, false));
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_library, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
            case TYPE_ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                final Library library = LIBRARIES[position - 1];
                itemViewHolder.mNameText.setText(library.getName());
                itemViewHolder.mDeveloperText.setText(library.getDeveloper());
                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SystemUtil.openLink(library.getLink(), mActivity);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return LIBRARIES.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_name)
        TextView mNameText;
        @BindView(R.id.text_developer)
        TextView mDeveloperText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
