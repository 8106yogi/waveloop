package com.swssm.waveloop;

import android.content.Context;
import android.widget.LinearLayout;

public class WaveformSegmentView extends LinearLayout{

	public WaveformSegmentView(Context context) {
		super(context);

		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) );
		
	}
	
	public void createWaveformView(int[] totalFrameData, SentenceSegment segment)
	{
		// 너무 길어지면 스크롤 퍼포먼스에 영향을 미쳐서 1000픽셀 길이로 잘라냄.
		int count = 0;
     	for( int innerOffset = 0; innerOffset < segment.size; innerOffset += 1000 )
     	{
     		int beginFrame = segment.startOffset+innerOffset;
     		int width = ((innerOffset+1000)<segment.size)?1000:segment.size-innerOffset;
     		
     		WaveformView waveformView = new WaveformView( getContext() );
         	waveformView.setData(totalFrameData, beginFrame, beginFrame + width, 250 );
         	
         	addView( waveformView );
         	
         	count++;
     	}
	}
	

}
