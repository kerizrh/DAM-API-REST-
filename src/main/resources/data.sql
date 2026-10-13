-- Insercion de datos iniciales en las tablas creadas por Hibernate ("User" y "product")
INSERT INTO "User" (id, name, last_name, email, password, active) 
VALUES (1, 'Marcos', 'Martinez', 'mmartinez@temp.com', '{bcrypt}$2a$10$uvWNfki.WxQhUD6e51di0uvC9G0DiGly7/PvyLR0Ay0mpFV24YklG', true);

INSERT INTO product (code, name, status) 
VALUES (1, 'Mochila backpack Adidas', true);

INSERT INTO product (code, name, status) 
VALUES (2, 'USB 2.0 Security', true);
