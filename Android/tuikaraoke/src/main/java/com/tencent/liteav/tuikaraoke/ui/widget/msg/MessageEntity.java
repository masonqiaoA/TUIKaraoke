package com.tencent.liteav.tuikaraoke.ui.widget.msg;

public class MessageEntity {
    public static final int TYPE_NORMAL       = 0;
    public static final int TYPE_WAIT_AGREE   = 1;
    public static final int TYPE_AGREED       = 2;
    public static final int TYPE_WELCOME      = 3;
    public static final int TYPE_ORDERED_SONG = 4;
    public static final int TYPE_ERROR        = -1;

    public String  userId;
    public String  userName;
    public String  content;
    public String  invitedId;
    public String  linkUrl;
    public int     type;
    public int     color;
    public boolean isChat;
    public String  songName;
}
