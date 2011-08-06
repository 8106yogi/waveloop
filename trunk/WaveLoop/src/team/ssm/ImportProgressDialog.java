package team.ssm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import team.ssm.soundfile.CheapSoundFile;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Audio;

public class ImportProgressDialog extends ProgressDialog {

	//private long mLoadingStartTime;
	private long mLoadingLastUpdateTime;
	private boolean mLoadingKeepGoing;
	//private ProgressDialog mProgressDialog;
	private ArrayList<Integer> mAudioIDs;
	private DbAdapter mDba;
	private ContentResolver mContentResolver;
	private Context mContext;
	
	private Handler mHandler = new Handler();

	
	private FinishLoading mFinishLoading;

	public enum EFinishResult
	{
		eFR_OK,
		eFR_FILEERROR,
		eFR_FILENOTFOUNDERROR,
		eFR_SUSPEND,
		
		eFR_EXCEPTION,
	}
	
	public interface FinishLoading {
        public void finish( EFinishResult result );// dialog가 dismiss 될 때 호출되는 함수.
    }
	
	public void setFinishLoading( FinishLoading finishLoading )
	{
		mFinishLoading = finishLoading;
	}
	public void setContentResolver( ContentResolver cr )
	{
		mContentResolver = cr;
	}
	
	public ImportProgressDialog(Context context)
	{
		super(context);
		mDba = new DbAdapter(context);
		mContext = context;
		mFinishLoading = null;
		
		// 프로그레스 다이얼로그 초기설정
		setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setTitle("오디오 파일을 가져옵니다.");
        setMessage("");
        setCancelable(true);
        setOnCancelListener(
            new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    mLoadingKeepGoing = false;
                }
            });
        
        setMax(100);

		
	}
	
	public void setAudioIDs( ArrayList<Integer> iIDs )
	{
		mAudioIDs = iIDs;
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	
	}
	
	public void beginThread()
	{
		//mLoadingStartTime = System.currentTimeMillis();
        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        
        beginLoading();
        
	}
	
        
	private void beginLoading()
	{
		
		new Thread(new Runnable()
        {
			final CheapSoundFile.ProgressListener listener =
		        new CheapSoundFile.ProgressListener() {
					private double mFractionComplete;
		            public boolean reportProgress(double fractionComplete)
		            {
		            	mFractionComplete = fractionComplete;
		                long now = System.currentTimeMillis();
		                if (now - mLoadingLastUpdateTime > 100)
		                {
		                	setProgress( (int)(getMax() * mFractionComplete) );
		                    mLoadingLastUpdateTime = now;
		                }
		                return mLoadingKeepGoing;
		            }
		        };
		    
		    
		        
            public void run() 
            {
            	EFinishResult result = EFinishResult.eFR_OK; 
            	
            	for( final Integer id : mAudioIDs )// 선택된 오디오 파일을 돌아준다 
            	{
            		mHandler.post( new Runnable()
            		{
            			public void run()
            			{
            				setMessage( getTitleFromMediaDB(id) );
            			}
            		});
            		
            		//post()
            		//ImportProgressDialog.
            		//runOnUiThread();
            		//setMessage( getTitleFromMediaDB(id) );
            		try {
            			// DB를 탐색해서 이미 있는 파일이라면 경고 메세지를 띄워주자
            			
            			
                    	// 절대 경로를 가지고 사운드 파일 인스턴스 생성. 
            			String path = getAbsolutePathFromMediaDB(id);
                    	CheapSoundFile mSoundFile = CheapSoundFile.create(path, listener);

                    	// 에러처리
                        if (mSoundFile == null) {
                        	result = EFinishResult.eFR_FILEERROR;
                        	break;
                        }
                        
                        if(mLoadingKeepGoing == false) {
                        	result = EFinishResult.eFR_SUSPEND;
                        	break;
                        }
                        
                        setProgress( (int)(getMax()) );
                        
                        // 파형정보를 기반으로 문장을 나누는 작업을 하자.
                        //ArrayList<SentenceSegment> segs = 
                        //	SentenceSegment.makeSegments( mSoundFile.getFrameGains() );
                        
                        
                        
                        
                        // 두개의 파일을 잘 저장한 다음
                        String strFileName = id.toString() + ".wfd";
                        File outputFile = mContext.getFileStreamPath(strFileName);
                        //File outputFile = mContext.getExternalFilesDir(null);
                        if( outputFile.exists() == false )
        					outputFile.createNewFile();
        				if( outputFile.canWrite() )
        				{
	                        FileOutputStream fileOutputStream = new FileOutputStream(outputFile, false);
	                        
	                        saveWaveformFile(mSoundFile.getFrameGains(), fileOutputStream);
	                        
	                        // 문장정보 저장
	                        SentenceSegmentList ssList = new SentenceSegmentList();
	                        ssList.create( mSoundFile.getFrameGains() );
	                        ssList.writeToFile(fileOutputStream);
	                        
	                        
	                        
	                        
	                        String wavePath = outputFile.getAbsolutePath();
	                        
	                        // DB에 입력하자
	                        mDba.open();
	                        mDba.createBook(path, wavePath, id.toString());
	                        mDba.close();
	                        
        				}
                        
                        // 여기까지 에러처리 ㄷㄷ
                    } catch(final FileNotFoundException e) {
                    	result = EFinishResult.eFR_FILENOTFOUNDERROR;
                    	break;
            		} catch(final IOException e) {
            			result = EFinishResult.eFR_FILEERROR;
                    	break;
                    } catch (final Exception e) {
                    	//dismiss();
                    	// 토스트 메세지 출력 
                    	//Toast.makeText(mContext,"Exception! ", Toast.LENGTH_SHORT ).show();
                    	//finishLoading( EFinishResult.eFR_OK );
                    	
                    	result = EFinishResult.eFR_EXCEPTION;
                    	break;
                    }
            	}

            	
            	//Toast.makeText(mContext,"로드가 정상적으로 완료되었습니다.", Toast.LENGTH_LONG ).show();
            	finishLoading( result );
                
            }



			



			private void saveWaveformFile(final int[] frameGains,
					FileOutputStream fileOutputStream ) throws IOException, FileNotFoundException {
				
				DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
				
				int frameLength = frameGains.length;
				dataOutputStream.writeInt(frameLength);
				
				
				ObjectOutputStream objectOutputStream = new ObjectOutputStream (fileOutputStream);
				objectOutputStream.writeObject(frameGains);
				
				// 파일 작성 완료.
			}



			
			private String getStringFromMediaDB(final Integer id, final String columnName) {
    			// id 를 가지고 DB로부터 오디오파일 경로를 가져온다.
				
		    	Uri uriExternal = Audio.Media.EXTERNAL_CONTENT_URI;

		    	String selection = "( (_ID LIKE ?) )";
		    	String[] selectionArgs = { id.toString() };
		    	String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
		    	
		    	//Cursor cursorInt = mCr.query(uriInternal, null, selection, selectionArgs, sortOrder);
		    	Cursor cursor = mContentResolver.query(uriExternal, null, selection, selectionArgs, sortOrder);
		    	cursor.moveToPosition(0);
		    	String strResult = cursor.getString(cursor.getColumnIndex(columnName));

		    	return strResult;

			}
			
			private String getAbsolutePathFromMediaDB(final Integer id) {
    			// id 를 가지고 DB로부터 오디오파일 경로를 가져온다.
		    	return getStringFromMediaDB(id, Audio.AudioColumns.DATA);
			}
			
			private String getTitleFromMediaDB(final Integer id) {
 		    	return getStringFromMediaDB(id, Audio.AudioColumns.TITLE);
			}




			
        }).start();

		
	}
	
	
	private void finishLoading( EFinishResult result )
	{
		dismiss();
		
		if( null != mFinishLoading )
		{
			mFinishLoading.finish(result);
		}
		
		
	}
	
		
}
 




