package androidx.recyclerview.widget;

import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;

public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final AsyncListDiffer<T> mHelper;

    protected ListAdapter(DiffUtil.ItemCallback<T> diffCallback) {
        this.mHelper = new AsyncListDiffer<>(new AdapterListUpdateCallback(this), new AsyncDifferConfig.Builder(diffCallback).build());
    }

    protected ListAdapter(AsyncDifferConfig<T> config) {
        this.mHelper = new AsyncListDiffer<>(new AdapterListUpdateCallback(this), config);
    }

    public void submitList(List<T> list) {
        this.mHelper.submitList(list);
    }

    /* access modifiers changed from: protected */
    public T getItem(int position) {
        return this.mHelper.getCurrentList().get(position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mHelper.getCurrentList().size();
    }
}
