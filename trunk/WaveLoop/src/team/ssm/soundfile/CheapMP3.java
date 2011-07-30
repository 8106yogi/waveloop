/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package team.ssm.soundfile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//import javazoom.jlme.decoder.*;

/**
 * CheapMP3 represents an MP3 file by doing a "cheap" scan of the file,
 * parsing the frame headers only and getting an extremely rough estimate
 * of the volume level of each frame.
 * 
 * TODO: Useful unit tests might be to look for sync in various places:
 * FF FA
 * FF FB
 * 00 FF FA
 * FF FF FA
 * ([ 00 ] * 12) FF FA
 * ([ 00 ] * 13) FF FA
 */
public class CheapMP3 extends CheapSoundFile {
    public static Factory getFactory() {
        return new Factory() {
            public CheapSoundFile create() {
                return new CheapMP3();
            }
            public String[] getSupportedExtensions() {
                return new String[] { "mp3" };
            }
        };
    }

    // Member variables representing frame data
    private int mNumFrames;
    private int[] mFrameOffsets;
    private int[] mFrameLens;
    private int[] mFrameGains;
    private int mFileSize;
    private int mAvgBitRate;
    private int mGlobalSampleRate;
    private int mGlobalChannels;

    // Member variables used during initialization
    private int mMaxFrames;
    private int mBitrateSum;
    private int mMinGain;
    private int mMaxGain;

    public CheapMP3() {
    }

    public int getNumFrames() {
        return mNumFrames;
    }

    public int[] getFrameOffsets() {
        return mFrameOffsets;
    }

    public int getSamplesPerFrame() {
        return 1152;
    }

    public int[] getFrameLens() {
        return mFrameLens;
    }

    public int[] getFrameGains() {
        return mFrameGains;
    }

    public int getFileSizeBytes() {
        return mFileSize;        
    }

    public int getAvgBitrateKbps() {
        return mAvgBitRate;
    }

    public int getSampleRate() {
        return mGlobalSampleRate;
    }

    public int getChannels() {
        return mGlobalChannels;
    }

    public String getFiletype() {
        return "MP3";
    }

    /**
     * MP3 supports seeking into the middle of the file, no header needed,
     * so this method is supported to hear exactly what a "cut" of the file
     * sounds like without needing to actually save a file to disk first.
     */
    public int getSeekableFrameOffset(int frame) {
        if (frame <= 0) {
            return 0;
        } else if (frame >= mNumFrames) {
            return mFileSize;
        } else {
            return mFrameOffsets[frame];
        }
    }

