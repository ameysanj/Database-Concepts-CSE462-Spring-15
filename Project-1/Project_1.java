package edu.buffalo.cse462;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.pool.OracleDataSource;


/**
 * 
 * @author Amey Mahajan
 *
 */

public class Project01_Main {


	public static void main(String[] args) {

		/*
		 * Connection with the database - AM
		 * 
		 */
		try {
			OracleDataSource ds = new OracleDataSource();
			ds.setUser(args[0]);
			ds.setPassword(args[1]);
			ds.setURL("jdbc:oracle:thin:@aos.acsu.buffalo.edu:1521/aos.buffalo.edu");
			Connection conn = ds.getConnection();
			Statement stmt = conn.createStatement();

			/**
			 * @author Amey Mahajan
			 * 
			 * Initial groundwork of view creations required for both the queries
			 * 
			 */

			/*
			 * View having the city details of the one mentioned in the args - AM
			 * 
			 */

			String mainCity = "Main_City";

			String view_Main_City = "CREATE VIEW "+mainCity+"(City, Phi,Lamda) AS "
					+"SELECT CITY, ((Latitude*3.14)/180), ((Longitude*3.14)/180)"
					+"FROM MAP "
					+ "WHERE CITY = " + "'" 
					+ args[3]+"'";

			/*
			 * View having all the cities except the one in the args - AM
			 * 
			 */

			String otherCities = "OtherCities";

			String view_Without_Main_City = "CREATE VIEW "+otherCities+"(City, Phi,Lamda) AS "
					+"SELECT CITY, ((Latitude*3.14)/180), ((Longitude*3.14)/180)"
					+"FROM MAP "
					+ "WHERE CITY <> " + "'" 
					+ args[3]+"'";

			/*
			 * View having haversines of locations - AM
			 * 
			 */

			String HaverViewName = "Haver";

			String Haver = "CREATE VIEW "+ HaverViewName +"(City,hav) AS "
					+ "SELECT O.City,(  (SIN((M.PHI - O.PHI)/2) * SIN((M.PHI - O.PHI)/2))+COS(M.PHI)*COS(O.PHI)*(SIN((M.LAMDA - O.LAMDA)/2)*SIN((M.LAMDA - O.LAMDA)/2)) ) "
					+ "FROM Main_City M,OtherCities O";

			/*
			 * View having distances in it - AM
			 * 
			 */

			String Distance_From_Main_City = "Distance_From_Main_City";

			String d_f_m = "CREATE VIEW "+Distance_From_Main_City+"(CITY,Distance) AS "
					+ "SELECT O.City , ( 12742 * aTAN2((SQRT(H.HAV)), (SQRT(1-H.HAV))) ) AS d "
					+ "FROM OtherCities O JOIN Haver H "
					+ "ON O.City = H.City";

			/*
			 * Execution of Queries that lead to the generation of view containing Distances - AM
			 * 
			 */
			ResultSet m = stmt.executeQuery(view_Main_City);
			ResultSet v = stmt.executeQuery(view_Without_Main_City);
			ResultSet h = stmt.executeQuery(Haver);
			ResultSet d = stmt.executeQuery(d_f_m);

			/**
			 * @author Amey Mahajan
			 *
			 * Project_1_Part_2_RANGE_QUERY ->
			 *
			 * Given the name of a city c and a radius r (expressed in kilo- meters) return all the cities whose distance from c is strictly less than r.
			 * 
			 * 
			 */
			if(args[2].equals("RANGE_QUERY")){

				/*
				 * RANGE_QUERY Query - AM
				 * 
				 */
				String Range_Query = "SELECT CITY, ROUND(Distance,2) AS Distance "
						+ "FROM Distance_From_Main_City "
						+"WHERE Distance <" + args[4]
								+ " ORDER BY DISTANCE ASC";

				/*
				 * Execution of the Queries pertaining to RANGE_QUERY Only - AM
				 * 
				 */
				ResultSet Range_Query_Answer = stmt.executeQuery(Range_Query);

				/*
				 * Printing the RANGE_QUERY answer - AM
				 * 
				 */
				while(Range_Query_Answer.next()){
					System.out.println(Range_Query_Answer.getString("City") + " " + Range_Query_Answer.getString("Distance"));
				}
			}

			/**
			 * @author Amey Mahajan
			 * 
			 * Project_1_Part_2_NN_QUERY ->
			 *
			 * Given the name of a city c and a strictly positive integer k return the k cities that are closest to c.
			 * 
			 */

			if(args[2].equals("NN_QUERY")){

				/*
				 * View Creation and Query for NN_Query - AM
				 * 
				 */
				String  Neighbours= "Neighbours";

				String View_For_City = "CREATE VIEW "+ Neighbours+ " AS "
						+ "SELECT D1.CITY, ROUND(D1.Distance,2) AS Distance "
						+ "FROM "+ Distance_From_Main_City +" D1 "
						+"ORDER BY DISTANCE ASC";

				String NN_Query = "SELECT CITY, ROUND(Distance,2) AS Distance "
						+ " FROM " + Neighbours
						+ " WHERE ROWNUM <= "+ args[4];

				/*
				 * Execution of Queries pertaining to NN_QUERY Only - AM
				 * 
				 */
				ResultSet n = stmt.executeQuery(View_For_City);
				ResultSet NN_Query_Answer = stmt.executeQuery(NN_Query);

				/*
				 * Printing the NN_QUERY Answer - AM
				 * 
				 */
				while(NN_Query_Answer.next()){
					System.out.println(NN_Query_Answer.getString("City") + " " + NN_Query_Answer.getString("Distance"));
				}

				/*
				 *  Dropping the View having Neighbours - AM
				 *  
				 */
				ResultSet d_Neighbours = stmt.executeQuery("DROP VIEW "+ Neighbours);
			}

			/*
			 * Dropping all the Views - AM
			 * 
			 */
			ResultSet d_Main = stmt.executeQuery("DROP VIEW "+ mainCity);
			ResultSet d_Others = stmt.executeQuery("DROP VIEW " + otherCities);
			ResultSet d_Haver = stmt.executeQuery("DROP VIEW "+ HaverViewName);
			ResultSet d_dfm = stmt.executeQuery("DROP VIEW "+ Distance_From_Main_City);

			stmt.close();
		}
		catch (SQLException e) {
			System.out.println("Could connect to the Oracle database");
			e.printStackTrace();
			System.exit(0);
		}


	}
}
