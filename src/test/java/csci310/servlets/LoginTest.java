package csci310.servlets;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

import csci310.CreateStockTable;
import csci310.CreateUserTable;
import csci310.DropStockTable;
import csci310.DropUserTable;
import csci310.SQL;
import io.cucumber.java.Before;


public class LoginTest extends Mockito {
	@Before
	public void resetUserTable() {
		DropUserTable du = new DropUserTable();
		CreateUserTable cu = new CreateUserTable();
	}
	
	
	@Test
	public void testDoPost() throws Exception {
		
	SQL.register("hyunjae","Hj1234");
		
	HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        when(request.getParameter("username")).thenReturn("hyunjae");
        when(request.getParameter("password")).thenReturn("Hj1234");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        
        new Login().doPost(request, response);
        
        writer.flush();
        
        assertTrue(stringWriter.toString().contains("Welcome, " + "hyunjae"));
        
        
        
        HttpServletRequest request2 = mock(HttpServletRequest.class);       
        HttpServletResponse response2 = mock(HttpServletResponse.class);
        
        when(request2.getParameter("username")).thenReturn("hyunjae");
        when(request2.getParameter("password")).thenReturn("Hj1");
        
        StringWriter stringWriter2 = new StringWriter();
        PrintWriter writer2 = new PrintWriter(stringWriter2);
        when(response2.getWriter()).thenReturn(writer2);
        
        new Login().doPost(request2, response2);
        
        assertTrue(stringWriter2.toString().contains("Incorrect username or password. Please try again! :)"));
        
        
        //trigger lock out
        Login lg = new Login();
        lg.doPost(request2, response2);
        lg.doPost(request2, response2);
        lg.doPost(request2, response2);
        assertTrue(stringWriter2.toString().contains("user locked"));
		
	}

}
