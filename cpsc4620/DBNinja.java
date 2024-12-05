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

		try {
		connect_to_db();
		if(conn != null){
			// 1. Insert into orders table
			String orderSQL = "INSERT INTO ordertable ( customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_IsComplete) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(orderSQL, Statement.RETURN_GENERATED_KEYS);
			if(o.getCustID() != -1){
			pstmt.setInt(1, o.getCustID());
			} else {
				pstmt.setObject(1, null);
			}
			pstmt.setString(2, o.getOrderType());
			pstmt.setString(3, o.getDate());
			pstmt.setDouble(4, o.getCustPrice());
			pstmt.setDouble(5, o.getBusPrice());
			pstmt.setBoolean(6, o.getIsComplete());
			int rowsAffected = pstmt.executeUpdate();

			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			int generatedOrderID = -1;
			if (generatedKeys.next()) {
				generatedOrderID = generatedKeys.getInt(1);
			} else {
				throw new SQLException("Failed to retrieve generated OrderID.");
			}
			// 2. Handle order type specific tables
			String orderType = o.getOrderType();
			switch (orderType.toLowerCase()) {
				case "delivery":
					DeliveryOrder delivery = (DeliveryOrder) o;
					String deliverySQL = "INSERT INTO delivery (ordertable_OrderID, delivery_HouseNum, delivery_Street, delivery_City, delivery_State, delivery_Zip, delivery_IsDelivered) VALUES (?, ?, ?, ?, ?, ?, ?)";
					pstmt = conn.prepareStatement(deliverySQL);
					String[] addressParts = delivery.getAddress().split("\t");
					if (addressParts.length != 5) {
						throw new SQLException("Invalid address format for delivery order: " + delivery.getAddress());
					}
					pstmt.setInt(1, generatedOrderID);
					pstmt.setString(2, addressParts[0]); // House number
					pstmt.setString(3, addressParts[1]); // Street
					pstmt.setString(4, addressParts[2]); // City
					pstmt.setString(5, addressParts[3]); // State
					pstmt.setString(6, addressParts[4]); // Zip
					pstmt.setBoolean(7, delivery.getIsComplete());
					pstmt.executeUpdate();
					break;
				case "pickup":
					String pickupSQL = "INSERT INTO pickup (ordertable_OrderID, pickup_IsPickedUp) VALUES (?, ?)";
					pstmt = conn.prepareStatement(pickupSQL);
					pstmt.setInt(1, generatedOrderID);
					pstmt.setBoolean(2, false);
					pstmt.executeUpdate();
					break;
				case "dinein":
					DineinOrder dineIn = (DineinOrder) o;
					String dineinSQL = "INSERT INTO dinein (ordertable_OrderID, dinein_TableNum) VALUES (?, ?)";
					pstmt = conn.prepareStatement(dineinSQL);
					pstmt.setInt(1, generatedOrderID);
					pstmt.setInt(2, dineIn.getTableNum());
					pstmt.executeUpdate();
					break;
				default:
					throw new SQLException("Unknown order type: " + orderType);
			}

			// 3. Add pizzas and their details
			for (Pizza p : o.getPizzaList()) {
				addPizza(java.sql.Timestamp.valueOf(o.getDate()), generatedOrderID, p);
			}

			// 4. Add order discounts
			for (Discount d : o.getDiscountList()) {
				try {
					connect_to_db();
					String discountSQL = "INSERT INTO order_discount (ordertable_OrderID, discount_DiscountID) VALUES (?, ?)";
					pstmt = conn.prepareStatement(discountSQL);
					pstmt.setInt(1, generatedOrderID);
					pstmt.setInt(2, d.getDiscountID());
					pstmt.executeUpdate();
				} finally {
					if (conn != null) {
						conn.close();
					}
				}

			}
}
		} finally {
			if (conn != null) {
				conn.close();
			}
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

		try {
			connect_to_db();
			if(conn != null){
				// 1. Insert pizza details
				String pizzaSQL = "INSERT INTO pizza (ordertable_OrderID, pizza_PizzaState, pizza_PizzaDate, pizza_CrustType, pizza_Size, pizza_CustPrice, pizza_BusPrice) VALUES (?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(pizzaSQL, Statement.RETURN_GENERATED_KEYS);
				pstmt.setInt(1, orderID);
				pstmt.setString(2, p.getPizzaState());
				pstmt.setString(3, p.getPizzaDate());
				pstmt.setString(4, p.getCrustType());
				pstmt.setString(5, p.getSize());
				pstmt.setDouble(6, p.getCustPrice());
				pstmt.setDouble(7, p.getBusPrice());
				pstmt.executeUpdate();

				ResultSet generatedKeys = pstmt.getGeneratedKeys();
				int generatedPizzaID = -1;
				if (generatedKeys.next()) {
					generatedPizzaID = generatedKeys.getInt(1); // Retrieve the generated key
				} else {
					throw new SQLException("Failed to retrieve generated OrderID.");
				}

				// 2. Add toppings for this pizza
				for (Topping t : p.getToppings()) {
					String size = p.getSize();
					boolean isDouble = t.getDoubled();
					try {
						connect_to_db();
						if(conn != null){
							String toppingSQL = "INSERT INTO pizza_topping (pizza_PizzaID, topping_TopID, pizza_topping_IsDouble) VALUES (?, ?, ?)";
							pstmt = conn.prepareStatement(toppingSQL);
							pstmt.setInt(1, generatedPizzaID);
							pstmt.setInt(2, t.getTopID());
							pstmt.setBoolean(3, isDouble);
							pstmt.executeUpdate();

						}
					} finally {
						if (conn != null) {
							conn.close();
						}
					}
					double toppingAmt = 0;
					Topping topping = findToppingByName(t.getTopName());
					switch (size){
						case "Large":
							toppingAmt = Math.ceil(topping.getLgAMT());
							break;
						case "Medium":
							toppingAmt = Math.ceil(topping.getMedAMT());
							break;
						case "Small":
							toppingAmt = Math.ceil(topping.getSmallAMT());
							break;
						case "XLarge":
							toppingAmt = Math.ceil(topping.getXLAMT());
							break;
						default:
							toppingAmt = 1;
					}
					addToInventory(t.getTopID(), isDouble ? (-2*toppingAmt): (-1*toppingAmt));
				}

				// 3. Add pizza discounts
				for (Discount di : p.getDiscounts()) {
					try {
						connect_to_db();
						if(conn != null){
							String discountSQL = "INSERT INTO pizza_discount (pizza_PizzaID, discount_DiscountID) VALUES (?, ?)";
							pstmt = conn.prepareStatement(discountSQL);
							pstmt.setInt(1, generatedPizzaID);
							pstmt.setInt(2, di.getDiscountID());
							pstmt.executeUpdate();
						}
					} finally {
						if (conn != null) {
							conn.close();
						}
					}

				}
			}
		} finally {
			if(conn != null){
				conn.close();
			}
		}
		return p.getPizzaID();
	}

	public static int addCustomer(Customer c) throws SQLException, IOException
	{
		/*
		 * This method adds a new customer to the database.
		 *
		 */
		int generatedCustID = -1;
		try {
			connect_to_db();
			if(conn != null) {
				String sql = "INSERT INTO customer (customer_FName, customer_LName, customer_PhoneNum) VALUES ( ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, c.getFName());
				pstmt.setString(2, c.getLName());
				pstmt.setString(3, c.getPhone());
				pstmt.executeUpdate();
				ResultSet generatedKeys = pstmt.getGeneratedKeys();

				if (generatedKeys.next()) {
					generatedCustID = generatedKeys.getInt(1); // Retrieve the generated key
				} else {
					throw new SQLException("Failed to retrieve generated OrderID.");
				}

			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
//		return -1;
		return generatedCustID;
	}

	public static void completeOrder(int ordertable_OrderID, order_state newState ) throws SQLException, IOException
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

		try {
			connect_to_db();
			if (conn != null) {
				switch (newState) {
					case PREPARED:
						// Mark the order as complete
						String orderSQL = "UPDATE ordertable SET ordertable_IsComplete = true WHERE ordertable_OrderID = ?";
						PreparedStatement pstmt = conn.prepareStatement(orderSQL);
						pstmt.setInt(1, ordertable_OrderID);
						pstmt.executeUpdate();

						// Mark all pizzas in the order as complete
						String pizzaSQL = "UPDATE pizza SET pizza_PizzaState = 'Completed' WHERE ordertable_OrderID = ?";
						pstmt = conn.prepareStatement(pizzaSQL);
						pstmt.setInt(1, ordertable_OrderID);
						pstmt.executeUpdate();
						break;

					case DELIVERED:
						// Update delivery status
						String deliverySQL = "UPDATE delivery SET delivery_IsDelivered = true WHERE ordertable_OrderID = ?";
						pstmt = conn.prepareStatement(deliverySQL);
						pstmt.setInt(1, ordertable_OrderID);
						pstmt.executeUpdate();
						break;

					case PICKEDUP:
						// Update pickup status
						String pickupSQL = "UPDATE pickup SET pickup_IsPickedUp = true WHERE ordertable_OrderID = ?";
						pstmt = conn.prepareStatement(pickupSQL);
						pstmt.setInt(1, ordertable_OrderID);
						pstmt.executeUpdate();
						break;
				}
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

		try {
			connect_to_db();
			if (conn != null) {
				String query = "SELECT ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ROUND(ordertable_CustPrice,2) AS ordertable_CustPrice, ROUND(ordertable_BusPrice, 2) AS ordertable_BusPrice, ordertable_IsComplete FROM ordertable WHERE " +
						"CASE " +
						"WHEN ? = 1 THEN ordertable_IsComplete = 0 " +
						"WHEN ? = 2 THEN ordertable_IsComplete = 1 " +
						"ELSE 1=1 " +
						"END " +
						"ORDER BY ordertable_OrderDateTime";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, status);
				pstmt.setInt(2, status);
				ResultSet rs = pstmt.executeQuery();

				while (rs.next()) {
					int orderId = rs.getInt("ordertable_OrderID");
					int custId = rs.getInt("customer_CustID");
					String orderType = rs.getString("ordertable_OrderType");
					String date = rs.getString("ordertable_OrderDateTime");
					double custPrice = rs.getDouble("ordertable_CustPrice");
					double busPrice = rs.getDouble("ordertable_BusPrice");
					boolean isComplete = rs.getBoolean("ordertable_IsComplete");

					Order order = null;

					// Create appropriate order type based on orderType
					switch (orderType) {
						case "delivery":
							try {
								connect_to_db();
								String queryDel = "SELECT * FROM delivery WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtDel = conn.prepareStatement(queryDel);
								pstmtDel.setInt(1, orderId);
								ResultSet rsDel = pstmtDel.executeQuery();
								if(rsDel.next()) {
									String HouseNum = rsDel.getString("delivery_HouseNum");
									String Street = rsDel.getString("delivery_Street");
									String City = rsDel.getString("delivery_City");
									String State = rsDel.getString("delivery_State");
									String Zip = rsDel.getString("delivery_Zip");
									boolean isDelivered = rsDel.getBoolean("delivery_IsDelivered");
									String address = HouseNum + "\t" + Street + "\t" + City + "\t" + State + "\t" + Zip;
									order =
											new DeliveryOrder(orderId, custId, date, custPrice, busPrice, isComplete, address, isDelivered);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}
							break;

						case "dinein":
							try {
								connect_to_db();
								String queryDinein = "SELECT * FROM dinein WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtDinein = conn.prepareStatement(queryDinein);
								pstmtDinein.setInt(1, orderId);
								ResultSet rsDinein = pstmtDinein.executeQuery();
								if(rsDinein.next()) {
									int tableNum = rsDinein.getInt("dinein_TableNum");
									order =
											new DineinOrder(orderId, custId, date, custPrice, busPrice, isComplete, tableNum);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}
							break;

						case "pickup":
							try {
								connect_to_db();
								String queryPickup = "SELECT * FROM pickup WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtPickup = conn.prepareStatement(queryPickup);
								pstmtPickup.setInt(1, orderId);
								ResultSet rsPickup = pstmtPickup.executeQuery();
								if(rsPickup.next()) {
									boolean isPickedUp = rsPickup.getBoolean("pickup_IsPickedUp");
									order =
											new PickupOrder(orderId, custId, date, custPrice, busPrice, isComplete, isPickedUp);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}

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
			}
		} finally {
			// Only try to close if connection exists
			if (conn != null) {
					conn.close();
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
		Order order = null;
		try {
			connect_to_db();
			if (conn != null) {
				String query = "SELECT * FROM ordertable ORDER BY ordertable_OrderID DESC LIMIT 1;";
				PreparedStatement pstmt = conn.prepareStatement(query);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					int orderId = rs.getInt("ordertable_OrderID");
					int custId = rs.getInt("customer_CustID");
					String orderType = rs.getString("ordertable_OrderType");
					String date = rs.getString("ordertable_OrderDateTime");
					int custPrice = rs.getInt("ordertable_CustPrice");
					int busPrice = rs.getInt("ordertable_BusPrice");
					boolean isComplete = rs.getBoolean("ordertable_IsComplete");
					order = new Order(orderId, custId, orderType, date, custPrice, busPrice, isComplete);
					switch (orderType) {
						case "delivery":
							try {
								connect_to_db();
								String queryDel = "SELECT * FROM delivery WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtDel = conn.prepareStatement(queryDel);
								pstmtDel.setInt(1, orderId);
								ResultSet rsDel = pstmtDel.executeQuery();
								if(rsDel.next()) {
									String HouseNum = rsDel.getString("delivery_HouseNum");
									String Street = rsDel.getString("delivery_Street");
									String City = rsDel.getString("delivery_City");
									String State = rsDel.getString("delivery_State");
									String Zip = rsDel.getString("delivery_Zip");
									boolean isDelivered = rsDel.getBoolean("delivery_IsDelivered");
									String address = HouseNum + "\t" + Street + "\t" + City + "\t" + State + "\t" + Zip;
									order =
											new DeliveryOrder(orderId, custId, date, custPrice, busPrice, isComplete, address, isDelivered);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}
							break;

						case "dinein":
							try {
								connect_to_db();
								String queryDinein = "SELECT * FROM dinein WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtDinein = conn.prepareStatement(queryDinein);
								pstmtDinein.setInt(1, orderId);
								ResultSet rsDinein = pstmtDinein.executeQuery();
								if(rsDinein.next()) {
									int tableNum = rsDinein.getInt("dinein_TableNum");
									order =
											new DineinOrder(orderId, custId, date, custPrice, busPrice, isComplete, tableNum);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}
							break;

						case "pickup":
							try {
								connect_to_db();
								String queryPickup = "SELECT * FROM pickup WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtPickup = conn.prepareStatement(queryPickup);
								pstmtPickup.setInt(1, orderId);
								ResultSet rsPickup = pstmtPickup.executeQuery();
								if(rsPickup.next()) {
									boolean isPickedUp = rsPickup.getBoolean("pickup_IsPickedUp");
									order =
											new PickupOrder(orderId, custId, date, custPrice, busPrice, isComplete, isPickedUp);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}

							break;
					}
					// Add pizzas to the order
					ArrayList<Pizza> pizzas = getPizzas(order);
					order.setPizzaList(pizzas);

					// Add discounts to the order
					ArrayList<Discount> discounts = getDiscounts(order);
					order.setDiscountList(discounts);
				}
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return order;
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException
	{
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *
		 */
		ArrayList<Order> orders = new ArrayList<>();
		try {
			connect_to_db();
			if (conn != null) {
				String query = "SELECT * FROM ordertable WHERE DATE(ordertable_OrderDateTime) = ?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, date);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					int orderId = rs.getInt("ordertable_OrderID");
					int custId = rs.getInt("customer_CustID");
					String orderType = rs.getString("ordertable_OrderType");
					String orderDateTime = rs.getString("ordertable_OrderDateTime");
					double custPrice = rs.getDouble("ordertable_CustPrice");
					double busPrice = rs.getDouble("ordertable_BusPrice");
					boolean isComplete = rs.getBoolean("ordertable_IsComplete");

					Order order = new Order(orderId, custId, orderType, orderDateTime, custPrice, busPrice, isComplete);
					switch (orderType) {
						case "delivery":
							try {
								connect_to_db();
								String queryDel = "SELECT * FROM delivery WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtDel = conn.prepareStatement(queryDel);
								pstmtDel.setInt(1, orderId);
								ResultSet rsDel = pstmtDel.executeQuery();
								if(rsDel.next()) {
									String HouseNum = rsDel.getString("delivery_HouseNum");
									String Street = rsDel.getString("delivery_Street");
									String City = rsDel.getString("delivery_City");
									String State = rsDel.getString("delivery_State");
									String Zip = rsDel.getString("delivery_Zip");
									boolean isDelivered = rsDel.getBoolean("delivery_IsDelivered");
									String address = HouseNum + "\t" + Street + "\t" + City + "\t" + State + "\t" + Zip;
									order =
											new DeliveryOrder(orderId, custId, date, custPrice, busPrice, isComplete, address, isDelivered);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}
							break;

						case "dinein":
							try {
								connect_to_db();
								String queryDinein = "SELECT * FROM dinein WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtDinein = conn.prepareStatement(queryDinein);
								pstmtDinein.setInt(1, orderId);
								ResultSet rsDinein = pstmtDinein.executeQuery();
								if(rsDinein.next()) {
									int tableNum = rsDinein.getInt("dinein_TableNum");
									order =
											new DineinOrder(orderId, custId, date, custPrice, busPrice, isComplete, tableNum);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}
							break;

						case "pickup":
							try {
								connect_to_db();
								String queryPickup = "SELECT * FROM pickup WHERE ordertable_OrderID = ?";
								PreparedStatement pstmtPickup = conn.prepareStatement(queryPickup);
								pstmtPickup.setInt(1, orderId);
								ResultSet rsPickup = pstmtPickup.executeQuery();
								if(rsPickup.next()) {
									boolean isPickedUp = rsPickup.getBoolean("pickup_IsPickedUp");
									order =
											new PickupOrder(orderId, custId, date, custPrice, busPrice, isComplete, isPickedUp);
								}
							} finally {
								if(conn != null){
									conn.close();
								}
							}

							break;
					}
					ArrayList<Pizza> pizzas = getPizzas(order);
					order.setPizzaList(pizzas);

					// Add discounts to the order
					ArrayList<Discount> discounts = getDiscounts(order);
					order.setDiscountList(discounts);
					orders.add(order);
				}
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return orders;
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
			String query = "SELECT * FROM discount ORDER BY discount_DiscountName";
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
		Discount discount = null;
		try {
			connect_to_db();
			if (conn != null) {
				String query = "SELECT * FROM discount WHERE discount_DiscountName = ?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, name);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					int ID = rs.getInt("discount_DiscountID");
					String discountName = rs.getString("discount_DiscountName");
					float amt = rs.getFloat("discount_Amount");
					boolean isPercent = rs.getBoolean("discount_IsPercent");
					discount = new Discount(ID, discountName, amt, isPercent);
				}
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return discount;
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
			String query = "SELECT * FROM customer ORDER BY customer_LName";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
//			System.out.println(rs);
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
		Customer customer = null;
		try {
			connect_to_db();
			if (conn != null) {
				String query = "SELECT * FROM customer WHERE customer_PhoneNum = ?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, phoneNumber);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					int id = rs.getInt("customer_CustID");
					String fName = rs.getString("customer_FName");
					String lName = rs.getString("customer_LName");
					String phone = rs.getString("customer_PhoneNum");
					customer = new Customer(id, fName, lName, phone);
				}
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return customer;
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
			String query = "SELECT topping_TopID, topping_TopName, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT, topping_CustPrice, topping_BusPrice, topping_MinINVT, topping_CurINVT FROM topping ORDER BY topping_TopName";
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
		Topping topping = null;
		try {
			connect_to_db();
			if(conn != null) {
				String query = "SELECT topping_TopID, topping_TopName, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT, ROUND(topping_CustPrice, 2) AS topping_CustPrice, ROUND(topping_BusPrice,2 ) AS topping_BusPrice, topping_MinINVT, topping_CurINVT FROM topping WHERE topping_TopName = ?";
				PreparedStatement os = conn.prepareStatement(query);
				os.setString(1, name);
				ResultSet rs = os.executeQuery();
				while(rs.next()) {
					int id = rs.getInt("topping_TopID");
					String topName = rs.getString("topping_TopName");
					float smallAMT = rs.getFloat("topping_SmallAMT");
					float medAMT = rs.getFloat("topping_MedAMT");
					float largeAMT = rs.getFloat("topping_LgAMT");
					float xlAMT = rs.getFloat("topping_XLAMT");
					double custPrice = rs.getDouble("topping_CustPrice");
					double busPrice = rs.getDouble("topping_BusPrice");
					int minINVT = rs.getInt("topping_MinINVT");
					int curINVT = rs.getInt("topping_CurINVT");

					topping = new Topping(id, topName, smallAMT, medAMT, largeAMT, xlAMT, custPrice, busPrice, minINVT, curINVT);
				}
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
		return topping;
	}

	public static ArrayList<Topping> getToppingsOnPizza(Pizza p) throws SQLException, IOException
	{
		/*
		 * This method builds an ArrayList of the toppings ON a pizza.
		 * The list can then be added to the Pizza object elsewhere in the
		 */
		ArrayList<Topping> toppings = new ArrayList<>();
		try{
			connect_to_db();
			if(conn != null) {
				String query = "SELECT t.*, pt.pizza_topping_IsDouble " +
						"FROM pizza_topping pt " +
						"JOIN topping t ON pt.topping_TopID = t.topping_TopID " +
						"WHERE pt.pizza_PizzaID = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setInt(1, p.getPizzaID());
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					int topID = rs.getInt("topping_TopID");
					String topName = rs.getString("topping_TopName");
					double smallAMT = rs.getDouble("topping_SmallAMT");
					double medAMT = rs.getDouble("topping_MedAMT");
					double lgAMT = rs.getDouble("topping_LgAMT");
					double xlAMT = rs.getDouble("topping_XLAMT");
					double custPrice = rs.getDouble("topping_CustPrice");
					double busPrice = rs.getDouble("topping_BusPrice");
					int minINVT = rs.getInt("topping_MinINVT");
					int curINVT = rs.getInt("topping_CurINVT");
					boolean isDouble = rs.getInt("pizza_topping_IsDouble") == 1;
					// Create a Topping object and set if it's doubled
					Topping topping = new Topping(topID, topName, smallAMT, medAMT, lgAMT, xlAMT, custPrice, busPrice, minINVT, curINVT);
					topping.setDoubled(isDouble);
					toppings.add(topping);
				}
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
		return toppings;
	}

	public static void addToInventory(int toppingID, double quantity) throws SQLException, IOException
	{
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 *
		 * */
		try{
			connect_to_db();
			if(conn != null) {
				String query = "UPDATE topping SET topping_CurINVT = topping_CurINVT + ? WHERE topping_TopID = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setDouble(1, quantity);
				stmt.setInt(2, toppingID);
				stmt.executeUpdate();
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
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
			String query = "SELECT pizza_PizzaID, pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, ROUND(pizza_CustPrice, 2) AS pizza_CustPrice, ROUND(pizza_BusPrice, 2) AS pizza_BusPrice, ordertable_OrderID FROM pizza WHERE ordertable_OrderID = ? ORDER BY pizza_PizzaID";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, o.getOrderID());
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()) {
				int pizzaID = rs.getInt("pizza_PizzaID");
				String size = rs.getString("pizza_Size");
				String crustType = rs.getString("pizza_CrustType");
				String pizzaState = rs.getString("pizza_PizzaState");
				String pizzaDate = rs.getString("pizza_PizzaDate");
				double custPrice = rs.getDouble("pizza_CustPrice");
				double busPrice = rs.getDouble("pizza_BusPrice");
//				System.out.println("pizzaID" + pizzaID);
//				System.out.println("custPrice" + custPrice);
//				System.out.println("busPrice" + busPrice);

				Pizza pizza = new Pizza(pizzaID, size, crustType, o.getOrderID(),
						pizzaState, pizzaDate, custPrice, busPrice);

				// Add toppings to the pizza
				ArrayList<Topping> toppings = getToppingsOnPizza(pizza);
//				for(Topping t : toppings) {
//					pizza.addToppings(t, t.getDoubled());  // We'll need to update this if we track "extra" toppings
//				}
				pizza.setToppings(toppings);

				// Add discounts to the pizza
				ArrayList<Discount> discounts = getDiscounts(pizza);
//				for(Discount d : discounts) {
//					pizza.addDiscounts(d);
//				}
				pizza.setDiscounts(discounts);

				pizzas.add(pizza);
			}
//			System.out.println("Pizza array:" + pizzas);
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
		ArrayList<Discount> discounts = new ArrayList<>();

		try {
			connect_to_db();
			if(conn != null) {
				String query = "SELECT d.* FROM discount d " +
						"JOIN order_discount od ON d.discount_DiscountID = od.discount_DiscountID " +
						"WHERE od.ordertable_OrderID = ? " +
						"ORDER BY d.discount_DiscountID";

				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, o.getOrderID());
				ResultSet rs = pstmt.executeQuery();

				while (rs.next()) {
					int discountId = rs.getInt("discount_DiscountID");
					String discountName = rs.getString("discount_DiscountName");
					float amount = rs.getFloat("discount_Amount");
					boolean isPercent = rs.getBoolean("discount_IsPercent");

					Discount discount = new Discount(discountId, discountName, amount, isPercent);
					discounts.add(discount);
				}
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
		return discounts;
	}

	public static ArrayList<Discount> getDiscounts(Pizza p) throws SQLException, IOException
	{
		/*
		 * Build an array list of all the Discounts associted with the Pizza.
		 *
		 */
		ArrayList<Discount> discounts = new ArrayList<>();
		try {
			connect_to_db();
			String query = "SELECT d.* " +
					"FROM pizza_discount pd " +
					"JOIN discount d ON pd.discount_DiscountID = d.discount_DiscountID " +
					"WHERE pd.pizza_PizzaID = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, p.getPizzaID());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int discountID = rs.getInt("discount_DiscountID");
				String discountName = rs.getString("discount_DiscountName");
				float amount = rs.getFloat("discount_Amount");
				boolean isPercent = rs.getBoolean("discount_IsPercent");
				Discount discount = new Discount(discountID, discountName, amount, isPercent);
				discounts.add(discount);
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
		return discounts;
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException
	{
		/*
		 * Query the database from the base customer price for that size and crust pizza.
		 *
		 */
		double baseCustPrice = 0.0;
//		System.out.println("Size: " + size);
//		System.out.println("Crust: " + crust);
		try {
			connect_to_db();
			if(conn != null) {
				String query = "SELECT baseprice_CustPrice FROM baseprice WHERE baseprice_Size = ? AND baseprice_CrustType = ?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, size);
				pstmt.setString(2, crust);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					baseCustPrice =  rs.getDouble("baseprice_CustPrice");
				}
			}
			}finally {
			if(conn != null) {
				conn.close();
			}
		}
		return baseCustPrice;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException
	{
		/*
		 * Query the database from the base business price for that size and crust pizza.
		 *
		 */
		double baseBusPrice = 0.0;
		try {
			connect_to_db();
			if(conn != null) {
				String query = "SELECT baseprice_BusPrice FROM baseprice WHERE baseprice_Size = ? AND baseprice_CrustType = ?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, size);
				pstmt.setString(2, crust);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					baseBusPrice = rs.getDouble("baseprice_BusPrice");
				}
			}
		}finally {
			if(conn != null) {
				conn.close();
			}
		}
		return baseBusPrice;
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
		try {
			connect_to_db();
			String query ="SELECT * FROM ToppingPopularity";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();

			//Print Header
			System.out.printf("%-19s %-15s\n", "Topping", "Topping Count");
			System.out.printf("%-19s %-15s\n", "-------", "-------------");

			//Print row
			while (rs.next()) {
				String toppingName = rs.getString("Topping");
				int usedCount = rs.getInt("ToppingCount");

				// Format and print the row
				System.out.printf("%-19s %-15d\n", toppingName, usedCount);
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
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
		try {
			connect_to_db();
			String query ="SELECT * FROM ProfitByPizza";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();

			//Print Header
			System.out.printf("%-19s %-19s %-19s %-19s\n", "Pizza Size", "Pizza Crust", "Profit", "Last Order Date");
			System.out.printf("%-19s %-19s %-19s %-19s\n", "----------", "-----------", "------", "---------------");
			//Print row
			while (rs.next()) {
				String Size = rs.getString("Size");
				String Crust = rs.getString("Crust");
				double Profit = rs.getDouble("Profit");
				String OrderMonth = rs.getString("OrderMonth");

				// Format and print the row
				System.out.printf("%-19s %-19s %-19.2f %-19s\n", Size, Crust, Profit, OrderMonth);
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
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
		try {
			connect_to_db();
			String query ="SELECT * FROM ProfitByOrderType";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();

			//Print Header
			System.out.printf("%-19s %-19s %-19s %-19s %-19s\n", "Customer Type", "Order Month", "Total Order Price", "Total Order Cost", "Profit");
			System.out.printf("%-19s %-19s %-19s %-19s %-19s\n", "-------------", "-----------", "-----------------", "----------------", "------");
			//Print row
			while (rs.next()) {
				String customerType = rs.getString("customerType");
				String OrderMonth = rs.getString("OrderMonth");
				double TotalOrderPrice = rs.getDouble("TotalOrderPrice");
				double TotalOrderCost = rs.getDouble("TotalOrderCost");
				double profit = rs.getDouble("Profit");

				// Format and print the row
				System.out.printf("%-19s %-19s %-19.2f %-19.2f %-19.2f\n", customerType == null ? "" : customerType, OrderMonth, TotalOrderPrice, TotalOrderCost, profit);
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
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
