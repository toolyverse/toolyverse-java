# Auto Deploy CI/CD Github

First, generate an SSH key pair to securely connect your GitHub repository with your deployment server.

```bash
ssh-keygen -t ed25519 -a 200 -C "mail@hotmail.com"
```

After generating the key, you need to add the public key to your GitHub account&#x20;

```bash
cat ~/.ssh/id_ed25519.pub
```

* Copy the output and go to [GitHub SSH Keys settings](https://github.com/settings/ssh/new).
* Paste the copied key into the SSH key field and give it a title.

To allow GitHub to access your server, append the public SSH key to the `~/.ssh/authorized_keys` file on your server:

<pre class="language-bash"><code class="lang-bash"><strong>sudo nano ~/.ssh/authorized_keys
</strong></code></pre>

For the GitHub Actions to authenticate with your server, you need to add the private SSH key as a secret in your GitHub repository

```bash
cat ~/.ssh/id_ed25519
```

* Copy the private key.
* In your GitHub repository, go to **Settings > Secrets and Variables > Actions**.
* Create a new secret with the name `SSH_PRIVATE_KEY` and paste the private key into the value field.

Now, set up a GitHub Actions workflow to automate the deployment process

* Create a file named `deploy.yaml` in the `.github/workflows/` directory of your repository.
* Add the following content to define the deployment process:
* Note! : Don't forget to change `<PROJECT_DIR>`

```yaml
name: Deploy
on:
  push:
    branches:
      - dev
      - master
    paths:
      - 'src/**'
      - 'public/**'
      - 'package.json'
      - '.github/workflows/deploy.yaml'
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Deploy via SSH
        uses: appleboy/ssh-action@v0.1.10
        with:
          host: ${{ secrets.SSH_HOST }}
          username: ${{ secrets.SSH_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          port: ${{ secrets.SSH_PORT }}
          script: |
            cd /home/<PROJECT_DIR>
            git reset --hard
            git checkout master
            git pull origin master
            mvn clean install -DskipTests
            cd target
            cp *.war /opt/tomcat/webapps/
            sudo systemctl restart tomcat

```