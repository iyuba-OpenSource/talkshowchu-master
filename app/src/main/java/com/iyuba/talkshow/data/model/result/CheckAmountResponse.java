package com.iyuba.talkshow.data.model.result;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Administrator on 2016/12/19/019.
 */

@Root(name = "response", strict = false)
public class CheckAmountResponse {
    @Element(name = "result", required = false)
    int result;
    @Element(name = "message", required = false)
    String message;
    @Element(name = "amount", required = false)
    int amount;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
