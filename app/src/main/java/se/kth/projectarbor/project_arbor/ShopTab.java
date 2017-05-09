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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by Fredrik Pihlqvist, Johan Andersson, Pethrus Gärdborn on 2017-04-28.
 */

public class ShopTab extends Fragment {

    private Button btnBuyWater;
    private Button btnBuySun;
    private TextView tvMoney;

    private TextView tvShopWater;
    private TextView tvShopSun;

    private int money;

    // To store money and make it available to other parts of app
    private SharedPreferences sharedPreferences;

    // Add more items as needed
    public enum StoreItem {
        WATER(300, 4),
        SUN(200, 4);

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
                tvMoney.setText("Curreny: " + money);
                sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
            // Receives messages from MainService to update display weather data
            } else if (intent.getAction().equals(MainService.TREE_DATA)) {
                Bundle extras = intent.getExtras();
                tvShopSun.setText("SUN: " + extras.getInt("SUN"));
                tvShopWater.setText("WATER: " + extras.getInt("WATER"));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_tab, container, false);

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


        tvMoney = (TextView) view.findViewById(R.id.tvMoney);
        tvMoney.setText("Curreny "+ this.money);

        tvShopSun = (TextView) view.findViewById(R.id.tvShopSun);
        tvShopWater = (TextView) view.findViewById(R.id.tvShopWater);

        btnBuyWater = (Button) view.findViewById(R.id.btnWater);
        btnBuyWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.WATER);
                tvMoney.setText("Currency: "+ money);
                Toast.makeText(getContext(), "Bought Water", Toast.LENGTH_SHORT).show();
            }
        });

        btnBuySun = (Button) view.findViewById(R.id.btnSun);
        btnBuySun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "BUY SUN");
                buy(StoreItem.SUN);
                tvMoney.setText("Curreny: "+money);
                Toast.makeText(getContext(), "Bought Sun", Toast.LENGTH_SHORT).show();
            }
        });
        // If button held for a LONG time
        btnBuyWater.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                money += 20;
                tvMoney.setText("Currency: " + money);
                return true;
            }
        });

        btnBuySun.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
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

