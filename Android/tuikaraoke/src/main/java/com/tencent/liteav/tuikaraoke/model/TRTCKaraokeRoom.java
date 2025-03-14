package com.tencent.liteav.tuikaraoke.model;

import android.content.Context;

import com.tencent.liteav.tuikaraoke.model.impl.TRTCKaraokeRoomImpl;
import com.tencent.liteav.tuikaraoke.model.TRTCKaraokeRoomDef.UserInfo;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
import com.tencent.qcloud.tuicore.interfaces.TUIValueCallback;

import java.util.List;

public abstract class TRTCKaraokeRoom {

    /**
     * 获取 TRTCKaraokeRoom 单例对象
     *
     * @param context Android 上下文，内部会转为 ApplicationContext 用于系统 API 调用
     * @return TRTCKaraokeRoom 实例
     * @note 可以调用 {@link TRTCKaraokeRoom#destroySharedInstance()} 销毁单例对象
     */
    public static synchronized TRTCKaraokeRoom sharedInstance(Context context) {
        return TRTCKaraokeRoomImpl.sharedInstance(context);
    }

    /**
     * 销毁 TRTCKaraokeRoom 单例对象
     *
     * @note 销毁实例后，外部缓存的 TRTCKaraokeRoom 实例不能再使用，需要重新调用 {@link TRTCKaraokeRoom#sharedInstance(Context)} 获取新实例
     */
    public static void destroySharedInstance() {
        TRTCKaraokeRoomImpl.destroySharedInstance();
    }

    //////////////////////////////////////////////////////////
    //
    //                 基础接口
    //
    //////////////////////////////////////////////////////////

    /**
     * 设置组件回调接口
     * <p>
     * 您可以通过 TRTCKaraokeRoomObserver 获得 TRTCKaraokeRoom 的各种状态通知
     *
     * @param delegate 回调接口
     * @note TRTCKaraokeRoom 中的事件，默认是在 Main Thread 中回调给您；
     */
    public abstract void setDelegate(TRTCKaraokeRoomObserver delegate);

    /**
     * 登录
     *
     * @param sdkAppId 您可以在实时音视频控制台 >【[应用管理](https://console.cloud.tencent.com/trtc/app)】> 应用信息中查看 SDKAppID
     * @param userId   当前用户的 ID，字符串类型，只允许包含英文字母（a-z 和 A-Z）、数字（0-9）、连词符（-）和下划线（\_）
     * @param userSig  腾讯云设计的一种安全保护签名，获取方式请参考 [如何计算 UserSig](https://cloud.tencent.com/document/product/647/17275)。
     * @param callback 登录回调，成功时 code 为0
     */
    public abstract void login(int sdkAppId, String userId, String userSig, TUICallback callback);

    /**
     * 退出登录
     */
    public abstract void logout(TUICallback callback);

    /**
     * 设置用户信息，您设置的用户信息会被存储于腾讯云 IM 云服务中。
     *
     * @param userName  用户昵称
     * @param avatarURL 用户头像
     * @param callback  是否设置成功的结果回调
     */
    public abstract void setSelfProfile(String userName, String avatarURL, TUICallback callback);

    //////////////////////////////////////////////////////////
    //
    //                 房间管理接口
    //
    //////////////////////////////////////////////////////////

    /**
     * 创建房间（主播调用）
     * <p>
     * 主播正常的调用流程是：
     * 1. 主播调用`createRoom`创建新的语音聊天室，此时传入房间 ID、上麦是否需要房主确认、麦位数等房间属性信息。
     * 2. 主播创建房间成功后，调用`enterSeat`进入座位。
     * 3. 主播收到组件的`onSeatListChange`麦位表变化事件通知，此时可以将麦位表变化刷新到 UI 界面上。
     * 4. 主播还会收到麦位表有成员进入的`onAnchorEnterSeat`的事件通知，此时会自动打开麦克风采集。
     *
     * @param roomId    房间标识，需要由您分配并进行统一管理。
     * @param roomParam 房间信息，用于房间描述的信息，例如房间名称，封面信息等。如果房间列表和房间信息都由您的服务器自行管理，可忽略该参数。
     * @param callback  创建房间的结果回调，成功时 code 为0.
     */
    public abstract void createRoom(int roomId, TRTCKaraokeRoomDef.RoomParam roomParam, TUICallback callback);

    /**
     * 销毁房间（房主调用）
     * <p>
     * 房主在创建房间后，可以调用这个函数来销毁房间。
     */
    public abstract void destroyRoom(TUICallback callback);

    /**
     * 进入房间（听众调用）
     * <p>
     * 听众进房收听的正常调用流程如下：
     * 1.【听众】向您的服务端获取最新的语音聊天室列表，可能包含多个直播间的 roomId 和房间信息。
     * 2. 听众选择一个语音聊天室，调用`enterRoom`并传入房间号即可进入该房间。
     * 3. 进房后会收到组件的`onRoomInfoChange`房间属性变化事件通知，此时可以记录房间属性并做相应改变，例如 UI 展示房间名、记录上麦是否需要请求主播同意等。
     * 4. 进房后会收到组件的`onSeatListChange`麦位表变化事件通知，此时可以将麦位表变化刷新到 UI 界面上。
     * 5. 进房后还会收到麦位表有主播进入的`onAnchorEnterSeat`的事件通知。
     *
     * @param roomId   房间标识
     * @param callback 进入房间是否成功的结果回调
     */
    public abstract void enterRoom(int roomId, TUICallback callback);

    /**
     * 退出房间
     *
     * @param callback 退出房间是否成功的结果回调
     */
    public abstract void exitRoom(TUICallback callback);

