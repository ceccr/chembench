package edu.unc.ceccr.test;

import java.sql.SQLException;

import org.hibernate.HibernateException;

public class CreateUser {
	public static void main(String[] args) throws HibernateException, ClassNotFoundException, SQLException {
//		SignUpAction.createDB("new");
//		SignUpAction.createDB("admin");
//		Sig
//		SignUpAction.createDB("weifan");
//		SignUpAction.createDB("diane");
//		SignUpAction.createDB("mihir");
//		SignUpAction.createDB("chris");
//		SignUpAction.createDB("berk");
//		SignUpAction.createDB("sasha");
//		SignUpAction.createDB("test");	
		
		//boolean testWorked = NewDBTest.queryUsers("julia");
		//System.out.println("**Test Worked: " + testWorked);
		
		NewDBTest.queryPredictors("ap1_43.sdf", "ap1_43.act");
	}
}