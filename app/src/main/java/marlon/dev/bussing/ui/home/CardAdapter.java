package marlon.dev.bussing.ui.home;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import marlon.dev.bussing.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<CardLists> cardListsArrayList;

    public CardAdapter(Context context, ArrayList<CardLists> cardListsArrayList) {
        this.context = context;
        this.cardListsArrayList = cardListsArrayList;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView busNum, fromLoc, toWhereLoc, currentLoc;
        public ImageView busCompImage;

        public CardViewHolder(View view) {
            super(view);
            //intialize
            cardView = view.findViewById(R.id.cardView);
            busNum = view.findViewById(R.id.busNumber);
            fromLoc = view.findViewById(R.id.fromLocation);
            toWhereLoc = view.findViewById(R.id.toWhereLocation);
            currentLoc = view.findViewById(R.id.currentLocation);
            busCompImage = view.findViewById(R.id.busCompany);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflater
        View view = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        //get element
        CardLists cardLists = cardListsArrayList.get(position);
        holder.busNum.setText(cardLists.getBusNumber());
        holder.fromLoc.setText(cardLists.getFromLocation());
        holder.toWhereLoc.setText(cardLists.getToWhereLocation());
        holder.currentLoc.setText(cardLists.getCurrentLocation());
        holder.busCompImage.setImageResource(cardLists.getBusCompanyImage());
    }

    @Override
    public int getItemCount() {
        return cardListsArrayList.size();
    }

}
