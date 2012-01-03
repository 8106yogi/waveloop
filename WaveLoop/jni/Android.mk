LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
LOCAL_MODULE    := audio-tools
LOCAL_ARM_MODE := arm

LOCAL_SRC_FILES := OSLESMediaPlayer.c NativeMP3Decoder.cpp mad/bit.c mad/decoder.c mad/fixed.c mad/frame.c mad/huffman.c mad/layer12.c mad/layer3.c mad/stream.c mad/synth.c mad/timer.c mad/version.c


LOCAL_CFLAGS := -DHAVE_CONFIG_H -DFPM_ARM -ffast-math -O3

LOCAL_LDLIBS    += -lOpenSLES


include $(BUILD_SHARED_LIBRARY)
