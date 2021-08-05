package com.openbravo.pos.sales.shared;

import java.awt.Component;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.forms.AppViewConnection;
/*import com.openbravo.data.loader.JDBCSentence.JDBCDataResultSet;
import com.openbravo.data.loader.PreparedSentence.PreparedSentencePars;*/
import com.openbravo.pos.payment.PaymentInfo;
import com.openbravo.pos.sales.DataLogicReceipts;
import com.openbravo.pos.sales.JPanelTicket;
import com.openbravo.pos.sales.JTicketsBag;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.ticket.UserInfo;
import com.openbravo.pos.util.AltEncrypter;

import net.communication.RmiServerIntf;

public class AttenderSaleBagMain   {

	private static final long serialVersionUID = 1L;
	static AttenderTicket ticket = null;
	static ProductInfoExt productInfo = null;
	static AppConfig config=null;
	static String[] arg= {"C:\\Users\\An.Mohammed\\unicentaopos.properties"};
	static Session session = null;
	

	public static Session createDBSession()
	{
		if (session == null) {
			try {
 
				String sDBURL = config.getProperty("db1.URL");
				String sDBShema = config.getProperty("db.schema");
				String sDBUser = config.getProperty("db.user");
				String sDBPassword = config.getProperty("db.password");
				if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
					AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
					sDBPassword = cypher.decrypt(sDBPassword.substring(6));
				}
				
				// Session("jdbc:mysql://localhost:3306/unicentaoposmaster?useSSL=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT",
				// "root123","root@123");

				session = new Session(
						sDBURL + sDBShema
								+ "?useSSL=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT",
						sDBUser, sDBPassword);

			} catch (SQLException e) {
				e.printStackTrace();
			}

			return session;
		} else {
			return session;
		}
	}
	
	public static void main(String[] args) throws BasicException, InterruptedException  {
	
		System.out.println("AttenderSaleBagMain");
		
    	
		//if you read argument else
		if (args.length == 0) {
			

			String oldmess = "";
	        
			//continuously reading the data from server/service
	    	while(true){
	    		
				try {
					RmiServerIntf server = (RmiServerIntf)Naming.lookup("//localhost/AppController");
					//System.out.println("server::"+server.getMessage());
					if(!(server.getMessage()).equals(oldmess)){
						oldmess= server.getMessage();
					     System.out.println("AttenderSaleBagMain:::"+server.getMessage());
						//Thread.sleep(2000);
					     String[] arg = oldmess.split(" ");

							HashMap<String, String> productCodeWithUnit = new HashMap<String, String>();
							String customerName = arg[0];
							System.out.println("CustomerName:" + customerName);
							String[] product;
							for (int i = 1; i < arg.length; i++) {
								product = arg[i].split(":");
								productCodeWithUnit.put(product[0].toString(), product[1].toString());
							}
							attenderBag(productCodeWithUnit, customerName);
							
					}
				} catch (MalformedURLException e) {
					
					System.out.println(e.getMessage());
					System.out.println("Waiting for Connection.");
					System.out.println();
					
					Thread.sleep(60000);
				} catch (RemoteException e) {
					System.out.println(e.getMessage());
					System.out.println("Waiting for connection or request.....");
					System.out.println();
					
					Thread.sleep(30000);
			
				} catch (NotBoundException e) {
					System.out.println(e.getMessage());
					System.out.println("Waiting for Connection..");
					System.out.println();
					
					Thread.sleep(60000);
				}
			
	        
	    	}
			//if you doing multiple 
	    	/*
			
			Scanner sc = new Scanner(System.in);
			boolean flag = true;
			
			while (flag) {
				String strs = sc.nextLine();
				if (!strs.equalsIgnoreCase("quit")) {
					String[] arg = strs.split(" ");

					HashMap<String, String> productCodeWithUnit = new HashMap<String, String>();
					String customerName = arg[0];
					System.out.println("CustomerName:" + customerName);
					String[] product;
					for (int i = 1; i < arg.length; i++) {
						product = arg[i].split(":");
						productCodeWithUnit.put(product[0].toString(), product[1].toString());
					}
					attenderBag(productCodeWithUnit, customerName);
				} else {
					flag = false;
				}
			}
		*/}
		else{
			//while(true){
			HashMap<String, String> productCodeWithUnit = new HashMap<String, String>();
			String customerName = args[0];
			System.out.println("CustomerName:" + customerName);
			String[] product;
			for (int i = 1; i < args.length; i++) {
				product = args[i].split(":");
				productCodeWithUnit.put(product[0].toString(), product[1].toString());
				}
			attenderBag(productCodeWithUnit, customerName);
			
		//}
		}
	
	}
	public static boolean pushtoPOS(String request){
		
		System.out.println("request===========>"+request);
		HashMap<String, String> productCodeWithUnit = new HashMap<String, String>();
		
		String[] args = null; 
		if(request!=null){
		args = request.split(" ");
		}
		
		String customerName = args[0];
		System.out.println("CustomerName:" + customerName);
		String[] product;
		for (int i = 1; i < args.length; i++) {
			product = args[i].split(":");
			productCodeWithUnit.put(product[0].toString(), product[1].toString());
			}
		try {
			attenderBag(productCodeWithUnit, customerName);
			return true;
		} catch (BasicException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public static void attenderBag(HashMap<String, String> productCodeWithUnit,String customerName) throws BasicException {
		
		
		//load the config file and create the session
		locadConfig();
		
		//HashMap<String, String> productCodeWithUnit = new HashMap<String, String>();
		
		//product code and product units
		//productCodeWithUnit.put("301","1");
		//productCodeWithUnit.put("203","2");
		
		
		//String productCode = "203";
		//String customerName = "Customer2";
		
		//double units = 1;
		
		HashMap<String, ProductInfoExt> productInfoExtList = new HashMap<String, ProductInfoExt>();
		HashMap<String, TaxInfo> taxInfoList = new HashMap<String, TaxInfo>();
			
		for (Map.Entry<String, String> productCode : productCodeWithUnit.entrySet()) {
				
			ProductInfoExt productInfoExt = getProductDetails(productCode.getKey(), customerName);
			System.out.println("productCode.getKey()===>"+productCode.getKey()+", productInfoExt.getName()==>"+productInfoExt.getReference());
			productInfoExtList.put(productInfoExt.getReference(), productInfoExt);
			
			TaxInfo taxInfo = taxInfo();
			taxInfoList.put(productInfoExt.getReference(), taxInfo);
		}
			
		
		//ProductInfoExt productInfoExt = getProductDetails(productCode, customerName);
		//TaxInfo taxInfo = taxInfo();
		CustomerInfo customerInfo = attenderCustomerInfo();
		
		
		
		createSharedTicket(productInfoExtList, taxInfoList, customerInfo, productCodeWithUnit);    
     	
		
	}
	
	
	private static CustomerInfo attenderCustomerInfo() {
		if(productInfo != null){
			
			return ticket.attenderCustomerInfo();
			
		}
		return null;
	}

	private static ProductInfoExt getProductDetails(String productCode, String name) {
		//Get the product Details and set it  
		 ticket = new AttenderTicket(productCode, name);
		 
		 productInfo = ticket.attenderSaleProduct();
		
		//System.out.println("Id:"+productInfo.getID());
		//System.out.println("Name:"+productInfo.getName());
		return productInfo;
	}
	
	private static TaxInfo taxInfo(){
		if(productInfo != null){
			TaxInfo taxInfo = ticket.attenderSalesTax();
			System.out.println("TaxInfo::"+taxInfo.getName());
			return taxInfo;
		}
		return null;
	}
	
	public static void locadConfig() throws BasicException{
		if(null == config){
			
		 config = new AppConfig(arg);
		 config.load();
		 
		 createSession(config);
		}
	}
	public static void createSession(AppProperties props) throws BasicException{
		if(null == session){
		 session = AppViewConnection.createSession(props); 
		 System.out.println(""+session);
		}
	}
	
	public static void createSharedTicket(HashMap<String, ProductInfoExt> productInfoExtList, HashMap<String, TaxInfo> taxInfoList, CustomerInfo customerInfo, HashMap<String, String> productCodeWithUnit){
		TicketInfo ticket = new TicketInfo();    
        ticket.setLoyaltyCardNumber("");
        List<TicketLineInfo>  m_aLines = new ArrayList<>();
        int _m_line = 0;
        Properties attributes = null;
        
        TicketLineInfo m_aLinesTicketLineInfo=null;
        String ticketId=ticket.getId();//UUID.randomUUID().toString();
        System.out.println("ticketId:"+ticketId);
		for (Map.Entry<String, ProductInfoExt> productInfoObject : productInfoExtList.entrySet()) {
			String pCode = productInfoObject.getKey();
			ProductInfoExt product = productInfoObject.getValue();
			attributes = new Properties();
			
			double dMultiply = 1.0;
			double dPrice = product.getPriceSell();
			  
			for (Map.Entry<String, TaxInfo> taxInfotaxObject : taxInfoList.entrySet()) {

				
				if (taxInfotaxObject.getKey().equals(pCode)) {
					TaxInfo taxInfo = taxInfotaxObject.getValue();
					 m_aLinesTicketLineInfo = new TicketLineInfo(product, dMultiply, dPrice, taxInfo, attributes);
				
					 	
				        for(Map.Entry<String, String> units: productCodeWithUnit.entrySet()){
				        	
				        	if(units.getKey().equals(pCode)){
				        		m_aLinesTicketLineInfo.setMultiply(Double.parseDouble(units.getValue()));
				        		break;//units match then exit from loop
				        	}
				        
				        }
				        
				        
				        m_aLinesTicketLineInfo.setProductAttSetInstId(null);
				      
				        m_aLinesTicketLineInfo.setTicket(ticketId, _m_line++);//m_sTicket, _m_line
				        //m lines contain arraylist data 
				        m_aLines.add(m_aLinesTicketLineInfo);
				        break;//taxes match then exit from loop
				}
				
			}
		}
		
        /*Properties attributes1 = new Properties();
        double dMultiply1 = 1.0;
        double dPrice1 = product.getPriceSell();
        TicketLineInfo m_aLinesTicketLineInfo1 = new  TicketLineInfo(  product,  dMultiply1,  dPrice1,  taxInfotax,  attributes1);
        */
       
       // m_aLines.add(m_aLinesTicketLineInfo1);
        
        ticket.setLines(m_aLines);
        
        ticket.setCustomer(null);
        ticket.setDate(new Date());
        ticket.setPickupId(0);
        ticket.setIsProcessed(false);
       //m_locked,m_response in constructor null
        
        ticket.setPayments(null);
        String activeCash=UUID.randomUUID().toString();
        System.out.println("activeCash:::"+activeCash);
        ticket.setActiveCash(activeCash);
        ticket.setHostname(null);
        ticket.getId();//created in the constructor 
        System.out.println("msID :ticket.getId() ::"+ticket.getId());
        //UserInfo userInfo = new UserInfo("2", "Employee");
        UserInfo userInfo = new UserInfo("0", "Administrator");
        ticket.setUser(userInfo);
        //multiple,nsum not using 
        ticket.setOldTicket(false);
        
        
        if(null != customerInfo && "" != customerInfo.getName()){
        CustomerInfoExt customerInfoExt =new CustomerInfoExt(customerInfo.getId());
        customerInfoExt.setName(customerInfo.getName());
        ticket.setCustomer(customerInfoExt);
        }
        
        List<PaymentInfo> l = new ArrayList<>();
        ticket.setPayments(null);
        ticket.setTaxes(null);
        ticket.setTicketType(0);
        ticket.setTip(false);
        
        
        //end ticket 
        DataLogicReceipts dataLogicReceipts = new DataLogicReceipts();
        try {
        	String sharedId = UUID.randomUUID().toString();
        	System.out.println("sharedId::"+sharedId);
			dataLogicReceipts.insertSharedTicket(sharedId, ticket, 0);
		} catch (BasicException e) {
			e.printStackTrace();
		}
    
	}
	
		
}

 class AttenderTicket extends JPanelTicket{

	 AttenderTicket(){
	 }
	 
	 AttenderTicket(String code, String name){
			super(code, name); 
		 }
		 
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JTicketsBag getJTicketsBag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Component getSouthComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void resetSouthComponent() {
		// TODO Auto-generated method stub
		
	}
	

 
}
