# Go Development Environment Setup

### 1. Download Go

```bash
# download the latest version of Go. Visit [Go's official download page](https://go.dev/dl/) for the most recent version.
wget https://go.dev/dl/go1.24.4.linux-amd64.tar.gz

# Remove Previous Installation (if exists)
sudo rm -rf /usr/local/go

# extract from archive
sudo tar -C /usr/local -xzf go1.24.4.linux-amd64.tar.gz

# Add Go to your PATH by adding this line to your `~/.bashrc` or `~/.zshrc`:
export PATH=$PATH:/usr/local/go/bin

#Apply the changes:
source ~/.bashrc  # or source ~/.zshrc if you use zsh

# Verify Installation
go version
```

Let's create a simple Hello World program to verify everything works correctly.

### 1. Create a Test Project

```bash
# Create and enter a new project directory
mkdir go-sample
cd go-sample

# Initialize a new Go module
go mod init example.com/hello
```

### 2. Create Your First Go Program

Create a new file named `main.go`:

```bash
nano main.go
```

Add the following code:

```go
package main

import "fmt"

func main() {
    fmt.Println("Hello World!")
}
```

### 3. Run the Program

```bash
go run main.go
```

Expected output:
```
Hello World!
```

## Next Steps

- Learn about [Go modules](https://go.dev/blog/using-go-modules)
- Explore the [Go standard library](https://pkg.go.dev/std)
- Check out the [Go tour](https://go.dev/tour/)
- Read the [Effective Go](https://go.dev/doc/effective_go) guide

## Additional Resources

- [Official Go Documentation](https://go.dev/doc/)
- [Go by Example](https://gobyexample.com/)
- [Go Package Documentation](https://pkg.go.dev/)
