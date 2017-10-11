package com.oralce.client.pool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.oralce.client.bean.DBbean;

public class DBInitInfo {
	public static List<DBbean> beans = null;
	static {
		
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("database.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		beans = new ArrayList<DBbean>();
		// �������� ���Դ�xml �������ļ����л�ȡ
		// Ϊ�˲��ԣ�������ֱ��д��
		DBbean beanOracle = new DBbean();
		beanOracle.setDriverName("oracle.jdbc.driver.OracleDriver");
		beanOracle.setUrl(prop.getProperty("db.url"));
		beanOracle.setUserName(prop.getProperty("db.name"));
		beanOracle.setPassword(prop.getProperty("db.password"));

		beanOracle.setMinConnections(5);
		beanOracle.setMaxConnections(100);

		beanOracle.setPoolName("testPool");
		beans.add(beanOracle);
	}

	public static void main(String[] args) {
		System.out.println(beans.get(0).getUserName());
	}
}
