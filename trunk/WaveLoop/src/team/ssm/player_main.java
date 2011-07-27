package team.ssm;

import java.io.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.media.*;
import android.os.*;
import android.provider.MediaStore.Audio;
import android.view.*;
import android.widget.*;

public class player_main extends Activity {
    ArrayList<String> mList;
    int mIdx;
    MediaPlayer mPlayer;
    Button mPlayBtn;
    TextView mFileName;
    SeekBar mProgress;
    boolean wasPlaying;
    String mFilepath;
    String mWavePath;
    HorizontalScrollView mWaveformView;
    LinearLayout mWaveformLayout;
    
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);
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
        	dba.close();
        	//filepath= intent.getStringExtra("오디오파일경로");
        }
         
         
         mList = new ArrayList<String>();
         mPlayer = new MediaPlayer();
         //String sdPath; 
         
         /*
         // SD 카드가 없을 시 에러 처리한다.
         String ext = Environment.getExternalStorageState();
         String sdPath;
         if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
             Toast.makeText(this, "SD 카드가 반드시 필요합니다.", Toast.LENGTH_LONG).show();
             finish();
             return;
         }
         */

         /*
         // SD 카드 루트의 MP3 파일 목록을 구한다.
         sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
         File sdRoot = new File(sdPath);
        
         FilenameFilter filter = new FilenameFilter() {
             public boolean accept(File dir, String name) {
                  return name.endsWith(".mp3");
             }
         };
        */
        
         /** 오디오파일 지원포맷: MP3/AAC/MP4/WAV **/
         
         //String[] mplist = sdRoot.list(filter);
         
        /*
         if (mplist.length == 0) {
             Toast.makeText(this, "재생할 파일이 없습니다.", Toast.LENGTH_LONG).show();
             finish();
             return;
         }
         for(String s : mplist) {
             mList.add(sdPath + "/" + s);
         }
         mIdx = 0;
		*/
         
         // 웨이브폼 스크롤뷰 추가
         mWaveformView = (HorizontalScrollView)findViewById(R.id.WaveformScrollView);
         mWaveformLayout = (LinearLayout)findViewById(R.id.WaveformScrollViewLayout);

         /* 
         ImageView iv = new ImageView(this);
         iv.setImageResource(R.drawable.ic_tab_option);
         mWaveformLayout.addView( iv );
         */
         
         // 버튼들의 클릭 리스너 등록
         mFileName = (TextView)findViewById(R.id.filename);
         mPlayBtn = (Button)findViewById(R.id.play);
         mPlayBtn.setOnClickListener(mClickPlay);
         findViewById(R.id.stop).setOnClickListener(mClickStop);
         findViewById(R.id.prev).setOnClickListener(mClickPrevNext);
         findViewById(R.id.next).setOnClickListener(mClickPrevNext);
         
         
         // 완료 리스너, 시크바 변경 리스너 등록
         mPlayer.setOnCompletionListener(mOnComplete);
         mPlayer.setOnSeekCompleteListener(mOnSeekComplete);
         mProgress = (SeekBar)findViewById(R.id.progress);
         mProgress.setOnSeekBarChangeListener(mOnSeek);
         mProgressHandler.sendEmptyMessageDelayed(0,200);
        
         // 첫 곡 읽기 및 준비
         /*
         if (LoadMedia(mIdx) == false) {
             Toast.makeText(this, "파일을 읽을 수 없습니다.", Toast.LENGTH_LONG).show();
             finish();
         }
         */
         
         // 임시로 특정 경로에 있는 파일을 읽어옴.
         
         
         // 두개의 파일을 잘 저장한 다음
         File wavefile = new File(mWavePath);
         //String strFileName = "5.wfd";
         //File wavefile = this.getFileStreamPath(strFileName);
         
         //File outputFile = mContext.getExternalFilesDir(null);
         //outputFile.
         // 폴더 생성해야 하는데!!!!
         
         
         if( wavefile.canRead() )
         {
        	try {
	         	FileInputStream fileInputStream = new FileInputStream(wavefile);
	         	DataInputStream dataInputStream = new DataInputStream(fileInputStream);
	         	
	         	int frameLength = dataInputStream.readInt();
	         	int[] frameGains = new int[frameLength];
	         	
	         	ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
	         	frameGains = (int[]) objectInputStream.readObject();
	         	
	         	for( int i = 0; i < frameLength; i+=500 )
	         	{
	         		WaveformView waveformView = new WaveformView(this);
		         	waveformView.setData(frameGains, i, ((i+500)<frameLength)?(i+500):frameLength
		         			, 300 );
		         	mWaveformLayout.addView( waveformView );
	         	}
	         	
	         	dataInputStream.close();
            	objectInputStream.close();
            	
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
         	
         }
         
         
    }
    
    

    // 액티비티 종료시 재생 강제 종료
    public void onDestroy() {
       super.onDestroy();
       if (mPlayer != null) {
         mPlayer.release();
         mPlayer = null;
       }
   }

   // 항상 준비 상태여야 한다.
    boolean LoadMedia(int idx) {
         try {
             //DB인덱스를 통해 파일경로를 인자로 넣어야함));
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
         mFileName.setText("파일 : " + mFilepath);
         mProgress.setMax(mPlayer.getDuration());
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

    // 재생 정지. 재시작을 위해 미리 준비해 놓는다.
   Button.OnClickListener mClickStop = new View.OnClickListener() {
         public void onClick(View v) {
             mPlayer.stop();
             mPlayBtn.setText("Play");
             mProgress.setProgress(0);
             Prepare();
         }
   };
  
   Button.OnClickListener mClickPrevNext = new View.OnClickListener() {
         public void onClick(View v) {
             boolean wasPlaying = mPlayer.isPlaying();
            
             if (v.getId() == R.id.prev) {
                  mIdx = (mIdx == 0 ? mList.size() - 1:mIdx - 1);
             } else {
                  mIdx = (mIdx == mList.size() - 1 ? 0:mIdx + 1);
             }
            
             mPlayer.reset();
             LoadMedia(mIdx);

             // 이전에 재생중이었으면 다음 곡 바로 재생
             if (wasPlaying) {
                  mPlayer.start();
                  mPlayBtn.setText("Pause");
             }
         }
   };

   // 재생 완료되면 다음곡으로
   MediaPlayer.OnCompletionListener mOnComplete = new MediaPlayer.OnCompletionListener() {
         public void onCompletion(MediaPlayer arg0) {
             mIdx = (mIdx == mList.size() - 1 ? 0:mIdx + 1);
             mPlayer.reset();
             LoadMedia(mIdx);
             mPlayer.start();
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
                  mProgress.setProgress(mPlayer.getCurrentPosition());
             }
             mProgressHandler.sendEmptyMessageDelayed(0,200);
         }
    };

    // 재생 위치 이동
    SeekBar.OnSeekBarChangeListener mOnSeek = new SeekBar.OnSeekBarChangeListener() {
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             if (fromUser) {
                  mPlayer.seekTo(progress);
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