    /**
     * 获取指定userId的用户信息，如果为null，则获取房间内所有人的信息
     *
     * @param callback 用户详细信息回调
     */
    public abstract void getUserInfoList(List<String> userIdList, TUIValueCallback<List<UserInfo>> callback);

    //////////////////////////////////////////////////////////
    //
    //                 麦位管理接口
    //
    //////////////////////////////////////////////////////////

    /**
     * 主动上麦（听众端和主播均可调用）
     * <p>
     * 上麦成功后，房间内所有成员会收到`onSeatListChange`和`onAnchorEnterSeat`的事件通知。
     *
     * @param seatIndex 需要上麦的麦位序号
     * @param callback  操作回调
     */
    public abstract void enterSeat(int seatIndex, TUICallback callback);

    /**
     * 主动下麦（听众端和主播均可调用）
     * <p>
     * 下麦成功后，房间内所有成员会收到`onSeatListChange`和`onAnchorLeaveSeat`的事件通知。
     *
     * @param callback 操作回调
     */
    public abstract void leaveSeat(TUICallback callback);

    /**
     * 踢人下麦(主播调用)
     * <p>
     * 主播踢人下麦，房间内所有成员会收到`onSeatListChange`和`onAnchorLeaveSeat`的事件通知。
     *
     * @param seatIndex 需要踢下麦的麦位序号
     * @param callback  操作回调
     */
    public abstract void kickSeat(int seatIndex, TUICallback callback);

    /**
     * 静音/解禁对应麦位的麦克风(主播调用)
     * <p>
     * 房间内所有成员会收到`onSeatListChange`和`onSeatMute`的事件通知。
     * 对应 seatIndex 座位上的主播，会自动调用 muteAudio 进行静音/解禁
     *
     * @param seatIndex 麦位序号
     * @param isMute    true:静音 fasle:解除静音
     * @param callback  操作回调
     */
    public abstract void muteSeat(int seatIndex, boolean isMute, TUICallback callback);

    /**
     * 封禁/解禁某个麦位(主播调用)
     * <p>
     * 房间内所有成员会收到`onSeatListChange`和`onSeatClose`的事件通知。
     *
     * @param seatIndex 麦位序号
     * @param isClose   true:封禁 fasle:解除封禁
     * @param callback  操作回调
     */
    public abstract void closeSeat(int seatIndex, boolean isClose, TUICallback callback);

    //////////////////////////////////////////////////////////
    //
    //                 本地音频操作接口
    //
    //////////////////////////////////////////////////////////
    /**
     * 开启本地静音
     *
     * @param mute 是否静音
     */
    public abstract void muteLocalAudio(boolean mute);

    //////////////////////////////////////////////////////////
    //
    //                 消息发送接口
    //
    //////////////////////////////////////////////////////////

    /**
     * 在房间中广播文本消息，一般用于弹幕聊天
     *
     * @param message  文本消息
     * @param callback 发送结果回调
     */
    public abstract void sendRoomTextMsg(String message, TUICallback callback);

    /**
     * 在房间中广播自定义（信令）消息，一般用于广播点赞和礼物消息
     *
     * @param cmd      命令字，由开发者自定义，主要用于区分不同消息类型
     * @param message  文本消息
     * @param callback 发送结果回调
     */
    public abstract void sendRoomCustomMsg(String cmd, String message, TUICallback callback);

    //////////////////////////////////////////////////////////
    //
    //                 邀请信令消息
    //
    //////////////////////////////////////////////////////////

    /**
     * 向用户发送邀请
     *
     * @param cmd      业务自定义指令
     * @param userId   邀请的用户ID
     * @param content  邀请的内容
     * @param callback 发送结果回调
     * @return inviteId 用于标识此次邀请ID
     */
    public abstract String sendInvitation(String cmd, String userId, String content, TUICallback callback);

    /**
     * 接受邀请
     *
     * @param id       邀请ID
     * @param callback 接受操作的回调
     */
    public abstract void acceptInvitation(String id, TUICallback callback);

    /**
     * 开始播放音乐
     *
     * @param musicID      音乐的表演ID
     * @param originalUrl  音乐的原唱
     * @param accompanyUrl 音乐的伴奏
     */
    public abstract void startPlayMusic(int musicID, String originalUrl, String accompanyUrl);

    /**
     * 停止播放音乐
     */
    public abstract void stopPlayMusic();

    /**
     * 切换伴奏或原唱
     *
     * @param isOriginal true：开启；false：关闭
     */
    public abstract void switchMusicAccompanimentMode(boolean isOriginal);

    /**
     * 设置背景音乐的音量。
     *
     * @param musicVolume 音量大小，取值范围为0 - 100，默认值：100。
     */
    public abstract void setMusicVolume(int musicVolume);

    /**
     * 开启耳返
     *
     * @param enable true：开启；false：关闭
     */
    public abstract void enableVoiceEarMonitor(boolean enable);

    /**
     * 设置语音音量
     *
     * @param voiceVolume 音量大小，取值范围为0 - 100，默认值：100。
     */
    public abstract void setVoiceVolume(int voiceVolume);

    /**
     * 调整背景音乐的音调高低
     *
     * @param musicPitch 音调，默认值是0.0f，范围是：[-1 ~ 1] 之间的浮点数；
     */
    public abstract void setMusicPitch(float musicPitch);

    /**
     * 设置人声的混响效果
     *
     * @param reverbType 混响效果，范围：[0 ~ 7]， 默认值 0
     */
    public abstract void setVoiceReverbType(int reverbType);

    /**
     * 设置人声的变声特效
     *
     * @param changerType 混响效果，范围：[0 ~ 11]，默认值 0
     */
    public abstract void setVoiceChangerType(int changerType);

    /**
     * 更新NTP网络校时
     *
     */
    public abstract void updateNetworkTime();
}
