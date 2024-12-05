USE PizzaDB;

DELIMITER //

-- Stored Procedure 1
CREATE PROCEDURE ADDCUSTOMER(IN FNAME VARCHAR(30), IN LNAME VARCHAR(30), IN PHONENUM VARCHAR(30))
BEGIN
    INSERT INTO customer (customer_FName, customer_LName, customer_PhoneNum)
    VALUES (FNAME, LNAME, PHONENUM);
end //

-- Stored Procedure 2
CREATE PROCEDURE CalculateInventoryStatus(
    IN p_ToppingName VARCHAR(30),
    OUT p_Status VARCHAR(50)
)
BEGIN
    DECLARE cur_inventory INT;
    DECLARE min_inventory INT;

    -- Get the current and minimum inventory levels
    SELECT topping_CurINVT, topping_MinINVT
    INTO cur_inventory, min_inventory
    FROM topping
    WHERE topping_TopName = p_ToppingName;

    -- Determine inventory status
    IF cur_inventory < min_inventory THEN
        SET p_Status = CONCAT(p_ToppingName, ' is below the required inventory level!');
    ELSE
        SET p_Status = CONCAT(p_ToppingName, ' has sufficient inventory.');
    END IF;
END //

-- Stored Function 1
CREATE FUNCTION CalculateOrderCost (
    p_OrderID INT
) RETURNS DECIMAL(10,2)
READS SQL DATA
BEGIN
    DECLARE totalCost DECIMAL(10,2);
    SET totalCost = (
        SELECT SUM(pizza_CustPrice)
        FROM pizza
        WHERE ordertable_OrderID = p_OrderID
    );
    RETURN totalCost;
END //

-- Stored Function 2
CREATE FUNCTION GetCustomerOrderCount(customer_id INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE order_count INT;
    SELECT COUNT(*)
    INTO order_count
    FROM ordertable
    WHERE customer_CustID = customer_id;
    RETURN order_count;
END //

-- Update Trigger 1
CREATE TRIGGER PIZZASTATUS AFTER UPDATE ON pizza
FOR EACH ROW
BEGIN
    IF NEW.pizza_PizzaState = 'completed' THEN
        UPDATE ordertable
        SET ordertable_isComplete = 1
        WHERE ordertable_OrderID = NEW.ordertable_OrderID;
    end if;
end //

-- Update Trigger 2
CREATE TRIGGER BEFOREPIZZASTATUS
AFTER UPDATE ON pizza
FOR EACH ROW
BEGIN
    IF NEW.pizza_PizzaState = 'in progress' THEN
        UPDATE ordertable
        SET ordertable_isComplete = 0
        WHERE ordertable_OrderID = NEW.ordertable_OrderID;
    end if;
end //

-- Insert Trigger 1
CREATE TRIGGER DEFORDERTYPE
BEFORE INSERT ON ordertable
FOR EACH ROW
    BEGIN
        IF NEW.ordertable_OrderType IS NULL THEN
            SET NEW.ordertable_OrderType = 'dinein';
        end if;
    end //

-- Insert Trigger 2
CREATE TRIGGER DEFTOPPINGINV
BEFORE INSERT ON topping
FOR EACH ROW
    BEGIN
        IF NEW.topping_CurINVT IS NULL THEN
            SET NEW.topping_CurINVT = 100;
        end if;
    end //

DELIMITER ;

