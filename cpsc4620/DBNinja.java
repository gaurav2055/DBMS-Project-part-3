package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/*
 * This file is where you will implement the methods needed to support this application.
 * You will write the code to retrieve and save information to the database and use that
 * information to build the various objects required by the application.
 *
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 *
 * This class also has static string variables for pickup, delivery and dine-in.
 * DO NOT change these constant values.
 *
 * You can add any helper methods you need, but you must implement all the methods
 * in this class and use them to complete the project.  The autograder will rely on
 * these methods being implemented, so do not delete them or alter their method
 * signatures.
 *
 * Make sure you properly open and close your DB connections in any method that
 * requires access to the DB.
 * Use the connect_to_db below to open your connection in DBConnector.
 * What is opened must be closed!
 */

/*
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// DO NOT change these variables!
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";

	public enum order_state {
		PREPARED,
		DELIVERED,
		PICKEDUP
	}


	private static boolean connect_to_db() throws SQLException, IOException
	{

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	public static void addOrder(Order o) throws SQLException, IOException
	{
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, pickup, pizzas, toppings
		 * on pizzas, order discounts and pizza discounts.
		 *
		 * This is a KEY method as it must store all the data in the Order object
		 * in the database and make sure all the tables are correctly linked.
		 *
		 * Remember, if the order is for Dine In, there is no customer...
		 * so the cusomter id coming from the Order object will be -1.
		 *
		 */

		connect_to_db();

		try {
			// 1. Insert into orders table
			String orderSQL = "INSERT INTO orders (OrderID, CustID, OrderType, Date, CustPrice, BusPrice, isComplete) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(orderSQL);
			pstmt.setInt(1, o.getOrderID());
			pstmt.setInt(2, o.getCustID());
			pstmt.setString(3, o.getOrderType());
			pstmt.setString(4, o.getDate());
			pstmt.setDouble(5, o.getCustPrice());
			pstmt.setDouble(6, o.getBusPrice());
			pstmt.setBoolean(7, o.getIsComplete());
			pstmt.executeUpdate();

			// 2. Handle order type specific tables
			if (o instanceof DeliveryOrder) {
				DeliveryOrder delivery = (DeliveryOrder) o;
				String deliverySQL = "INSERT INTO delivery (OrderID, Address) VALUES (?, ?)";
				pstmt = conn.prepareStatement(deliverySQL);
				pstmt.setInt(1, o.getOrderID());
				pstmt.setString(2, delivery.getAddress());
				pstmt.executeUpdate();
			} else if (o instanceof DineinOrder) {
				DineinOrder dineIn = (DineinOrder) o;
				String dineinSQL = "INSERT INTO dine_in (OrderID, TableNum) VALUES (?, ?)";
				pstmt = conn.prepareStatement(dineinSQL);
				pstmt.setInt(1, o.getOrderID());
				pstmt.setInt(2, dineIn.getTableNum());
				pstmt.executeUpdate();
			} else if (o instanceof PickupOrder) {
				String pickupSQL = "INSERT INTO pickup (OrderID, IsPickedUp) VALUES (?, ?)";
				pstmt = conn.prepareStatement(pickupSQL);
				pstmt.setInt(1, o.getOrderID());
				pstmt.setBoolean(2, false);
				pstmt.executeUpdate();
			}

			// 3. Add pizzas and their details
			for (Pizza p : o.getPizzaList()) {
				addPizza(java.sql.Timestamp.valueOf(o.getDate()), o.getOrderID(), p);
			}

			// 4. Add order discounts
			for (Discount d : o.getDiscountList()) {
				String discountSQL = "INSERT INTO order_discount (OrderID, DiscountID) VALUES (?, ?)";
				pstmt = conn.prepareStatement(discountSQL);
				pstmt.setInt(1, o.getOrderID());
				pstmt.setInt(2, d.getDiscountID());
				pstmt.executeUpdate();
			}

		} finally {
			conn.close();
		}
	}

	public static int addPizza(java.util.Date d, int orderID, Pizza p) throws SQLException, IOException
	{
		/*
		 * Add the code needed to insert the pizza into the database.
		 * Keep in mind you must also add the pizza discounts and toppings
		 * associated with the pizza.
		 *
		 * NOTE: there is a Date object passed into this method so that the Order
		 * and ALL its Pizzas can be assigned the same DTS.
		 *
		 * This method returns the id of the pizza just added.
		 *
		 */

//		return -1;
		connect_to_db();

		try {
			// 1. Insert pizza details
			String pizzaSQL = "INSERT INTO pizza (PizzaID, OrderID, PizzaState, PizzaDate, CrustType, Size, CustPrice, BusPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(pizzaSQL);
			pstmt.setInt(1, p.getPizzaID());
			pstmt.setInt(2, orderID);
			pstmt.setString(3, p.getPizzaState());
			pstmt.setString(4, p.getPizzaDate());
			pstmt.setString(5, p.getCrustType());
			pstmt.setString(6, p.getSize());
			pstmt.setDouble(7, p.getCustPrice());
			pstmt.setDouble(8, p.getBusPrice());
			pstmt.executeUpdate();

			// 2. Add toppings for this pizza
			for (Topping t : p.getToppings()) {
				String toppingSQL = "INSERT INTO pizza_topping (PizzaID, TopID, Extra) VALUES (?, ?, ?)";
				pstmt = conn.prepareStatement(toppingSQL);
				pstmt.setInt(1, p.getPizzaID());
				pstmt.setInt(2, t.getTopID());
				pstmt.setBoolean(3, t.getDoubled());
				pstmt.executeUpdate();
			}

			// 3. Add pizza discounts
			for (Discount di : p.getDiscounts()) {
				String discountSQL = "INSERT INTO pizza_discount (PizzaID, DiscountID) VALUES (?, ?)";
				pstmt = conn.prepareStatement(discountSQL);
				pstmt.setInt(1, p.getPizzaID());
				pstmt.setInt(2, di.getDiscountID());
				pstmt.executeUpdate();
			}

			return p.getPizzaID();

		} finally {
			conn.close();
		}
	}

	public static int addCustomer(Customer c) throws SQLException, IOException
	{
		/*
		 * This method adds a new customer to the database.
		 *
		 */
		connect_to_db();

		try {
			String sql = "INSERT INTO customer (CustID, FName, LName, Phone) VALUES (?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, c.getCustID());
			pstmt.setString(2, c.getFName());
			pstmt.setString(3, c.getLName());
			pstmt.setString(4, c.getPhone());
			pstmt.executeUpdate();

			return c.getCustID();

		} finally {
			conn.close();
		}
//		return -1;
	}

	public static void completeOrder(int OrderID, order_state newState ) throws SQLException, IOException
	{
		/*
		 * Mark that order as complete in the database.
		 * Note: if an order is complete, this means all the pizzas are complete as well.
		 * However, it does not mean that the order has been delivered or picked up!
		 *
		 * For newState = PREPARED: mark the order and all associated pizza's as completed
		 * For newState = DELIVERED: mark the delivery status
		 * FOR newState = PICKEDUP: mark the pickup status
		 *
		 */
		connect_to_db();

		try {
			switch (newState) {
				case PREPARED:
					// Mark the order as complete
					String orderSQL = "UPDATE orders SET isComplete = true WHERE OrderID = ?";
					PreparedStatement pstmt = conn.prepareStatement(orderSQL);
					pstmt.setInt(1, OrderID);
					pstmt.executeUpdate();

					// Mark all pizzas in the order as complete
					String pizzaSQL = "UPDATE pizza SET PizzaState = 'Completed' WHERE OrderID = ?";
					pstmt = conn.prepareStatement(pizzaSQL);
					pstmt.setInt(1, OrderID);
					pstmt.executeUpdate();
					break;

				case DELIVERED:
					// Update delivery status
					String deliverySQL = "UPDATE delivery SET IsDelivered = true WHERE OrderID = ?";
					pstmt = conn.prepareStatement(deliverySQL);
					pstmt.setInt(1, OrderID);
					pstmt.executeUpdate();
					break;

				case PICKEDUP:
					// Update pickup status
					String pickupSQL = "UPDATE pickup SET IsPickedUp = true WHERE OrderID = ?";
					pstmt = conn.prepareStatement(pickupSQL);
					pstmt.setInt(1, OrderID);
					pstmt.executeUpdate();
					break;
			}
		} finally {
			conn.close();
		}
	}


	public static ArrayList<Order> getOrders(int status) throws SQLException, IOException
	{
		/*
		 * Return an ArrayList of orders.
		 * 	status   == 1 => return a list of open (ie oder is not completed)
		 *           == 2 => return a list of completed orders (ie order is complete)
		 *           == 3 => return a list of all the orders
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 *
		 * You must fully populate the Order object, this includes order discounts,
		 * and pizzas along with the toppings and discounts associated with them.
		 *
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */
		ArrayList<Order> orders = new ArrayList<>();

		// Only proceed if we can connect to the database
		if (!connect_to_db()) {
			return orders;
		}

		try {
			String baseQuery = "SELECT o.*, " +
					"di.TableNum, " +
					"d.Address, d.IsDelivered, " +
					"p.IsPickedUp " +
					"FROM orders o " +
					"LEFT JOIN dine_in di ON o.OrderID = di.OrderID " +
					"LEFT JOIN delivery d ON o.OrderID = d.OrderID " +
					"LEFT JOIN pickup p ON o.OrderID = p.OrderID ";

			// Add WHERE clause based on status
			switch(status) {
				case 1: // Open orders
					baseQuery += "WHERE o.isComplete = false ";
					break;
				case 2: // Completed orders
					baseQuery += "WHERE o.isComplete = true ";
					break;
				case 3: // All orders
					break;
			}

			baseQuery += "ORDER BY o.OrderID";

			PreparedStatement pstmt = conn.prepareStatement(baseQuery);
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()) {
				int orderId = rs.getInt("OrderID");
				int custId = rs.getInt("CustID");
				String orderType = rs.getString("OrderType");
				String date = rs.getString("Date");
				double custPrice = rs.getDouble("CustPrice");
				double busPrice = rs.getDouble("BusPrice");
				boolean isComplete = rs.getBoolean("isComplete");

				Order order = null;

				// Create appropriate order type based on orderType
				switch(orderType) {
					case "delivery":
						String address = rs.getString("Address");
						order = new DeliveryOrder(orderId, custId, date, custPrice, busPrice, isComplete, address);
						break;

					case "dinein":
						int tableNum = rs.getInt("TableNum");
						order = new DineinOrder(orderId, custId, date, custPrice, busPrice, isComplete, tableNum);
						break;

					case "pickup":
						boolean isPickedUp = rs.getBoolean("IsPickedUp");
						order = new PickupOrder(orderId, custId, date, custPrice, busPrice, isComplete, isPickedUp);
						break;
				}

				if (order != null) {
					// Add pizzas to the order
					ArrayList<Pizza> pizzas = getPizzas(order);
					order.setPizzaList(pizzas);

					// Add discounts to the order
					ArrayList<Discount> discounts = getDiscounts(order);
					order.setDiscountList(discounts);

					orders.add(order);
				}
			}
		} finally {
			// Only try to close if connection exists
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// Log the error but don't throw it
					System.err.println("Error closing database connection: " + e.getMessage());
				}
			}
		}

		return orders;
