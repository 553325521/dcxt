package cn.wifiedu.ssm.util.waimai.down;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;

import cn.wifiedu.ssm.util.waimai.SignUtil;

public class Result {
   
    private Map<String, Object> result;

    public Result(Map<String, Object> result) {
        this.result = result;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    /**
     * 设置下行order.create接口值
     */
    public void setCreateResult(Map<String, Object> config, boolean flag, String ZHYOrderId) {
        Map<String, Object> body = new HashMap<String, Object>();
        if (flag) {
            body.put("errno", "0");
            body.put("error", "success");
            Map<String, Object> source_order_id = new HashMap<String, Object>();
            Random rand = new Random(Integer.MAX_VALUE);
            source_order_id.put("source_order_id", ZHYOrderId);
            body.put("data", source_order_id);
        } else {
            body.put("errno", "-1");
            body.put("error", "check sign failed!");
            Map<String, Object> source_order_id = new HashMap<String, Object>();
            Random rand = new Random(Integer.MAX_VALUE);
            source_order_id.put("source_order_id", System.currentTimeMillis() + rand.nextInt(Integer.MAX_VALUE));
            body.put("data", source_order_id);
        }
        result.put("body", JSON.toJSON(body));
        result.put("cmd", "resp.order.create");
        String sign = SignUtil.getSign(result, config);
        result.put("body", body);
        result.put("sign", sign);
    }

    /**
     * 设置下行order.status.push接口值
     */
    public void setPushStatusResult(Map<String, Object> config, boolean flag) {
        Map<String, Object> body = new HashMap<String, Object>();
        if (flag) {
            body.put("errno", "0");
            body.put("error", "success");
        } else {
            body.put("errno", "-1");
            body.put("error", "check sign failed!");
        }
        result.put("body", JSON.toJSON(body));
        result.put("cmd", "resp.order.status.push");
        String sign = SignUtil.getSign(result, config);
        result.put("sign", sign);
        result.put("body", body);
    }

    /**
     * 设置下行order.cancle接口值
     */
    public void setCancelResult(Map<String, Object> config, boolean flag) {
        Map<String, Object> body = new HashMap<String, Object>();
        if (flag) {
            body.put("errno", "0");
            body.put("error", "success");
        } else {
            body.put("errno", "-1");
            body.put("error", "check sign failed!");
        }
        result.put("body", JSON.toJSON(body));
        result.put("cmd", "resp.order.user.cancel");
        String sign = SignUtil.getSign(result, config);
        result.put("sign", sign);
        result.put("body", body);
    }

    /**
     * 设置下行order.partrefund.push接口值
     */
    public void setPartRefundPushResult(Map<String, Object> config, boolean flag) {
        Map<String, Object> body = new HashMap<String, Object>();
        if (flag) {
            body.put("errno", "0");
            body.put("error", "success");
        } else {
            body.put("errno", "-1");
            body.put("error", "check sign failed!");
        }
        result.put("body", JSON.toJSON(body));
        result.put("cmd", "resp.order.partrefund.push");
        String sign = SignUtil.getSign(result, config);
        result.put("sign", sign);
        result.put("body", body);
    }
}
