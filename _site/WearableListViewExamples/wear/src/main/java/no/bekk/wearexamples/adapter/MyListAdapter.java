package no.bekk.wearexamples.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import no.bekk.wearexamples.R;
import no.bekk.wearexamples.domain.Item;

public class MyListAdapter extends WearableListView.Adapter {

    private final List<Item> items;
    private final Context context;

    public MyListAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(new ItemView(context));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        Item item = items.get(position);
        ItemView view = (ItemView) holder.itemView;
        view.itemContentView.setText(item.getContent());
        if (item.isDone()) {
            view.imageView.setImageResource(R.drawable.ic_action_done);
        }
        else {
            view.imageView.setImageResource(R.drawable.ic_action_attach);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends WearableListView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected class ItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {
        private static final int ANIMATION_DURATION_MS = 150;
        private static final float SHRINK_CIRCLE_RATIO = .80f;
        private static final float SHRINK_LABEL_ALPHA = .5f;
        private static final float EXPAND_LABEL_ALPHA = 1f;

        public final TextView itemContentView;
        public CircledImageView imageView;
        private final ObjectAnimator expandCircleAnimator;
        private final ObjectAnimator expandLabelAnimator;
        private final AnimatorSet expandAnimator;

        private final float expandCircleRadius;
        private final float shrinkCircleRadius;
        private final ObjectAnimator shrinkCircleAnimator;
        private final ObjectAnimator shrinkLabelAnimator;
        private final AnimatorSet shrinkAnimator;

        public ItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.todo_item_layout, this);
            this.itemContentView = (TextView) findViewById(R.id.text);
            this.imageView = (CircledImageView) findViewById(R.id.image);
            expandCircleRadius = imageView.getCircleRadius();
            shrinkCircleRadius = expandCircleRadius * SHRINK_CIRCLE_RATIO;

            shrinkCircleAnimator = ObjectAnimator.ofFloat(imageView, "circleRadius",
                    expandCircleRadius, shrinkCircleRadius);
            shrinkLabelAnimator = ObjectAnimator.ofFloat(itemContentView, "alpha",
                    EXPAND_LABEL_ALPHA, SHRINK_LABEL_ALPHA);
            shrinkAnimator = new AnimatorSet().setDuration(ANIMATION_DURATION_MS);
            shrinkAnimator.playTogether(shrinkCircleAnimator, shrinkLabelAnimator);

            expandCircleAnimator = ObjectAnimator.ofFloat(imageView, "circleRadius",
                    shrinkCircleRadius, expandCircleRadius);
            expandLabelAnimator = ObjectAnimator.ofFloat(itemContentView, "alpha",
                    SHRINK_LABEL_ALPHA, EXPAND_LABEL_ALPHA);
            expandAnimator = new AnimatorSet().setDuration(ANIMATION_DURATION_MS);
            expandAnimator.playTogether(expandCircleAnimator, expandLabelAnimator);
        }

        @Override
        public void onCenterPosition(boolean animate) {
            if (animate) {
                shrinkAnimator.cancel();
                if (!expandAnimator.isRunning()) {
                    expandCircleAnimator.setFloatValues(imageView.getCircleRadius(), expandCircleRadius);
                    expandLabelAnimator.setFloatValues(itemContentView.getAlpha(), EXPAND_LABEL_ALPHA);
                    expandAnimator.start();
                }
            } else {
                expandAnimator.cancel();
                imageView.setCircleRadius(expandCircleRadius);
                itemContentView.setAlpha(EXPAND_LABEL_ALPHA);
            }
        }

        @Override
        public void onNonCenterPosition(boolean animate) {
            if (animate) {
                expandAnimator.cancel();
                if (!shrinkAnimator.isRunning()) {
                    shrinkCircleAnimator.setFloatValues(imageView.getCircleRadius(), shrinkCircleRadius);
                    shrinkLabelAnimator.setFloatValues(itemContentView.getAlpha(), SHRINK_LABEL_ALPHA);
                    shrinkAnimator.start();
                }
            } else {
                shrinkAnimator.cancel();
                imageView.setCircleRadius(shrinkCircleRadius);
                itemContentView.setAlpha(SHRINK_LABEL_ALPHA);
            }
        }
    }
}
