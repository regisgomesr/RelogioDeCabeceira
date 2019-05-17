package com.cursoandroid.relogiodecabeceira;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private ViewHolder mViewHolder = new ViewHolder();
    private boolean mRunnableStopped = false;
    private boolean mIsBatteryOn = true;

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            mViewHolder.mTextBatteryLevel.setText(String.valueOf(level) + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.mViewHolder.mTextHourMinute = (TextView) this.findViewById(R.id.text_hours_minute);
        this.mViewHolder.mTextSeconds = (TextView) this.findViewById(R.id.text_seconds);
        this.mViewHolder.mTextBatteryLevel = (TextView) this.findViewById(R.id.text_battery_level);
        this.mViewHolder.mCheckBattery = (CheckBox) this.findViewById(R.id.check_battery);
        this.mViewHolder.mImageOption = (ImageView) this.findViewById(R.id.image_option);
        this.mViewHolder.mImageClose = (ImageView) this.findViewById(R.id.image_close);
        this.mViewHolder.mLinearOptions = (LinearLayout) this.findViewById(R.id.linear_options);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.registerReceiver(this.mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        this.mViewHolder.mCheckBattery.setChecked(true);

        this.mViewHolder.mLinearOptions.animate().translationY(500)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

        this.setListener();

    }

    @Override
    public void onResume() {
        super.onResume();

        this.mRunnableStopped = false;
        this.startBedside();
    }

    @Override
    public void onStop() {
        super.onStop();

        this.mRunnableStopped = true;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.check_battery) {

            this.toggleCheckBattery();
        } else if (id == R.id.image_option) {
            // Abrir o menu

            this.mViewHolder.mLinearOptions.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearOptions.animate().translationY(0)
                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

        } else if (id == R.id.image_close) {
            // Fechar o menu

            this.mViewHolder.mLinearOptions.animate().translationY(this.mViewHolder.mLinearOptions.getMeasuredHeight())
                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

        }

    }

    private void toggleCheckBattery() {

        if (this.mIsBatteryOn) {
            this.mIsBatteryOn = false;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.GONE);

        } else {

            this.mIsBatteryOn = true;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.VISIBLE);
        }

    }


    private void setListener() {

        this.mViewHolder.mCheckBattery.setOnClickListener(this);
        this.mViewHolder.mImageOption.setOnClickListener(this);
        this.mViewHolder.mImageClose.setOnClickListener(this);
    }

    // Metodo relogio de cabeceira pegando a hora minuto e segundo
    private void startBedside() {

        final Calendar calendar = Calendar.getInstance();

        this.mRunnable = new Runnable() {
            @Override
            public void run() {


                if (mRunnableStopped)
                    return;

                // Busca horario
                calendar.setTimeInMillis(System.currentTimeMillis());

                // formata horario
                String hourMinutesFormat = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                String secondsFormat = String.format("%02d", calendar.get(Calendar.SECOND));

                // atribui os valores
                mViewHolder.mTextHourMinute.setText(hourMinutesFormat);
                mViewHolder.mTextSeconds.setText(secondsFormat);

                // Calcula a proxima execucao
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - (now % 1000));

                mHandler.postAtTime(mRunnable, next);

            }
        };

        this.mRunnable.run();


    }


    private static class ViewHolder {

        TextView mTextHourMinute;
        TextView mTextSeconds;
        TextView mTextBatteryLevel;
        CheckBox mCheckBattery;
        ImageView mImageOption;
        ImageView mImageClose;
        LinearLayout mLinearOptions;
    }

}
