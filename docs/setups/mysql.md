```

sudo apt install mariadb-server mariadb-client -y

sudo mariadb-secure-installation

sudo systemctl start mariadb

mariadb -u root -p


sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf

# Comment out or change this line:
# bind-address = 127.0.0.1

# To allow all IPs:
bind-address = 0.0.0.0

sudo systemctl restart mariadb
# or
sudo service mariadb restart

mysql -u root -p

-- Use the actual user table instead of the view

UPDATE mysql.global_priv SET Host='%' WHERE User='root' AND Host='localhost';

FLUSH PRIVILEGES;


CREATE USER 'newuser'@'%' IDENTIFIED BY 'secure_password123';
CREATE DATABASE testdb;
GRANT ALL PRIVILEGES ON testdb.* TO 'newuser'@'%';
FLUSH PRIVILEGES;

CREATE TABLE todos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    done BOOLEAN NOT NULL DEFAULT FALSE
);

```