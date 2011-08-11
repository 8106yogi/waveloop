package team.ssm;

import java.io.*;

import android.app.*;
import android.content.*;
import android.database.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

public class player_main extends Activity {
    
    static MediaPlayer mPlayer;
    Button mPlayBtn;
    Button mNextBtn;
    Button mPrevBtn;
    
    TextView mArtist;
    TextView mTitle;
    TextView mAlbum;
    SeekBar mProgress;
    boolean wasPlaying;
    String mFilepath;
    String mWavePath;
    WaveformScrollView mWaveformView;
    LinearLayout mWaveformLayout;
    //String strMediaDBIndex;
    WaveLoopActivity wla;
    
    SentenceSegmentList sentenceSegmentList;

    
    ProgressDialog mLoadingDialog;
    
    private Handler mLoadingHandler = new Handler();
    
   //private int index = 0;
    //private HFling hf = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);
        

        mPlayer = new MediaPlayer();
         
         // 웨이브폼 스크롤뷰 추가
         mWaveformView = (WaveformScrollView)findViewById(R.id.WaveformScrollView);
         mWaveformLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);
         
         mWaveformView.setOnTouchListener(mOnScrollViewTouchListener);
         mWaveformView.setSeekBar( (SeekBar)findViewById(R.id.progress) );
         mWaveformView.setMediaPlayer(mPlayer);
         
         //mWaveformView.dis
         //mWaveformView.setOn
         
         //mWaveformView.setOn
         //mWaveformView.fling(0);
         //mWaveformView.setSmoothScrollingEnabled(false);
         

         // 버튼들의 클릭 리스너 등록
         mArtist = (TextView)findViewById(R.id.artist);
         mTitle = (TextView)findViewById(R.id.title);
         mAlbum = (TextView)findViewById(R.id.album);
         mPlayBtn = (Button)findViewById(R.id.play);
         mPlayBtn.setOnClickListener(mClickPlay);
         
         mNextBtn = (Button)findViewById(R.id.next);
         mNextBtn.setOnClickListener(mClickNext);
         
         mPrevBtn = (Button)findViewById(R.id.prev);
         mPrevBtn.setOnClickListener(mClickPrev);
         
         // 완료 리스너, 시크바 변경 리스너 등록
         //mPlayer.setOnCompletionListener(mOnComplete);
         mPlayer.setOnSeekCompleteListener(mOnSeekComplete);
         mProgress = (SeekBar)findViewById(R.id.progress);
         //mProgress.setOnSeekBarChangeListener(mOnSeek);
         mProgressHandler.sendEmptyMessageDelayed(0,200);
         mScrollHandler.sendEmptyMessageDelayed(0,16);

         
         
         // 인텐트로 출력할 파일 결정하는 코드 아래로 옮김.
         Intent intent = getIntent();
         if (intent != null)
         {
         	long lDataIndex = intent.getIntExtra("오디오파일경로", 0);
         	DbAdapter dba = new DbAdapter(getBaseContext());
         	dba.open();
         	Cursor cursor = dba.fetchAllBooks();
         	cursor.moveToPosition((int) lDataIndex);
         	mFilepath = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_FILEPATH));
         	mWavePath = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_WAVEPATH));
         	String strMediaDBIndex = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_MEDIA_DB_ID));
         	dba.close();
         	
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
        	         	
        	         	
        	         	
        	         
        	         	sentenceSegmentList = new SentenceSegmentList();
        	         	sentenceSegmentList.readFromFile(fileInputStream);
        	         	int widthSum = 0;
        	         	int widthSum2 = 0;
    					SentenceSegment[] segs = sentenceSegmentList.getSegments();
    					for(int i = 0; i < segs.length; ++i )
    					{
    						SentenceSegment segment = segs[i];
    						
    						final LinearLayout ll = new LinearLayout(player_main.this);
    						ll.setOrientation(LinearLayout.HORIZONTAL);
    						ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) );
    						
    						widthSum2 += segment.size;
    						int count = 0;
            	         	for( int innerOffset = 0; innerOffset < segment.size; innerOffset+=1000 )
            	         	{
            	         		int width = ((innerOffset+1000)<segment.size)?1000:segment.size-innerOffset;
            	         		WaveformView waveformView = new WaveformView(player_main.this);
            		         	waveformView.setData(frameGains, segment.startOffset+innerOffset, 
            		         			segment.startOffset+innerOffset + width, 200 );
            		         	widthSum += width;
            		         	ll.addView( waveformView );
            		         	count++;
            	         	}
            	         	
            	         	mLoadingHandler.post( new Runnable()
                    		{
                    			public void run()
                    			{
                    				mWaveformLayout.addView(ll);
                    				//ViewTreeObserver ov =
                    				//	mWaveformLayout.getViewTreeObserver().;
                    				//mWaveformLayout.
                    			}
                    		});
        		         	
            	         	
    					}
    					
    					
    					
    					
    					
    					
        	         	fileInputStream.close();
                    	
        	         	
        				
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
       // this.hf = (HFling)findViewById(R.id.scrollview);

        //test();
        //initBtn();  


         
    }

	private void setAudioInfoUIFromMediaDB( String strMediaDBIndex ) {
		String selection = "( (_ID LIKE ?) )";
		String[] selectionArgs = { strMediaDBIndex };
		String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
		Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;
		
		Cursor curMedia = getContentResolver().query(uriExternal, null, selection, selectionArgs, sortOrder);
		if(curMedia.getCount() == 1)
		{
			curMedia.moveToPosition(0);
			String artist = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.ARTIST));
			String album = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.ALBUM));
			String title = curMedia.getString(curMedia.getColumnIndex(Audio.AudioColumns.TITLE));
			mArtist.setText(artist);
		    mTitle.setText(title);
		    mAlbum.setText(album);
		     
		}
	}
    
    /*
    private void test() {
    	
    	ImageView iv1 = new ImageView(this);
    	ImageView iv2 = new ImageView(this);
    	ImageView iv3 = new ImageView(this);
    	ImageView iv4 = new ImageView(this);
    	
    	
    	iv1.setImageResource(R.drawable.oo);
    	iv2.setImageResource(R.drawable.ww);
    	iv3.setImageResource(R.drawable.oo);
    	iv4.setImageResource(R.drawable.ww);
    	
    	iv1.setTag(new Integer(10));
    	iv2.setTag(new Integer(11));
    	iv3.setTag(new Integer(12));
    	iv4.setTag(new Integer(13));
    	
    	hf.addChildView(iv1);
    	hf.addChildView(iv2);
    	hf.addChildView(iv3);
    	hf.addChildView(iv4);
    	
    	//hf.setObserver(this);
    }
    
    private void xx() {
    	Log.i("CC", "ScrollTo");
    	boolean result = hf.ScrollToIndex(index);
    	Log.i("CC", "RESULT " + result);
    	index++;
    	if(2 < index) // error case
    		index = 0;
    	//hf.smoothScrollTo(hf.getScrollX() + 30, hf.getScrollY());
    }
    
    
	public void onClick(View v) {
		Log.i("CC", "CLICK");
		if() {
			xx();
		}
	}
	
    
	public void update(Observable observable, Object data) {
		View v = (View)data;
		Integer it = (Integer)v.getTag();
		Log.i("test", "IT " + it);
	}
    */

    // 액티비티 종료시 재생 강제 종료
    public void onDestroy() {
       super.onDestroy();
       /*
       if (mPlayer != null) {
         mPlayer.release();
         mPlayer = null;
       }
       */
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

   // 재생 및 일시 정지
   Button.OnClickListener mClickPlay = new View.OnClickListener() {
         public void onClick(View v) {
             if (mPlayer.isPlaying() == false) {
            	 mPlayer.start();
                 mPlayBtn.setText("Pause");
             } else {
                 mPlayer.pause();
                 mPlayBtn.setText("Play");
             }
         }
    };
    
    // 다음문장으로 이동.
    Button.OnClickListener mClickNext = new View.OnClickListener() {
        public void onClick(View v) {
        	
        	int offset = sentenceSegmentList.getNextSentenceOffset(mWaveformView.getScrollX()/2);
        	mWaveformView.smoothScrollTo(offset*2, 0);
        	
        	@SuppressWarnings("unused")
			int a = mWaveformLayout.getMeasuredWidth();
        	
        	
        	
        }
    };
    
 // 다음문장으로 이동.
    Button.OnClickListener mClickPrev = new View.OnClickListener() {
        public void onClick(View v) {
        	
        	int offset = sentenceSegmentList.getPrevSentenceOffset(mWaveformView.getScrollX()/2);
        	mWaveformView.smoothScrollTo(offset*2, 0);
        	
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
	   	@Override
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
                  int pos = (int)((double)(mWaveformLayout.getMeasuredWidth()) * getPlayerCurrentRate() );
                  mWaveformView.smoothScrollTo(pos, 0);
             }
             mScrollHandler.sendEmptyMessageDelayed(0,32);
         }
    };

    // 재생 위치 이동
    SeekBar.OnSeekBarChangeListener mOnSeek = new SeekBar.OnSeekBarChangeListener() {
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             if (fromUser) {
                  //mPlayer.seekTo(progress);
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