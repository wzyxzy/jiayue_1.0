/************************************************************************
Snail Proprietary
Copyright (c) 2014, Snail Incorporated. All Rights Reserved

Snail, Inc., 4675 Stevens Creek Blvd, Santa Clara, CA 95051, USA

All data and information contained in or disclosed by this document are
confidential and proprietary information of Snail, and all rights
therein are expressly reserved. By accepting this material, the
recipient agrees that this material and the information contained
therein are held in confidence and in trust. The material may only be
used and/or disclosed as authorized in a license agreement controlling
such use and disclosure.
************************************************************************/

package com.jiayue.vr;

public class Definition {
    public static final String  DOWNLOAD_PATH               = "/sdcard/SamplePlayer/Downloader";
    public static final String  LOCALFILE_PATH              = "/sdcard/SamplePlayer";
    public static final String  DOWNLOAD_LOCALFILE_PATH     = "/sdcard/SamplePlayer/temp.vi";
    public static final String DEFAULT_DATAFILE_PATH        = "/data/data/";
    public static final String DEFAULT_HISTORYLIST_FILENAME = "history.txt";
    public static final String  WARNING_MESSAGE             = "Warning";
    public static final String  ERROR_MESSAGE               = "Error";
    public static final String PREFIX_HTTP                  = "http://";
    public static final String PREFIX_HTTPS                 = "https://";
    public static final String PREFIX_RTSP                  = "rtsp://";
    public static final String PREFIX_MEDIAFILE_MTV         = "mtv://";
    public static final String [] PREFIX_SUGGEST            =  { "www.", "http://www.", "https://www.", "rtsp://", "http://winserver1/test/"};
    
    public static final String SUFFIX_HTM                   = ".htm";
    public static final String SUFFIX_HTML                  = ".html";
    public static final String SUFFIX_PHP                   = ".php";
    public static final String SUFFIX_ASP                   = ".asp";
    public static final String SUFFIX_ASPX                  = ".aspx";
    
    public static final String STOP                         = "Stop";
    public static final String START                        = "Start";
    public static final String PAUSE                        = "Pause";
    public static final String RUN                          = "Run";
    public static final String MUTE                         = "Mute";
    public static final String UNMUTE                       = "Unmute";
    
    public static final String ZOOM_MODE_ORIGINAL           = "Original";
    public static final String ZOOM_MODE_LETTERBOX          = "LetterBox";
    public static final String ZOOM_MODE_PANSCAN            = "PanScan";
    public static final String ZOOM_MODE_FITWINDOW          = "FullWindow";
    public static final String ZOOM_MODE_ZOOMIN             = "ZoomIn";

    public static final String ASPECT_RATIO_AUTO            = "Auto";
    public static final String ASPECT_RATIO_11              = "1 : 1";
    public static final String ASPECT_RATIO_43              = "4 : 3";
    public static final String ASPECT_RATIO_169             = "16 : 9";
    public static final String ASPECT_RATIO_21              = "2 : 1";
    public static final String ASPECT_RATIO_2331            = "2.33 : 1";
    
    public static final String MOTION                       = "Motion";
    public static final String TOUCH                        = "Touch";
    public static final String KEY_FOV                     	= "Fov";
    public static final String KEY_PROJECTIONTYPE          	= "ProjectionType";
    public static final String KEY_VIDEOSPLICEFORMAT     	= "VidepSpliceFormat";
    public static final String KEY_PLAY_URL                 = "play_url";
    public static final String KEY_SENSORMODE               = "sensormode";
    public static final String KEY_SCALE                    = "scale";


    public static final String LOG_SETTING                  = "log_setting";
    public static final String HISTORY_URL                  = "history_url";
}
