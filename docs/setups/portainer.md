```bash

docker volume create portainer_data

docker run -d -p 8000:8000 -p 9443:9443 --name portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer-ce:lts

```

### nginx conf:

```bash

# Subdomain configuration for admin.grkn.info - Portainer
server {
    server_name admin.grkn.info;
    
    # Let's Encrypt challenge directory
    location /.well-known/acme-challenge/ {
        root /var/www/html;
        allow all;
    }
    
    # Static assets (CSS, JS, images) - serve directly from Portainer
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        proxy_pass https://127.0.0.1:9443;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # SSL settings
        proxy_ssl_verify off;
        proxy_ssl_session_reuse on;
        
        # Cache static assets
        expires 1y;
        add_header Cache-Control "public, immutable";
        
        # Disable logging for static assets
        access_log off;
    }
    
    # Main Portainer proxy configuration
    location / {
        # Proxy to Portainer HTTPS port
        proxy_pass https://127.0.0.1:9443;
        
        # Essential headers for Portainer
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;
        
        # WebSocket support for Portainer (required for container logs, terminal, etc.)
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Increase timeouts for long-running operations
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Handle SSL verification (since Portainer uses self-signed cert)
        proxy_ssl_verify off;
        proxy_ssl_session_reuse on;
        
        # Buffer settings for better performance
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
        proxy_busy_buffers_size 8k;
        
        # Disable proxy cache for dynamic content
        proxy_cache off;
    }
    
    # Optional: Direct access to Portainer API (if needed)
    location /api/ {
        proxy_pass https://127.0.0.1:9443/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_ssl_verify off;
        
        # API specific timeouts
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
    
    # Security: Block sensitive files (keep existing security rules)
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    location ~ /(\.htaccess|\.htpasswd|\.env|composer\.(json|lock)|package\.json|yarn\.lock)$ {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    # Logging
    access_log /var/log/nginx/admin_grkn.info_access.log;
    error_log /var/log/nginx/admin_grkn.info_error.log;
    
    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/letsencrypt/live/grkn.info/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/grkn.info/privkey.pem; # managed by Certbot
    include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot
    
    # Security Headers (modified for Portainer compatibility)
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Frame-Options SAMEORIGIN always;  # Changed from DENY to SAMEORIGIN for Portainer UI
    add_header X-Content-Type-Options nosniff always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
}

server {
    if ($host = admin.grkn.info) {
        return 301 https://$host$request_uri;
    } # managed by Certbot
    
    listen 80;
    server_name admin.grkn.info;
    return 404; # managed by Certbot
}
```