    public void ReadFile(File inputFile)
            throws java.io.FileNotFoundException,
            java.io.IOException {
        super.ReadFile(inputFile);
        
        
        
        
        
        ArrayList<Integer> array = new ArrayList<Integer>();
        
        NativeMP3Decoder decoder = new NativeMP3Decoder( inputFile.getAbsolutePath() ); 
        while( true )
        {
        	float[] samples = new float[882];//44100hz 가정하에.44100/50으로 초당 50개 
        	int size = decoder.readSamples( samples );
        	if(size <= 0)
        		break;
        	
        	float sum = 0;
        	for(int i = 0; i < size; ++i )
        	{
        		sum += java.lang.Math.abs(samples[i]);
        	}
        	int result = (int)((sum / size) * 500.f);
        	array.add(result);
        	
        	float fProgress = decoder.getProgress();
        	
        	if (mProgressListener != null) {
                boolean keepGoing = mProgressListener.reportProgress(fProgress);
                if (!keepGoing) {
                    break;
                }
            }
        	
        }
            
        mFrameGains = new int[array.size()];
        for(int i = 0; i < array.size(); ++i )
        {
        	mFrameGains[i] = array.get(i);
        }
        
        
        
        
        
        
        
        
        
        /*
        int startMs = 0;
        ArrayList<Integer> list = new ArrayList<Integer>();
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
        float totalMs = 0;
        boolean seeking = true;
       
        File file = inputFile;//new File(path);
        
        //File file = new File(path);
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file), 8 * 1024 * 1024);
        try {
          Bitstream bitstream = new Bitstream(inputStream);
          Decoder decoder = new Decoder();
       
          int nGainMax = 0;
          int nCount = 0;
          boolean done = false;
          while (! done) {
            Header frameHeader = bitstream.readFrame();
            if (frameHeader == null) {
              done = true;
            } else {
              totalMs += frameHeader.ms_per_frame();
       
              if (totalMs >= startMs) {
                seeking = false;
              }
       
              if (! seeking) {
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
       
                //if (output.getSampleFrequency() != 44100
                //    || output.getChannelCount() != 2) {
                //  throw new com.mindtherobot.libs.mpg.DecoderException("mono or non-44100 MP3 not supported");
                //}
       
                int gainPerFrame = output.getSampleFrequency() /50;
                short[] pcm = output.getBuffer();
                
                for (short s : pcm) {
                	//if(nCount%4 == 0)
                	{
                		int val = (int) (java.lang.Math.abs(s)/200.f); 
                        if(nGainMax < val)
                    	    nGainMax = val;
                	}
                	++nCount;
                    if(nCount > gainPerFrame)
                    {
                    	list.add(nGainMax);
                    	nGainMax = 0;
                    	nCount = 0;
                    	
                    }
                }
               //for (short s : pcm) {
               //   outStream.write(s & 0xff);
               //   outStream.write((s >> 8 ) & 0xff);
               // }
       //       }
       //
       //       if (totalMs >= (startMs + maxMs)) {
       //         done = true;
       //       }
              
            }
            bitstream.closeFrame();
          }
          
        //list;
          mNumFrames = list.size();
          mFrameGains = new int[mNumFrames];
          for( int i = 0; i< mNumFrames; ++i )
          {
        	  mFrameGains[i] = list.get(i);
          }
       
          //return outStream.toByteArray();
        } catch (BitstreamException e) {
          //throw new IOException("Bitstream error: " + e);
        } catch (DecoderException e) {
          //Log.w(TAG, "Decoder error", e);
          //throw new com.mindtherobot.libs.mpg.DecoderException(e);
        } finally {
          //IOUtils.safeClose(inputStream);
        }
        
        
         */
        
        
        
        /*
        mNumFrames = 0;
        mMaxFrames = 64;  // This will grow as needed
        mFrameOffsets = new int[mMaxFrames];
        mFrameLens = new int[mMaxFrames];
        mFrameGains = new int[mMaxFrames];
        mBitrateSum = 0;
        mMinGain = 255;
        mMaxGain = 0;

        // No need to handle filesizes larger than can fit in a 32-bit int
        mFileSize = (int)mInputFile.length();

        FileInputStream stream = new FileInputStream(mInputFile);

        int pos = 0;
        int offset = 0;
        byte[] buffer = new byte[12];
        while (pos < mFileSize - 12) {
            // Read 12 bytes at a time and look for a sync code (0xFF)
            while (offset < 12) {
                offset += stream.read(buffer, offset, 12 - offset);
            }
            int bufferOffset = 0;
            while (bufferOffset < 12 &&
                    buffer[bufferOffset] != -1)
                bufferOffset++;

            if (mProgressListener != null) {
                boolean keepGoing = mProgressListener.reportProgress(
                    pos * 1.0 / mFileSize);
                if (!keepGoing) {
                    break;
                }
            }

            if (bufferOffset > 0) {
                // We didn't find a sync code (0xFF) at position 0;
                // shift the buffer over and try again
                for (int i = 0; i < 12 - bufferOffset; i++)
                    buffer[i] = buffer[bufferOffset + i];
                pos += bufferOffset;
                offset = 12 - bufferOffset;
                continue;
            }

            // Check for MPEG 1 Layer III or MPEG 2 Layer III codes
            int mpgVersion = 0;
            if (buffer[1] == -6 || buffer[1] == -5) {
                mpgVersion = 1;
            } else if (buffer[1] == -14 || buffer[1] == -13) {
                mpgVersion = 2;
            } else {
                bufferOffset = 1;
                for (int i = 0; i < 12 - bufferOffset; i++)
                    buffer[i] = buffer[bufferOffset + i];
                pos += bufferOffset;
                offset = 12 - bufferOffset;
                continue;
            }

            // The third byte has the bitrate and samplerate
            int bitRate;
            int sampleRate;
            if (mpgVersion == 1) {
                // MPEG 1 Layer III
                bitRate = BITRATES_MPEG1_L3[(buffer[2] & 0xF0) >> 4];
                sampleRate = SAMPLERATES_MPEG1_L3[(buffer[2] & 0x0C) >> 2];
            } else {
                // MPEG 2 Layer III
                bitRate = BITRATES_MPEG2_L3[(buffer[2] & 0xF0) >> 4];
                sampleRate = SAMPLERATES_MPEG2_L3[(buffer[2] & 0x0C) >> 2];
            }

            if (bitRate == 0 || sampleRate == 0) {
                bufferOffset = 2;
                for (int i = 0; i < 12 - bufferOffset; i++)
                    buffer[i] = buffer[bufferOffset + i];
                pos += bufferOffset;
                offset = 12 - bufferOffset;
                continue;
            }

            // From here on we assume the frame is good
            mGlobalSampleRate = sampleRate;
            int padding = (buffer[2] & 2) >> 1;
            int frameLen = 144 * bitRate * 1000 / sampleRate + padding;

            int gain;
            if ((buffer[3] & 0xC0) == 0xC0) {
                // 1 channel
                mGlobalChannels = 1;
                if (mpgVersion == 1) {
                    gain = ((buffer[10] & 0x01) << 7) +
                        ((buffer[11] & 0xFE) >> 1);
                } else {
                    gain = ((buffer[9] & 0x03) << 6) +
                    ((buffer[10] & 0xFC) >> 2);
                }
            } else {
                // 2 channels
                mGlobalChannels = 2;
                if (mpgVersion == 1) {
                    gain = ((buffer[9]  & 0x7F) << 1) +
                        ((buffer[10] & 0x80) >> 7);
                } else {
                    gain = 0;  // ???
                }
            }

            mBitrateSum += bitRate;

            mFrameOffsets[mNumFrames] = pos;
            mFrameLens[mNumFrames] = frameLen;
            mFrameGains[mNumFrames] = gain;
            if (gain < mMinGain)
                mMinGain = gain;
            if (gain > mMaxGain)
                mMaxGain = gain;

            mNumFrames++;
            if (mNumFrames == mMaxFrames) {
                // We need to grow our arrays.  Rather than naively
                // doubling the array each time, we estimate the exact
                // number of frames we need and add 10% padding.  In
                // practice this seems to work quite well, only one
                // resize is ever needed, however to avoid pathological
                // cases we make sure to always double the size at a minimum.

                mAvgBitRate = mBitrateSum / mNumFrames;
                int totalFramesGuess =
                    ((mFileSize / mAvgBitRate) * sampleRate) / 144000;
                int newMaxFrames = totalFramesGuess * 11 / 10;
                if (newMaxFrames < mMaxFrames * 2)
                    newMaxFrames = mMaxFrames * 2;

                int[] newOffsets = new int[newMaxFrames];
                int[] newLens = new int[newMaxFrames];
                int[] newGains = new int[newMaxFrames];
                for (int i = 0; i < mNumFrames; i++) {
                    newOffsets[i] = mFrameOffsets[i];
                    newLens[i] = mFrameLens[i];
                    newGains[i] = mFrameGains[i];
                }
                mFrameOffsets = newOffsets;
                mFrameLens = newLens;
                mFrameGains = newGains;
                mMaxFrames = newMaxFrames;
            }

            stream.skip(frameLen - 12);
            pos += frameLen;
            offset = 0;
        }

        // We're done reading the file, do some postprocessing
        if (mNumFrames > 0)
            mAvgBitRate = mBitrateSum / mNumFrames;
        else
            mAvgBitRate = 0;
            
            
            */
    }

