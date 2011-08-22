package team.ssm;

import java.io.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.database.*;
import android.gesture.*;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class player_main extends Activity implements OnGesturePerformedListener{
    
    static MediaPlayer mPlayer;
    Button mPlayBtn; 
    Button mPlay2Btn;
    Button mNextBtn;
    Button mPrevBtn;
    Button mBookmarkBtn;
    ToggleButton mRepeatBtn;
    
    long mMediaDBID;
    long mDataRowID;
    TextView mArtist;
    TextView mTitle;
    TextView mAlbum;
    TextView mCurtime;
    TextView mTotaltime;
    
    SeekBar mProgress;
    boolean wasPlaying;
    String mFilepath;
    String mWavePath;
    WaveformScrollView mWaveformView;
    LinearLayout mWaveformLayout;
    View[]	mWaveformSemgnets;
    //String strMediaDBIndex;
    WaveLoopActivity wla;

    HorizontalScrollView hv;

    
    SentenceSegmentList sentenceSegmentList;


    
    ProgressDialog mLoadingDialog;
    
    private Handler mLoadingHandler = new Handler();
    
    private int index = 0;
    private RealViewSwitcher hf = null;
    private HorizontalScrollView scrollView;
    private ViewGroup contentView;

    
    private boolean mIsLoop;
    private int mLoopStartIndex;
    private int mLoopFinishIndex;
    //private final Context mCtx; 
    private GestureLibrary mLibrary;
    GestureOverlayView gestures;
    //LinearLayout linear;
    FrameLayout frame;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mPlayer = new MediaPlayer();
         
         // 웨이브폼 스크롤뷰 추가
         mWaveformView = (WaveformScrollView)findViewById(R.id.WaveformScrollView);
         mWaveformLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
         
         mWaveformView.setOnTouchListener(mOnScrollViewTouchListener);
         mWaveformView.setSeekBar( (SeekBar)findViewById(R.id.progress) );
         mWaveformView.setMediaPlayer(mPlayer);
         mWaveformView.setInnerLayout(mWaveformLayout);
         
         
         //int www = mWaveformView.getLayoutParams().width;
         //mWaveformView.dis
         //mWaveformView.setOn
         
         //mWaveformView.setOn
         //mWaveformView.fling(0);\
         //mWaveformView.setSmoothScrollingEnabled(false);
         
         
         
         mArtist = (TextView)findViewById(R.id.artist);
         mTitle = (TextView)findViewById(R.id.title);
         mAlbum = (TextView)findViewById(R.id.album);
         mCurtime = (TextView)findViewById(R.id.cur_time);
         mTotaltime = (TextView)findViewById(R.id.total_time);
         
         // 버튼들의 클릭 리스너 등록
         
         mPlayBtn = (Button)findViewById(R.id.play);
         mPlayBtn.setOnClickListener(mClickPlay);
         
         mNextBtn = (Button)findViewById(R.id.next_sentence);
         mNextBtn.setOnClickListener(mClickNext);
         
         mPrevBtn = (Button)findViewById(R.id.prev_sentence);
         mPrevBtn.setOnClickListener(mClickPrev);
         
         mBookmarkBtn = (Button)findViewById(R.id.bookmark);
         mBookmarkBtn.setOnClickListener(mClickBookmark);
         
         mRepeatBtn = (ToggleButton)findViewById(R.id.repeat);
         mRepeatBtn.setOnClickListener(mClickRepeat);
         
         
         // 완료 리스너, 시크바 변경 리스너 등록
         //mPlayer.setOnCompletionListener(mOnComplete);
         mPlayer.setOnSeekCompleteListener(mOnSeekComplete);
         mPlayer.setOnCompletionListener(mOnCompletionListener);
         mProgress = (SeekBar)findViewById(R.id.progress);
         //mProgress.setOnSeekBarChangeListener(mOnSeek);
         //mProgressHandler.sendEmptyMessageDelayed(0,200);
         mPlaytimeHandler.sendEmptyMessageDelayed(0,1000);
         mScrollHandler.sendEmptyMessageDelayed(0,16);

         
         // 기정의된 제스쳐를 앱에서 반드시 로드하여 사용. GestureLibraries 클래스를 사용..
         mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
         if (!mLibrary.load()) {
             finish();
         }
         
         
         // 앱에서 제스쳐를 인식하기 위해서 XML layout에 GestureOverlayView을 추가. 제스쳐리스너 등록.
         
         gestures = (GestureOverlayView)findViewById(R.id.gestures);
         
         
         // 인텐트로 출력할 파일 결정하는 코드 아래로 옮김.
         Intent intent = getIntent();
         if (intent != null)
         {
        	mDataRowID = intent.getIntExtra("오디오파일경로", 0);
        	final int startSegmentIndex = intent.getIntExtra("start_segment_index", 0);
         	DbAdapter dba = new DbAdapter(getBaseContext());
         	dba.open();
         	Cursor cursor = dba.fetchBook(mDataRowID);
         	//cursor.moveToPosition((int) mDataRowID);
         	cursor.moveToPosition(0);
         	mFilepath = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_FILEPATH));
         	mWavePath = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_WAVEPATH));
         	String strMediaDBIndex = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_MEDIA_DB_ID));
         	dba.close();
         	mMediaDBID = Long.parseLong(strMediaDBIndex);
         	
         	setAudioInfoUIFromMediaDB(strMediaDBIndex);
         }

         
         // 첫 곡 읽기 및 준비
         if (LoadMedia() == false) {
             Toast.makeText(this, "파일을 읽을 수 없습니다.", Toast.LENGTH_LONG).show();
             finish();
         }
        
         
        new Thread(new Runnable()
        {
        	public void run()
        	{
        		
        		 // 파형 정보와 문장정보 파일을 읽어들이는 과정을 쓰레드로 처리 필요.
        		mLoadingHandler.post( new Runnable()
        		{
        			public void run()
        			{
        				mLoadingDialog = ProgressDialog.show( player_main.this, "", "파일을 읽고 있습니다...", true );
        			}
        		});
	         	
                 
                 
                 
        		// 두개의 파일을 잘 읽어들인다
                 File wavefile = new File(mWavePath);
                 if( wavefile.canRead() )
                 {
                	try {
        	         	FileInputStream fileInputStream = new FileInputStream(wavefile);
        	         	DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        	         	
        	         	int frameLength = dataInputStream.readInt();
        	         	int[] frameGains = new int[frameLength];
        	         	
        	         	ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        	         	frameGains = (int[]) objectInputStream.readObject();
        	         	final int nFrameGainsCount = frameGains.length;
        	         	
        	         	
        	         
        	         	sentenceSegmentList = new SentenceSegmentList();
        	         	sentenceSegmentList.readFromFile(fileInputStream);
        	         	
        	         	
        	         	
        	         	fileInputStream.close();
        	         	
        	         	//addSideView();
        	         	
        	         	
        	         	//int widthSum = 0;
        	         	//int widthSum2 = 0;
    					SentenceSegment[] segs = sentenceSegmentList.getSegments();
    					
    					mWaveformSemgnets = new View[segs.length];
    					
    					
    					for(int i = 0; i < segs.length; ++i )
    					{
    						SentenceSegment segment = segs[i];
    						
    						final LinearLayout ll = new LinearLayout(player_main.this);
    						ll.setOrientation(LinearLayout.HORIZONTAL);
    						ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) );
    						
    						//widthSum2 += segment.size;
    						int count = 0;
            	         	for( int innerOffset = 0; innerOffset < segment.size; innerOffset+=1000 )
            	         	{
            	         		int width = ((innerOffset+1000)<segment.size)?1000:segment.size-innerOffset;
            	         		WaveformView waveformView = new WaveformView(player_main.this);
            		         	waveformView.setData(frameGains, segment.startOffset+innerOffset, 
            		         			segment.startOffset+innerOffset + width, 200 );
            		         	//widthSum += width;
            		         	ll.addView( waveformView );
            		         	count++;
            	         	}
            	         	
            	         	mWaveformSemgnets[i] = ll;
            	         	
    					}
    					
    					
    					
        	         	mLoadingHandler.post( new Runnable() {
        	         		private void addSideView() {
                				WaveformSideView sideView = new WaveformSideView(player_main.this);
                				sideView.setSizeCallback(new WaveformSideView.SizeCallback() {
        							public int getWidth() {
        								return mWaveformView.getMeasuredWidth();
        							}
        							public int getHeight() {
        								return mWaveformView.getMeasuredHeight();
        							}
                				});
                				mWaveformLayout.addView(sideView);
                			}
        	         		
        	         		private LinearLayout addRulerView() {
        	         			LinearLayout rulerLayout = new LinearLayout(player_main.this);
                				rulerLayout.setOrientation(LinearLayout.HORIZONTAL);
                				rulerLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) );
                				
                				int length = nFrameGainsCount;
                				while(length > 0) {
                					int width = (length > 50)?50:length; 
                					rulerLayout.addView(new WaveformRulerView(player_main.this, width));
                					length -= 50;
                				}
                				
                				return rulerLayout;
        	         		}
        	         		
        	         		private LinearLayout addWaveformLinearView()
        	         		{
        	         			final LinearLayout innerLayout = new LinearLayout(player_main.this);
                				innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                				innerLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) );
        						
                				for(int i = 0; i < mWaveformSemgnets.length; ++i ){
                					innerLayout.addView(mWaveformSemgnets[i]);
                				}
                				return innerLayout;
        	         		}
        	         		
                			public void run() {
                				addSideView();
                				
                				final LinearLayout rulerLayout = addRulerView();
                				final LinearLayout innerLayout = addWaveformLinearView();

                				
                				final LinearLayout outerLayout = new LinearLayout(player_main.this);
                				outerLayout.setOrientation(LinearLayout.VERTICAL);
                				outerLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) );
        						
                				outerLayout.addView(rulerLayout);
                				outerLayout.addView(innerLayout);
                				mWaveformLayout.addView(outerLayout);
                				mWaveformView.setmWaveformSemgnets(mWaveformSemgnets);
                				mWaveformView.setSentenceSegmentList(sentenceSegmentList);
                				
                				addSideView();
                			}
                		});
    					
    					
    					//addSideView();
    					
    					
    					
        			} catch (StreamCorruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (ClassNotFoundException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
                 	
                 	
                 	// 파일 작성 완료.
        			
        			mLoadingHandler.post( new Runnable()
            		{
            			public void run()
            			{
            				mLoadingDialog.dismiss();
            				mLoadingDialog = null;
            			}
            		});
        			
                 	
                 }
                
        		 
        	 }
         } ).start();

         //dialog.hide();
        this.hf = (RealViewSwitcher)findViewById(R.id.ButtonScrollView);
        
        
        /*
        scrollView = (HorizontalScrollView) findViewById(R.id.buttonScrollView);	//가로스크롤뷰
        contentView = (ViewGroup) findViewById(R.id.button_scroll);	//가로스크롤뷰에 담긴 전체 리니어레이아웃
        scrollView.setOnTouchListener(new ScrollPager(scrollView, contentView));
        scrollView.post(new Runnable() {
	        public void run() {
	          //scrollView.scrollTo(0, contentView.getPaddingTop());
	          scrollView.scrollTo(contentView.getPaddingLeft(), 0);
	         }
        });
        */
    }
      
  
         
    

	private void setAudioInfoUIFromMediaDB( String strMediaDBIndex ) {
		String selection = "( (_ID LIKE ?) )";
		String[] selectionArgs = { strMediaDBIndex };
		String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
		Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
		int tMin = 0, tSec =0;
		
		Cursor curMedia = getContentResolver().query(uriExternal, null, selection, selectionArgs, sortOrder);
		if(curMedia.getCount() == 1)
		{
			curMedia.moveToPosition(0);
			String artist = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.ARTIST));
			String album = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.ALBUM));
			String title = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.TITLE));
			
			int tot_time = curMedia.getInt(curMedia.getColumnIndex(Audio.AudioColumns.DURATION));
			if(tot_time<=0){
				tot_time=0;
			}
			else{
				int tot_time_sec = (int)(double)curMedia.getInt(curMedia.getColumnIndex(Audio.AudioColumns.DURATION))/1000;
				tMin = tot_time_sec / 60;
				tSec = tot_time_sec % 60;
				
			} 
			
			//int cur = mPlayer.getCurrentPosition(); 
 			mArtist.setText(artist);
		    mTitle.setText(title);
		    mAlbum.setText(album);
		    //mCurtime.setText(cur);
		    String strTime = String.format("%02d:%02d" , tMin, tSec);
		    mTotaltime.setText(strTime);
		    //mTotaltime.setText(cur);
		}
		//mPlaytimeHandler.sendEmptyMessageDelayed(0,1000);
	}
    

  
    
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item=menu.add(0,1,0,"제스쳐");
        item.setIcon(R.drawable.ic_gesturebuilder);
        menu.add(0,2,0,"번역").setIcon(android.R.drawable.ic_menu_preferences);
        //menu.add(0,3,0,"전체삭제").setIcon(android.R.drawable.ic_menu_delete);
        
        return true;
    }
    
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
              Toast.makeText(this,"제스쳐모드",Toast.LENGTH_SHORT).show();
              //onGestureMode();
              //Intent ges_it = new Intent(player_main.this, gestureActivity.class);
              
              
              
              //GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
              
           
              //gestures = (GestureOverlayView)View.inflate(this, R.layout.gesture_overlay_view, null);
              //gestures = (GestureOverlayView)findViewById(R.id.gestures);
              //gestures.addOnGesturePerformedListener(this);
              //frame.addView(gestures);
              //frame.addView(linear);
              //setContentView(frame);
              
              /*
              gestures.setEnabled(true);
              gestures.setEventsInterceptionEnabled(true);
              gestures.setGestureVisible(true);
              gestures.addOnGesturePerformedListener(this);
       		*/
              gestures.setVisibility(View.VISIBLE);
              gestures.addOnGesturePerformedListener(this);
               return true;
        
        case 2:
              Toast.makeText(this,"Google Speech API...!!",Toast.LENGTH_SHORT).show();
              return true;
        	        
        }
        return false;
    }
	
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
  		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
  		
  		// We want at least one prediction
  		if (predictions.size() > 0) {
  			Prediction prediction = predictions.get(0);
  			// We want at least some confidence in the result
  				if (prediction.score > 1.0) {
  				// Show the spell
	  				if(prediction.name.equals("play / pause")){
	  					//Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
		  				  if (mPlayer.isPlaying() == false) {
		  					Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
		  				  }
		  				  else{
		  					Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();  
		  				  }
		  				  gesturePlay();
	  					
	  				}
	  				if(prediction.name.equals("prev")){
	  					Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
	  					//mp.pause();
	  		        	gesturePrev();
	  				}
	  				if(prediction.name.equals("next")){
	  					Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
	  					//mp.pause();
	  					gestureNext();
	  				}
	  				if(prediction.name.equals("bookmark")){
	  					Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
	  					gestureBookmark();
	  					
	  				}
	  				if(prediction.name.equals("repeat")){
	  					Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
	  					gestureRepeat();
	  				}
	  				
  			}
  		}
  	}
	
    public void onBackPressed(){
    	if(gestures.getVisibility() == View.VISIBLE){
    		gestures.setVisibility(View.INVISIBLE);
    	}
    	else
    		finish();
    }

    // 액티비티 종료시 재생 강제 종료
    public void onDestroy() {
       super.onDestroy();
       
       if (mPlayer != null) {
         mPlayer.release();
         mPlayer = null;
       }
       
       if(mLoadingDialog != null){
    	   mLoadingDialog.dismiss();
    	   mLoadingDialog = null;
       }
    	   
   }

   // 항상 준비 상태여야 한다.
    boolean LoadMedia() {
         try {
             //DB인덱스를 통해 파일경로를 인자로 넣어야함
        	 mPlayer.setDataSource(mFilepath);
         } catch (IllegalArgumentException e) {
             return false;
         } catch (IllegalStateException e) {
             return false;
         } catch (IOException e) {
             return false;
         }
         
         if (Prepare() == false) {
             return false;
         }
         mPlayer.start();
         mPlayer.seekTo( mPlayer.getDuration() );
         //mPlayer.seekTo(0);
         mPlayer.pause();

         return true;
   }
  
   boolean Prepare() {
         try {
             mPlayer.prepare();
         } catch (IllegalStateException e) {
             return false;
         } catch (IOException e) {
             return false;
         }
         return true;
   }

   
   // 해당 제스쳐(재생/일지정지, 이전문장, 다음문장, 북마크, 반복재생)가 입력될때 호출되는 메소드
   public String getTimeString( int sec )
	{
		//String.
		int minute = sec/60;
		int second = sec - minute*60;
		return minute + ":" + second;
	}
    
   public void gesturePlay(){
	    if (mPlayer.isPlaying() == false) {
		   	 mWaveformView.forceStop();
		   	 //mWaveformView.smoothScrollTo(mWaveformView.getScrollX(), mWaveformView.getScrollY());
		   	 mPlayer.start();
		   	 
		      mPlayBtn.setText("Pause");
		      // 여기서 스크롤뷰를 세팅하고.
		        
	    } else {
	        mPlayer.pause();
	        mPlayBtn.setText("Play");
	    }
   }
   
   public void gesturePrev(){
	   	mPlayer.pause();
	   	int offset = sentenceSegmentList.getPrevSentenceOffset(mWaveformView.getScrollX()/2);
	   	mWaveformView.scrollSmoothTo(offset*2, 0);
	   	mWaveformView.forceStop();
	   	mPlayer.start();
   }
   
   public void gestureNext(){
	   	mPlayer.pause();
	   	int offset = sentenceSegmentList.getNextSentenceOffset(mWaveformView.getScrollX()/2);
	   	mWaveformView.scrollSmoothTo(offset*2, 0);
	   	mWaveformView.forceStop();
	   	mPlayer.start();
   }
   
   public void gestureRepeat(){
	   //mPlayer.pause();
	   mIsLoop=!mIsLoop; //토글
	   mRepeatBtn.setChecked(mIsLoop);
		
	   // 현재 위치 지정.
       mLoopStartIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2);
       mLoopFinishIndex = mLoopStartIndex;
   }
   
   public void gestureBookmark(){
   	
       	// 이곳에서 문장노트에 추가를 한다.
       	
       	// 현재 문장을 가져온다.
       	SentenceSegment seg = sentenceSegmentList.getCurrentSentenceByOffset(mWaveformView.getScrollX()/2);
       	int segIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2);
       	
       	if( seg.isSilence )
       	{
       		Toast.makeText(player_main.this, "문장만 추가 가능합니다.", Toast.LENGTH_SHORT).show();
       		return;
       	}
       	
       	// 이미 있는 문장이면 추가하지 않는다.
       	DbAdapter dba = new DbAdapter(getBaseContext());
        	//dba.open();
       	//dba.close();
       	
       	// DB에 추가를 하고.
       	
       	
       	
       	dba.open();
        	dba.createBook2( mMediaDBID, 						// data row id
        			segIndex,									// start segment id
        			segIndex,									// end segment id
        			getTimeString(seg.startOffset/50), 			// start time
        			getTimeString((seg.startOffset+seg.size)/50),	// end time
        			"메모 없음", 										// memo
        			0,											// star rate 
        			Color.GRAY);								// color
        	
        	dba.close();
        	
        	
       	// 뷰를 업데이트.
              	
        	Toast.makeText(player_main.this, "문장노트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
       
   }
   
   
   
   // 재생 및 일시 정지
   Button.OnClickListener mClickPlay = new View.OnClickListener() {
         public void onClick(View v) {
             if (mPlayer.isPlaying() == false) {
            	 mWaveformView.forceStop();
            	 //mWaveformView.smoothScrollTo(mWaveformView.getScrollX(), mWaveformView.getScrollY());
            	 mPlayer.start();
            	 
                 mPlayBtn.setText("Pause");
                 // 여기서 스크롤뷰를 세팅하고.
                 
             } else {
                 mPlayer.pause();
                 mPlayBtn.setText("Play");
             }
         }
    };
    
    
    // 다음문장으로 이동.
    Button.OnClickListener mClickNext = new View.OnClickListener() {
        public void onClick(View v) {
        	mPlayer.pause();
        	int offset = sentenceSegmentList.getNextSentenceOffset(mWaveformView.getScrollX()/2);
        	mWaveformView.scrollSmoothTo(offset*2, 0);
        	
        }
    };
    
    // 이전문장으로 이동.
    Button.OnClickListener mClickPrev = new View.OnClickListener() {
        public void onClick(View v) {
        	mPlayer.pause();
        	int offset = sentenceSegmentList.getPrevSentenceOffset(mWaveformView.getScrollX()/2);
        	mWaveformView.scrollSmoothTo(offset*2, 0);
        	
        }
    };
    
    // 문장노트 추가.
    Button.OnClickListener mClickBookmark = new View.OnClickListener() {
    	
    	public String getTimeString( int sec )
    	{
    		//String.
    		int minute = sec/60;
    		int second = sec - minute*60;
    		return minute + ":" + second;
    	}
    	
        public void onClick(View v) {
        	// 이곳에서 문장노트에 추가를 한다.
        	
        	// 현재 문장을 가져온다.
        	SentenceSegment seg = sentenceSegmentList.getCurrentSentenceByOffset(mWaveformView.getScrollX()/2);
        	int segIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2);
        	
        	if( seg.isSilence )
        	{
        		Toast.makeText(player_main.this, "문장만 추가 가능합니다.", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	
        	// 이미 있는 문장이면 추가하지 않는다.
        	DbAdapter dba = new DbAdapter(getBaseContext());
         	//dba.open();
        	//dba.close();
        	
        	// DB에 추가를 하고.
        	
        	
        	
        	dba.open();
         	dba.createBook2( mMediaDBID, 						// data row id
         			segIndex,									// start segment id
         			segIndex,									// end segment id
         			getTimeString(seg.startOffset/50), 			// start time
         			getTimeString((seg.startOffset+seg.size)/50),	// end time
         			"메모 없음", 										// memo
         			0,											// star rate 
         			Color.GRAY);								// color
         	
         	dba.close();
         	
         	
        	// 뷰를 업데이트.
         	
         	
         	Toast.makeText(player_main.this, "문장노트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
         	
         	
        }
    };
    

    
	// 재생 정지. 재시작을 위해 미리 준비해 놓는다.
	Button.OnClickListener mClickRepeat = new View.OnClickListener() {
        public void onClick(View v) {
             mIsLoop = mRepeatBtn.isChecked();
             // 현재 위치 지정.
             mLoopStartIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2);
             mLoopFinishIndex = mLoopStartIndex;

        }
	};
   
	// 재생 정지. 재시작을 위해 미리 준비해 놓는다.
	Button.OnClickListener mClickStop = new View.OnClickListener() {
        public void onClick(View v) {
             mPlayer.stop();
             mPlayBtn.setText("Play");
             //mProgress.setProgress(0);
             Prepare();
        }
   };
   
   View.OnTouchListener mOnScrollViewTouchListener = new View.OnTouchListener() {
	   	public boolean onTouch(View v, MotionEvent event) {
	   		if (mPlayer.isPlaying() == true) {
				mPlayer.pause();
            	mPlayBtn.setText("Play");
			}
			return false;
		}
   };
   



  


   // 에러 발생시 메시지 출력
   MediaPlayer.OnErrorListener mOnError = new MediaPlayer.OnErrorListener() {
         public boolean onError(MediaPlayer mp, int what, int extra) {
             String err = "OnError occured. what = " + what + " ,extra = " + extra;
             Toast.makeText(player_main.this, err, Toast.LENGTH_LONG).show();
             return false;
         }
   };

   // 위치 이동 완료 처리
   MediaPlayer.OnSeekCompleteListener mOnSeekComplete = new MediaPlayer.OnSeekCompleteListener() {
         public void onSeekComplete(MediaPlayer mp) {
             if (wasPlaying) {
                  mPlayer.start();
             }
         }
   };
   
   MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
	   public void onCompletion(MediaPlayer arg0){
		   //int duration = mPlayer.getDuration() - mPlayer.getCurrentPosition();
		   
	   }
	   
   };

   
   /*
   // 0.2초에 한번꼴로 재생 위치 갱신
    Handler mProgressHandler = new Handler() {
         public void handleMessage(Message msg) {
             if (mPlayer == null) return;
             if (mPlayer.isPlaying()) {
                  //mProgress.setProgress(mPlayer.getCurrentPosition());
            	
             }
             mProgressHandler.sendEmptyMessageDelayed(0,200);
         }
    };
   */
    
    //1초에 한 번씩 재생시간 갱신
    Handler mPlaytimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            
        	if (mPlayer == null) return;
            if (mPlayer.isPlaying()) {
            	int cur =  mPlayer.getCurrentPosition()/1000;
           	 	int cMin = cur/60;
                int cSec = cur%60;
                String strTime = String.format("%02d:%02d" , cMin, cSec);
                mCurtime.setText(strTime);
            }
            mPlaytimeHandler.sendEmptyMessageDelayed(0,1000);
       }
   };
   
    
    public double getPlayerCurrentRate()
    {
    	return ((double)mPlayer.getCurrentPosition() / (double)mPlayer.getDuration());
    }
    
    // 0.016초에 한번꼴로 재생 위치 갱신
    Handler mScrollHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if (mPlayer == null) return;
			if (mPlayer.isPlaying()) {
				//int duration = mPlayer.getDuration();
				//int width = mWaveformLayout.getMeasuredWidth();
				int pos = (int)((double)(mWaveformLayout.getMeasuredWidth()-mWaveformView.getMeasuredWidth()) * getPlayerCurrentRate() );
				mWaveformView.scrollTo(pos, 0);
              
				//updateCurrentSegmentColor();
				if( mIsLoop == true )
				{
					int segIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2);
					if( segIndex < mLoopStartIndex-1 || segIndex > mLoopFinishIndex )
					{
						int startOffset = sentenceSegmentList.getCurrentSentenceByIndex(mLoopStartIndex).startOffset;
            		  
						//mWaveformView.scrollTo(startOffset*2, 0);
						//mPlayer.seekTo((int) ((int) startOffset*20));
						//Prepare();
						
						double position = (double)(startOffset*2)/(double)(mWaveformLayout.getMeasuredWidth()-mWaveformView.getMeasuredWidth())*(double)mPlayer.getDuration();
						mPlayer.seekTo( (int)position );
						Prepare();
					}
				}
              
			}
			mScrollHandler.sendEmptyMessageDelayed(0,30);
		}

		
    };

    // 재생 위치 이동
    SeekBar.OnSeekBarChangeListener mOnSeek = new SeekBar.OnSeekBarChangeListener() {
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             if (fromUser) {
                  //mPlayer.seekTo(progress);
            	 //int cur = mPlayer.getCurrentPosition()/1000;
                
            	
                 
             }
         }

         public void onStartTrackingTouch(SeekBar seekBar) {
             wasPlaying = mPlayer.isPlaying();
             if (wasPlaying) {
                  mPlayer.pause();
             }
         }

         public void onStopTrackingTouch(SeekBar seekBar) {
         }
    };
}