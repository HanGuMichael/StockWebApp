package csci310.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import csci310.Portfolio;
import csci310.SQL;
import csci310.servlets.RemoveStock.AddStockData;

@WebServlet("/UpdatePrices")
public class UpdatePrices extends HttpServlet{
	
	public class AddStockData{
		private JSONArray date = new JSONArray();
		private JSONArray price = new JSONArray();
		private JSONObject update = new JSONObject();
		private JSONArray SPV = new JSONArray();
		private double currentPortfolioValue;
		private int prevPortfolioValue;
		public AddStockData(JSONArray labels, JSONArray prices, JSONObject updates, JSONArray SPVP, double value, int value2) {
			date = labels;
			price = prices;
			update = updates;
			SPV = SPVP;
			currentPortfolioValue = value;
			prevPortfolioValue = value2;
		}
	}
	
	private Gson gson = new Gson();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String username = request.getParameter("username");
		String startDate = request.getParameter("startdate_graph");
        String endDate = request.getParameter("enddate_graph");
		String APIKey = "btjeu1f48v6tfmo5erv0";
		response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs=null;
		ResultSet rs2=null;
		HashSet<String> tickers =new HashSet<String>();
		try {
			if(username.equals("fakeusername")) {
				conn = DriverManager.getConnection("exception trigger test");
			}
			conn = DriverManager.getConnection("jdbc:sqlite:project.db");
			ps = conn.prepareStatement("SELECT * FROM users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				int userID = rs.getInt("userID");
				ps2=conn.prepareStatement("SELECT * FROM stocks WHERE userID=?");
				ps2.setInt(1, userID);
				rs2=ps2.executeQuery();
				while(rs2.next()) {
					String ticker1 =rs2.getString("ticker");
					tickers.add(ticker1);
				}
			}
		}catch(SQLException sqle) {
			System.out.println("sqle: "+sqle.getMessage());
		}
		Iterator<String> i = tickers.iterator();
		JSONObject updatedPrices = new JSONObject();
		while (i.hasNext()) {
			String ticker2 = i.next();
			//connect to API
	        String website1 = "https://finnhub.io/api/v1/quote?symbol="+ ticker2 
	        		+"&token=" + APIKey;
	        URL url1 = new URL(website1);
	  		HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
	  		con1.setRequestMethod("GET");
	  		con1.connect(); 
			
			//read json
	  		Scanner sc1 = new Scanner(url1.openStream());
	  		String result1 = "";
	  		while(sc1.hasNext()) result1 += sc1.nextLine();
	  		sc1.close();
	  		//System.out.println(result);
	  		JSONObject obj = new JSONObject(result1);
  			double currentPrice = obj.getDouble("c");
  			System.out.println(ticker2 +" "+ currentPrice);
  			updatedPrices.put(ticker2,currentPrice);
		}
		PrintWriter out = response.getWriter();
        //response.setContentType("application/json");
        //response.setCharacterEncoding("UTF-8");
        //out.print(updatedPrices);
	    //out.flush(); 
	    Portfolio p = new Portfolio(username, startDate, endDate);
        if(!username.equals("fakeusername")) {
  		try {
  			ps = conn.prepareStatement("SELECT * FROM users WHERE username=?");
  			ps.setString(1,username);
  			rs = ps.executeQuery();
  			if(rs.next()) {
  				int userID = rs.getInt("userID");
  				ps2=conn.prepareStatement("SELECT * FROM stocks WHERE userID=?");
  				ps2.setInt(1, userID);
  				rs2=ps2.executeQuery();
  				while(rs2.next()) {
  					String ticker2 =rs2.getString("ticker");
  					String dayPurchase2 = rs2.getString("dayPurchase");
  					String daySold2 = rs2.getString("daySold");
  					int quantity2 = rs2.getInt("quantity");
  					p.addStock(ticker2, quantity2, dayPurchase2, daySold2);
  				}
  			}
  			p.populatePortfolioValue();
  			JSONArray price = new JSONArray();
  			JSONArray date = new JSONArray();
  			for(int j =0;j<p.tradingDate.length;j++) {
  				price.put(p.portfolioValue[j]);
  				date.put(p.tradingDate[j]);
  			}
  			
  			long startDateEpoch = 0;
  	        long endDateEpoch = 0;
  	        try {
  	        	//currTime = System.currentTimeMillis()/1000;
  	        	startDateEpoch = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(startDate+" 22:00:00").getTime() / 1000;
  	        	endDateEpoch = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(endDate+" 22:00:00").getTime() / 1000;
  			} catch (ParseException e) {
  				//e.printStackTrace();
  			}
  	        JSONObject prices = new JSONObject();

  			String website = "https://finnhub.io/api/v1/stock/candle?symbol=SPY&resolution=D&from=" + (long)(startDateEpoch-86400) + 
	        		"&to=" + endDateEpoch + "&token=" + APIKey;
	        URL url = new URL(website);
	  		HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  		con.setRequestMethod("GET");
	  		con.connect();
			
	  		//read json
	  		Scanner sc = new Scanner(url.openStream());
	  		String result = "";
	  		while(sc.hasNext()) result += sc.nextLine();
	  		sc.close();
	  		//System.out.println(result);
	  	
			JSONObject obj = new JSONObject(result);
			JSONArray c = obj.getJSONArray("c");
			JSONArray t = obj.getJSONArray("t");
			int length = c.length();
			JSONArray price1 = new JSONArray();
	
			for(int j=0; j<length; j++) {
				price1.put(c.getDouble(j));
			}
			double temp = p.getCurrPortfolioValue();
			AddStockData asd = new AddStockData(date,price,updatedPrices,price1, temp, (temp==0)?0:(int)(temp*100/p.getPrevPortfolioValue())-100);
  		    response.setContentType("application/json");
  		    response.setCharacterEncoding("UTF-8");
  		    
  		    out.print(this.gson.toJson(asd));
  		    System.out.println(this.gson.toJson(asd).toString());
  		    out.flush(); 
  			
  		}catch(SQLException sqle) {
  			System.out.println("sqle: "+sqle.getMessage());
  		} catch (ParseException e) {}
        }
		try {
			if(rs!=null) {rs.close();}
			if(rs2!=null) {rs2.close();}
			if(ps!=null) {ps.close();}
			if(ps2!=null) {ps2.close();}
			if(conn!=null) {conn.close(); }
			if(username.equals("fakeusername")) {
				conn = DriverManager.getConnection("exception trigger test");
			}
		}catch(SQLException sqle) {
			System.out.println("sqle closing stuff: "+sqle.getMessage());
		}
	}

}
