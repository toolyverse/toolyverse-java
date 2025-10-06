# Postman API Testing Scripts

This document provides comprehensive Postman scripts for API testing, including pre-request scripts for request preparation and post-response scripts for validation and data extraction.

## Table of Contents

- [Pre-Request Scripts](#pre-request-scripts)
- [Post-Response Scripts](#post-response-scripts)
  - [Authentication & Token Management](#authentication--token-management)
  - [Basic Response Validation](#basic-response-validation)
  - [Field Validation](#field-validation)
  - [Complex Data Validation](#complex-data-validation)
  - [Pagination Handling](#pagination-handling)
  - [Data Extraction & Storage](#data-extraction--storage)

## Pre-Request Scripts

### Basic Request Body Processing

Use this script to clean and modify your request body before sending the request:

```javascript
// Clean the request body (remove comments and extra whitespace)
let bodyText = pm.request.body.raw;

// Remove lines starting with //
bodyText = bodyText.split('\n')
    .filter(line => !line.trim().startsWith('//'))
    .join('\n');

try {
    let requestBody = JSON.parse(bodyText);
    
    // Update specific fields
    requestBody.timestamp = new Date().toISOString();
    requestBody.userId = pm.environment.get("currentUserId");
    requestBody.sessionId = pm.globals.get("sessionId");
    
    // Set the updated body back to the request
    pm.request.body.raw = JSON.stringify(requestBody);
    
} catch (error) {
    console.log("Still couldn't parse JSON:", error.message);
}
```

**What this script does:**
- Removes comment lines starting with `//` from the request body
- Automatically adds timestamp, userId, and sessionId to requests
- Handles JSON parsing errors gracefully

## Post-Response Scripts

![alt text](image.png)

### Authentication & Token Management

Extract and store authentication tokens from API responses:

```javascript
// Parse the response JSON
const responseJson = pm.response.json();

// Extract and store authentication token
if (responseJson.token) {
    pm.environment.set("authToken", responseJson.token);
    pm.globals.set("bearerToken", "Bearer " + responseJson.token);
}

// Extract other useful data
if (responseJson.user) {
    pm.environment.set("userId", responseJson.user.id);
    pm.environment.set("userEmail", responseJson.user.email);
    pm.environment.set("userName", responseJson.user.name);
}

// Store refresh token if available
if (responseJson.refreshToken) {
    pm.environment.set("refreshToken", responseJson.refreshToken);
}
```

### Basic Response Validation

Standard tests for response status, timing, and headers:

```javascript
// Test response status
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Status code is successful", function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 201, 202]);
});

// Test response time
pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// Test headers
pm.test("Content-Type is JSON", function () {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});

pm.test("Has authentication header", function () {
    pm.expect(pm.response.headers.get("Authorization")).to.exist;
});
```

### Field Validation

#### Basic Field Existence and Type Checks

```javascript
const responseJson = pm.response.json();

pm.test("Response has required fields", function () {
    pm.expect(responseJson).to.have.property("id");
    pm.expect(responseJson).to.have.property("email");
    pm.expect(responseJson).to.have.property("token");
});

pm.test("Field types are correct", function () {
    pm.expect(responseJson.id).to.be.a("string");
    pm.expect(responseJson.email).to.be.a("string");
    pm.expect(responseJson.isActive).to.be.a("boolean");
    pm.expect(responseJson.createdAt).to.be.a("string");
});
```

#### String Length and Array Size Validation

```javascript
const responseJson = pm.response.json();

pm.test("String field lengths are valid", function () {
    // Test minimum length
    pm.expect(responseJson.token).to.have.lengthOf.at.least(10);
    
    // Test maximum length
    pm.expect(responseJson.email).to.have.lengthOf.at.most(100);
    
    // Test exact length
    pm.expect(responseJson.id).to.have.lengthOf(24);
    
    // Test range
    pm.expect(responseJson.username).to.have.lengthOf.within(3, 30);
});

pm.test("Array sizes are correct", function () {
    pm.expect(responseJson.permissions).to.be.an("array");
    pm.expect(responseJson.permissions).to.have.lengthOf.at.least(1);
    pm.expect(responseJson.roles).to.have.lengthOf.at.most(5);
});
```

#### Boolean and Conditional Validation

```javascript
const responseJson = pm.response.json();

pm.test("Boolean fields have correct values", function () {
    pm.expect(responseJson.isActive).to.be.true;
    pm.expect(responseJson.isDeleted).to.be.false;
    pm.expect(responseJson.emailVerified).to.be.a("boolean");
});

pm.test("Conditional field validation", function () {
    if (responseJson.userType === "admin") {
        pm.expect(responseJson.permissions).to.include("admin_access");
    }
    
    if (responseJson.isPremium) {
        pm.expect(responseJson.subscriptionId).to.exist;
        pm.expect(responseJson.subscriptionId).to.not.be.empty;
    }
});
```

### Complex Data Validation

#### Array and Object Structure Validation

```javascript
const responseJson = pm.response.json();

pm.test("Array contains expected elements", function () {
    pm.expect(responseJson.tags).to.be.an("array").that.includes("user");
    pm.expect(responseJson.permissions).to.include.members(["read", "write"]);
});

pm.test("Object structure validation", function () {
    pm.expect(responseJson.profile).to.be.an("object");
    pm.expect(responseJson.profile).to.have.all.keys("firstName", "lastName", "avatar");
    
    // Nested object validation
    pm.expect(responseJson.address.country).to.equal("USA");
    pm.expect(responseJson.settings.notifications.email).to.be.a("boolean");
});
```

### Pagination Handling

```javascript
const responseJson = pm.response.json();

pm.test("Pagination data is valid", function () {
    pm.expect(responseJson.pagination).to.exist;
    pm.expect(responseJson.pagination.currentPage).to.be.at.least(1);
    pm.expect(responseJson.pagination.totalPages).to.be.at.least(1);
    pm.expect(responseJson.pagination.totalItems).to.be.at.least(0);
});

// Set next page for subsequent requests
if (responseJson.pagination && responseJson.pagination.hasNextPage) {
    pm.environment.set("nextPage", responseJson.pagination.currentPage + 1);
} else {
    pm.environment.unset("nextPage");
}
```

### Data Extraction & Storage

```javascript
const responseJson = pm.response.json();

// Extract and store multiple user details
const userDetails = {
    id: responseJson.user.id,
    email: responseJson.user.email,
    role: responseJson.user.role,
    permissions: responseJson.user.permissions
};

pm.environment.set("currentUser", JSON.stringify(userDetails));

// Extract array data
if (responseJson.items && responseJson.items.length > 0) {
    const itemIds = responseJson.items.map(item => item.id);
    pm.environment.set("itemIds", JSON.stringify(itemIds));
    pm.environment.set("firstItemId", itemIds[0]);
    pm.environment.set("lastItemId", itemIds[itemIds.length - 1]);
}
```
