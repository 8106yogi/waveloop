



#include <jni.h>


// for native audio
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include <assert.h>
#include <sys/types.h>


// engine interfaces
static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;


// URI player interfaces
static SLObjectItf uriPlayerObject = NULL;
static SLPlayItf uriPlayerPlay;
static SLSeekItf uriPlayerSeek;
static SLPlaybackRateItf uriPlaybackRate;


// output mix interfaces
static SLObjectItf outputMixObject = NULL;




// create the engine and output mix objects
JNIEXPORT void Java_com_swssm_waveloop_audio_OSLESMediaPlayer_createEngine(JNIEnv* env, jclass clazz)
{
    SLresult result;
	
    // create engine
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    assert(SL_RESULT_SUCCESS == result);
	
    // realize the engine
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);
	
    // get the engine interface, which is needed in order to create other objects
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
    assert(SL_RESULT_SUCCESS == result);
	
    // create output mix, with environmental reverb specified as a non-required interface
    const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean req[1] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
    assert(SL_RESULT_SUCCESS == result);
	
    // realize the output mix
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    assert(SL_RESULT_SUCCESS == result);
	
}



// create URI audio player
JNIEXPORT jboolean Java_com_swssm_waveloop_audio_OSLESMediaPlayer_createAudioPlayer(JNIEnv* env, jclass clazz,
        jstring uri)
{
    SLresult result;

    // convert Java string to UTF-8
    const jbyte *utf8 = (*env)->GetStringUTFChars(env, uri, NULL);
    assert(NULL != utf8);

    // configure audio source
    // (requires the INTERNET permission depending on the uri parameter)
    SLDataLocator_URI loc_uri = {SL_DATALOCATOR_URI, (SLchar *) utf8};
    SLDataFormat_MIME format_mime = {SL_DATAFORMAT_MIME, NULL, SL_CONTAINERTYPE_UNSPECIFIED};
    SLDataSource audioSrc = {&loc_uri, &format_mime};

    // configure audio sink
    SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&loc_outmix, NULL};

    // create audio player
    const SLInterfaceID ids[2] = {SL_IID_SEEK, SL_IID_PLAYBACKRATE};
    const SLboolean req[2] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
    result = (*engineEngine)->CreateAudioPlayer(engineEngine, &uriPlayerObject, &audioSrc,
            &audioSnk, 2, ids, req);
    // note that an invalid URI is not detected here, but during prepare/prefetch on Android,
    // or possibly during Realize on other platforms
    assert(SL_RESULT_SUCCESS == result);

    // release the Java string and UTF-8
    (*env)->ReleaseStringUTFChars(env, uri, utf8);

    // realize the player
    result = (*uriPlayerObject)->Realize(uriPlayerObject, SL_BOOLEAN_FALSE);
    // this will always succeed on Android, but we check result for portability to other platforms
    if (SL_RESULT_SUCCESS != result) {
        (*uriPlayerObject)->Destroy(uriPlayerObject);
        uriPlayerObject = NULL;
        return JNI_FALSE;
    }

    // get the play interface
    result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_PLAY, &uriPlayerPlay);
    assert(SL_RESULT_SUCCESS == result);

    // get the seek interface
    result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_SEEK, &uriPlayerSeek);
    assert(SL_RESULT_SUCCESS == result);

	// get playback rate interface
	result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_PLAYBACKRATE, &uriPlaybackRate);
	assert(SL_RESULT_SUCCESS == result);
    
    /*
    SLpermille minRate, maxRate, stepSize, rate = 1000;
    SLuint32 capa;
    (*uriPlaybackRate)->GetRateRange(uriPlaybackRate, 0, &minRate, &maxRate, &stepSize, &capa);
    
    (*uriPlaybackRate)->SetRate(uriPlaybackRate, minRate);
    */
    return JNI_TRUE;
}

