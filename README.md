# Easy HTTP Request 🌐

A lightweight, dependency-free wrapper for Android's `HttpURLConnection`. It simplifies asynchronous networking by handling background tasks, parameter encoding, and callbacks for GET, POST, file uploads, and image downloading.

[![Documentation](https://img.shields.io/badge/Documentation-View_Site-blue?style=for-the-badge&logo=google-chrome)](https://www.malekalhafi.com/projects/http-lib.html)

**View full documentation and live examples at:** [https://www.malekalhafi.com/projects/http-lib.html](https://www.malekalhafi.com/projects/http-lib.html)

## Features
- **Zero Dependencies:** Single Java file drop-in.
- **Async by Default:** Handles background threading automatically.
- **Versatile:** Supports GET, POST, Multipart File Uploads, and Image Downloading.
- **Callbacks:** Simple interface for handling responses on the UI thread.

## Installation
1. Download `HttpRequest.java` from this repository.
2. Copy it into your Android project's source path (e.g., `com.example.utils`).
3. Add Internet permission to `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   
# Usage Examples 
##​ GET Request
```java
HashMap<String, String> params = new HashMap<>();
params.put("user_id", "101");
params.put("token", "xyz-123");

HttpRequest.GET("[https://api.example.com/data](https://api.example.com/data)", params, response -> {
    Log.d("API", "Response: " + response);
});
```
## POST Request
```java
HashMap<String, String> params = new HashMap<>();
params.put("username", "malek");
params.put("password", "123456");

HttpRequest.POST("[https://api.example.com/login](https://api.example.com/login)", params, response -> {
    if(response.contains("Success")) {
         Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
    }
});
```
## File Upload
```java
HttpRequest.Upload_File(
    "[https://api.example.com/upload.php](https://api.example.com/upload.php)", 
    "uploaded_file", 
    "/sdcard/DCIM/image.jpg", 
    response -> Log.d("Upload", "Server replied: " + response)
);
```
## Download image to bitmab
```java
HttpRequest.Download_Image("[https://example.com/avatar.png](https://example.com/avatar.png)", bitmap -> {
    if (bitmap != null) {
        myImageView.setImageBitmap(bitmap);
    }
});
```
