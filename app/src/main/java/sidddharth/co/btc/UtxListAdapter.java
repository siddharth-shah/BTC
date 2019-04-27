package sidddharth.co.btc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import sidddharth.co.btc.models.UTX;

public class UtxListAdapter extends RecyclerView.Adapter {
    private Context context;
    List<UTX> utxList;

    public UtxListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new UtxItemViewHolder(LayoutInflater.from(context).inflate(R.layout.utx_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((UtxItemViewHolder) viewHolder).utxHash.setText(utxList.get(i).getTransactionHash());
        ((UtxItemViewHolder) viewHolder).utxTime.setText(getLocalTime(utxList.get(i).getTimestamp()));
        ((UtxItemViewHolder) viewHolder).utxAmount.setText(String.valueOf(utxList.get(i).getTransactionAmnt()));
    }

    private String getLocalTime(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        Date d = new Date(timestamp);
        return simpleDateFormat.format(d);
    }

    @Override
    public int getItemCount() {
        if (utxList == null)
            return 0;
        return utxList.size();
    }

    public void addUTX(UTX utx) {
        if (utxList == null)
            utxList = new ArrayList<>();
        utxList.add(utx);
        notifyItemInserted(utxList.size() - 1);
    }

    class UtxItemViewHolder extends RecyclerView.ViewHolder {
        TextView utxHash;
        TextView utxAmount;
        TextView utxTime;

        public UtxItemViewHolder(@NonNull View itemView) {
            super(itemView);
            utxHash = itemView.findViewById(R.id.utx_hash);
            utxAmount = itemView.findViewById(R.id.transaction_amount);
            utxTime = itemView.findViewById(R.id.transaction_time);
        }
    }
}
