package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Fredrik Pihlqvist, Johan Andersson, Pethrus Gärdborn on 2017-04-28.
 */

public class ShopTab extends Fragment {

    private int money;
    private final int SUN_COLOR = 0xFFF2941F;
    private final int WATER_COLOR = 0xFF00B9D3;
    private final int PURCHASE_TEXT_SIZE = 54;
    private final int NO_MONEY_TEXT_SIZE = 32;
    private final int PURCHASE_BUTTON_TINT = 0x1F00FF00;
    private final int NO_MONEY_BUTTON_TINT = 0x1FFF0000;

    //private boolean animReady;
    //Detect Movements over time
    float xValue;
    float yValue;

    private ImageView btnWaterSmall;
    private ImageView btnWaterMedium;
    private ImageView btnWaterLarge;
    private ImageView btnSunSmall;
    private ImageView btnSunMedium;
    private ImageView btnSunLarge;

    private TextView goldenPollenView;

    // To store money and make it available to other parts of app
    private SharedPreferences sharedPreferences;

    private RelativeLayout.LayoutParams layoutParams;
    private TextView textReceipt;

    //Create a reference of type SoundHandler to access mainUIActivity's cached items
    private MainUIActivity.SoundHandler sh;

    // Add more items as needed
    public enum StoreItem {
        WATER_SMALL(75, 10),
        WATER_MEDIUM(200, 50),
        WATER_LARGE(500, 100),
        SUN_SMALL(75, 10),
        SUN_MEDIUM(200, 50),
        SUN_LARGE(500, 100);

        private int amount;
        private int cost;

        private StoreItem(int amount, int cost) {
            this.amount = amount;
            this.cost = cost;
        }

        public int getAmount() {
            return amount;
        }

