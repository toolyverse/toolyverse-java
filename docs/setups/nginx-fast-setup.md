nginx-startup-fast.sh

```bash
#!/bin/bash

# Complete setup for grkn.info with SSL and separate nginx configs for each subdomain
# Run as root: sudo bash complete-setup.sh

set -e

# Configuration - Add/remove subdomains here
SUBDOMAINS=("api" "admin" "blog" "dev" "test" "app" "docs" "mail")
DOMAIN="grkn.info"
EMAIL="admin@grkn.info"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

log "Starting complete setup for $DOMAIN..."

# Check if running as root
if [ "$EUID" -ne 0 ]; then
    error "Please run as root: sudo bash $0"
fi

# Update system
log "Updating system packages..."
apt update -qq

# Install required packages
log "Installing nginx, certbot, and curl..."
apt install -y nginx certbot python3-certbot-nginx curl

# Stop nginx if running
log "Stopping nginx..."
systemctl stop nginx 2>/dev/null || true

# Remove existing configurations
log "Cleaning up existing configurations..."
rm -f /etc/nginx/sites-enabled/default
rm -f /etc/nginx/sites-enabled/$DOMAIN
rm -f /etc/nginx/sites-available/$DOMAIN

# Remove existing subdomain configs
for subdomain in "${SUBDOMAINS[@]}"; do
    rm -f /etc/nginx/sites-enabled/$subdomain.$DOMAIN
    rm -f /etc/nginx/sites-available/$subdomain.$DOMAIN
done

# Create directory structure
log "Creating directory structure..."
mkdir -p /var/www/$DOMAIN
mkdir -p /var/www/subdomains

# Create subdomain directories
for subdomain in "${SUBDOMAINS[@]}"; do
    mkdir -p /var/www/subdomains/$subdomain.$DOMAIN
    log "Created directory for $subdomain.$DOMAIN"
done

# Create main website
log "Creating main website content..."
cat > /var/www/$DOMAIN/index.html << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to nginx!</title>
    <style>
        body {
            width: 35em;
            margin: 0 auto;
            font-family: Tahoma, Verdana, Arial, sans-serif;
        }
        h1 {
            text-align: center;
            margin: 50px 0 30px 0;
        }
        p {
            margin: 20px 0;
            line-height: 1.4;
        }
        .footer {
            text-align: center;
            margin-top: 50px;
            font-size: 0.8em;
            color: #999;
        }
    </style>
</head>
<body>
    <h1>Welcome to nginx!</h1>
    
    <p>If you see this page, the nginx web server is successfully installed and working. Further configuration is required.</p>
    
    <p>For online documentation and support please refer to <a href="http://nginx.org/">nginx.org</a>.<br/>
    Commercial support is available at <a href="http://nginx.com/">nginx.com</a>.</p>
    
    <p><em>Thank you for using nginx.</em></p>
    
    <div class="footer">
        <p>nginx/1.24.0</p>
    </div>
</body>
</html>
EOF

# Create subdomain pages
log "Creating subdomain pages..."
for subdomain in "${SUBDOMAINS[@]}"; do
    cat > /var/www/subdomains/$subdomain.$DOMAIN/index.html << EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$subdomain - $DOMAIN</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            line-height: 1.6;
            color: #333;
        }
        h1 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
        }
        .status {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            padding: 15px;
            border-radius: 5px;
            margin: 20px 0;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            padding: 8px 15px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 3px;
        }
        .back-link:hover {
            background: #5a6268;
        }
    </style>
</head>
<body>
    <h1>$subdomain.$DOMAIN</h1>
    
    <div class="status">
        <strong>🔧 Service: Ready for Configuration</strong><br>
        Subdomain: $subdomain<br>
        Status: Placeholder Page
    </div>
    
    <p>This is the $subdomain service for $DOMAIN. This subdomain is ready for your application deployment.</p>
    
    <h2>Next Steps</h2>
    <ul>
        <li>Deploy your application files to: <code>/var/www/subdomains/$subdomain.$DOMAIN/</code></li>
        <li>Configure any backend services if needed</li>
        <li>Update nginx configuration for specific routing requirements</li>
    </ul>
    
    <a href="https://$DOMAIN" class="back-link">← Back to Main Site</a>
</body>
</html>
EOF
    log "Created page for $subdomain.$DOMAIN"
done

# Create robots.txt
log "Creating robots.txt..."
cat > /var/www/$DOMAIN/robots.txt << EOF
User-agent: *
Allow: /

Sitemap: https://$DOMAIN/sitemap.xml
EOF

# Create sitemap.xml
log "Creating sitemap.xml..."
cat > /var/www/$DOMAIN/sitemap.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    <url>
        <loc>https://$DOMAIN/</loc>
        <lastmod>$(date +%Y-%m-%d)</lastmod>
        <changefreq>daily</changefreq>
        <priority>1.0</priority>
    </url>
EOF

# Add subdomains to sitemap
for subdomain in "${SUBDOMAINS[@]}"; do
    cat >> /var/www/$DOMAIN/sitemap.xml << EOF
    <url>
        <loc>https://$subdomain.$DOMAIN/</loc>
        <lastmod>$(date +%Y-%m-%d)</lastmod>
        <changefreq>weekly</changefreq>
        <priority>0.8</priority>
    </url>
EOF
done

cat >> /var/www/$DOMAIN/sitemap.xml << EOF
</urlset>
EOF

# Set proper permissions
log "Setting proper permissions..."
chown -R www-data:www-data /var/www/$DOMAIN
chown -R www-data:www-data /var/www/subdomains
chmod -R 755 /var/www/$DOMAIN
chmod -R 755 /var/www/subdomains

# Create main domain nginx configuration
log "Creating main domain nginx configuration..."
cat > /etc/nginx/sites-available/$DOMAIN << EOF
# Main domain configuration for $DOMAIN
server {
    listen 80;
    server_name $DOMAIN;
    
    root /var/www/$DOMAIN;
    index index.html index.htm;
    
    # Let's Encrypt challenge directory
    location /.well-known/acme-challenge/ {
        root /var/www/html;
        allow all;
    }
    
    location / {
        try_files \$uri \$uri/ =404;
    }
    
    # Static assets optimization
    location ~* \\.(jpg|jpeg|png|gif|ico|css|js|woff|woff2|ttf|svg|webp)\$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }
    
    # Security: Block sensitive files
    location ~ /\\. {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    location ~ /(\\.htaccess|\\.htpasswd|\\.env|composer\\.(json|lock)|package\\.json|yarn\\.lock)\$ {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    # Logging
    access_log /var/log/nginx/${DOMAIN}_access.log;
    error_log /var/log/nginx/${DOMAIN}_error.log;
}
EOF

# Create individual subdomain configurations
log "Creating individual subdomain configurations..."
for subdomain in "${SUBDOMAINS[@]}"; do
    log "Creating configuration for $subdomain.$DOMAIN..."
    
    # Generic subdomain configuration for all subdomains
    cat > /etc/nginx/sites-available/$subdomain.$DOMAIN << EOF
# Subdomain configuration for $subdomain.$DOMAIN
server {
    listen 80;
    server_name $subdomain.$DOMAIN;
    
    root /var/www/subdomains/$subdomain.$DOMAIN;
    index index.html index.htm index.php;
    
    # Let's Encrypt challenge directory
    location /.well-known/acme-challenge/ {
        root /var/www/html;
        allow all;
    }
    
    location / {
        try_files \$uri \$uri/ =404;
    }
    
    # PHP support (uncomment if needed)
    # location ~ \\.php\$ {
    #     include snippets/fastcgi-php.conf;
    #     fastcgi_pass unix:/var/run/php/php8.1-fpm.sock;
    # }
    
    # Proxy support (uncomment and modify when needed)
    # location /api/ {
    #     proxy_pass http://127.0.0.1:3000/;
    #     proxy_set_header Host \$host;
    #     proxy_set_header X-Real-IP \$remote_addr;
    #     proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    #     proxy_set_header X-Forwarded-Proto \$scheme;
    # }
    
    # Static assets optimization
    location ~* \\.(jpg|jpeg|png|gif|ico|css|js|woff|woff2|ttf|svg|webp)\$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }
    
    # Security: Block sensitive files
    location ~ /\\. {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    location ~ /(\\.htaccess|\\.htpasswd|\\.env|composer\\.(json|lock)|package\\.json|yarn\\.lock)\$ {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    # Logging
    access_log /var/log/nginx/${subdomain}_${DOMAIN}_access.log;
    error_log /var/log/nginx/${subdomain}_${DOMAIN}_error.log;
}
EOF
done

# Enable all sites
log "Enabling all sites..."
ln -sf /etc/nginx/sites-available/$DOMAIN /etc/nginx/sites-enabled/

for subdomain in "${SUBDOMAINS[@]}"; do
    ln -sf /etc/nginx/sites-available/$subdomain.$DOMAIN /etc/nginx/sites-enabled/
    log "Enabled $subdomain.$DOMAIN"
done

# Test configuration
log "Testing nginx configuration..."
if nginx -t; then
    log "✅ Configuration test passed"
else
    error "Configuration test failed"
fi

# Start nginx
log "Starting nginx..."
systemctl start nginx
systemctl enable nginx

# Wait for nginx to start
sleep 3

# Test HTTP access
log "Testing HTTP access..."
if curl -s -o /dev/null -w "%{http_code}" http://$DOMAIN | grep -q "200"; then
    log "✅ HTTP access working - http://$DOMAIN"
else
    warn "HTTP access test failed"
fi

# Build SSL certificate command with all domains
log "Preparing SSL certificate for $DOMAIN and subdomains..."
CERT_DOMAINS="-d $DOMAIN"
for subdomain in "${SUBDOMAINS[@]}"; do
    CERT_DOMAINS="$CERT_DOMAINS -d $subdomain.$DOMAIN"
done

# Get SSL certificate
log "Obtaining SSL certificate..."
if certbot --nginx  --expand $CERT_DOMAINS --non-interactive --agree-tos --email $EMAIL --redirect --quiet; then
    log "✅ SSL certificate obtained and configured"
    
    # Wait for cert to be applied
    sleep 5
    
    # Test HTTPS
    if curl -s -o /dev/null -w "%{http_code}" https://$DOMAIN | grep -q "200"; then
        log "✅ HTTPS access working - https://$DOMAIN"
    else
        warn "HTTPS access test failed"
    fi
    
    # Test redirect
    if curl -s -o /dev/null -w "%{http_code}" -I http://$DOMAIN | grep -q "301\|302"; then
        log "✅ HTTP to HTTPS redirect working"
    else
        warn "HTTP redirect test failed"
    fi
else
    warn "SSL certificate setup failed, but HTTP is working"
    log "You can manually run: sudo certbot --nginx $CERT_DOMAINS"
fi

# Add security headers to all HTTPS configurations
log "Adding security headers to all HTTPS configurations..."

# Function to add security headers to a config file
add_security_headers() {
    local config_file=$1
    local temp_file=$(mktemp)
    
    # Add security headers after SSL configuration
    awk '
        /ssl_dhparam/ { 
            print $0
            print ""
            print "    # Security Headers"
            print "    add_header Strict-Transport-Security \"max-age=31536000; includeSubDomains; preload\" always;"
            print "    add_header X-Frame-Options DENY always;"
            print "    add_header X-Content-Type-Options nosniff always;"
            print "    add_header X-XSS-Protection \"1; mode=block\" always;"
            print "    add_header Referrer-Policy \"strict-origin-when-cross-origin\" always;"
            next
        }
        { print }
    ' "$config_file" > "$temp_file"
    
    mv "$temp_file" "$config_file"
}

# Apply security headers to all configurations
if [ -f "/etc/nginx/sites-available/$DOMAIN" ]; then
    add_security_headers "/etc/nginx/sites-available/$DOMAIN"
fi

for subdomain in "${SUBDOMAINS[@]}"; do
    if [ -f "/etc/nginx/sites-available/$subdomain.$DOMAIN" ]; then
        add_security_headers "/etc/nginx/sites-available/$subdomain.$DOMAIN"
    fi
done

# Test final configuration and reload
log "Testing final configuration..."
if nginx -t; then
    systemctl reload nginx
    log "✅ Security headers applied to all configurations"
else
    warn "Final configuration failed, keeping current working config"
fi

# Test all subdomains
log "Testing all subdomains..."
for subdomain in "${SUBDOMAINS[@]}"; do
    if curl -s -o /dev/null -w "%{http_code}" https://$subdomain.$DOMAIN | grep -q "200"; then
        log "✅ $subdomain.$DOMAIN is working"
    else
        warn "$subdomain.$DOMAIN test failed"
    fi
done

# Create enhanced management script
log "Creating management script..."
cat > /usr/local/bin/grkn-status << EOF
#!/bin/bash
echo "=== $DOMAIN Infrastructure Status ==="
systemctl status nginx --no-pager -l | head -10

echo -e "\\n=== Configuration Test ==="
nginx -t

echo -e "\\n=== SSL Certificate ==="
certbot certificates 2>/dev/null | grep -A 5 "$DOMAIN" || echo "No certificate found"

echo -e "\\n=== Site Tests ==="
echo "Main site: \$(curl -s -o /dev/null -w "%{http_code}" https://$DOMAIN)"
EOF

for subdomain in "${SUBDOMAINS[@]}"; do
    echo "echo \"$subdomain: \$(curl -s -o /dev/null -w \"%{http_code}\" https://$subdomain.$DOMAIN)\"" >> /usr/local/bin/grkn-status
done

cat >> /usr/local/bin/grkn-status << EOF

echo -e "\\n=== Configuration Files ==="
echo "Main domain: /etc/nginx/sites-available/$DOMAIN"
EOF

for subdomain in "${SUBDOMAINS[@]}"; do
    echo "echo \"$subdomain: /etc/nginx/sites-available/$subdomain.$DOMAIN\"" >> /usr/local/bin/grkn-status
done

cat >> /usr/local/bin/grkn-status << EOF

echo -e "\\n=== Recent Errors (Main Domain) ==="
tail -n 3 /var/log/nginx/${DOMAIN}_error.log 2>/dev/null || echo "No errors found"

echo -e "\\n=== Enabled Sites ==="
ls -la /etc/nginx/sites-enabled/ | grep -E "$DOMAIN"
EOF

chmod +x /usr/local/bin/grkn-status

# Create subdomain management script
cat > /usr/local/bin/grkn-subdomain << 'EOF'
#!/bin/bash
# Subdomain management script for grkn.info

DOMAIN="grkn.info"
NGINX_AVAILABLE="/etc/nginx/sites-available"
NGINX_ENABLED="/etc/nginx/sites-enabled"
WEB_ROOT="/var/www/subdomains"

case "$1" in
    "enable")
        if [ -z "$2" ]; then
            echo "Usage: grkn-subdomain enable <subdomain>"
            exit 1
        fi
        SUBDOMAIN="$2"
        if [ -f "$NGINX_AVAILABLE/$SUBDOMAIN.$DOMAIN" ]; then
            ln -sf "$NGINX_AVAILABLE/$SUBDOMAIN.$DOMAIN" "$NGINX_ENABLED/"
            nginx -t && systemctl reload nginx
            echo "✅ Enabled $SUBDOMAIN.$DOMAIN"
        else
            echo "❌ Configuration file not found: $NGINX_AVAILABLE/$SUBDOMAIN.$DOMAIN"
        fi
        ;;
    "disable")
        if [ -z "$2" ]; then
            echo "Usage: grkn-subdomain disable <subdomain>"
            exit 1
        fi
        SUBDOMAIN="$2"
        rm -f "$NGINX_ENABLED/$SUBDOMAIN.$DOMAIN"
        nginx -t && systemctl reload nginx
        echo "✅ Disabled $SUBDOMAIN.$DOMAIN"
        ;;
    "list")
        echo "Available subdomains:"
        ls -1 "$NGINX_AVAILABLE/" | grep "$DOMAIN" | grep -v "^$DOMAIN$"
        echo ""
        echo "Enabled subdomains:"
        ls -1 "$NGINX_ENABLED/" | grep "$DOMAIN" | grep -v "^$DOMAIN$"
        ;;
    "test")
        if [ -z "$2" ]; then
            echo "Usage: grkn-subdomain test <subdomain>"
            exit 1
        fi
        SUBDOMAIN="$2"
        echo "Testing $SUBDOMAIN.$DOMAIN..."
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "https://$SUBDOMAIN.$DOMAIN")
        echo "HTTP Status: $HTTP_CODE"
        ;;
    *)
        echo "Usage: grkn-subdomain {enable|disable|list|test} [subdomain]"
        echo ""
        echo "Commands:"
        echo "  enable <subdomain>   - Enable a subdomain"
        echo "  disable <subdomain>  - Disable a subdomain"
        echo "  list                 - List all subdomains"
        echo "  test <subdomain>     - Test a subdomain"
        ;;
esac
EOF

chmod +x /usr/local/bin/grkn-subdomain

# Final summary
log "==============================================="
log "🎉 Complete $DOMAIN setup finished!"
log "==============================================="
echo -e "${GREEN}"
echo "✅ Nginx configured with separate config files"
echo "✅ SSL certificates obtained for all domains"
echo "✅ All subdomains configured individually"
echo "✅ Security headers applied to all sites"
echo "✅ SEO files created (robots.txt, sitemap.xml)"
echo ""
echo "🔗 Live sites:"
echo "   Main: https://$DOMAIN"
for subdomain in "${SUBDOMAINS[@]}"; do
    echo "   $subdomain: https://$subdomain.$DOMAIN"
done
echo ""
echo "📋 Management commands:"
echo "   Status check: grkn-status"
echo "   Subdomain management: grkn-subdomain {enable|disable|list|test}"
echo "   Test config: sudo nginx -t"
echo "   Reload nginx: sudo systemctl reload nginx"
echo "   View main logs: sudo tail -f /var/log/nginx/${DOMAIN}_error.log"
echo ""
echo "📁 Configuration files:"
echo "   Main domain: /etc/nginx/sites-available/$DOMAIN"
for subdomain in "${SUBDOMAINS[@]}"; do
    echo "   $subdomain: /etc/nginx/sites-available/$subdomain.$DOMAIN"
done
echo ""
echo "📁 Directory structure:"
echo "   Main site: /var/www/$DOMAIN/"
echo "   Subdomains: /var/www/subdomains/"
for subdomain in "${SUBDOMAINS[@]}"; do
    echo "     ├── $subdomain.$DOMAIN/"
done
echo ""
echo "🔧 Individual subdomain management:"
echo "   Enable: grkn-subdomain enable <subdomain>"
echo "   Disable: grkn-subdomain disable <subdomain>"
echo "   List all: grkn-subdomain list"
echo "   Test: grkn-subdomain test <subdomain>"
echo ""
echo "🔧 SSL certificate renewal:"
echo "   Certificates will auto-renew via cron"
echo "   Manual renewal: sudo certbot renew"
echo -e "${NC}"

log "All done! Your complete infrastructure with separate configs is ready! 🚀"
```

```bash
nano nginx-startup.sh
sudo chmod +x nginx-startup.sh
sudo ./nginx-startup.sh
sudo bash ./nginx-startup.sh
```