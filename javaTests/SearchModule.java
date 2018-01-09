package com.eInnovate.search;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.eInnovate.util.StringUtil;
import com.eInnovate.util.JDBCPool;
import com.eInnovate.util.Debugger;

import javax.sql.rowset.CachedRowSet;
import com.sun.rowset.CachedRowSetImpl;

import org.apache.log4j.Logger;

public class SearchModule {
	private static boolean isSupplierDefectTableCreated = false;

	private static Logger log = Logger.getLogger("SearchModule");

	public static String SEARCH_PROFILE_COMPANIES = "";

	/*
	 * Returns a cachedResultSet consisting of the following: 1. incidentId 2.
	 * ownerName 3. creationDate 4. status 5. custAssyPartNumber 6.
	 * engReviewCreated 7. materialReviewCreated 8. mrbReviewCreated 9.
	 * isScrapped 10. repairEstCreated 11. engReviewRequired 12.
	 * mrbReviewRequired public static CachedRowSet
	 * getOpenSupplierDefects(String compId, String boaId, String incidentType,
	 * boolean forSupplier) throws Exception { log.debug("getOpenSupplierDefects
	 * starting"); String tableName = ""; CachedRowSet cachedRS = null;
	 * 
	 * Connection conn = null; Statement stmt = null; ResultSet rSet = null;
	 * conn = JDBCPool.getConnection(); ArrayList incIds = new ArrayList();
	 * StringBuffer stmtBuf = new StringBuffer("SELECT INCIDENTID, ownerName,
	 * creationDate, status, custAssyPartNumber, engReviewCreated,
	 * materialReviewCreated, mrbReviewCreated, isScrapped, repairEstCreated,
	 * engReviewRequired, mrbReviewRequired "); stmtBuf.append("FROM " +
	 * tableName + " "); stmtBuf.append("WHERE "); if( !forSupplier ) {
	 * stmtBuf.append("( (groupName = '"); stmtBuf.append(compId);
	 * stmtBuf.append("') ");
	 * if(!QualityIncidentBean.PRODUCTION_DEFECT.equals(incidentType)) {
	 * stmtBuf.append("OR ( supplierInitiated = 1 AND buyCompId = '");
	 * stmtBuf.append(compId); stmtBuf.append("') "); } stmtBuf.append(") "); }
	 * else // you are a supplier, so the search is different {
	 * //stmtBuf.append("( " + JDBCPool.QUAL_INCIDENT_SUPPID + " = '");
	 * stmtBuf.append(compId); stmtBuf.append("' ) "); } if(
	 * !StringUtil.isEmpty(boaId) ) { stmtBuf.append("AND ");
	 * if(!QualityIncidentBean.PRODUCTION_DEFECT.equals(incidentType)) {
	 * stmtBuf.append("BUYERBOAID"); } else { stmtBuf.append("SUPPLIERBOAID"); }
	 * stmtBuf.append(" = '" + boaId + "' "); } stmtBuf.append(" AND ISOPEN = 1
	 * "); if(forSupplier &&
	 * !QualityIncidentBean.PRODUCTION_DEFECT.equals(incidentType) ) {
	 * stmtBuf.append("AND MATERIALREVIEWC = 1 "); } stmtBuf.append("ORDER BY
	 * CREATIONDATE ASC"); log.info("getOpenSupplierDefects() statement = " +
	 * stmtBuf.toString()); try { stmt = conn.createStatement(); rSet =
	 * stmt.executeQuery(stmtBuf.toString()); cachedRS = new CachedRowSetImpl();
	 * cachedRS.populate(rSet); }//try catch(Exception e) {
	 * log.error("getOpenSupplierDefects Exception " + e); throw new
	 * Exception("Internal Error. Please contact eInnovate customer support"); }
	 * finally { try { if (stmt != null) stmt.close(); if (conn != null)
	 * conn.close(); } catch (java.sql.SQLException sqle) {
	 * log.error("getQualityEngineerSupplierDefectIds SQLE = " + sqle); } }
	 * return cachedRS; }
	 */

