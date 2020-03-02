package com.gisicisky.smasterFitment.utl;


public class BaseVolume {

    // 欧唯
//	public static String PRODUCTID = "160fa2b00e412200160fa2b00e412201";
    // 酸奶机
    public static String PRODUCTID = "2498656adbe544afb5fa5c99f32da5a0";
    public static final String DB_NAME = "ouwei_device.db";
    public static final String REGISTER_USER = "REGISTER_USER";
    public static final String APP_USER = "APP_USER";
    public static final String APP_PWD = "APP_PWD";
    public static final String APP_NAME = "APP_NAME";
    public static final String LOGIN_USER = "LOGIN_USER";
    public static final String GET_USERINFO = "GET_USERINFO";
    public static final String FORGOT_PWD = "FORGOT_PWD";
    public static final String GET_DEVICELIST = "GET_DEVICELIST";
    public static final String UPDATE_USER_PWD = "UPDATE_USER_PWD";
    public static final String OLD_PWD = "OLD_PWD";
    public static final String NEW_PWD = "NEW_PWD";
    public static final String UPDATE_USER_NAME = "UPDATE_USER_NAME";
    /**
     * 用户离线
     */
    public static final String USER_NOT_LINE = "USER_NOT_LINE";

    public static final String INIT_DEVICE = "INIT_DEVICE";
    public static final String SET_DEVICE_PWD = "SET_DEVICE_PWD";
    public static final String COMMAND_DEVICE = "COMMAND_DEVICE";
    public static final String COMMAND_DEVICE_By_Qrcode = "COMMAND_DEVICE_By_Qrcode";
    public static final String CONTROL_DEVICE = "CONTROL_DEVICE";
    public static final String DELETE_BINDRELATION = "DELETE_BINDRELATION";
    public static final String BROADCAST_DEVICE_STATUE = "BROADCAST_DEVICE_STATUE";
    public static final String BROADCAST_DEVICE_CHANGED = "BROADCAST_DEVICE_CHANGED";
    public static final String DEVICE_MAC = "DEVICE_MAC";
    public static final String STATUS = "STATUS";
    public static final String BROADCAST_RECVPIPE_LAN = "BROADCAST_RECVPIPE_LAN";
    public static final String BROADCAST_RECVPIPE = "BROADCAST_RECVPIPE";

    /**
     * 更新数据
     */
    public static final String BROADCAST_DATA_ANALYSIS_FINISH = "BROADCAST_DATA_ANALYSIS_FINISH";

//	public static final String CLEAR_DATA_BUFFER_BY_DEVICE = "CLEAR_DATA_BUFFER_BY_DEVICE";
    /**
     * 是否是局域网连接成功
     */
    public static final String BROADCAST_DEVICE_CONNECTE_IS_LAN = "BROADCAST_DEVICE_CONNECTE_IS_LAN";

    // 登录广播
    public static final String BROADCAST_ON_LOGIN = "onLogin";

    /**
     * 获取所有设备状态
     */
    public static final String BROADCAST_GET_ALL_DEVICE_STATE = "BROADCAST_GET_ALL_DEVICE_STATE";
    /**
     * 获取到当前位置
     */
    public static final String BROADCAST_GET_NOW_ADDRESS = "BROADCAST_GET_NOW_ADDRESS";

    public static final String RESULT_DATA = "RESULT_DATA";
    public static final String REASH_DEVICE_STATE = "REASH_DEVICE_STATE";
    public static final String RESULT_XDEVICE = "RESULT_XDEVICE";
    public final static String GET_CAMERA_ERROR = "GET_CAMERA_ERROR";
    public final static String GET_CAMERA_OK = "GET_CAMERA_OK";
    public static final String ADD_DEVICE = "ADD_DEVICE";
    public static final String QrCode_NewDEVICE = "QrCode_NewDEVICE";
    public static final String UPDATE_ACTION_DATE = "UPDATE_ACTION_DATE";


    /**
     * 每个账号下，默认存在一个分组，为‘其他分组，ID：-999’
     */
    public static final int DEFAULT_GROUP_ID = -999;
    /**
     * 设备的默认型号
     */
    public static final String DEFAULT_DEVICE_TYPE = "TM-7000A";

    public static final String DEVICE = "DEVICE";
    public static final String GROUP = "GROUP";
    public static final String ALL_DEVICE = "ALL_DEVICE";

    /**
     * 未激活
     */
    public final static int DEVICE_NOT_ACTIVE = -1;
    /**
     * 正在连接
     */
    public final static int DEVICE_CONNECTING = 0;
    /**
     * 在线
     */
    public final static int DEVICE_ON_LINE = 1;
    /**
     * 离线
     */
    public final static int DEVICE_NOT_LINE = 2;
    /**
     * 正在校验密码
     */
    public final static int DEVICE_CHECK_PWD = 3;

    /**
     * 包头
     */
    public static final String COMMAND_HEAD = "aa";
    /**
     * 包尾
     */
    public static final String COMMAND_END = "55";


