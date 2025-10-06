// Connect to MongoDB as admin first
// mongo --host localhost --port 27017 -u admin -p

// Method 1: Create database and user in one go
use myapp_db

// Create user with full permissions on the database
db.createUser({
  user: "myapp_user",
  pwd: "secure_password_123",
  roles: [
    {
      role: "dbOwner",
      db: "myapp_db"
    }
  ]
})

// Verify user creation
db.getUsers()

// Method 2: More granular permissions (alternative approach)
use myapp_db

db.createUser({
  user: "myapp_admin",
  pwd: "admin_password_456", 
  roles: [
    {
      role: "readWrite",
      db: "myapp_db"
    },
    {
      role: "dbAdmin", 
      db: "myapp_db"
    },
    {
      role: "userAdmin",
      db: "myapp_db"
    }
  ]
})

// Method 3: Create user with custom role (maximum flexibility)
use myapp_db

// First create a custom role with all permissions
db.createRole({
  role: "fullAccess",
  privileges: [
    {
      resource: { db: "myapp_db", collection: "" },
      actions: [
        "find", "insert", "update", "remove", "createIndex", "dropIndex",
        "createCollection", "dropCollection", "collStats", "dbStats",
        "indexStats", "validate", "compact", "reIndex"
      ]
    }
  ],
  roles: []
})

// Then create user with the custom role
db.createUser({
  user: "custom_user",
  pwd: "custom_password_789",
  roles: [
    {
      role: "fullAccess",
      db: "myapp_db"
    }
  ]
})

// Test the connection (run this after creating user)
// mongo --host localhost --port 27017 -u myapp_user -p secure_password_123 --authenticationDatabase myapp_db

// Additional useful commands:

// Show current database
db.getName()

// List all users in current database
db.getUsers()

// Update user password
db.updateUser("myapp_user", {
  pwd: "new_password"
})

// Grant additional roles to existing user
db.grantRolesToUser("myapp_user", [
  { role: "readWrite", db: "another_db" }
])

// Remove user
// db.dropUser("username")

// Show user privileges
db.runCommand({
  usersInfo: "myapp_user",
  showPrivileges: true
})