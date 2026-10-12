INSERT INTO customer (name)
SELECT 'John'
WHERE NOT EXISTS (
    SELECT 1 FROM customer WHERE name = 'John'
);

INSERT INTO customer (name)
SELECT 'Kiran'
WHERE NOT EXISTS (
    SELECT 1 FROM customer WHERE name = 'Kiran'
);

INSERT INTO customer (name)
SELECT 'Sagar'
WHERE NOT EXISTS (
    SELECT 1 FROM customer WHERE name = 'Sagar'
);

INSERT INTO `transaction` (customer_id, amount, date)
SELECT c.id, 120.00, '2025-01-10'
FROM customer c
WHERE c.name = 'John'
  AND NOT EXISTS (
      SELECT 1 FROM `transaction` t
      WHERE t.customer_id = c.id AND t.amount = 120.00 AND t.date = '2025-01-10'
  );

INSERT INTO `transaction` (customer_id, amount, date)
SELECT c.id, 75.00, '2025-02-05'
FROM customer c
WHERE c.name = 'John'
  AND NOT EXISTS (
      SELECT 1 FROM `transaction` t
      WHERE t.customer_id = c.id AND t.amount = 75.00 AND t.date = '2025-02-05'
  );

INSERT INTO `transaction` (customer_id, amount, date)
SELECT c.id, 200.00, '2025-01-15'
FROM customer c
WHERE c.name = 'Kiran'
  AND NOT EXISTS (
      SELECT 1 FROM `transaction` t
      WHERE t.customer_id = c.id AND t.amount = 200.00 AND t.date = '2025-01-15'
  );

INSERT INTO `transaction` (customer_id, amount, date)
SELECT c.id, 80.00, '2025-03-20'
FROM customer c
WHERE c.name = 'Kiran'
  AND NOT EXISTS (
      SELECT 1 FROM `transaction` t
      WHERE t.customer_id = c.id AND t.amount = 80.00 AND t.date = '2025-03-20'
  );