	public static CachedRowSet searchForPotentialRfqSuppliers(HashMap searchHash)
			throws Exception {
		log.info("searchForPotentialRfqSuppliers()");
		Debugger.dumpHashMap(searchHash);
		CachedRowSet cachedRS = null;
		Statement statementObj = null;
		ResultSet resultSetObj = null;
		Connection connectObj = JDBCPool.getConnection();
		StringBuffer sqlString = new StringBuffer();
		sqlString
				.append("SELECT COMPANYID, NAME, URL, KEYWORDS, CONTACTEMAIL, TELEPHONE, DESCRIPTION FROM company WHERE ");
		String keywords = (String) searchHash.get("keywords");
		if (keywords != null) {
			sqlString.append("(");
			int length = keywords.length();
			log.info("searchForPotentialRfqSuppliers()  keyword length = "
					+ length);
			int start = 0;
			String subString = null;
			boolean needOR = false;
			for (int x = 0; x < length; x++) {
				// Test for last character
				if (length - x == 1) {
					if (needOR)
						sqlString.append(" OR ");
					subString = keywords.substring(start, x + 1);
					sqlString.append(" KEYWORDS LIKE '%"
							+ subString.toUpperCase() + "%'");
					start = x + 1;
				} else if (keywords.charAt(x) == ' ') // Test for space
				{
					if (needOR)
						sqlString.append(" OR ");
					subString = keywords.substring(start, x);
					sqlString.append(" KEYWORDS LIKE '%"
							+ subString.toUpperCase() + "%'");
					while (keywords.charAt(x + 1) == ' ' && x < length)
						x = x + 1;
					start = x + 1;
					needOR = true;
				}
			}
			sqlString.append(")");
		}
		if (searchHash.get("companyName") != null)
			sqlString.append(" AND NAME LIKE '%"
					+ (String) searchHash.get("companyName") + "%'");
		if (searchHash.get("city") != null)
			sqlString.append(" AND CITY = '" + (String) searchHash.get("city")
					+ "'");

		if (searchHash.get("state") != null)
			sqlString.append(" AND STATE = '"
					+ (String) searchHash.get("state") + "'");

		if (searchHash.get("zipCode") != null)
			sqlString.append(" AND ZIPCODE = '"
					+ (String) searchHash.get("zipCode") + "'");
		if (searchHash.get("areaCode") != null)
			sqlString.append(" AND AREACODE = '"
					+ (String) searchHash.get("areaCode") + "'");

		log.info("searchForPotentialRfqSuppliers()  sqlstring = "
				+ sqlString.toString());
		try {
			statementObj = connectObj.createStatement();
			resultSetObj = statementObj.executeQuery(sqlString.toString());
			cachedRS = new CachedRowSetImpl();
			cachedRS.populate(resultSetObj);
		} catch (Exception e) {
			log.error("getOpenSupplierDefects Exception " + e);
			e.printStackTrace();
			throw new Exception(
					"Internal Error.  Please contact eInnovate customer support");
		} finally {
			try {
				if (statementObj != null)
					statementObj.close();
				if (connectObj != null)
					connectObj.close();
			} catch (java.sql.SQLException sqle) {
				log.error("getQualityEngineerSupplierDefectIds SQLE = " + sqle);
			}
		}
		return cachedRS;
	}

	public static CachedRowSet listUsersInCompany(String companyId)
			throws Exception {
		log.info("listUsersInCompany() companyId = " + companyId);
		CachedRowSet cachedRS = null;
		Statement statementObj = null;
		ResultSet resultSetObj = null;
		Connection connectObj = JDBCPool.getConnection();
		StringBuffer sqlString = new StringBuffer();
		sqlString
				.append("SELECT USERID, FIRSTNAME, LASTNAME, EMAIL FROM user WHERE COMPANYID = '");
		sqlString.append(companyId);
		sqlString.append("'");

		log.info("listUsersInCompany()  sqlstring = " + sqlString.toString());
		try {
			statementObj = connectObj.createStatement();
			resultSetObj = statementObj.executeQuery(sqlString.toString());
			cachedRS = new CachedRowSetImpl();
			cachedRS.populate(resultSetObj);
		} catch (Exception e) {
			log.error("listUsersInCompany() Exception " + e);
			e.printStackTrace();
			throw new Exception(
					"Internal Error.  Please contact eInnovate customer support");
		} finally {
			try {
				if (statementObj != null)
					statementObj.close();
				if (connectObj != null)
					connectObj.close();
			} catch (java.sql.SQLException sqle) {
				log.error("listUsersInCompany() SQLE = " + sqle);
			}
		}
		return cachedRS;
	}

