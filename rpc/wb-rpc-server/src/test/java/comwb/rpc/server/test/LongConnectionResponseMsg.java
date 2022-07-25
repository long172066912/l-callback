package comwb.rpc.server.test;


import java.util.Map;
import java.util.Set;

/**
 * 长连接消息
 */
public class LongConnectionResponseMsg {

    private int qwe;

    /**
     * 版本号 必须
     */
    private int ver;

    /**
     * 消息标识id
     */
    private String callId;

    /**
     * 状态
     */
    private int cd; // 0:成功

    /**
     * 状态描述
     */
    private String desc;

    /**
     * 创建时间
     */
    private long time = System.currentTimeMillis();

    /**
     * 与客户端交互的业务协议key
     */
    private String cmd;

    /**
     * 与客户端交互的业务协议具体内容
     */
    private Map<String, Object> body;

    /**
     * 黑名单
     */
    private Set<String> blackList;

    public LongConnectionResponseMsg() {
    }

    public LongConnectionResponseMsg(String cmd, Map<String, Object> body) {
        this.cmd = cmd;
        this.body = body;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public Set<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(Set<String> blackList) {
        this.blackList = blackList;
    }

    public int getQwe() {
        return qwe;
    }

    public LongConnectionResponseMsg setQwe(int qwe) {
        this.qwe = qwe;
        return this;
    }
}
