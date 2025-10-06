
```yaml
services:
  minio:
    # image: 'quay.io/minio/minio:latest'
    image: 'minio/minio:RELEASE.2025-04-22T22-12-26Z'
    command: 'server /data --console-address ":9001"'
    environment:
      - MINIO_SERVER_URL=$MINIO_SERVER_URL
      - MINIO_BROWSER_REDIRECT_URL=$MINIO_BROWSER_REDIRECT_URL
      - MINIO_ROOT_USER=$SERVICE_USER_MINIO
      - MINIO_ROOT_PASSWORD=$SERVICE_PASSWORD_MINIO
    volumes:
      - 'minio-data:/data'
    healthcheck:
      test:
        - CMD
        - mc
        - ready
        - local
      interval: 5s
      timeout: 20s
      retries: 10


```