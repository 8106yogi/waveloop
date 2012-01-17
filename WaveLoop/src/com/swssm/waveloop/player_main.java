package com.swssm.waveloop;

import java.io.*;
import java.util.*;

import com.swssm.waveloop.R;
import com.swssm.waveloop.audio.OSLESMediaPlayer;


import android.app.*;
import android.content.*;
import android.database.*;
import android.gesture.*;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.media.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class player_main extends Activity {
    
	static OSLESMediaPlayer mOSLESPlayer;
    //static MediaPlayer mPlayer;
    static ImageButton mPlayBtn; 
    //Button mPlay2Btn;
    ImageButton mNextBtn;
    ImageButton mPrevBtn;
    ImageButton mBookmarkBtn;
    ToggleButton mRepeatBtn;
    ImageButton mRepeatPrevBtn;
    ImageButton mRepeatNextBtn;
    
    long mMediaDBID;
    long mDataRowID;
    TextView mArtist;
    TextView mTitle;
    TextView mAlbum;
    static TextView mCurtime;
    TextView mTotaltime;
    
    SeekBar mProgress;
    boolean wasPlaying;
    String strMediaDBIndex;
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

    private int mLoopCount;
    private boolean mIsLoop;
    private int mLoopStartIndex;
    private int mLoopCenterIndex;
    private int mLoopFinishIndex;
    
    private int mLoopStartPos;
    private int mLoopFinishPos;
    
    
    private GestureLibrary mLibrary;
    
    private int mStartSegmentIndex;
    
    GestureOverlayView gestures;
    FrameLayout frame;
    ArrayList<Prediction> predictions;
    //MenuItem item;
    Toast mToast;
    
    
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

    public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
    public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";
    
	public void PauseAnotherMusic( Context context ) {
	    Intent i = new Intent(SERVICECMD);
	    i.putExtra(CMDNAME, CMDPAUSE);
	    context.sendBroadcast(i);
	}
	
	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra(CMDNAME);

            if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
            	
            	if( mOSLESPlayer.isPlaying() )
            		mOSLESPlayer.pause();
            	else
            		mOSLESPlayer.play();
            	
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
            	mOSLESPlayer.pause();
            } else if (CMDSTOP.equals(cmd)) {
            	mOSLESPlayer.pause();
            	mOSLESPlayer.seekTo(0);
            }
            
        }
    };
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        // 다른 뮤직 앱을 일시정지시키고 
        PauseAnotherMusic(this);
        
        /*
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        //commandFilter.addAction(NEXT_ACTION);
        //commandFilter.addAction(PREVIOUS_ACTION);
        registerReceiver(mIntentReceiver, commandFilter);
        */
        
        
        //this.setWallpaper(new Bitmap());

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        
        if( mOSLESPlayer == null )
        {
        	mOSLESPlayer = new OSLESMediaPlayer();
            mOSLESPlayer.createEngine();
        }
        
        //mPlayer = new MediaPlayer();
        
        mStartSegmentIndex = 0;
        
        
        
        
         // 웨이브폼 스크롤뷰 추가
         mWaveformView = (WaveformScrollView)findViewById(R.id.WaveformScrollView);
         mWaveformLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
         
         mWaveformView.setOSLESPlayer(mOSLESPlayer);
         //mWaveformView.setMediaPlayer(mPlayer);
         mWaveformView.setInnerLayout(mWaveformLayout);
         mWaveformView.setHorizontalScrollBarEnabled(false);
         mWaveformView.setSeekBar( (SeekBar)findViewById(R.id.progress) );
         mWaveformView.setOnTouchListener(mOnScrollViewTouchListener);
         
         mArtist = (TextView)findViewById(R.id.artist);
         mTitle = (TextView)findViewById(R.id.title);
         mAlbum = (TextView)findViewById(R.id.album);
         mCurtime = (TextView)findViewById(R.id.cur_time);
         mTotaltime = (TextView)findViewById(R.id.total_time);
         
         // 버튼들의 클릭 리스너 등록
         
         mPlayBtn = (ImageButton)findViewById(R.id.play);
         mPlayBtn.setOnClickListener(mClickPlay);
         mPlayBtn.setOnLongClickListener(mLongClickPlay);
        	
         //mPlayBtn.setEnabled(false);
         
         mNextBtn = (ImageButton)findViewById(R.id.next_sentence);
         mNextBtn.setOnClickListener(mClickNext);
         
         mPrevBtn = (ImageButton)findViewById(R.id.prev_sentence);
         mPrevBtn.setOnClickListener(mClickPrev);
         
         mBookmarkBtn = (ImageButton)findViewById(R.id.bookmark);
         mBookmarkBtn.setOnClickListener(mClickBookmark);
         
         mRepeatBtn = (ToggleButton)findViewById(R.id.repeat);
         //mRepeatBtn.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_menu_revert)); 
         mRepeatBtn.setOnClickListener(mClickRepeat);
         mRepeatBtn.setOnLongClickListener(mLongClickRepeat);
         
         mRepeatPrevBtn = (ImageButton)findViewById(R.id.prev_area);
         mRepeatPrevBtn.setOnClickListener(mClickRepeatPrev);
         mRepeatPrevBtn.setEnabled(false);
         
         mRepeatNextBtn = (ImageButton)findViewById(R.id.next_area);
         mRepeatNextBtn.setOnClickListener(mClickRepeatNext);
         mRepeatNextBtn.setEnabled(false);
         
         
         // 완료 리스너, 시크바 변경 리스너 등록
         //mPlayer.setOnCompletionListener(mOnComplete);
         //mPlayer.setOnSeekCompleteListener(mOnSeekComplete);
         //mPlayer.setOnCompletionListener(mOnCompletionListener);
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
        	
        	mStartSegmentIndex = intent.getIntExtra("start_segment_index", 0);
         	DbAdapter dba = new DbAdapter(getBaseContext());
         	dba.open();
         	Cursor cursor = dba.fetchBook(mDataRowID);
         	//cursor.moveToPosition((int) mDataRowID);
         	cursor.moveToPosition(0);
         	mFilepath = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_FILEPATH));
         	mWavePath = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_WAVEPATH));
         	strMediaDBIndex = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_MEDIA_DB_ID));
         	dba.close();
         	//mMediaDBID = Long.parseLong(strMediaDBIndex);
         	
         	setAudioInfoUIFromMediaDB(strMediaDBIndex);
         }

         
         // 첫 곡 읽기 및 준비
         if (LoadMedia() == false) {
             //Toast.makeText(this, "파일을 읽을 수 없습니다.", Toast.LENGTH_LONG).show();
        	 showToastMessage("파일을 읽을 수 없습니다.");
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
            		         			segment.startOffset+innerOffset + width, 250 );
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
                				mProgress.setMax( nFrameGainsCount*2 );
                				
                				addSideView();
                				
                				
                				mWaveformView.post(new Runnable() {
                				    public void run() {
                				    	final int startOffset = sentenceSegmentList.getCurrentStartOffsetByIndex(mStartSegmentIndex);
                				    	mWaveformView.scrollTo(startOffset*2, 0);
                				    	
                				    	//double position = (double)(startOffset*2)/(double)(mWaveformLayout.getMeasuredWidth()-mWaveformView.getMeasuredWidth())*(double)mPlayer.getDuration();
                				    	//mPlayer.seekTo((int)position);
                				    	
                				    	double position = (double)(startOffset*2)/(double)(mWaveformLayout.getMeasuredWidth()-mWaveformView.getMeasuredWidth())*(double)mOSLESPlayer.getDuration();
                				    	mOSLESPlayer.seekTo((int)position);
                				    	
                				    } 
                				});
                				
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
            				
            				if(mOSLESPlayer != null)
            				{
            					mOSLESPlayer.play();
            					mPlayBtn.setImageResource(R.drawable.pause_bkgnd);
            				}
            			}
            		});
        			
                 	
                 }
                
        		 
        	 }
         } ).start();

         //dialog.hide();
        //this.hf = (RealViewSwitcher)findViewById(R.id.ButtonScrollView);
        
        
        
    }
    
    private boolean isActivityBackground = false;  
    public void OnPause()
    {
    	isActivityBackground = true;
    }
    public void OnResume()
    {
    	isActivityBackground = false;
    }
    
    public void showToastMessage( String strMessage )
    {
    	//mToast.cancel();
    	mToast.setText(strMessage);
   	 	mToast.show();
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
		
	}
    

    
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0,1,0,"재생 옵션");
        menu.add(0,2,0,"구간반복 옵션");
        menu.add(0,3,0,"제스쳐").setIcon(R.drawable.ic_gesturebuilder);
        //menu.add(0,2,0,"번역").setIcon(android.R.drawable.ic_menu_preferences);

        return true;
    }
 
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
        case 1:
        	PlayerOptionDialog.ShowPlaybackOption(this);
        	return true;
        case 2:
        	PlayerOptionDialog.ShowRepeatOption(this);
        	return true;
        case 3:
        	showToastMessage("제스쳐모드");
        	
            gestures.setVisibility(View.VISIBLE);
            gestures.addOnGesturePerformedListener(mListener);
 
            return true;
        	        
        }
        return false;
    }
	
      		
    OnGesturePerformedListener mListener = new OnGesturePerformedListener() {
    	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    
    		predictions = mLibrary.recognize(gesture);
  		
  		// We want at least one prediction
  		if (predictions.size() != 0) {
  			Prediction prediction = predictions.get(0);
  			String name = prediction.name;
  			// We want at least some confidence in the result
  				if (prediction.score > 1.0) {
  				// Show the spell
	  				if(name.equals("play / pause")){
		  				  gesturePlay();
	  					
	  				}
	  				else if(name.equals("prev")){
	  					showToastMessage(prediction.name);
	  		        	gesturePrev();
	  				}
	  				else if(name.equals("next")){
	  					showToastMessage(prediction.name);
	  					gestureNext();
	  				}
	  				else if(name.equals("bookmark")){
	  					showToastMessage(prediction.name);
	  					gestureBookmark();
	  					
	  				}
	  				else if(name.equals("repeat")){
	  					gestureRepeat();
	  				}
	  				return;
  			}
  				return;
  		}
  		return;
    }
   };
    
	// 플레이어 액티비티에서 Back버튼을 눌렀을때의 동작처리 
    public void onBackPressed(){
    	if(gestures.getVisibility() == View.VISIBLE){
    		gestures.setVisibility(View.INVISIBLE);
    		gestures.removeOnGesturePerformedListener(mListener);
    		if( predictions != null )
    			predictions.clear();
    	}
    	else
    		finish();
    }

    // 액티비티 종료시 재생 강제 종료
    public void onDestroy() {
        super.onDestroy();
       
        if(mLoadingDialog != null){
    	    mLoadingDialog.dismiss();
    	    mLoadingDialog = null;
        }

    }

    // 항상 준비 상태여야 한다.
    boolean LoadMedia() {

    	if(mOSLESPlayer != null)
    	{
    		mOSLESPlayer.releaseAudioPlayer();
            mOSLESPlayer.createAudioPlayer(mFilepath);
            
            mOSLESPlayer.setRate( GlobalOptions.playbackSpeed );
            mOSLESPlayer.seekTo(0);
            mOSLESPlayer.pause();
    		
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
	   
	  	if(mOSLESPlayer.isPlaying() == false ) {
		   	 mWaveformView.forceStop();

		   	mOSLESPlayer.play();
		   	mPlayBtn.setImageResource(R.drawable.pause_bkgnd);
		   	showToastMessage("play");
		      
		        
	    } else {
	    	mOSLESPlayer.pause();
	    	mPlayBtn.setImageResource(R.drawable.play_bkgnd);
	        showToastMessage("pause");
	    }
	    
   }
   
	public void gesturePrev(){
	   processPrevSentence();
	}
   
	public void gestureNext(){
		processNextSentence();
	}
   
   public void gestureRepeat(){
	   
	   mRepeatBtn.toggle();
	   showToastMessage( "repeat " + ((mRepeatBtn.isChecked())?"on":"off") );
	   processRepeat();
	   
   }
   
   public void gestureBookmark(){
   	
       	// 이곳에서 문장노트에 추가를 한다.
       	
       	// 현재 문장을 가져온다.
       	SentenceSegment seg = sentenceSegmentList.getCurrentSentenceByOffset(mWaveformView.getScrollX()/2);
       	int segIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2, 0);
       	
       	if( seg.isSilence )
       	{
       		//Toast.makeText(player_main.this, "문장만 추가 가능합니다.", Toast.LENGTH_SHORT).show();
       		showToastMessage("문장만 추가 가능합니다.");
       		return;
       	}
       	
       	// 이미 있는 문장이면 추가하지 않는다.
       	DbAdapter dba = new DbAdapter(getBaseContext());
       	dba.open();
    	Cursor cur2 = dba.fetchBookFromMediaID2(strMediaDBIndex);
    	for(int i = 0; i < cur2.getCount(); ++i )
		{
			cur2.moveToPosition(i);
			//long sentence_mdb_id = cur2.getLong(cur2.getColumnIndex(DbAdapter.KEY_SENTENCE_MDB_ID));
			int start_id = cur2.getInt(cur2.getColumnIndex(DbAdapter.KEY_START_ID));
			//long end_id = cur2.getLong(cur2.getColumnIndex(DbAdapter.KEY_END_ID));
			
			if(segIndex == start_id){
				showToastMessage("이미 존재하는 문장입니다.");
				return;
			}
		}
       	
       	
       	dba.open();
    	dba.createBook2( strMediaDBIndex, 					// data row id
    			segIndex,									// start segment id
    			segIndex,									// end segment id
    			getTimeString(seg.startOffset/50), 			// start time
    			getTimeString((seg.startOffset+seg.size)/50),	// end time
    			"", 										// memo
    			0,											// star rate 
    			0xffffff63);								// color
    	
    	dba.close();
        	
        	
       	// 뷰를 업데이트.
              	
        	//Toast.makeText(player_main.this, "문장노트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
        	showToastMessage("문장노트에 추가되었습니다.");
   }
   
   
   
   // 재생 및 일시 정지
   Button.OnClickListener mClickPlay = new View.OnClickListener() {
         public void onClick(View v) {
             //if (mPlayer.isPlaying() == false) {
        	 if(mOSLESPlayer.isPlaying() == false ) {
            	 mWaveformView.forceStop();

            	 mOSLESPlayer.play();
            	 mPlayBtn.setImageResource(R.drawable.pause_bkgnd);
            	 
             } else {
            	 mOSLESPlayer.pause();
                 mPlayBtn.setImageResource(R.drawable.play_bkgnd);
             }
         }
    };
    
    
 // 재생 및 일시 정지
    Button.OnLongClickListener mLongClickPlay = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
        	PlayerOptionDialog.ShowPlaybackOption(player_main.this); 
            return true;
        }
        
        
     
    };
    
    Button.OnLongClickListener mLongClickRepeat = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
        	PlayerOptionDialog.ShowRepeatOption(player_main.this);
        	return true;
        }
    };
    
    private boolean isWithinRepeatArea( int index )
    {
    	if( index < mLoopStartIndex ||
    		index > mLoopFinishIndex ) {
    		return false;
    	}
    	
    	return true;
    }
    
    private void processNextSentence()
    {
    	int nextIndex = sentenceSegmentList.getNextSentenceIndex(mWaveformView.getScrollX()/2);
    	if(mIsLoop) {
    		if( false == isWithinRepeatArea(nextIndex) )
    			return;
    	}
    	
    	int offset = sentenceSegmentList.getCurrentStartOffsetByIndex(nextIndex);
    	int position = calcPositionByOffset(offset*2);
    	mOSLESPlayer.seekTo(position);
    }
    
    private void processPrevSentence()
    {
    	int nextIndex = sentenceSegmentList.getPrevSentenceIndex(mWaveformView.getScrollX()/2);
    	if(mIsLoop) {
    		if( false == isWithinRepeatArea(nextIndex) )
    			return;
    	}

    	int offset = sentenceSegmentList.getCurrentStartOffsetByIndex(nextIndex);
    	int position = calcPositionByOffset(offset*2);
    	mOSLESPlayer.seekTo(position);
    	
    }
    
    // 다음문장으로 이동.
    Button.OnClickListener mClickNext = new View.OnClickListener() {
        public void onClick(View v) {
        	processNextSentence();
        }
    };
    
    // 이전문장으로 이동.
    Button.OnClickListener mClickPrev = new View.OnClickListener() {
        public void onClick(View v) {
        	processPrevSentence();
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
        	int segIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2, 0);
        	int sametime=0;
        	
        	if( seg.isSilence )
        	{
        		//Toast.makeText(player_main.this, "문장만 추가 가능합니다.", Toast.LENGTH_SHORT).show();
        		showToastMessage("문장만 추가 가능합니다.");
        		return;
        	}
        	
        	// 이미 있는 문장이면 추가하지 않는다.
        	DbAdapter dba = new DbAdapter(getBaseContext());
         	dba.open();
        	Cursor cur2 = dba.fetchBookFromMediaID2(strMediaDBIndex);
        	for(int i = 0; i < cur2.getCount(); ++i )
    		{
    			cur2.moveToPosition(i);
    			//long sentence_mdb_id = cur2.getLong(cur2.getColumnIndex(DbAdapter.KEY_SENTENCE_MDB_ID));
    			int start_id = cur2.getInt(cur2.getColumnIndex(DbAdapter.KEY_START_ID));
    			//long end_id = cur2.getLong(cur2.getColumnIndex(DbAdapter.KEY_END_ID));
    			
    			if(segIndex == start_id){
    				showToastMessage("이미 존재하는 문장입니다.");
    				return;
    			}
    		}
         	
         	
        	dba.createBook2( strMediaDBIndex, 					// data row id
         			segIndex,									// start segment id
         			segIndex,									// end segment id
         			getTimeString(seg.startOffset/50), 			// start time
         			getTimeString((seg.startOffset+seg.size)/50),	// end time
         			"", 										// memo
         			0,											// star rate 
         			0xffffff63);								// color
         	
         	dba.close();
         	
         	
        	// 뷰를 업데이트.
         	
         	
         	//Toast.makeText(player_main.this, "문장노트에 추가되었습니다.", Toast.LENGTH_SHORT).show();
         	showToastMessage("문장노트에 추가되었습니다.");
         	
         	
        }
    };
    

    void processRepeat()
    {
    	boolean isLoop = mRepeatBtn.isChecked();
    	if(isLoop) {
    		// 문장인지 확인하고 
    		SentenceSegment seg = sentenceSegmentList.getCurrentSentenceByOffset(mWaveformView.getScrollX()/2);
    		if(seg.isSilence) {
    			//Toast.makeText(getApplicationContext(), "문장만 구간반복이 가능합니다.", Toast.LENGTH_SHORT).show();
    			showToastMessage("문장만 구간반복이 가능합니다.");
    			mRepeatBtn.setChecked(false);
    			return;
    		}
    	}
    	else{
    		//mRepeatBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.repeat_bkgnd)); 
    	}
    	
        mIsLoop = isLoop;
        mRepeatPrevBtn.setEnabled(mIsLoop);
        mRepeatNextBtn.setEnabled(mIsLoop);
 
        // 현재 위치 지정.
        if(mIsLoop) {
        	if(GlobalOptions.repeatCount == 0)
        		mLoopCount = Integer.MAX_VALUE;
        	else
        		mLoopCount = GlobalOptions.repeatCount;
        	
        	mLoopCenterIndex = sentenceSegmentList.getCurrentSentenceIndex(mWaveformView.getScrollX()/2, 0);
            mLoopFinishIndex = mLoopCenterIndex;
            mLoopStartIndex = mLoopCenterIndex;
            
            //mWaveformSemgnets[mLoopCenterIndex]
            redrawRepeatArea(true);
            
            updateRepeatPosition();
        }
        else
        {
        	redrawRepeatArea(false);
        	
        	mOSLESPlayer.setNoLoop();
        }
        
    }
    
    private void redrawRepeatArea( boolean isLoop )
    {
		for(int i = mLoopStartIndex; i < mLoopFinishIndex+1; ++i )
			mWaveformSemgnets[i].setBackgroundColor( (isLoop)?0xFFffff00:0x00000000 );
    }
    
    
    private int getSentencesTotalLength()
    {
    	// UI상의 실제 길이. 나중엔 sentenceSegmentList에서 미리 계산해두고 얻어와야 할듯.
    	return (mWaveformLayout.getMeasuredWidth()-mWaveformView.getMeasuredWidth());
    }
    
    private int calcPositionByOffset(int offset)
    {
    	// 픽셀 단위 오프셋으로부터 플레이어 seek position을 계산.
    	//return (int)((double)(offset)/(double)(getSentencesTotalLength())*(double)mPlayer.getDuration());
    	return (int)((double)(offset)/(double)(getSentencesTotalLength())*(double)mOSLESPlayer.getDuration());
    	
    }
    
    private void updateRepeatPosition()
    {
    	final int RepeatMargin = 150;// 0.15초.
 
    	int startOffset = sentenceSegmentList.getCurrentSentenceByIndex(mLoopStartIndex).startOffset;
    	int finishOffset = sentenceSegmentList.getCurrentSentenceByIndex(mLoopFinishIndex).startOffset + 
    						sentenceSegmentList.getCurrentSentenceByIndex(mLoopFinishIndex).size;
    	
    	mLoopStartPos = calcPositionByOffset(startOffset*2) - RepeatMargin;
    	mLoopFinishPos = calcPositionByOffset(finishOffset*2) + RepeatMargin;
    	
    	//mOSLESPlayer.setLoop(mLoopStartPos, mLoopFinishPos);
    	
    }
    
    
    
	// 구간반복.
	Button.OnClickListener mClickRepeat = new View.OnClickListener() {
        public void onClick(View v) {
        	processRepeat();
        }
	};
	
	// 구간반복 왼쪽 확장.
	Button.OnClickListener mClickRepeatPrev = new View.OnClickListener() {
        public void onClick(View v) {
        	
        	redrawRepeatArea(false);
        	
        	if(mLoopStartIndex == mLoopFinishIndex) {
        		mLoopStartIndex-=2;
        		if(mLoopStartIndex < 0)
        			mLoopStartIndex = 0;
        	} else if(mLoopCenterIndex > mLoopStartIndex) {
        		mLoopStartIndex-=2;
        		if(mLoopStartIndex < 0)
        			mLoopStartIndex = 0;
        	} else if(mLoopCenterIndex < mLoopFinishIndex) {
        		
        		mLoopFinishIndex-=2;
        		if(mLoopFinishIndex < mLoopCenterIndex)
        			mLoopFinishIndex = mLoopCenterIndex;
        	}
        	
        	redrawRepeatArea(true);
        	
        	updateRepeatPosition();
        }
	};
	
	// 구간반복 오른쪽 확장.
	Button.OnClickListener mClickRepeatNext = new View.OnClickListener() {
        public void onClick(View v) {
        	
        	redrawRepeatArea(false);
        	
        	if(mLoopStartIndex == mLoopFinishIndex) {
        		mLoopFinishIndex+=2;
        		if(mLoopFinishIndex >= mWaveformSemgnets.length)
        			mLoopFinishIndex = mWaveformSemgnets.length-1;
        	} else if(mLoopCenterIndex > mLoopStartIndex) {
        		mLoopStartIndex+=2;
        		if(mLoopStartIndex > mLoopCenterIndex)
        			mLoopStartIndex = mLoopCenterIndex;
        	} else if(mLoopCenterIndex < mLoopFinishIndex) {
        		
        		mLoopFinishIndex+=2;
        		if(mLoopFinishIndex >= mWaveformSemgnets.length)
        			mLoopFinishIndex = mWaveformSemgnets.length-1;
        	}
        	
        	redrawRepeatArea(true);
        	
        	updateRepeatPosition();
        }
	};
	
	
   
	// 재생 정지. 재시작을 위해 미리 준비해 놓는다.
	Button.OnClickListener mClickStop = new View.OnClickListener() {
        public void onClick(View v) {
             //mPlayer.stop();
        	mOSLESPlayer.stop();
             //mPlayBtn.setText("Play");
             //mProgress.setProgress(0);
             
        }
   };
   
   View.OnTouchListener mOnScrollViewTouchListener = new View.OnTouchListener() {
	   	public boolean onTouch(View v, MotionEvent event) {
	   		//if (mPlayer.isPlaying() == true) {
	   		if(mOSLESPlayer.isPlaying() == true) {
	   			mOSLESPlayer.pause();
	   			//mPlayer.pause();
            	mPlayBtn.setImageResource(R.drawable.play_bkgnd);
			}
			return false;
		}
   };

   

   // 에러 발생시 메시지 출력
   MediaPlayer.OnErrorListener mOnError = new MediaPlayer.OnErrorListener() {
         public boolean onError(MediaPlayer mp, int what, int extra) {
             String err = "OnError occured. what = " + what + " ,extra = " + extra;
             //Toast.makeText(player_main.this, err, Toast.LENGTH_LONG).show();
             showToastMessage(err);
             return false;
         }
   };

   // 위치 이동 완료 처리
   MediaPlayer.OnSeekCompleteListener mOnSeekComplete = new MediaPlayer.OnSeekCompleteListener() {
         public void onSeekComplete(MediaPlayer mp) {
             if (wasPlaying) {
                  //mPlayer.start();
            	 mOSLESPlayer.play();
             }
         }
   };
   
   MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
	   public void onCompletion(MediaPlayer arg0){
		   //int duration = mPlayer.getDuration() - mPlayer.getCurrentPosition();
		   
	   }
	   
   };

   

    //1초에 한 번씩 재생시간 갱신   
   Handler mPlaytimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(mOSLESPlayer == null) return;
            if(mOSLESPlayer.isPlaying()) {
        	//if (mPlayer == null) return;
            //if (mPlayer.isPlaying()) {
            	//int cur =  mPlayer.getCurrentPosition()/1000;
            	int cur =  mOSLESPlayer.getPosition()/1000;
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
    	//return ((double)mPlayer.getCurrentPosition() / (double)mPlayer.getDuration());
    	return ((double)mOSLESPlayer.getPosition() / (double)mOSLESPlayer.getDuration());
    }
    
    // 0.016초에 한번꼴로 재생 위치 갱신
    Handler mScrollHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		//if (mPlayer == null) return;
			//if (mPlayer.isPlaying()) {
    		if(mOSLESPlayer == null) return;
    		//if(mOSLESPlayer.isPlaying()) 
    		{
				//int duration = mPlayer.getDuration();
				//int width = mWaveformLayout.getMeasuredWidth();
				
    			// 액티비티가 활성화되어있을 때만 스크롤해주기.
    			//if(isActivityBackground == false)
    			{
    				
    				int newPos = (int)((double)(mWaveformLayout.getMeasuredWidth()-mWaveformView.getMeasuredWidth()) * getPlayerCurrentRate() );
    				int currentPos = mWaveformView.getScrollX();
    				int nextPos = currentPos;
    				nextPos += (newPos-currentPos)/2;
    				
    				mWaveformView.scrollTo(nextPos, 0);
    				//Log.d("handler", "scroll pos : " + pos );
    			}
    			
				//updateCurrentSegmentColor();
				if( mIsLoop == true )
				{
					int currentPosition = mOSLESPlayer.getPosition();
					if(currentPosition < mLoopStartPos || currentPosition > mLoopFinishPos )
					{
						mOSLESPlayer.seekTo(mLoopStartPos+1);
						
						if(mLoopCount != Integer.MAX_VALUE)
							mLoopCount--;
						if(mLoopCount < 0)
						{
							// 구간반복 종료.
							mRepeatBtn.toggle();
							processRepeat();
						}
					}
				}
              
			}
    		
    		mWaveformView.updateScroll();
			mScrollHandler.sendEmptyMessageDelayed(0,16);
		}
		
    };

    // 재생 위치 이동
    SeekBar.OnSeekBarChangeListener mOnSeek = new SeekBar.OnSeekBarChangeListener() {
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             if (fromUser) {
             }
         }

         public void onStartTrackingTouch(SeekBar seekBar) {
        	 wasPlaying = mOSLESPlayer.isPlaying();
             if (wasPlaying) {
            	 mOSLESPlayer.pause();
                 mPlayBtn.setImageResource(R.drawable.play_bkgnd);
             }
         }

         public void onStopTrackingTouch(SeekBar seekBar) {
         }
    };
}