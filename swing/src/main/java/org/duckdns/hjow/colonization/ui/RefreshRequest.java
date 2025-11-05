package org.duckdns.hjow.colonization.ui;

/** 화면 새로고침 큐에 들어갈 요청 항목 */
public class RefreshRequest {
    protected String request;
    protected int cycle;
    
    public RefreshRequest() {}

    public RefreshRequest(String request, int cycle) {
        this();
        this.request = request;
        this.cycle = cycle;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }
    
    
}
