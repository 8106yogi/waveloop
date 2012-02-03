package com.swssm.waveloop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerOptionDialog {
	public static void ShowPlaybackOption( final Context context )
	{
		AlertDialog.Builder builder;
    	AlertDialog alertDialog;
    	
    	
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.option_item, null);
    	 
    	TextView subTitle = (TextView)layout.findViewById(R.option.subtitle);
    	subTitle.setText("재생 속도");
    	 
    	TextView seekMin = (TextView)layout.findViewById(R.option.seek_min);
    	TextView seekMax = (TextView)layout.findViewById(R.option.seek_max);
    	 
    	seekMin.setText("0.50x");
    	seekMax.setText("2.00x");
    	 
    	final SeekBar seekBar = (SeekBar)layout.findViewById(R.option.seekBar);
    	final Button resetButton = (Button)layout.findViewById(R.option.reset_button);
    	
    	 // 이건 0.01x 단위. 0부터 시작하므로 항상 50(최소속도 0.5x)를 빼준다.
    	seekBar.setMax(150);
    	seekBar.setProgress( (GlobalOptions.playbackSpeed-500)/10 );
    	
    	seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    			// 재생속도 변경!
    			GlobalOptions.playbackSpeed = (seekBar.getProgress() + 50)*10;
    			UpdateSpeedButtonText(resetButton);
    			UpdatePlayerSpeed(); 
    		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
    	});
    	
    	UpdateSpeedButtonText(resetButton);
    	
		resetButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				seekBar.setProgress(50);
			}
    	});
    	
    	
    	
    	builder = new AlertDialog.Builder(context);
    	builder.setCancelable(false);
    	builder.setTitle("재생 옵션");
    	builder.setView(layout);
    	builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int which){
    			GlobalOptions.save(context);
    		}
    	} );
    	
    	
    	alertDialog = builder.create();
    	alertDialog.show();
	}
	
	private static void UpdateSpeedButtonText(Button button)
    {
    	float fSpeedSec = (float)GlobalOptions.playbackSpeed/1000.0f;
    	button.setText(String.format("%.2fx", fSpeedSec ));
    }
    
	private static void UpdatePlayerSpeed()
    {
    	//if(player_main.mOSLESPlayer != null)
    		PlayerProxy.setRate( GlobalOptions.playbackSpeed );
    }
	
	
	//
	public static void ShowRepeatOption( final Context context )
	{
		AlertDialog.Builder builder;
    	AlertDialog alertDialog;
    	
    	
    	LinearLayout layout = new LinearLayout(context);
    	layout.setOrientation(LinearLayout.VERTICAL);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    			ViewGroup.LayoutParams.FILL_PARENT,
    			ViewGroup.LayoutParams.WRAP_CONTENT,
    			0.0F);
    	layout.setLayoutParams(params);
    	
    	layout.addView( MakeRepeatCountOptionView(context) );
    	layout.addView( MakeRepeatDelayTimeOptionView(context) );
    	
    	
    	builder = new AlertDialog.Builder(context);
    	builder.setCancelable(false);
    	builder.setTitle("구간반복 옵션");
    	builder.setView(layout);
    	builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialog, int which){
    			GlobalOptions.save(context);
    		}
    	} );
    	
    	
    	alertDialog = builder.create();
    	alertDialog.show();
	}
	
	
	private static View MakeRepeatCountOptionView( Context context )
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.option_item, null);
    	 
    	TextView subTitle = (TextView)layout.findViewById(R.option.subtitle);
    	subTitle.setText("구간반복 횟수");
    	 
    	TextView seekMin = (TextView)layout.findViewById(R.option.seek_min);
    	TextView seekMax = (TextView)layout.findViewById(R.option.seek_max);
    	
    	seekMin.setText("0");
    	seekMax.setText("20");
    	
    	final SeekBar seekBar = (SeekBar)layout.findViewById(R.option.seekBar);
    	final Button resetButton = (Button)layout.findViewById(R.option.reset_button);
    	
    	
    	seekBar.setMax(20);
    	seekBar.setProgress( GlobalOptions.repeatCount );
    	
    	seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    			GlobalOptions.repeatCount = seekBar.getProgress();
    			UpdateRepeatCountButtonText(resetButton);
    			//UpdatePlayerSpeed();
    		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
    	});
    	
    	UpdateRepeatCountButtonText(resetButton);
    	
		resetButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				seekBar.setProgress(0);
			}
    	});
    	
    	return layout;
	}
	
	private static void UpdateRepeatCountButtonText(Button button)
    {
		if(GlobalOptions.repeatCount == 0)
			button.setText(String.format("무제한", GlobalOptions.repeatCount ));
		else
			button.setText(String.format("%d번", GlobalOptions.repeatCount ));
    }

	
	private static View MakeRepeatDelayTimeOptionView( Context context )
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.option_item, null);
    	
    	TextView subTitle = (TextView)layout.findViewById(R.option.subtitle);
    	subTitle.setText("구간반복 대기시간");
    	 
    	TextView seekMin = (TextView)layout.findViewById(R.option.seek_min);
    	TextView seekMax = (TextView)layout.findViewById(R.option.seek_max);
    	
    	seekMin.setText("0.0s");
    	seekMax.setText("2.0s");
    	
    	final SeekBar seekBar = (SeekBar)layout.findViewById(R.option.seekBar);
    	final Button resetButton = (Button)layout.findViewById(R.option.reset_button);
    	
    	
    	seekBar.setMax(20);
    	seekBar.setProgress( GlobalOptions.repeatDelayTime/100 );
    	
    	seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    			GlobalOptions.repeatDelayTime = seekBar.getProgress()*100;
    			UpdateRepeatDelayButtonText(resetButton);
    			//UpdatePlayerSpeed();
    		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
    	});
    	
    	UpdateRepeatDelayButtonText(resetButton);
    	
		resetButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				seekBar.setProgress(1);
			}
    	});
    	
    	return layout;
	}
	
	
	private static void UpdateRepeatDelayButtonText(Button button)
    {
		float fTime = GlobalOptions.repeatDelayTime/1000.0f;
		button.setText(String.format("%.1fs", fTime ));
    }
	
}
