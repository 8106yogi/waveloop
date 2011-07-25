package team.ssm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import team.ssm.soundfile.CheapSoundFile;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

public class SoundLoadProgressDialog extends ProgressDialog {

	private long mLoadingStartTime;
	private long mLoadingLastUpdateTime;
	private boolean mLoadingKeepGoing;
	//private ProgressDialog mProgressDialog;
	private ArrayList<String> mAudioPaths;
	private DbAdapter mDba;
	private Context mContext;

	public SoundLoadProgressDialog(Context context)
	{
		super(context);
		
		mDba = new DbAdapter(context);
		
		mContext = context;
		
		// 프로그레스 다이얼로그 초기설정
		setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setTitle("오디오 파일을 가져옵니다.");
        setCancelable(true);
        setOnCancelListener(
            new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    mLoadingKeepGoing = false;
                }
            });
        
        setMax(100);

		
	}
	
	public void setAudioPaths( ArrayList<String> paths )
	{
		mAudioPaths = paths;
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	
	}
	
	public void beginThread()
	{
		mLoadingStartTime = System.currentTimeMillis();
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
            	
            	
            	for( final String path : mAudioPaths )// 선택된 오디오 파일을 돌아준다 
            	{
            		try {
            			// DB를 탐색해서 이미 있는 파일이라면 경고 메세지를 띄워주자
            			
            			
                    	// 절대 경로를 가지고 사운드 파일 인스턴스 생성. 
                    	CheapSoundFile mSoundFile = CheapSoundFile.create(path, listener);

                    	// 에러처리
                        if (mSoundFile == null) {
                        	dismiss();
                        	// 토스트 메세지 출력 
                            return;
                        }
                        
                        
                        
                        // 문장단위로 분할하는 작업을 해 주고
                        
                        // 두개의 파일을 잘 저장한 다음
                        String strFileName = "test.wfd";
                        File outputFile = mContext.getFileStreamPath(strFileName);
                        //File outputFile = mContext.getExternalFilesDir(null);
                        //outputFile.
                        // 폴더 생성해야 하는데!!!!
                        
                        
                        
                        boolean bCreateReturn = outputFile.createNewFile();
                        if( bCreateReturn && outputFile.canWrite() )
                        {
                        	final int[] frameGains = mSoundFile.getFrameGains();
                        	
                        	FileOutputStream outputFileStream = new FileOutputStream(outputFile);
                        	
                        	//outputFileStream // 오브젝트 사이즈 저장
                        	
                        	ObjectOutputStream obj_out = new ObjectOutputStream (outputFileStream);
                        	obj_out.writeObject(frameGains);
                        	
                        	// 파일 작성 완료.
                        	
                        }
                        
                        String wavePath = outputFile.getAbsolutePath();
                        
                        
                        // DB에 입력하자
                        mDba.open();
                        mDba.createBook(path, wavePath);
                        mDba.close();
                        
                        
                        
                        // 여기까지 에러처리 ㄷㄷ
                    } catch (final Exception e) {
                    	dismiss();
                    	
                    	// 토스트 메세지 출력 
                    	return;
                    }
            	}

            	dismiss();
                
            }
        }).start();

		
	}
	
	
	private void finishLoading()
	{
		// 다음 로딩할 파일이 있으면 하고
		// 없으면 걍 종료?
		// 파일로 저장해야지
		// DB에 저장도 해야지
	}
	
		
}
 





