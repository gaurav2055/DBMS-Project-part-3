USE PizzaDB;

-- topping rows
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Pepperoni', 1.25, 0.2, 100, 50, 2, 2.75, 3.5, 4.5);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Sausage', 1.25, 0.15, 100,50, 2.5, 3, 3.5, 4.25);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Ham', 1.5, 0.15, 78, 25, 2, 2.5, 3.25, 4);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Chicken', 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Green Pepper', 0.5, 0.02, 79, 25, 1, 1.5, 2, 2.5);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Onion', 0.5, 0.02, 85, 25, 1, 1.5, 2, 2.75);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Roma Tomato', 0.75, 0.03, 86, 10, 2, 3, 3.5, 4.5);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Mushrooms', 0.75, 0.1, 52, 50, 1.5, 2, 2.5, 3);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Black Olives', 0.6, 0.1, 39, 25, 0.75, 1, 1.5, 2);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Pineapple', 1, 0.25, 15, 0, 1, 1.25, 1.75, 2);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Jalapenos', 0.5, 0.05, 64, 0, 0.5, 0.75, 1.25, 1.75);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Banana Peppers', 0.5, 0.05, 36, 0, 0.6, 1, 1.3, 1.75);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Regular Cheese', 0.5, 0.12, 250, 50, 2, 3.5, 5, 7);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Four Cheese Blend', 1, 0.15, 150, 25, 2, 3.5, 5, 7);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Feta Cheese', 1.5, 0.18, 75, 0, 1.75, 3, 4, 5.5);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Goat Cheese', 1.5, 0.2, 54, 0, 1.6, 2.75, 4, 5.5);
INSERT INTO topping (topping_TopName, topping_CustPrice, topping_BusPrice, topping_CurINVT, topping_MinINVT, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT)
VALUES ('Bacon', 1.5, 0.25, 89, 0, 1, 1.5, 2, 3);


-- discount rows
INSERT INTO discount (discount_DiscountName, discount_Amount, discount_IsPercent)
VALUES ('Employee', 15, 1);
INSERT INTO discount (discount_DiscountName, discount_Amount, discount_IsPercent)
VALUES ('Lunch Special Medium', 1, 0);
INSERT INTO discount (discount_DiscountName, discount_Amount, discount_IsPercent)
VALUES ('Lunch Special Large', 2, 0);
INSERT INTO discount (discount_DiscountName, discount_Amount, discount_IsPercent)
VALUES ('Specialty Pizza', 1.5, 0);
INSERT INTO discount (discount_DiscountName, discount_Amount, discount_IsPercent)
VALUES ('Happy Hour', 10, 1);
INSERT INTO discount (discount_DiscountName, discount_Amount, discount_IsPercent)
VALUES ('Gameday Special', 20, 1);


-- baseprice rows
INSERT INTO baseprice VALUES ('Small', 'Thin', 3, 0.5);
INSERT INTO baseprice VALUES ('Small', 'Original', 3, 0.75);
INSERT INTO baseprice VALUES ('Small', 'Pan', 3.5, 1);
INSERT INTO baseprice VALUES ('Small', 'Gluten-Free', 4, 2);
INSERT INTO baseprice VALUES ('Medium', 'Thin', 5, 1);
INSERT INTO baseprice VALUES ('Medium', 'Original', 5, 1.5);
INSERT INTO baseprice VALUES ('Medium', 'Pan', 6, 2.25);
INSERT INTO baseprice VALUES ('Medium', 'Gluten-Free', 6.25, 3);
INSERT INTO baseprice VALUES ('Large', 'Thin', 8, 1.25);
INSERT INTO baseprice VALUES ('Large', 'Original', 8, 2);
INSERT INTO baseprice VALUES ('Large', 'Pan', 9, 3);
INSERT INTO baseprice VALUES ('Large', 'Gluten-Free', 9.5, 4);
INSERT INTO baseprice VALUES ('XLarge', 'Thin', 10, 2);
INSERT INTO baseprice VALUES ('XLarge', 'Original', 10, 3);
INSERT INTO baseprice VALUES ('XLarge', 'Pan', 11.5, 4.5);
INSERT INTO baseprice VALUES ('XLarge', 'Gluten-Free', 12.5, 6);

-- order 1
START TRANSACTION;

INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES (NULL, 'dinein', '2024-03-05 12:03:00', 19.75, 3.68, 1);

SET @CurOrderID = LAST_INSERT_ID();

INSERT INTO dinein VALUES (@CurOrderID, 21);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Thin', 'completed', '2024-03-05 12:03:00', 19.75, 3.68, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 1);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 2, 0);

INSERT INTO pizza_discount VALUES (@CurPizzaID, 3);

COMMIT;

-- order 2
START TRANSACTION;

INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES (NULL, 'dinein', '2024-04-03 12:05:00', 19.78, 4.63, 1);

SET @CurOrderID = LAST_INSERT_ID();

INSERT INTO dinein VALUES (@CurOrderID, 4);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Medium', 'Pan', 'completed', '2024-04-03 12:05:00', 12.85, 3.23, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 15, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 9, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 7, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 8, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 12, 0);

