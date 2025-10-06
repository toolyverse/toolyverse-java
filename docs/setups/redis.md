## Redis Installation

```bash
# Install prerequisites
sudo apt-get install lsb-release curl gpg

# Add Redis repository
curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg
sudo chmod 644 /usr/share/keyrings/redis-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list

# Install Redis
sudo apt-get update
sudo apt-get install redis

# Enable and start Redis service
sudo systemctl enable redis-server
sudo systemctl start redis-server
```

### Test Redis Installation

```bash
# Connect to Redis CLI
redis-cli

# Test connection (should return PONG)
127.0.0.1:6379> ping
# Output: PONG

redis-cli -h 127.0.0.1 -p 6379 -a your_password ping
```
## Redis Installation Via Docker

docker run command:
`docker run -d -p 6379:6379 redis`


docker-compose.yml:

```bash
version: '3'
services:
  redis:
    image: redis
    ports:
      - "6379:6379"

```
## Redis Allow other ips:
```
sudo nano /etc/redis/redis.conf

bind 127.0.0.1 ::1 => bind 0.0.0.0 ::

protected-mode yes => no

OR
protected-mode yes
requirepass your_strong_password_here

sudo systemctl restart redis-server
sudo systemctl restart redis
```