//		return null;
	}

	public static Order getLastOrder() throws SQLException, IOException
	{
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there will ALWAYS be a "last order"!
		 */
		return null;
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException
	{
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *
		 */
		return null;
	}

public static ArrayList<Discount> getDiscountList() throws SQLException, IOException
{

	/*
	//		 * Query the database for all the available discounts and
	//		 * return them in an arrayList of discounts ordered by discount name.
	//		 *
	//
	*/
	ArrayList<Discount> discounts = new ArrayList<>();
	if(connect_to_db()) {  // Check if connection is successful
		String query = "SELECT * FROM discount";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()){
			int ID = rs.getInt("discount_DiscountID");
			String discountName = rs.getString("discount_DiscountName");
			float amt = rs.getFloat("discount_Amount");
			boolean isPercent = rs.getBoolean("discount_IsPercent");
			Discount dis = new Discount(ID, discountName, amt, isPercent);
			discounts.add(dis);
		}
		conn.close();
	}
	return discounts;
}

	public static Discount findDiscountByName(String name) throws SQLException, IOException
	{
		/*
		 * Query the database for a discount using its name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *
		 */
		return null;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException
	{
		/*
		 * Query the data for all the customers and return an arrayList of all the customers.
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */
		ArrayList<Customer> customerList = new ArrayList<>();
		connect_to_db();

		if(connect_to_db()) {
			String query = "SELECT * FROM customer";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println(rs);
			while (rs.next()) {
				int id = rs.getInt("customer_CustID");
				String fName = rs.getString("customer_FName");
				String lName = rs.getString("customer_LName");
				String phone = rs.getString("customer_PhoneNum");
				Customer customer = new Customer(id, fName, lName, phone);
				customerList.add(customer);
			}
			conn.close();
		}
		return customerList;
	}

	public static Customer findCustomerByPhone(String phoneNumber)  throws SQLException, IOException
	{
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *
		 */
		return null;
	}

	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
		/*
		 * COMPLETED...WORKING Example!
		 *
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with
		 * your database from Java.
		 *
		 * Notice how the connection to the DB made at the start of the
		 *
		 */

		connect_to_db();

		/*
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 *
		 */
		String cname1 = "";
		String cname2 = "";
		String query = "Select customer_FName, customer_LName From customer WHERE customer_CustID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while(rs.next())
		{
			cname1 = rs.getString(1) + " " + rs.getString(2);
		}

		/*
		 * an BETTER example of the same query using a prepared statement...
		 * with exception handling
		 *
		 */
		try {
			PreparedStatement os;
			ResultSet rset2;
			String query2;
			query2 = "Select customer_FName, customer_LName From customer WHERE customer_CustID=?;";
			os = conn.prepareStatement(query2);
			os.setInt(1, CustID);
			rset2 = os.executeQuery();
			while(rset2.next())
			{
				cname2 = rset2.getString("customer_FName") + " " + rset2.getString("customer_LName"); // note the use of field names in the getSting methods
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// process the error or re-raise the exception to a higher level
		}

		conn.close();

		return cname1;
		// OR
		// return cname2;

	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException
	{
		/*
		 * Query the database for the aviable toppings and
		 * return an arrayList of all the available toppings.
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */
		ArrayList<Topping> toppings = new ArrayList<>();

		// First check if we can connect to the database
		if(!connect_to_db()) {
			return toppings;  // Return empty list if connection fails
		}

		try {
			String query = "SELECT topping_TopID, topping_TopName, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT, topping_CustPrice, topping_BusPrice, topping_MinINVT, topping_CurINVT FROM topping";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()) {
				int id = rs.getInt("topping_TopID");
				String topName = rs.getString("topping_TopName");
				float smallAMT = rs.getFloat("topping_SmallAMT");
				float medAMT = rs.getFloat("topping_MedAMT");
				float largeAMT = rs.getFloat("topping_LgAMT");
				float xlAMT = rs.getFloat("topping_XLAMT");
				float custPrice = rs.getFloat("topping_CustPrice");
				float busPrice = rs.getFloat("topping_BusPrice");
				int minINVT = rs.getInt("topping_MinINVT");
				int curINVT = rs.getInt("topping_CurINVT");

				Topping topping = new Topping(id, topName, smallAMT, medAMT, largeAMT, xlAMT, custPrice, busPrice, minINVT, curINVT);
				toppings.add(topping);
			}
		} finally {
			// Make sure we close the connection in a finally block
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException e) {
					System.err.println("Error closing database connection: " + e.getMessage());
				}
			}
		}

		return toppings;
	}

	public static Topping findToppingByName(String name) throws SQLException, IOException
	{
		/*
		 * Query the database for the topping using its name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *
		 */
		return null;
	}

	public static ArrayList<Topping> getToppingsOnPizza(Pizza p) throws SQLException, IOException
	{
		/*
		 * This method builds an ArrayList of the toppings ON a pizza.
		 * The list can then be added to the Pizza object elsewhere in the
		 */

		return null;
	}

	public static void addToInventory(int toppingID, double quantity) throws SQLException, IOException
	{
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 *
		 * */
	}


	public static ArrayList<Pizza> getPizzas(Order o) throws SQLException, IOException
	{
		/*
		 * Build an ArrayList of all the Pizzas associated with the Order.
		 *
		 */

		connect_to_db();
		ArrayList<Pizza> pizzas = new ArrayList<>();

		try {
			String query = "SELECT * FROM pizza WHERE OrderID = ? ORDER BY PizzaID";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, o.getOrderID());
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()) {
				int pizzaID = rs.getInt("PizzaID");
				String size = rs.getString("Size");
				String crustType = rs.getString("CrustType");
				String pizzaState = rs.getString("PizzaState");
				String pizzaDate = rs.getString("PizzaDate");
				double custPrice = rs.getDouble("CustPrice");
				double busPrice = rs.getDouble("BusPrice");

				Pizza pizza = new Pizza(pizzaID, size, crustType, o.getOrderID(),
						pizzaState, pizzaDate, custPrice, busPrice);

				// Add toppings to the pizza
				ArrayList<Topping> toppings = getToppingsOnPizza(pizza);
				for(Topping t : toppings) {
					pizza.addToppings(t, false);  // We'll need to update this if we track "extra" toppings
				}

				// Add discounts to the pizza
				ArrayList<Discount> discounts = getDiscounts(pizza);
				for(Discount d : discounts) {
					pizza.addDiscounts(d);
				}

				pizzas.add(pizza);
			}

			return pizzas;

		} finally {
			conn.close();
		}
