package com.swssm.waveloop;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SentenceSegmentList {

	private SentenceSegment[] mSegmentList;
	
	public final SentenceSegment[] getSegments()
	{
		return mSegmentList;
	}
	
	public void create( int[] waveformData )
	{
		ArrayList<SentenceSegment> segments = new ArrayList<SentenceSegment>();
		
		int nextIndex = 0;
		int nCount = waveformData.length;
		for (int i = 0; i < nCount; ++i )
		{
			nextIndex = nCount;
			
			int value = waveformData[i];
			boolean isSilence = (5 > value)? true:false;
			if(isSilence)// 무음이면
			{
				
				for(int j = i+1; j < nCount; ++j )
				{
					if( 2 < waveformData[j] )
					{
						nextIndex = j;
						break;
					}
				}
			}
			else// 소리가 있으면
			{
				for (int j = i+1; j < nCount; ++j)
				{
	                if ( 3 > waveformData[j] )
	                {
	                    nextIndex = j;
	                    break;
	                }
	            }
			}
			
			if (i < nextIndex)
			{
				// 클래스를 만들어서 리스트에 넣어줌.
				
				SentenceSegment segment = new SentenceSegment(isSilence, i, nextIndex-i );
				segments.add(segment);
				
				i = nextIndex-1;

	        }
		}
		
		
		//////////////////////////////////////////////////////////////////
		
		// 동일한 타입이 연속이면 (문장+문장 형태이거나 무음+무음) 합쳐버린다.
	    for (int i = 0; i < segments.size()-1; ++i) {
	    	SentenceSegment currSeg = segments.get(i);
	    	SentenceSegment nextSeg = segments.get(i+1);
	        
	        if (currSeg.isSilence == nextSeg.isSilence) {
	        	currSeg.size += nextSeg.size;
	        	segments.remove(i+1);
	            --i;
	        }
	    }
	    
	    //////////////////////////////////////////////////////////////////
	    // 30(0.3초) 이하의 길이는 앞뒤 segment를 병합해버린다.
	    for (int i = 0; i < segments.size(); ++i) {
	    	SentenceSegment currSeg = segments.get(i);
	    	
	        if ( currSeg.size < 15 ) {
	        	
	            // 뒤 세그먼트 구하고
	            // 현재 세그먼트 구해서
	            // 앞 세그먼트의 사이즈에
	            // 현재와 다음 세그먼트의 사이즈를 더해줌

	            // 앞 세그먼트 구하고
	        	SentenceSegment prevSeg = null;
	        	SentenceSegment nextSeg = null;
	            
	            if ( i >= 1 ) {
	            	prevSeg = segments.get(i-1);
	            }
	            if ( i < segments.size()-1 ) {
	            	nextSeg = segments.get(i+1);
	            }
	            
	            if ( prevSeg != null && nextSeg != null ) {
	                if (prevSeg.isSilence == false && nextSeg.isSilence == false) {
	                    
	                	prevSeg.size += currSeg.size + nextSeg.size;
	                	segments.remove(i);
	                	segments.remove(i);
	                	
	                    --i;
	                }
	            }
	        }
	    }
	    
	    //////////////////////////////////////////////////////////////////

	    
	    mSegmentList = segments.toArray(new SentenceSegment[0]);
			    
	}
	
	public void readFromFile( FileInputStream fileInputStream ) throws IOException, ClassNotFoundException
	{
     	ObjectInputStream segInputStream = new ObjectInputStream (fileInputStream);
     	mSegmentList = (SentenceSegment[])segInputStream.readObject();
	}
	
	public void writeToFile( FileOutputStream fileOutputStream ) throws IOException
	{
		ObjectOutputStream objectOutputStream = new ObjectOutputStream (fileOutputStream);
		objectOutputStream.writeObject(mSegmentList);
	}
	
	public SentenceSegment getCurrentSentenceByOffset( int currentOffset )
	{
		return getCurrentSentenceByIndex( getCurrentSentenceIndex( currentOffset, 0 ) );
	}
	
	public SentenceSegment getCurrentSentenceByIndex( int index )
	{
		if( index >= 0 && index < mSegmentList.length )
			return mSegmentList[index];
		
		return null;
	}
	
	public int getCurrentStartOffsetByIndex( int index )
	{
		return mSegmentList[index].startOffset;
	}
	
	public int getCurrentSentenceIndex( int currentOffset, int diverging )
	{
		for(int i = 0;i < mSegmentList.length; ++i )
		{
			if( currentOffset >= mSegmentList[i].startOffset + diverging &&
				currentOffset < mSegmentList[i].startOffset + mSegmentList[i].size + diverging )
				return i;
		}

		return 0;
	}
	
	public int getNextSentenceIndex( int currentOffset )
	{
		
		int currentIndex = getCurrentSentenceIndex( currentOffset, -10 );
		int nextIndex = currentIndex;
		
		if( mSegmentList[currentIndex].isSilence )
			nextIndex = currentIndex+1;
		else
			nextIndex = currentIndex+2;
		
		if( nextIndex >= mSegmentList.length )
			nextIndex = currentIndex;
		
		return nextIndex;		
	}
	
	public int getNextSentenceOffset( int currentOffset )
	{
		int nextIndex = getNextSentenceIndex(currentOffset);
		if( nextIndex >= mSegmentList.length )
			return currentOffset;
		
		return mSegmentList[nextIndex].startOffset;
	}
	
	public int getPrevSentenceIndex( int currentOffset )
	{
		int currentIndex = getCurrentSentenceIndex( currentOffset, 10 );
		int prevIndex = currentIndex;
		// startOffset으로부터 일정사이즈 이상이면 현재 문장 처음으로 이동하고.
		// startOffset에 가까우면 앞문장의 startOffset으로 이동한다.
		if( mSegmentList[currentIndex].isSilence )
			prevIndex = currentIndex-1;
		else
			prevIndex = currentIndex;

		if( prevIndex <= 0 )
			prevIndex = 0;

		return prevIndex;
		
	}
	
	public int getPrevSentenceOffset( int currentOffset )
	{
		int prevIndex = getPrevSentenceIndex(currentOffset);
		if( prevIndex <= 0 )
			return currentOffset;

		return mSegmentList[prevIndex].startOffset;
	}

	
	
}