INSERT INTO pizza_discount VALUES (@CurPizzaID, 4);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Small', 'Original', 'completed', '2024-04-03 12:05:00', 6.93, 1.40, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 4, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 12, 0);

INSERT INTO order_discount VALUES (@CurOrderID, 2);

COMMIT;

-- order 3
START TRANSACTION;

CALL ADDCUSTOMER('Andrew', 'Wilkes-Krier', '8642545861');

SET @CurCustID = LAST_INSERT_ID();

CALL CREATEORDER (@CurCustID, 'pickup', '2024-03-03 21:30:00', 89.28, 19.80, 1);

SET @CurOrderID = LAST_INSERT_ID();

INSERT INTO pickup VALUES (@CurOrderID, 1);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Original', 'completed', '2024-03-03 21:30:00', 14.88, 3.30, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Original', 'completed', '2024-03-03 21:30:00', 14.88, 3.30, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Original', 'completed', '2024-03-03 21:30:00', 14.88, 3.30, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Original', 'completed', '2024-03-03 21:30:00', 14.88, 3.30, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Original', 'completed', '2024-03-03 21:30:00', 14.88, 3.30, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Original', 'completed', '2024-03-03 21:30:00', 14.88, 3.30, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);

COMMIT;

-- order 4
START TRANSACTION;
SELECT customer_CustID INTO @CurCustID
FROM customer
WHERE customer_FName = 'Andrew' AND customer_LName = 'Wilkes-Krier' AND customer_PhoneNum = '8642545861'
LIMIT 1;

INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES (@CurCustID, 'delivery', '2024-04-20 19:11:00', 68.95, 23.62, 1);

SET @CurOrderID = LAST_INSERT_ID();

INSERT INTO delivery VALUES (@CurOrderID, 115, 'Party Blvd', 'Anderson', 'SC', '29621', 0);

-- INSERT INTO order_discount VALUES (@CurOrderID, 6);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('XLarge', 'Original', 'completed', '2024-04-20 19:11:00', 27.94, 9.19, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 2, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 14, 0);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('XLarge', 'Original', 'completed', '2024-04-20 19:11:00', 31.50, 6.25, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 3, 1);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 10, 1);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 14, 0);

INSERT INTO pizza_discount VALUES (@CurPizzaID, 4);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('XLarge', 'Original', 'completed', '2024-04-20 19:11:00', 26.75, 8.18, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 4, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 17, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 14, 0);

INSERT INTO order_discount VALUES (@CurOrderID, 6);

COMMIT;

-- order 5
START TRANSACTION;

INSERT INTO customer (customer_FName, customer_LName, customer_PhoneNum)
VALUES ('Matt', 'Engers', '8644749953');

SET @CurCustID = LAST_INSERT_ID();

INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES (@CurCustID, 'pickup', '2024-03-02 17:30:00', 27.45, 7.88, 1);

SET @CurOrderID = LAST_INSERT_ID();

INSERT INTO pickup VALUES (@CurOrderID, 1);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('XLarge', 'Gluten-Free', 'completed', '2024-03-02 17:30:00', 27.45, 7.88, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 5, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 6, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 7, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 8, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 9, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 16, 0);

INSERT INTO pizza_discount VALUES (@CurPizzaID, 4);

COMMIT;

-- order 6
START TRANSACTION;

INSERT INTO customer (customer_FName, customer_LName, customer_PhoneNum)
VALUES ('Frank', 'Turner', '8642328944');

SET @CurCustID = LAST_INSERT_ID();

INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES (@CurCustID, 'delivery', '2024-03-02 18:17:00', 25.81, 4.24, 1);

SET @CurOrderID = LAST_INSERT_ID();

INSERT INTO delivery VALUES (@CurOrderID, 6745, 'Wessex St', 'Anderson', 'SC', 29621, 0);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Thin', 'completed', '2024-03-02 18:17:00', 25.81, 4.24, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 4, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 5, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 6, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 8, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 14, 1);

COMMIT;

-- order 7
START TRANSACTION;

INSERT INTO customer (customer_FName, customer_LName, customer_PhoneNum)
VALUES ('Milo', 'Auckerman', '8648785679');

SET @CurCustID = LAST_INSERT_ID();

INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES (@CurCustID, 'delivery', '2024-04-13 20:32:00', 31.66, 6.00, 1);

SET @CurOrderID = LAST_INSERT_ID();

INSERT INTO delivery VALUES (@CurOrderID, 8879, 'Suburban', 'Anderson', 'SC', 29621, 0);

-- INSERT INTO order_discount VALUES (@CurOrderID, 1);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Thin', 'completed', '2024-04-13 20:32:00', 18.00, 2.75, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 14, 1);

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES ('Large', 'Thin', 'completed', '2024-04-13 20:32:00', 19.25, 3.25, @CurOrderID);

SET @CurPizzaID = LAST_INSERT_ID();

INSERT INTO pizza_topping VALUES (@CurPizzaID, 13, 0);
INSERT INTO pizza_topping VALUES (@CurPizzaID, 1, 1);

INSERT INTO order_discount VALUES (@CurOrderID, 1);

COMMIT;