void setPlayState( SLuint32 state )
{
	SLresult result;
	
    // make sure the URI audio player was created
    if (NULL != uriPlayerPlay) {
		
        // set the player's state
        result = (*uriPlayerPlay)->SetPlayState(uriPlayerPlay, state);
        assert(SL_RESULT_SUCCESS == result);
    }
	
}


SLuint32 getPlayState()
{
	SLresult result;
	
    // make sure the URI audio player was created
    if (NULL != uriPlayerPlay) {
		
        SLuint32 state;
        result = (*uriPlayerPlay)->GetPlayState(uriPlayerPlay, &state);
        assert(SL_RESULT_SUCCESS == result);
        
        return state;
    }
    
    return 0;
	
}

// play
JNIEXPORT void Java_com_swssm_waveloop_audio_OSLESMediaPlayer_play(JNIEnv* env, jclass clazz )
{
    setPlayState(SL_PLAYSTATE_PLAYING);
}

// stop
JNIEXPORT void Java_com_swssm_waveloop_audio_OSLESMediaPlayer_stop(JNIEnv* env, jclass clazz )
{
    setPlayState(SL_PLAYSTATE_STOPPED);
}

// pause
JNIEXPORT void Java_com_swssm_waveloop_audio_OSLESMediaPlayer_pause(JNIEnv* env, jclass clazz )
{
	setPlayState(SL_PLAYSTATE_PAUSED);
}

// pause
JNIEXPORT jboolean Java_com_swssm_waveloop_audio_OSLESMediaPlayer_isPlaying(JNIEnv* env, jclass clazz )
{
	return (getPlayState() == SL_PLAYSTATE_PLAYING);
}


// get duration
JNIEXPORT void Java_com_swssm_waveloop_audio_OSLESMediaPlayer_seekTo(JNIEnv* env, jclass clazz, jint position )
{
     if (NULL != uriPlayerPlay) {

		SLresult result;

        result = (*uriPlayerSeek)->SetPosition(uriPlayerSeek, position, SL_SEEKMODE_FAST);
        assert(SL_RESULT_SUCCESS == result);
    }

}


// get duration
JNIEXPORT jint Java_com_swssm_waveloop_audio_OSLESMediaPlayer_getDuration(JNIEnv* env, jclass clazz )
{
     if (NULL != uriPlayerPlay) {

		SLresult result;
		
        SLmillisecond msec;
        result = (*uriPlayerPlay)->GetDuration(uriPlayerPlay, &msec);
        assert(SL_RESULT_SUCCESS == result);
        
        return msec;
    }
    
	return 0.0f;
}

// get current position
JNIEXPORT jint Java_com_swssm_waveloop_audio_OSLESMediaPlayer_getPosition(JNIEnv* env, jclass clazz )
{
     if (NULL != uriPlayerPlay) {

		SLresult result;
		
        SLmillisecond msec;
        result = (*uriPlayerPlay)->GetPosition(uriPlayerPlay, &msec);
        assert(SL_RESULT_SUCCESS == result);
        
        return msec;
    }
    
	return 0.0f;
}



// create URI audio player
JNIEXPORT jboolean Java_com_swssm_waveloop_audio_OSLESMediaPlayer_setLoop(JNIEnv* env, jclass clazz,
																			  jint startPos, jint endPos )
{
	SLresult result;
	
    result = (*uriPlayerSeek)->SetLoop(uriPlayerSeek, SL_BOOLEAN_TRUE, startPos, endPos);
    assert(SL_RESULT_SUCCESS == result);

	return JNI_TRUE;
}


// create URI audio player
JNIEXPORT jboolean Java_com_swssm_waveloop_audio_OSLESMediaPlayer_setNoLoop(JNIEnv* env, jclass clazz )
{
	SLresult result;
	if (NULL != uriPlayerSeek) {
	// enable whole file looping
    result = (*uriPlayerSeek)->SetLoop(uriPlayerSeek, SL_BOOLEAN_TRUE, 0, SL_TIME_UNKNOWN );
    assert(SL_RESULT_SUCCESS == result);
	
	}
	return JNI_TRUE;
}






















