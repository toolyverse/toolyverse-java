### Docker Installation
```bash

sudo apt install ca-certificates curl

sudo curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update

sudo apt install docker-ce docker-ce-cli containerd.io -y

docker --version

sudo systemctl enable docker

sudo usermod -aG docker $USER
sudo usermod -aG docker <username>

newgrp docker

docker pull hello-world

docker images

sudo docker run hello-world

sudo curl -L "https://github.com/docker/compose/releases/download/$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep 'tag_name' | cut -d'"' -f4)/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose

docker-compose --version


#lazy docker
curl https://raw.githubusercontent.com/jesseduffield/lazydocker/master/scripts/install_update_linux.sh | bash

ls -la ~/.local/bin/
echo 'export PATH=$PATH:~/.local/bin' >> ~/.bashrc
source ~/.bashrc
lazydocker




sudo systemctl stop docker
sudo systemctl start docker


# remove all
docker rm -f $(docker ps -aq)


```
