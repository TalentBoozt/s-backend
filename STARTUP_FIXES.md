# Startup Error Fixes

## Issues Fixed

### 1. **MongoConfig Bean Creation Failure** ✅
**Problem**: 
- `mongoDatabaseFactory()` bean was calling `getMongoDatabaseFactory()` which throws `IllegalStateException` if DATABASE_URI is "World"
- This prevented application startup

**Solution**:
- Added try-catch in `mongoDatabaseFactory()` bean method
- Provides clearer error message indicating missing configuration
- Error now clearly states what environment variables are needed

**Files Changed**:
- `src/main/java/com/talentboozt/s_backend/config/MongoConfig.java`

### 2. **JWT Service Configuration Validation** ✅
**Problem**:
- JWT secret key could be "World" (default) causing failures when JWT operations are attempted
- No validation to catch this early

**Solution**:
- Added `validateJwtSecret()` helper method
- Added validation to all JWT methods: `generateToken()`, `validateToken()`, `generateRefreshToken()`, `getUserFromToken()`
- Added default value "World" to `@Value` annotation to prevent null issues
- Throws clear error message if JWT secret is not configured

**Files Changed**:
- `src/main/java/com/talentboozt/s_backend/shared/security/service/JwtService.java`

### 3. **TenantContextFilter Initialization** ✅
**Problem**:
- Filter might initialize before servlet context is ready

**Solution**:
- Added `initFilterBean()` override to ensure proper initialization
- Filter now properly initializes before use

**Files Changed**:
- `src/main/java/com/talentboozt/s_backend/shared/tenant/TenantContextFilter.java`

## Required Environment Variables

For the application to start successfully, ensure these are set:

```bash
# Required for MongoDB
DATABASE_URI=mongodb://your-connection-string
DATABASE=your-database-name

# Required for JWT
JWT_SECRET=your-jwt-secret-key

# Required for server
SERVER_PORT=8080
```

## Error Messages

The application will now provide clear error messages:

1. **MongoDB Configuration Missing**:
   ```
   MongoDB configuration is required. Please set DATABASE_URI and DATABASE environment variables.
   ```

2. **JWT Secret Missing**:
   ```
   JWT secret key is not configured. Please set jwt-token.secret property.
   ```

## Testing

To verify the fixes:

1. **Test with missing DATABASE_URI**:
   ```bash
   # Should fail with clear error message
   unset DATABASE_URI
   java -jar s-backend.jar
   ```

2. **Test with missing JWT_SECRET**:
   ```bash
   # Should fail when JWT operation is attempted
   unset JWT_SECRET
   # Application starts but JWT operations fail with clear error
   ```

3. **Test with all configuration**:
   ```bash
   # Should start successfully
   export DATABASE_URI=mongodb://localhost:27017/test
   export DATABASE=test
   export JWT_SECRET=your-secret-key
   export SERVER_PORT=8080
   java -jar s-backend.jar
   ```

## Notes

- All fixes maintain backward compatibility
- Error messages are production-friendly (don't expose sensitive info)
- Validation happens early to catch configuration issues quickly
- Application will fail fast with clear messages rather than cryptic errors
