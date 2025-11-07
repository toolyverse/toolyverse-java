```
server {
    listen 80;
    listen 443 ssl;
    server_name api.toolyverse.io;

    root /var/www/api.toolyverse.io;
    index index.html;

    ssl_certificate /etc/ssl/cert.pem;
    ssl_certificate_key /etc/ssl/server.key;

    # SSL settings
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Serve images as CDN (no auto-download)
    location /images/ {
        alias /var/www/api.toolyverse.io/images/;

        # Prevent auto-download, display inline instead
        add_header Content-Disposition "inline";

        # Set proper MIME types
        types {
            image/jpeg jpg jpeg;
            image/png png;
            image/gif gif;
            image/webp webp;
            image/svg+xml svg;
        }

        # Cache control for CDN
        expires 30d;
        add_header Cache-Control "public, immutable";

        # CORS headers (if needed for cross-origin access)
        add_header Access-Control-Allow-Origin "*";
        add_header Access-Control-Allow-Methods "GET, OPTIONS";

        # Security
        add_header X-Content-Type-Options "nosniff";
    }

    # Proxy all other requests to your application
    location / {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

**Key changes:**

1. **`/images/` location block** - Add this above your main `/` location
2. **`Content-Disposition: inline`** - Forces browser to display, not download
3. **`alias`** - Points to your actual image directory
4. **Cache headers** - Improves CDN performance (30 days cache)
5. **CORS headers** - Allows cross-origin access if needed

**Usage in your code:**

```
# Store images in:
/var/www/api.toolyverse.io/images/

# Access via:
https://api.toolyverse.io/images/photo.jpg