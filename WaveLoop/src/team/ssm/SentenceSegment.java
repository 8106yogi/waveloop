package team.ssm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class SentenceSegment implements Serializable {

	private static final long serialVersionUID = -6112331169298149437L;
	public boolean isSilence;
	public int startOffset;
	public int size;
	
	
	public SentenceSegment( boolean isSilence, int startOffset, int size )
	{
		this.isSilence = isSilence;
		this.startOffset = startOffset;
		this.size = size;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeBoolean(isSilence);
		out.writeInt(startOffset);
		out.writeInt(size);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		isSilence = in.readBoolean();
		startOffset = in.readInt();
		size = in.readInt();
	}
	
	public static ArrayList<SentenceSegment> makeSegments( int[] frameGains )
	{
		ArrayList<SentenceSegment> segments = new ArrayList<SentenceSegment>();
		
		int nextIndex = 0;
		int nCount = frameGains.length;//soundFile.getNumFrames();
		//int[] frameGains = soundFile.getFrameGains();
		for (int i = 0; i < nCount; ++i )
		{
			int value = frameGains[i];
			boolean isSilence = (2 > value)? true:false;
			if(isSilence)// 무음이면
			{
				for(int j = i+1; j < nCount; ++j )
				{
					if( 2 <= frameGains[j] )
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
	                if ( 2 > frameGains[j] )
	                {
	                    nextIndex = j;
	                    break;
	                }
	            }
			}
			
			if (i < nextIndex)
			{
				// 클래스를 만들어서 리스트에 넣어줌.
				
				SentenceSegment segment = new SentenceSegment(isSilence, i, nextIndex-i+1 );
				segments.add(segment);
				
				i = nextIndex;

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
	    
	    
		return segments;

	    //////////////////////////////////////////////////////////////////
		
	}
}