        public int getCost() {
            return cost;
        }
    }

/* //TODO: Check if MainUI does the same as this one
    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Receives messages from pedometer as user is walking to increase money and update display
            if (intent.getAction().equals(Pedometer.STORE_BROADCAST)) {
                money += intent.getIntExtra("MONEY", 0);
                textMoney.setText(money + "gp");
                sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
            // Receives messages from MainService to update display weather data
            } else if (intent.getAction().equals(MainService.TREE_DATA)) {
                Bundle extras = intent.getExtras();
                // tvShopSun.setText("SUN: " + extras.getInt("SUN"));
                // tvShopWater.setText("WATER: " + extras.getInt("WATER"));
            }
        }
    }
*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //get SoundHandler from MainUIActivity
        sh = ((MainUIActivity)getActivity()).getSoundHandler();


        View view = inflater.inflate(R.layout.fragment_shop_tab, container, false);

        // These may be used when doing animation programmatically
        //Display display = getActivity().getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        //display.getSize(size);
        //layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        //       ViewGroup.LayoutParams.WRAP_CONTENT);
        //textReceipt = new TextView(getActivity());

        textReceipt = (TextView) view.findViewById(R.id.text_receipt);
        textReceipt.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        textReceipt.setTextSize(PURCHASE_TEXT_SIZE);
        //textReceipt.setLayoutParams(layoutParams);
        textReceipt.setVisibility(View.INVISIBLE);

        final Animation animationAppear = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.shop_receipt_appear);
        final Animation animationWait = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.shop_receipt_wait);
        final Animation animationDisappear = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.shop_receipt_disappear);

        // The following three method calls, make possible to execute the three animations (see xml)
        // in sequence.

        // This listener makes the feedback text visible when the user buys an item
        // and translates the text to the center
        animationAppear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                textReceipt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textReceipt.startAnimation(animationWait);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // This listener makes the feedback text stand still for a short while
        animationWait.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textReceipt.startAnimation(animationDisappear);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // This listener makes the feedback text go off screen
        animationDisappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textReceipt.setVisibility(View.INVISIBLE);
                textReceipt.setTextSize(PURCHASE_TEXT_SIZE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //ViewGroup layout = (ViewGroup) view.findViewById(R.id.shop_top_layout);
        //layout.addView(textReceipt);

        // Setup a filter for money
        /* // TODO: See if this works after integrich
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.STORE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new Receiver(), filter);
        */
        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);

        /*
        //If money has been stored earlier, read from sharedPreferences
        if (sharedPreferences.contains("STORE_MONEY")) {
            money = sharedPreferences.getInt("STORE_MONEY", 0);
        // Else, set initial money value
        } else {
            money = 10;
            sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
        }

        textMoney = (TextView) view.findViewById(R.id.text_money);
        textMoney.setText(this.money + "gp");

        */

        goldenPollenView = (TextView) view.findViewById(R.id.text_money);
        goldenPollenView.setText(MainUIActivity.goldenPollen + "gp");


        // Buttons that give feedback when pressed
        btnWaterSmall = (ImageView) view.findViewById(R.id.box_water_small);
        btnWaterSmall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return genOnTouch(btnWaterSmall, WATER_COLOR, event, StoreItem.WATER_SMALL,animationAppear);
            }
        });

        btnWaterMedium = (ImageView) view.findViewById(R.id.box_water_medium);
        btnWaterMedium.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return genOnTouch(btnWaterMedium, WATER_COLOR, event, StoreItem.WATER_MEDIUM, animationAppear);
            }
        });

        btnWaterLarge = (ImageView) view.findViewById(R.id.box_water_large);
        btnWaterLarge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return genOnTouch(btnWaterLarge, WATER_COLOR, event, StoreItem.WATER_LARGE, animationAppear);
            }
        });

        btnSunSmall = (ImageView) view.findViewById(R.id.box_sun_small);
        btnSunSmall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return genOnTouch(btnSunSmall, SUN_COLOR, event, StoreItem.SUN_SMALL, animationAppear);
            }
        });

        btnSunMedium = (ImageView) view.findViewById(R.id.box_sun_medium);
        btnSunMedium.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return genOnTouch(btnSunMedium, SUN_COLOR, event, StoreItem.SUN_MEDIUM, animationAppear);
            }
        });

        btnSunLarge = (ImageView) view.findViewById(R.id.box_sun_large);
        btnSunLarge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return genOnTouch(btnSunLarge, SUN_COLOR, event, StoreItem.SUN_LARGE, animationAppear);
            }
        });

        return view;
    }

    // Buy item and start animation
    private boolean purchaseItem(StoreItem item, final int color, Animation anim) {
        Log.d("ARBOR", "purchasing");
        textReceipt.setTextColor(ColorStateList.valueOf(color));
            if(buy(item)) {
                goldenPollenView.setText(MainUIActivity.goldenPollen + "gp");
                ((MainUIActivity)getActivity()).treeTab.getTvPollen().setText(MainUIActivity.goldenPollen + "gp");
                textReceipt.startAnimation(anim);
                return true;
            }
            else {
                textReceipt.startAnimation(anim);
                return false;
            }
    }

    // Returns true if possible to make purchase
    private boolean withdrawMoney(int purchase) {
        if (MainUIActivity.goldenPollen - purchase < 0) {
            return false;
        }
        else {
            MainUIActivity.goldenPollen -= purchase;
            sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
            return true;
        }
    }

    // Broadcast your purchase to the main service
    private boolean buy(StoreItem item) {
        Log.d("ARBOR","BUY ");

        // If enough money to buy item
        if(withdrawMoney(item.getCost())) {
            Intent intent = new Intent(getActivity(), MainService.class);
            intent.putExtra("MESSAGE_TYPE", MainService.MSG_PURCHASE);
            intent.putExtra("STORE_ITEM", item);
            getActivity().startService(intent);
            textReceipt.setText("+" + item.amount);
            return true;
        } else {
            textReceipt.setText("Not enough pollen");
            textReceipt.setTextSize(NO_MONEY_TEXT_SIZE);
            return false;
        }
    }

    int addMoney(int newMoney) {
        return (MainUIActivity.goldenPollen += newMoney);
    }

    TextView getGoldenPollenView() {
        return goldenPollenView;
    }
    //A generic onTouch to be used by every button
    public boolean genOnTouch(ImageView iV, int color, MotionEvent e, StoreItem item, Animation animation){
        int action = e.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            iV.setColorFilter(PURCHASE_BUTTON_TINT);
            iV.setColorFilter(NO_MONEY_BUTTON_TINT);
            xValue = e.getX();
            yValue = e.getY();
            Log.d("ARBOR", ("X: " + Float.toString(xValue) + "Y: " + Float.toString(yValue)));
            return true;
        }else if(action == MotionEvent.ACTION_MOVE){
            if((Math.abs(e.getX()- xValue) > 10)){
                iV.clearColorFilter();
            }

        }else if(action == MotionEvent.ACTION_UP){
            if(!(Math.abs(e.getX()- xValue) > 10)) {
                if (purchaseItem(item, color, animation)) {
                    sh.playShopWater();
                } else {
                    sh.playNoMoney();
                }
            }
            Log.d("ARBOR", "CLEARED");
            iV.clearColorFilter();
            return true;
        }else if (action == MotionEvent.ACTION_CANCEL) {
            iV.clearColorFilter();
            return true;
        }
        return false;
    }
}

