package com.talentboozt.s_backend.domains.sys_tracking.controller.monitor;

import com.talentboozt.s_backend.domains._private.dto.PagedResponse;
import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.*;
import com.talentboozt.s_backend.domains.auth.dto.PermissionRequest;
import com.talentboozt.s_backend.domains.auth.model.PermissionModel;
import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import com.talentboozt.s_backend.domains.sys_tracking.service.monitor.MonitoringService;
import com.talentboozt.s_backend.domains.auth.service.PermissionServiceImpl;
import com.talentboozt.s_backend.domains.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.bson.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final RoleService roleService;
    private final PermissionServiceImpl permissionService;

    @GetMapping("/overview")
    public DashboardOverviewDTO getOverview(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        Instant fromInstant = from.toInstant();
        Instant toInstant = to.toInstant();
        return monitoringService.getOverview(trackingId, fromInstant, toInstant);
    }

    @GetMapping("/debug/raw-events")
    public ResponseEntity<List<Document>> getRawEvents() {
        return ResponseEntity.ok(monitoringService.getRawEvents(5));
    }

    @GetMapping("/debug/stats")
    public ResponseEntity<String> getStats() {
        return ResponseEntity.ok(monitoringService.getDebugStats());
    }

    @GetMapping("/page-views")
    public List<TimeSeriesPoint> getPageViews(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "day") String granularity) {
        return monitoringService.getPageViews(trackingId, from, to, granularity);
    }

    @GetMapping("/page-clicks")
    public List<TimeSeriesPoint> getClicks(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "day") String granularity) {
        return monitoringService.getPageClicks(trackingId, from, to, granularity);
    }

    @GetMapping("/page-performance")
    public List<TimeSeriesPoint> getPagePerformance(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "day") String granularity) {
        return monitoringService.getPagePerformance(trackingId, from, to, granularity);
    }

    @GetMapping("/event-types")
    public List<EventTypeCount> getEventTypes(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getEventCounts(trackingId, from, to);
    }

    @GetMapping("/performance")
    public PerformanceMetricsDTO getPerformance(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getPerformance(trackingId, from, to);
    }

    @GetMapping("/sessions")
    public PagedResponse<SessionViewDTO> getSessionViews(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return monitoringService.getSessionViews(trackingId, from, to, page, size);
    }

    @GetMapping("/geo")
    public List<LoginLocationAggregateDTO> getGeoData(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getGeoLocationCounts(trackingId, from, to);
    }

    @GetMapping("/devices/deprecated")
    public Map<String, Long> getDeviceInfoDeprecated(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId) {
        return monitoringService.getDeviceInfo(trackingId);
    }

    @GetMapping("/browsers/deprecated")
    public Map<String, Long> getBrowserStatsDeprecated(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId) {
        return monitoringService.getBrowserStats(trackingId);
    }

    @GetMapping("/browsers")
    public List<DeviceBrowserStatDTO> getBrowserStats(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.aggregateBrowserStats(trackingId, from, to);
    }

    @GetMapping("/devices")
    public List<DeviceBrowserStatDTO> getDeviceStats(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.aggregateDeviceStats(trackingId, from, to);
    }

    @GetMapping("/screen-sizes")
    public List<ScreenResolutionCount> getScreenSizeStats(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.aggregateScreenResolutions(trackingId, from, to);
    }

    @GetMapping("/session-details")
    public SessionViewDetail getSessionDetails(
            @RequestParam(defaultValue = "TALENTBOOZT_JP_COMBINED") String trackingId,
            @RequestParam String sessionId) throws ChangeSetPersister.NotFoundException {
        return monitoringService.getSessionDetails(trackingId, sessionId);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getBasicStats() {
        return ResponseEntity.ok(monitoringService.getBasicStats());
    }

    @GetMapping("/active-users-by-role")
    public ResponseEntity<List<RoleUserCountDTO>> getActiveUsersByRole() {
        return ResponseEntity.ok(monitoringService.getActiveUsersByRole());
    }

    @GetMapping("/permissions-usage")
    public ResponseEntity<List<Document>> getPermissionUsage() {
        return ResponseEntity.ok(monitoringService.getPermissionUsage());
    }

    @GetMapping("/suspicious-activities")
    public ResponseEntity<List<SuspiciousActivityDTO>> getSuspiciousActivities() {
        return ResponseEntity.ok(monitoringService.getSuspiciousActivities());
    }

    @GetMapping("/abnormal-session-durations/{minSeconds}/{maxSeconds}")
    public ResponseEntity<List<Document>> getAbnormalSessionDurations(@PathVariable long minSeconds,
            @PathVariable long maxSeconds) {
        return ResponseEntity.ok(monitoringService.detectAbnormalSessionDurations(minSeconds, maxSeconds));
    }

    @GetMapping("/high-frequency-endpoint-access/{thresholdPerMinute}")
    public ResponseEntity<List<Document>> getHighFrequencyAccess(@PathVariable int thresholdPerMinute) {
        return ResponseEntity.ok(monitoringService.detectHighFrequencyAccess(thresholdPerMinute));
    }

    @GetMapping("/multiple-ips-per-user/{timeWindowMinutes}")
    public ResponseEntity<List<Document>> getUsersWithMultipleIps(@PathVariable long timeWindowMinutes) {
        return ResponseEntity.ok(monitoringService.detectMultipleIpsPerUser(timeWindowMinutes));
    }

    @GetMapping("/geo-anomalies/{threshold}")
    public ResponseEntity<List<Document>> getGeolocationAnomalies(@PathVariable long threshold) {
        return ResponseEntity.ok(monitoringService.detectGeolocationAnomalies(threshold));
    }

    @GetMapping("/js-errors/{threshold}")
    public ResponseEntity<List<Document>> getExcessiveJsErrors(@PathVariable int threshold) {
        return ResponseEntity.ok(monitoringService.detectClientErrorsPerUser(threshold));
    }

    @PostMapping("/anonymous-protected-access")
    public ResponseEntity<List<Document>> getAnonymousAccesses(@RequestBody List<String> protectedEndpoints) {
        return ResponseEntity.ok(monitoringService.detectAnonymousAccessingProtectedEndpoints(protectedEndpoints));
    }

    @GetMapping("/role/get")
    public List<RoleModel> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/role/name/{name}")
    public RoleModel getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name).orElse(null);
    }

    @PostMapping("/role/add")
    public RoleModel createRole(@RequestBody RoleModel role) {
        return roleService.addRole(role);
    }

    @PutMapping("/role/update/{id}")
    public RoleModel updateRole(@PathVariable String id, @RequestBody RoleModel role) {
        return roleService.updateRole(id, role);
    }

    @DeleteMapping("/role/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/role/{rodeId}/permissions")
    public List<String> getPermissionsForRole(@PathVariable String rodeId) {
        return roleService.getPermissionsByRole(rodeId);
    }

    @PutMapping("/role/{rodeId}/update/permissions")
    public void updateRolePermissions(@PathVariable String rodeId, @RequestBody List<String> permissions) {
        roleService.updateRolePermissions(rodeId, permissions);
    }

    @GetMapping("/permissions/get")
    public List<PermissionModel> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @PostMapping("/permissions/add")
    public ResponseEntity<PermissionModel> create(@RequestBody PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(request));
    }

    @PutMapping("/permissions/update/{id}")
    public PermissionModel updatePermission(@PathVariable String id, @RequestBody PermissionRequest request) {
        return permissionService.updatePermission(id, request);
    }

    @DeleteMapping("/permissions/delete/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/backfill-roles")
    public ResponseEntity<String> backfillRoles() {
        try {
            monitoringService.backfillMissingRoles();
            return ResponseEntity.ok("Backfill completed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
