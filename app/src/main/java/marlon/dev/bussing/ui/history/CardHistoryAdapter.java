package marlon.dev.bussing.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import marlon.dev.bussing.R;
import marlon.dev.bussing.ui.history.CardHistoryAdapter;
import marlon.dev.bussing.ui.history.CardHistoryLists;

public class CardHistoryAdapter extends RecyclerView.Adapter<CardHistoryAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<CardHistoryLists> cardHistoryListsArrayList;

    public CardHistoryAdapter(Context context, ArrayList<CardHistoryLists> cardHistoryListsArrayList) {
        this.context = context;
        this.cardHistoryListsArrayList = cardHistoryListsArrayList;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public CardView cardHistoryView;
        public TextView transactType, transactDate;
        public ImageView imageType;

        public CardViewHolder(View view) {
            super(view);
            cardHistoryView = view.findViewById(R.id.cardHistory);
            transactType = view.findViewById(R.id.transactionType);
            transactDate = view.findViewById(R.id.date);
            imageType = view.findViewById(R.id.transactionTypeImage);
        }
    }

    @Override
    public CardHistoryAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflater
        View view = LayoutInflater.from(context).inflate(R.layout.card_history, parent, false);
        return new CardHistoryAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardHistoryAdapter.CardViewHolder holder, int position) {
        //get element
        CardHistoryLists cardHistoryLists = cardHistoryListsArrayList.get(position);
        holder.transactType.setText(cardHistoryLists.getTransactionType());
        holder.transactDate.setText(cardHistoryLists.getTransactionDate());
        holder.imageType.setImageResource(cardHistoryLists.getTransactionTypeIcon());
    }

    @Override
    public int getItemCount() {
        return cardHistoryListsArrayList.size();
    }

}
