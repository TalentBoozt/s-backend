# Security Leaks, Memory Leaks, and Resource Overuse - Fixes

## Issues Identified and Fixed

### 1. ✅ MongoClient Resource Leak (CRITICAL) - Fixed
**Problem**: `DynamicMongoDatabaseFactory.shutdown()` exists but is never called, leaving MongoDB connections open forever.

**Fix**: Added `@PreDestroy` hook in `SBackendApplication` to automatically call shutdown on application termination.

**Files Changed**:
- `SBackendApplication.java` - Added shutdown hook

### 2. ✅ RestTemplate Resource Overuse (CRITICAL) - Fixed
**Problem**: Multiple `RestTemplate` instances created without connection pooling/timeouts, causing socket leaks and resource exhaustion.

**Fix**: 
- Configured singleton `RestTemplate` bean with connection pooling and timeouts
- All `new RestTemplate()` calls refactored to use injected bean

**Files Changed**:
- `AppConfig.java` - Enhanced RestTemplate configuration
- `IpTimeZoneService.java` - Refactored
- `OAuthController.java` - Refactored
- `LinkedinAuthController.java` - Refactored
- `CaptchaVerificationController.java` - Refactored
- `GitHubAuthController.java` - Refactored
- `FacebookAuthController.java` - Refactored
- `ProxyController.java` - Refactored
- `GeoIPService.java` - Refactored

### 3. ✅ ThreadLocal Memory Leak in Async Operations (HIGH) - Fixed
**Problem**: `TenantContext` ThreadLocal may leak in async operations if not cleared.

**Fix**: Enhanced `ContextPropagatingTaskDecorator` to explicitly clear `TenantContext` in finally block.

**Files Changed**:
- `ContextPropagatingTaskDecorator.java` - Added TenantContext cleanup

### 4. ⚠️ Password Encryption Security Risk (HIGH) - Identified, Migration Needed
**Problem**: Static `KEY` and `IV` byte arrays stored in memory can be dumped via heap dumps.

**Status**: Identified - `SecureKeyManager.java` created as a secure alternative. Requires a migration plan to integrate into `EncryptionUtility` for new deployments and existing data.

### 5. ✅ Cache Monitoring (MEDIUM) - Implemented
**Problem**: No visibility into cache growth and potential unbounded growth.

**Fix**: Added `CacheMonitoringConfig` to periodically log cache sizes and enabled statistics recording for all Caffeine caches.

**Files Changed**:
- `CacheConfig.java` - Enabled stats recording
- `CacheMonitoringConfig.java` - New monitoring component

### 6. ✅ OkHttpClient Configuration (MEDIUM) - Fixed
**Problem**: `GeoLocationService` creates `OkHttpClient` without connection pooling/timeouts.

**Fix**: Configured `OkHttpClient` in `GeoLocationService` with connection pool and timeouts.

**Files Changed**:
- `GeoLocationService.java` - Configured OkHttpClient
