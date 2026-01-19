# MongoDB Index Conflict Fix

## Problem

The application was failing to start with the following error:

```
Cannot create index for 'occupation' in collection 'portal_employees'
Keys: {occupation: 1}
Options: {name: "occupation"}
Error: Index already exists with a different name: occupation_1
MongoDB error code: 85 (IndexOptionsConflict)
```

### Root Cause

1. **Spring Data MongoDB Auto-Index Creation**: With `spring.data.mongodb.auto-index-creation=true`, Spring automatically creates indexes based on `@Indexed` annotations in model classes.

2. **Existing MongoDB Indexes**: The database already had indexes created manually or by previous versions:
   - `occupation_1` (auto-generated name by MongoDB)
   - `email_1` (auto-generated name by MongoDB)

3. **Name Mismatch**: Spring Data MongoDB tried to create indexes with default names:
   - `occupation` (from `@Indexed` annotation)
   - `email` (from `@Indexed(unique = true)` annotation)

4. **Conflict**: MongoDB rejected the creation because indexes with the same keys but different names already existed.

## Solution

### 1. Removed Manual Index Creation
- Removed manual `ensureIndex()` calls from `MongoConfig.java`
- Spring Data MongoDB handles index creation automatically via `@Indexed` annotations

### 2. Specified Explicit Index Names
Updated `EmployeeModel.java` to use explicit index names matching existing MongoDB indexes:

```java
@Indexed(name = "occupation_1")
private String occupation;

@Indexed(unique = true, name = "email_1")
private String email;
```

This ensures Spring Data MongoDB uses the same index names as what's already in MongoDB, preventing conflicts.

## Files Changed

1. **`src/main/java/com/talentboozt/s_backend/config/MongoConfig.java`**
   - Removed manual index creation code
   - Removed unused imports (`Index`, `Sort`)
   - Added comments explaining that indexes are handled automatically

2. **`src/main/java/com/talentboozt/s_backend/domains/user/model/EmployeeModel.java`**
   - Added explicit `name` parameter to `@Indexed` annotations
   - Matches existing MongoDB index names: `occupation_1` and `email_1`

## How It Works Now

1. **On Application Startup**:
   - Spring Data MongoDB's `MongoPersistentEntityIndexCreator` scans for `@Indexed` annotations
   - It attempts to create/ensure indexes with the specified names
   - If an index with the same name already exists, MongoDB accepts it (no conflict)
   - If an index doesn't exist, it's created with the specified name

2. **Index Naming**:
   - By default, MongoDB auto-generates index names like `fieldName_1` or `fieldName_-1`
   - By specifying `name` in `@Indexed`, we control the exact index name
   - This prevents conflicts with existing indexes

## Alternative Solutions (Not Implemented)

### Option 1: Disable Auto-Index Creation
```properties
spring.data.mongodb.auto-index-creation=false
```
Then handle indexes manually or via migrations.

**Pros**: Full control over index creation
**Cons**: Need to manage indexes manually, risk of forgetting to create them

### Option 2: Drop and Recreate Indexes
Drop existing indexes and let Spring create new ones.

**Pros**: Clean slate
**Cons**: Requires downtime, potential data loss if indexes are critical

### Option 3: Use MongoDB Migrations
Use a tool like Mongock or manual scripts to manage indexes.

**Pros**: Version-controlled index changes
**Cons**: Additional complexity, requires migration tool setup

## Verification

After applying the fix, the application should:
1. ✅ Start successfully without index conflicts
2. ✅ Use existing MongoDB indexes (`occupation_1`, `email_1`)
3. ✅ Create missing indexes automatically if they don't exist
4. ✅ Not attempt to create duplicate indexes

## Testing

To verify the fix works:

1. **Check existing indexes in MongoDB**:
   ```javascript
   db.portal_employees.getIndexes()
   ```

2. **Start the application**:
   ```bash
   java -jar s-backend.jar
   ```

3. **Verify no index creation errors** in the logs

4. **Verify indexes still exist**:
   ```javascript
   db.portal_employees.getIndexes()
   // Should show occupation_1 and email_1 indexes
   ```

## Prevention

To prevent similar issues in the future:

1. **Always specify index names** when using `@Indexed`:
   ```java
   @Indexed(name = "fieldName_1")
   ```

2. **Document index names** in code comments or documentation

3. **Use consistent naming conventions**:
   - Simple indexes: `fieldName_1` or `fieldName_-1`
   - Compound indexes: `field1_1_field2_-1`
   - Unique indexes: `fieldName_unique`

4. **Consider using MongoDB migrations** for production environments

## Notes

- The fix maintains backward compatibility with existing MongoDB indexes
- No data migration is required
- The application will work with existing indexes without modification
- Future index changes should be coordinated with database migrations