    /**
     * 包头 位
     */
    public static final int COMMAND_LOC_BAOTOU = 0x00;
    /**
     * 指令 位
     */
    public static final int COMMAND_LOC_ZHILING = 0x01;
    /**
     * 主机类型 位
     */
    public static final int COMMAND_LOC_LEIXING = 0x03;
    /**
     * 加热 位
     */
    public static final int COMMAND_LOC_JIARE = 0x04;
    /**
     * 光波1 位
     */
    public static final int COMMAND_LOC_GUANGBO_1 = 0x05;
    /**
     * 光波2 位
     */
    public static final int COMMAND_LOC_GUANGBO_2 = 0x06;
    /**
     * 气泵 位
     */
    public static final int COMMAND_LOC_FENGBENG = 0x07;
    /**
     * 水泵1 位
     */
    public static final int COMMAND_LOC_SHUIBENG_1 = 0x08;
    /**
     * 水泵2 位
     */
    public static final int COMMAND_LOC_SHUIBENG_2 = 0x09;
    /**
     * 循环泵 位
     */
    public static final int COMMAND_LOC_XUNHUAN_BENG = 0x0a;
    /**
     * 水泵3 位
     */
    public static final int COMMAND_LOC_SHUIBENG_3 = 0x0b;
    /**
     * 空气 位
     */
    public static final int COMMAND_LOC_KONGQI = 0x0c;

    /**
     * 水底灯 位
     */
    public static final int COMMAND_LOC_ZHAOMING = 0x0d;
    /**
     * 背灯 位
     */
    public static final int COMMAND_LOC_BEIDENG = 0x0e;
    /**
     * 照明 位
     */
    public static final int COMMAND_LOC_SHUIDI_DENG = 0x0f;
    /**
     * 抽风扇 位
     */
    public static final int COMMAND_LOC_CHOUFENG_SHAN = 0x10;
    /**
     * O3 位
     */
    public static final int COMMAND_LOC_O3 = 0x11;
    /**
     * 进水 位
     */
    public static final int COMMAND_LOC_JINSHUI = 0x12;
    /**
     * 放水 位
     */
    public static final int COMMAND_LOC_FANGSHUI = 0x13;
    /**
     * 水位 位
     */
    public static final int COMMAND_LOC_SHUIWEI = 0x14;
    /**
     * 温度校准 位
     */
    public static final int COMMAND_LOC_WENDU_JIAOZHUN = 0x15;
    /**
     * 检测温度 位
     */
    public static final int COMMAND_LOC_JIANCE_WENDU = 0x16;
    /**
     * 预置温度 位
     */
    public static final int COMMAND_LOC_YUZHI_WENDU = 0x17;
    /**
     * 倒计时间 位
     */
    public static final int COMMAND_LOC_DAOJI_SHIJIAN = 0x18;
    /**
     * 预置时间 位
     */
    public static final int COMMAND_LOC_YUZHI_SHIJIAN = 0x19;
    /**
     * FM频率 位
     */
    public static final int COMMAND_LOC_FM_PINLV = 0x1c;
    /**
     * FM频道 位
     */
    public static final int COMMAND_LOC_FM_PINDAO = 0x1d;
    /**
     * 预约时间 位
     */
    public static final int COMMAND_LOC_BOFANG_SHIJIAN_HOUR = 0x1e;

    public static final int COMMAND_LOC_BOFANG_SHIJIAN_MIN = 0x1f;
    /**
     * 曲目号 位
     */
    public static final int COMMAND_LOC_QUMU_HAO = 0x20;
    /**
     * FM 位
     */
    public static final int COMMAND_LOC_FM = 0x22;
    /**
     * MP3 位
     */
    public static final int COMMAND_LOC_MP3 = 0x23;
    /**
     * 蓝牙 位
     */
    public static final int COMMAND_LOC_LANYA = 0x24;
    /**
     * AUX 位
     */
    public static final int COMMAND_LOC_AUX = 0x25;


    /**
     * 功能屏蔽 位
     */
    public static final int COMMAND_LOC_HIDE = 0x26;
    /**
     * 音量 位
     */
    public static final int COMMAND_LOC_YINLIANG = 0x28;
    /**
     * 主机状态 位
     */
    public static final int COMMAND_LOC_ZHUJI_ZHUANGTAI = 0x29;
    /**
     * 电话/求助 位
     */
    public static final int COMMAND_LOC_DIANHUA = 0x2a;
    /**
     * 摄氏度/华氏度 位
     */
    public static final int COMMAND_LOC_SHESHIDU_HUASHIDU = 0x2b;
    /**
     * 预约时间的开关 位
     */
    public static final int COMMAND_LOC_YUYUE_KAIGUAN = 0x2c;
    /**
     * 校验位 位
     */
    public static final int COMMAND_LOC_JIAOYAN = 0x2e;
    /**
     * 包尾位 位
     */
    public static final int COMMAND_LOC_BAOWEI = 0x2f;

}
