package com.oralce.client.bean;

import lombok.Data;

@Data
public class DBbean {
	// ���ӳ�����  
    private String driverName;  
    private String url;  
    private String userName;  
    private String password;  
    // ���ӳ�����  
    private String poolName;  
    private int minConnections = 1; // ���гأ���С������  
    private int maxConnections = 10; // ���гأ����������  
      
    private int initConnections = 5;// ��ʼ��������  
      
    private long connTimeOut = 1000;// �ظ�������ӵ�Ƶ��  
      
    private int maxActiveConnections = 100;// ���������������������ݿ��Ӧ  
      
    private long connectionTimeOut = 1000*60*20;// ���ӳ�ʱʱ�䣬Ĭ��20����  
      
    private boolean isCurrentConnection = true; // �Ƿ��õ�ǰ���ӣ�Ĭ��true  
      
    private boolean isCheakPool = true; // �Ƿ�ʱ������ӳ�  
    private long lazyCheck = 1000*60*60;// �ӳٶ���ʱ���ʼ ���  
    private long periodCheck = 1000*60*60;// ���Ƶ��  
      
      
      
    public DBbean(String driverName, String url, String userName,  
            String password, String poolName) {  
        super();  
        this.driverName = driverName;  
        this.url = url;  
        this.userName = userName;  
        this.password = password;  
        this.poolName = poolName;  
    }  
    public DBbean() {  
    }  
    public String getDriverName() {  
        if(driverName == null){  
            driverName = this.getDriverName()+"_"+this.getUrl();  
        }  
        return driverName;  
    }  
    public void setDriverName(String driverName) {  
        this.driverName = driverName;  
    }  
   
}
