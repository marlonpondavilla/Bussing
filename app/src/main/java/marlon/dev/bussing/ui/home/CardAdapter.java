package marlon.dev.bussing.ui.home;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.ViewGroup;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

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
        public TextView busNum, fromLoc, toWhereLoc, currentLoc, driver, conductor, capacity;
        public ImageView busCompImage;

        public CardViewHolder(View view) {
            super(view);
            // Initialize the views
            cardView = view.findViewById(R.id.cardView);
            busNum = view.findViewById(R.id.busNumber);
            fromLoc = view.findViewById(R.id.fromLocation);
            toWhereLoc = view.findViewById(R.id.toWhereLocation);
            currentLoc = view.findViewById(R.id.currentLocation);
            busCompImage = view.findViewById(R.id.busCompany);
            driver = view.findViewById(R.id.driverName);
            conductor = view.findViewById(R.id.conductorName);
            capacity = view.findViewById(R.id.capacity);
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
        // Get the current card data
        CardLists cardLists = cardListsArrayList.get(position);

        // Set the data for the current card
        holder.busNum.setText(cardLists.getBusNumber());
        holder.fromLoc.setText(cardLists.getFromLocation());
        holder.toWhereLoc.setText(cardLists.getToWhereLocation());
        holder.currentLoc.setText(cardLists.getCurrentLocation());
        holder.busCompImage.setImageResource(cardLists.getBusCompanyImage());
        holder.driver.setText(cardLists.getDriverName());
        holder.conductor.setText(cardLists.getConductorName());
        holder.capacity.setText(String.valueOf(cardLists.getCapacity()));

        LinearLayout detailsLayout = holder.itemView.findViewById(R.id.details);
        ImageView dropdownImage = holder.itemView.findViewById(R.id.dropdown);

        // Initially hide the details section and set its height to 0
        detailsLayout.setVisibility(View.GONE);
        detailsLayout.getLayoutParams().height = 0;

        // Set click listener on the card to toggle details visibility and rotate the image
        holder.cardView.setOnClickListener(v -> {
            // Check if the details section is currently visible or not
            if (detailsLayout.getVisibility() == View.GONE) {
                // Make sure to measure the layout before animating
                detailsLayout.setVisibility(View.VISIBLE);
                detailsLayout.measure(View.MeasureSpec.makeMeasureSpec(holder.itemView.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                int targetHeight = detailsLayout.getMeasuredHeight();

                // Animate the expansion of the details section
                ValueAnimator expandAnimator = ValueAnimator.ofInt(0, targetHeight);
                expandAnimator.addUpdateListener(valueAnimator -> {
                    // Set the new height of the details layout during animation
                    int height = (int) valueAnimator.getAnimatedValue();
                    detailsLayout.getLayoutParams().height = height;
                    detailsLayout.requestLayout();
                });
                expandAnimator.setDuration(300); // Duration of the expansion animation
                expandAnimator.start();

                // Rotate the dropdown image smoothly (rotate 180 degrees)
                ObjectAnimator rotateUp = ObjectAnimator.ofFloat(dropdownImage, "rotation", 0f, 180f);
                rotateUp.setDuration(300); // Duration of the rotation
                rotateUp.start();
            } else {
                // Animate the collapse of the details section
                ValueAnimator collapseAnimator = ValueAnimator.ofInt(detailsLayout.getHeight(), 0);
                collapseAnimator.addUpdateListener(valueAnimator -> {
                    // Set the new height of the details layout during animation
                    int height = (int) valueAnimator.getAnimatedValue();
                    detailsLayout.getLayoutParams().height = height;
                    detailsLayout.requestLayout();
                });
                collapseAnimator.setDuration(300); // Duration of the collapse animation
                collapseAnimator.start();

                collapseAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // After collapse animation, set the visibility to GONE
                        detailsLayout.setVisibility(View.GONE);
                        // Reset height back to 0
                        detailsLayout.getLayoutParams().height = 0;
                        detailsLayout.requestLayout();
                    }
                });

                // Rotate the dropdown image smoothly back to 0 degrees
                ObjectAnimator rotateDown = ObjectAnimator.ofFloat(dropdownImage, "rotation", 180f, 0f);
                rotateDown.setDuration(300); // Duration of the rotation
                rotateDown.start();
            }
        });
    }





    @Override
    public int getItemCount() {
        return cardListsArrayList.size();
    }

}
