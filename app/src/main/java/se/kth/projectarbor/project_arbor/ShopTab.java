package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by Fredrik Pihlqvist, Johan Andersson, Pethrus Gärdborn on 2017-04-28.
 */

public class ShopTab extends Fragment {

    private int money;

    private ImageView btnWaterSmall;
    private ImageView btnWaterMedium;
    private ImageView btnWaterLarge;
    private ImageView btnSunSmall;
    private ImageView btnSunMedium;
    private ImageView btnSunLarge;
    private TextView textMoney;

    // To store money and make it available to other parts of app
    private SharedPreferences sharedPreferences;

    // Add more items as needed
    public enum StoreItem {
        WATER_SMALL(10, 5),
        WATER_MEDIUM(50, 10),
        WATER_LARGE(100, 20),
        SUN_SMALL(10, 5),
        SUN_MEDIUM(50, 10),
        SUN_LARGE(100, 20);

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


    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Receives messages from pedometer as user is walking to increase money and update display
            if (intent.getAction().equals(Pedometer.STORE_BROADCAST)) {
                money += intent.getIntExtra("MONEY", 0);
                textMoney.setText("Curreny: " + money);
                sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
            // Receives messages from MainService to update display weather data
            } else if (intent.getAction().equals(MainService.TREE_DATA)) {
                Bundle extras = intent.getExtras();
                // tvShopSun.setText("SUN: " + extras.getInt("SUN"));
                // tvShopWater.setText("WATER: " + extras.getInt("WATER"));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_shop_tab, container, false);

        // Setup a filter for money
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.STORE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new Receiver(), filter);

        sharedPreferences = getActivity().getSharedPreferences("STORE_MONEY", Context.MODE_PRIVATE);

        // If money has been stored earlier, read from sharedPreferences
        if (sharedPreferences.contains("STORE_MONEY")) {
            money = sharedPreferences.getInt("STORE_MONEY", 0);

        // Else, set initial money value
        } else {
            money = 10;
            sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
        }

        textMoney = (TextView) view.findViewById(R.id.text_money);
        textMoney.setText(this.money + "gp");

        btnWaterSmall = (ImageView) view.findViewById(R.id.box_water_small);
        btnWaterSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.WATER_SMALL);
                textMoney.setText(ShopTab.this.money + "gp");
                //Toast.makeText(getContext(), "Bought Water (small)", Toast.LENGTH_SHORT).show();
            }
        });

        btnWaterMedium = (ImageView) view.findViewById(R.id.box_water_medium);
        btnWaterMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.WATER_MEDIUM);
                textMoney.setText(ShopTab.this.money + "gp");
                //Toast.makeText(getContext(), "Bought Water (medium)", Toast.LENGTH_SHORT).show();
            }
        });

        btnWaterLarge = (ImageView) view.findViewById(R.id.box_water_large);
        btnWaterLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.WATER_LARGE);
                textMoney.setText(ShopTab.this.money + "gp");
                //Toast.makeText(getContext(), "Bought Water (large)", Toast.LENGTH_SHORT).show();
            }
        });

        // ----
        btnSunSmall = (ImageView) view.findViewById(R.id.box_sun_small);
        btnSunSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.SUN_SMALL);
                textMoney.setText(ShopTab.this.money + "gp");
                //Toast.makeText(getContext(), "Bought Sun (small)", Toast.LENGTH_SHORT).show();
            }
        });

        btnSunMedium = (ImageView) view.findViewById(R.id.box_sun_medium);
        btnSunMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.SUN_MEDIUM);
                textMoney.setText(ShopTab.this.money + "gp");
                //Toast.makeText(getContext(), "Bought Sun (medium)", Toast.LENGTH_SHORT).show();
            }
        });

        btnSunLarge = (ImageView) view.findViewById(R.id.box_sun_large);
        btnSunLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.SUN_LARGE);
                textMoney.setText(ShopTab.this.money + "gp");
                //Toast.makeText(getContext(), "Bought Sun (large)", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    // Returns true if possible to make purchase
    private boolean  withdrawMoney(int purchase) {
        if (money - purchase < 0) {
            return false;
        }
        else {
            this.money -= purchase;
            return true;
        }
    }

    public void buy(StoreItem item) {
        Log.d("ARBOR","BUY ");

        // If enough money to buy item
        if(withdrawMoney(item.getCost())) {
            Intent intent = new Intent(getActivity(), MainService.class);
            intent.putExtra("MESSAGE_TYPE", MainService.MSG_PURCHASE);
            intent.putExtra("STORE_ITEM", item);
            getActivity().startService(intent);
        }
        else{
            CharSequence text = " Not Enough Money!" ;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(),text ,duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}