//		return null;
	}

	public static ArrayList<Discount> getDiscounts(Order o) throws SQLException, IOException
	{
		/*
		 * Build an array list of all the Discounts associted with the Order.
		 *
		 */
		connect_to_db();
		ArrayList<Discount> discounts = new ArrayList<>();

		try {
			String query = "SELECT d.* FROM discount d " +
					"JOIN order_discount od ON d.discount_DiscountID = od.DiscountID " +
					"WHERE od.OrderID = ? " +
					"ORDER BY d.discount_DiscountID";

			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, o.getOrderID());
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()) {
				int discountId = rs.getInt("discount_DiscountID");
				String discountName = rs.getString("discount_DiscountName");
				float amount = rs.getFloat("discount_Amount");
				boolean isPercent = rs.getBoolean("discount_IsPercent");

				Discount discount = new Discount(discountId, discountName, amount, isPercent);
				discounts.add(discount);
			}

			return discounts;

		} finally {
			conn.close();
		}
//		return null;
	}

	public static ArrayList<Discount> getDiscounts(Pizza p) throws SQLException, IOException
	{
		/*
		 * Build an array list of all the Discounts associted with the Pizza.
		 *
		 */

		return null;
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException
	{
		/*
		 * Query the database from the base customer price for that size and crust pizza.
		 *
		 */
		return 0.0;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException
	{
		/*
		 * Query the database from the base business price for that size and crust pizza.
		 *
		 */
		return 0.0;
	}


	public static void printToppingPopReport() throws SQLException, IOException
	{
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 * HINT: You need to match the expected output EXACTLY....I would suggest
		 * you look at the printf method (rather that the simple print of println).
		 * It operates the same in Java as it does in C and will make your code
		 * better.
		 *
		 */
	}

	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 * HINT: You need to match the expected output EXACTLY....I would suggest
		 * you look at the printf method (rather that the simple print of println).
		 * It operates the same in Java as it does in C and will make your code
		 * better.
		 *
		 */
	}

	public static void printProfitByOrderType() throws SQLException, IOException
	{
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 * HINT: You need to match the expected output EXACTLY....I would suggest
		 * you look at the printf method (rather that the simple print of println).
		 * It operates the same in Java as it does in C and will make your code
		 * better.
		 *
		 */
	}



	/*
	 * These private methods help get the individual components of an SQL datetime object.
	 * You're welcome to keep them or remove them....but they are usefull!
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}
