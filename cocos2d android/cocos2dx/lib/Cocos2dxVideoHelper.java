/****************************************************************************
Copyright (c) 2014 Chukong Technologies Inc.

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/

package org.cocos2dx.lib;

import java.lang.ref.WeakReference;

import org.cocos2dx.lib.Cocos2dxVideoView.OnVideoEventListener;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Cocos2dxVideoHelper {

    private FrameLayout mLayout = null;
    private Cocos2dxActivity mActivity = null;  
    private SparseArray<Cocos2dxVideoView> sVideoViews = null;
    private SparseArray<RelativeLayout> sSkipLayouts = null;
    static VideoHandler mVideoHandler = null;
    
    Cocos2dxVideoHelper(Cocos2dxActivity activity,FrameLayout layout)
    {
        mActivity = activity;
        mLayout = layout;
        
        mVideoHandler = new VideoHandler(this);
        sVideoViews = new SparseArray<Cocos2dxVideoView>();
        sSkipLayouts = new SparseArray<RelativeLayout>();
    }
    
    private static int videoTag = 0;
    private final static int VideoTaskCreate = 0;
    private final static int VideoTaskRemove = 1;
    private final static int VideoTaskSetSource = 2;
    private final static int VideoTaskSetRect = 3;
    private final static int VideoTaskStart = 4;
    private final static int VideoTaskPause = 5;
    private final static int VideoTaskResume = 6;
    private final static int VideoTaskStop = 7;
    private final static int VideoTaskSeek = 8;
    private final static int VideoTaskSetVisible = 9;
    private final static int VideoTaskRestart = 10;
    private final static int VideoTaskKeepRatio = 11;
    private final static int VideoTaskFullScreen = 12;
    private final static int VideoSkipVisible = 13;
    final static int KeyEventBack = 1000;
    
    static class VideoHandler extends Handler{
        WeakReference<Cocos2dxVideoHelper> mReference;
        
        VideoHandler(Cocos2dxVideoHelper helper){
            mReference = new WeakReference<Cocos2dxVideoHelper>(helper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case VideoTaskCreate: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._createVideoView(msg.arg1);
                break;
            }
            case VideoTaskRemove: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._removeVideoView(msg.arg1);
                break;
            }
            case VideoTaskSetSource: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._setVideoURL(msg.arg1, msg.arg2, (String)msg.obj);
                break;
            }
            case VideoTaskStart: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._startVideo(msg.arg1);
                break;
            }
            case VideoTaskSetRect: {
                Cocos2dxVideoHelper helper = mReference.get();
                Rect rect = (Rect)msg.obj;
                helper._setVideoRect(msg.arg1, rect.left, rect.top, rect.right, rect.bottom);
                break;
            }
            case VideoTaskFullScreen:{
                Cocos2dxVideoHelper helper = mReference.get();
                Rect rect = (Rect)msg.obj;
                if (msg.arg2 == 1) {
                    helper._setFullScreenEnabled(msg.arg1, true, rect.right, rect.bottom);
                } else {
                    helper._setFullScreenEnabled(msg.arg1, false, rect.right, rect.bottom);
                }
                break;
            }
            case VideoTaskPause: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._pauseVideo(msg.arg1);
                break;
            }
            case VideoTaskResume: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._resumeVideo(msg.arg1);
                break;
            }
            case VideoTaskStop: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._stopVideo(msg.arg1);
                break;
            }
            case VideoTaskSeek: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._seekVideoTo(msg.arg1, msg.arg2);
                break;
            }
            case VideoTaskSetVisible: {
                Cocos2dxVideoHelper helper = mReference.get();
                if (msg.arg2 == 1) {
                    helper._setVideoVisible(msg.arg1, true);
                } else {
                    helper._setVideoVisible(msg.arg1, false);
                }
                break;
            }
            case VideoTaskRestart: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper._restartVideo(msg.arg1);
                break;
            }
            case VideoTaskKeepRatio: {
                Cocos2dxVideoHelper helper = mReference.get();
                if (msg.arg2 == 1) {
                    helper._setVideoKeepRatio(msg.arg1, true);
                } else {
                    helper._setVideoKeepRatio(msg.arg1, false);
                }
                break;
            }
            case KeyEventBack: {
                Cocos2dxVideoHelper helper = mReference.get();
                helper.onBackKeyEvent();
                break;
            }
            case VideoSkipVisible: {
                Cocos2dxVideoHelper helper = mReference.get();
                if (msg.arg2 == 1) {
                    helper._setVideoSkipVisible(msg.arg1, true);
                } else {
                    helper._setVideoSkipVisible(msg.arg1, false);
                }
                break;
            }
            default:
                break;
            }
            
            super.handleMessage(msg);
        }
    }
    
    private class VideoEventRunnable implements Runnable
    {
        private int mVideoTag;
        private int mVideoEvent;
        
        public VideoEventRunnable(int tag,int event) {
            mVideoTag = tag;
            mVideoEvent = event;
        }
        @Override
        public void run() {
            nativeExecuteVideoCallback(mVideoTag, mVideoEvent);
        }
        
    }
    
    public static native void nativeExecuteVideoCallback(int index,int event);
    
    OnVideoEventListener videoEventListener = new OnVideoEventListener() {
        
        @Override
        public void onVideoEvent(int tag,int event) {
            mActivity.runOnGLThread(new VideoEventRunnable(tag, event));
        }
    };
    
    
    public static int createVideoWidget() {
        Message msg = new Message();
        msg.what = VideoTaskCreate;
        msg.arg1 = videoTag;
        mVideoHandler.sendMessage(msg);
        
        return videoTag++;
    }
    
    private void _createVideoView(int index) {
        Cocos2dxVideoView videoView = new Cocos2dxVideoView(mActivity,index);
        sVideoViews.put(index, videoView);
        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        mLayout.addView(videoView, lParams);
        videoView.setZOrderMediaOverlay(true);
        videoView.setOnCompletionListener(videoEventListener);
        _createSkipButton(index);
    }
    
    /** 设置Selector。 */
    private static StateListDrawable newSelector(Context context, int idNormal, int idPressed, int idFocused,
                    int idUnable) {
            StateListDrawable bg = new StateListDrawable();
            Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
            Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
            Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
            Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
            // View.PRESSED_ENABLED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
            // View.ENABLED_FOCUSED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
            // View.ENABLED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_enabled }, normal);
            // View.FOCUSED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_focused }, focused);
            // View.WINDOW_FOCUSED_STATE_SET
            bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
            // View.EMPTY_STATE_SET
            bg.addState(new int[] {}, normal);
            return bg;
    }
    
    @SuppressWarnings("deprecation")
	private void _createSkipButton(int index) {

        RelativeLayout skipLayout = new RelativeLayout(mActivity);
        sSkipLayouts.put(index, skipLayout);
        skipLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mLayout.addView(skipLayout);
        skipLayout.setVisibility(View.INVISIBLE);

        Button skipButton = new Button(mActivity);
        RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lParams.rightMargin = 100;
        lParams.bottomMargin = 100;
        skipLayout.addView(skipButton,lParams);


        skipButton.setText("");
        skipButton.setTextColor(Color.argb(180, 255, 255, 255));
        skipButton.setTextSize(20);
        //skipButton.setBackgroundResource(R.drawable.confirm);
        skipButton.setBackgroundDrawable(newSelector(mActivity, R.drawable.vedio_skip_btn, R.drawable.vedio_skip_btn_down, -1,-1));
        
        final int indexx = index;
        skipButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                mActivity.runOnGLThread(new VideoEventRunnable(indexx, 4));
            }
        });
    }
    public static void removeVideoWidget(int index){
        Message msg = new Message();
        msg.what = VideoTaskRemove;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _removeVideoView(int index) {
        Cocos2dxVideoView view = sVideoViews.get(index);
        if (view != null) {
            view.stopPlayback();
            sVideoViews.remove(index);
            mLayout.removeView(view);
        }
        _removeSkipButton(index);
    }

    private void _removeSkipButton(int index) {
        RelativeLayout view = sSkipLayouts.get(index);
        if (view != null) {
            sSkipLayouts.remove(index);
            mLayout.removeView(view);
        }
    }
    
    public static void setVideoUrl(int index, int videoSource, String videoUrl) {
        Message msg = new Message();
        msg.what = VideoTaskSetSource;
        msg.arg1 = index;
        msg.arg2 = videoSource;
        msg.obj = videoUrl;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoURL(int index, int videoSource, String videoUrl) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            switch (videoSource) {
            case 0:
                videoView.setVideoFileName(videoUrl);
                break;
            case 1:
                videoView.setVideoURL(videoUrl);
                break;
            default:
                break;
            }
        }
    }
    
    public static void setVideoRect(int index, int left, int top, int maxWidth, int maxHeight) {
        Message msg = new Message();
        msg.what = VideoTaskSetRect;
        msg.arg1 = index;
        msg.obj = new Rect(left, top, maxWidth, maxHeight);
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoRect(int index, int left, int top, int maxWidth, int maxHeight) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setVideoRect(left,top,maxWidth,maxHeight);
        }
    }
    
    public static void setFullScreenEnabled(int index, boolean enabled, int width, int height) {
        Message msg = new Message();
        msg.what = VideoTaskFullScreen;
        msg.arg1 = index;
        if (enabled) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        msg.obj = new Rect(0, 0, width, height);
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setFullScreenEnabled(int index, boolean enabled, int width,int height) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setFullScreenEnabled(enabled, width, height);
        }
    }
    
    private void onBackKeyEvent() {
        int viewCount = sVideoViews.size();
        for (int i = 0; i < viewCount; i++) {
            int key = sVideoViews.keyAt(i);
            Cocos2dxVideoView videoView = sVideoViews.get(key);
            if (videoView != null) {
                //videoView.setFullScreenEnabled(false, 0, 0);
                mActivity.runOnGLThread(new VideoEventRunnable(key, KeyEventBack));
            }
        }
    }
    
    public static void startVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskStart;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _startVideo(int index) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.start();
        }
    }
    
    public static void pauseVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskPause;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _pauseVideo(int index) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.pause();
        }
    }

    public static void resumeVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskResume;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _resumeVideo(int index) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.resume();
        }
    }
    
    public static void stopVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskStop;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _stopVideo(int index) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.stop();
        }
    }
    
    public static void restartVideo(int index) {
        Message msg = new Message();
        msg.what = VideoTaskRestart;
        msg.arg1 = index;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _restartVideo(int index) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.restart();
        }
    }
    
    public static void seekVideoTo(int index,int msec) {
        Message msg = new Message();
        msg.what = VideoTaskSeek;
        msg.arg1 = index;
        msg.arg2 = msec;
        mVideoHandler.sendMessage(msg);
    }
    
    private void _seekVideoTo(int index,int msec) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.seekTo(msec);
        }
    }
    
    public static void setVideoVisible(int index, boolean visible) {
        Message msg = new Message();
        msg.what = VideoTaskSetVisible;
        msg.arg1 = index;
        if (visible) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoVisible(int index, boolean visible) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            if (visible) {
                videoView.fixSize();
                videoView.setVisibility(View.VISIBLE);
            } else {
                videoView.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    public static void setVideoSkipVisible(int index, boolean visible) {
        Message msg = new Message();
        msg.what = VideoSkipVisible;
        msg.arg1 = index;
        if (visible) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        
        mVideoHandler.sendMessage(msg);
    }

    private void _setVideoSkipVisible(int index, boolean visible) {
        RelativeLayout view = sSkipLayouts.get(index);
        if (view != null) {
            if (visible) {
                view.setVisibility(View.VISIBLE);
            } else {
                 view.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void setVideoKeepRatioEnabled(int index, boolean enable) {
        Message msg = new Message();
        msg.what = VideoTaskKeepRatio;
        msg.arg1 = index;
        if (enable) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }
        mVideoHandler.sendMessage(msg);
    }
    
    private void _setVideoKeepRatio(int index, boolean enable) {
        Cocos2dxVideoView videoView = sVideoViews.get(index);
        if (videoView != null) {
            videoView.setKeepRatio(enable);
        }
    }
}