	/**
	 * searchForProfileCompanies
	 * 
	 * @author JABride
	 */
	public static CachedRowSet searchForProfileCompanies(HashMap searchHash,
			boolean isRfq) throws Exception {
		log.info("searchForProfileCompanies()");
		Connection connectObj = null;
		Statement statementObj = null;
		CachedRowSet cachedRS = null;
		ResultSet resultSetObj = null;
		connectObj = JDBCPool.getConnection();
		String sqlString = "SELECT COMPANYID, NAME, URL,  KEYWORDS, CONTACTEMAIL, TELEPHONE FROM company WHERE COMPANYID IS NOT NULL ";
		String keywords = (String) searchHash.get("keywords");
		if (keywords != null) {
			sqlString = sqlString + " AND (";
			int length = keywords.length();
			log.info("EISearchModule.searchProfile()  length = " + length);
			int start = 0;
			String subString = null;
			boolean needOR = false;
			for (int x = 0; x < length; x++) {
				// Test for last character
				if (length - x == 1) {
					if (needOR)
						sqlString = sqlString + " OR ";
					subString = keywords.substring(start, x + 1);
					sqlString = sqlString + " KEYWORDS LIKE '%"
							+ subString.toUpperCase() + "%'";
					start = x + 1;
				} else if (keywords.charAt(x) == ' ') // Test for space
				{
					if (needOR)
						sqlString = sqlString + " OR ";
					subString = keywords.substring(start, x);
					sqlString = sqlString + " KEYWORDS LIKE '%"
							+ subString.toUpperCase() + "%'";
					while (keywords.charAt(x + 1) == ' ' && x < length)
						x = x + 1;
					start = x + 1;
					needOR = true;
				}
			}
			sqlString = sqlString + ")";
		}

		/*
		 * if(searchHash.get("eiCode") != null) sqlString = sqlString + " AND
		 * EICODE = '" + (String)searchHash.get("eiCode") + "'";
		 */

		if (searchHash.get("sicCode") != null)
			sqlString = sqlString + " AND SICCODE = '"
					+ (String) searchHash.get("sicCode") + "'";

		if (searchHash.get("companyName") != null)
			sqlString = sqlString + " AND NAME LIKE '%"
					+ (String) searchHash.get("companyName") + "%'";
		if (searchHash.get("city") != null)
			sqlString = sqlString + " AND CITY = '"
					+ (String) searchHash.get("city") + "'";

		if (searchHash.get("state") != null)
			sqlString = sqlString + " AND STATE = '"
					+ (String) searchHash.get("state") + "'";

		if (searchHash.get("zipCode") != null)
			sqlString = sqlString + " AND ZIPCODE = '"
					+ (String) searchHash.get("zipCode") + "'";
		if (searchHash.get("areaCode") != null)
			sqlString = sqlString + " AND AREACODE = '"
					+ (String) searchHash.get("areaCode") + "'";

		/*
		 * Advanced Search Criteria if(searchHash.get("employeeCount") != null)
		 * sqlString = sqlString + " AND EMPLOYEECOUNT BETWEEN " +
		 * (String)searchHash.get("employeeCount");
		 * if(searchHash.get("salesActual") != null) sqlString = sqlString + "
		 * AND SALESACTUAL BETWEEN " + (String)searchHash.get("salesActual");
		 * if(searchHash.get("intlBiz") != null) sqlString = sqlString + " AND
		 * INTBIZ BETWEEN " + (String)searchHash.get("intlBiz");
		 * if(searchHash.get("ticker") != null) sqlString = sqlString + " AND
		 * TICKER = '" + (String)searchHash.get("ticker")+ "'";
		 * if(searchHash.get("femMinority") != null) sqlString = sqlString + "
		 * AND FEMMINORITY = '" + (String)searchHash.get("femMinority")+ "'";
		 */

		log.info("searchForProfileComp() sqlString = " + sqlString);
		try {
			statementObj = connectObj.createStatement();
			if (statementObj.execute(sqlString)) {
				int resultSize = 0;
				resultSetObj = statementObj.getResultSet();
				cachedRS = new CachedRowSetImpl();
				cachedRS.populate(resultSetObj);
			}
		} finally {
			try {
				if (statementObj != null)
					statementObj.close();
				if (connectObj != null)
					connectObj.close();
			} catch (SQLException x) {
				throw new Exception(
						"Problems closing statement and / or connection");
			}
		}
		return cachedRS;
	}
}