    public void WriteFile(File outputFile, int startFrame, int numFrames)
            throws java.io.IOException {
        outputFile.createNewFile();
        FileInputStream in = new FileInputStream(mInputFile);
        FileOutputStream out = new FileOutputStream(outputFile);
        int maxFrameLen = 0;
        for (int i = 0; i < numFrames; i++) {
            if (mFrameLens[startFrame + i] > maxFrameLen)
                maxFrameLen = mFrameLens[startFrame + i];
        }
        byte[] buffer = new byte[maxFrameLen];
        int pos = 0;
        for (int i = 0; i < numFrames; i++) {
            int skip = mFrameOffsets[startFrame + i] - pos;
            int len = mFrameLens[startFrame + i];
            if (skip > 0) {
                in.skip(skip);
                pos += skip;
            }
            in.read(buffer, 0, len);
            out.write(buffer, 0, len);
            pos += len;
        }
        in.close();
        out.close();
    }

    static private int BITRATES_MPEG1_L3[] = {
        0,  32,  40,  48,  56,  64,  80,  96,
        112, 128, 160, 192, 224, 256, 320,  0 };
    static private int BITRATES_MPEG2_L3[] = {
        0,   8,  16,  24,  32,  40,  48,  56,
        64,  80,  96, 112, 128, 144, 160, 0 };
    static private int SAMPLERATES_MPEG1_L3[] = {
        44100, 48000, 32000, 0 };
    static private int SAMPLERATES_MPEG2_L3[] = {
        22050, 24000, 16000, 0 };
};
