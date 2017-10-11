package com.oralce.client.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import java.util.Map.Entry;

import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.common.resources.Resources;
import com.oralce.client.manager.ConnectionPoolManager;
import com.oralce.client.pool.IConnectionPool;

public class Main {
	
	private static PreparedStatement pre = null;
	private static Connection conn = null;
	
	private static Scanner input = new Scanner(System.in);
	
	private static StringBuffer readmeStr = new StringBuffer()
		.append("...press 'Q' query sql\n")
		.append("...press 'E' execute sql\n")
		.append("...press 'I' import sql\n")
		.append("...press 'O' export sql\n")
		.append("...press 'DBLink' create dblink sql\n")
		.append("...press 'Quit' Quit\n");
	
	public static void main(String[] args) {
		
		// 初始化连接池
		try {
			Thread t = init();
			t.start();
			t.join();
			IConnectionPool pool = ConnectionPoolManager.getInstance().getPool("testPool");
			conn = pool.getConnection();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(readmeStr.toString());
		dispatcher(input.nextLine());
		
//		if (!line.toString().equals("I")) {
//			System.out.println("please input sql file Path:");
//			line = input.nextLine();
//			File f = new File(line);
//			if (!f.exists()) {
//				System.out.println("sqlfile not exist.press 'H' to query sql,plese 'Q' to quit");
//				line = input.nextLine();
//				if (line.equals("H")){
//					
//				}
//			}
//		}
		try {
			if (!conn.isClosed()){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.exit(0);
	}

	/**
	 * 派发不同的功能
	 * @param option
	 */
	private static void dispatcher(String option) {
		switch (option) {
			case "Q":
				try {
					querySQL();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "E":
				try {
					executeSQL();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "DBLink":
				try {
					createDBLink();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "I":
				try {
					importSQL();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "Quit":
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				System.exit(0);
			default:
				break;
		}
		System.out.println(readmeStr.toString());
		dispatcher(input.nextLine());
	}
	
	private static void importSQL(){
		
		System.out.println("please input sqlfile path:");
		String fileName = input.nextLine();
		File f = new File(fileName);
		if (!f.exists()){
			System.out.println("sql file is not exist");
			return;
		}
		
		ScriptRunner runner = new ScriptRunner(conn,false,false);
		runner.setErrorLogWriter(null);  
		runner.setLogWriter(null);  
		
		try {
			runner.runScript(new FileReader(f));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	
	/**
     * 传入连接来执行 SQL 脚本文件，这样可与其外的数据库操作同处一个事物中
     * @param conn 传入数据库连接
     * @param sqlFile SQL 脚本文件
     * @throws Exception
     */
    public void execute(Connection conn, File sqlFile) throws Exception {
        Statement stmt = null;
        List<String> sqlList = FileUtils.readLines(sqlFile,"UTF-8");
        stmt = conn.createStatement();
        for (String sql : sqlList) {
            stmt.addBatch(sql);
        }
        int[] rows = stmt.executeBatch();
        System.out.println("Row count:" + Arrays.toString(rows));
    }
    
	private static void executeSQL() throws Exception{
		System.out.println("please input execute sql:");
		String sql = input.nextLine();
		pre = conn.prepareStatement(sql);
		
		System.out.println("Row count:" + pre.executeUpdate()); 
	}
	
	private static void createDBLink() throws Exception{
		pre = conn.prepareStatement("create database link DB_MYNJ1 connect to UNION_CERT_TEST identified by UNION_CERT_TEST using '(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = 10.196.108.195)(PORT = 1521)))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = orcl)))'");
		
		System.out.println("Row count:" + pre.executeUpdate()); 
	}
	
	private static void querySQL() throws Exception{
		System.out.println("please input query sql:");
		String sql = input.nextLine();
		pre = conn.prepareStatement(sql);
		
		ResultSet rs = pre.executeQuery();
		
		for (Map<String, Object> resultMap : resultSetToList(rs)) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				System.out.print(resultMap.get(rs.getMetaData().getColumnName(i)) + "	");
			}
			System.out.println();
		}
	}
	public static List<Map<String, Object>> resultSetToList(ResultSet rs) throws java.sql.SQLException {
		if (rs == null) {
			return Collections.EMPTY_LIST;
		}
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			System.out.print(rs.getMetaData().getColumnName(i) + "	");
		}
		System.out.println();
		ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
		int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> rowData = new HashMap<>();
		while (rs.next()) {
			rowData = new HashMap<>(columnCount);
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}

	// 初始化
	public static Thread init() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				IConnectionPool pool = initPool();
				while (pool == null || !pool.isActive()) {
					pool = initPool();
				}
			}
		});
		return t;
	}

	public static IConnectionPool initPool() {
		return ConnectionPoolManager.getInstance().getPool("testPool");
	}
}
