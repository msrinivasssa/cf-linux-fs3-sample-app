package com.tanzu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@RestController
@RequestMapping("/api")
public class MetricsController {

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Uptime
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeBean.getUptime();
        Duration uptimeDuration = Duration.ofMillis(uptime);
        metrics.put("uptime", formatDuration(uptimeDuration));
        metrics.put("uptimeMillis", uptime);
        
        // Memory information
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        long heapCommitted = memoryBean.getHeapMemoryUsage().getCommitted();
        long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
        long nonHeapCommitted = memoryBean.getNonHeapMemoryUsage().getCommitted();
        
        Map<String, Object> memory = new HashMap<>();
        memory.put("heapUsed", formatBytes(heapUsed));
        memory.put("heapUsedBytes", heapUsed);
        memory.put("heapMax", formatBytes(heapMax));
        memory.put("heapMaxBytes", heapMax);
        memory.put("heapCommitted", formatBytes(heapCommitted));
        memory.put("heapCommittedBytes", heapCommitted);
        memory.put("heapFree", formatBytes(heapMax - heapUsed));
        memory.put("heapFreeBytes", heapMax - heapUsed);
        memory.put("heapUsagePercent", heapMax > 0 ? (heapUsed * 100.0 / heapMax) : 0);
        memory.put("nonHeapUsed", formatBytes(nonHeapUsed));
        memory.put("nonHeapUsedBytes", nonHeapUsed);
        memory.put("nonHeapCommitted", formatBytes(nonHeapCommitted));
        memory.put("nonHeapCommittedBytes", nonHeapCommitted);
        metrics.put("memory", memory);
        
        // CPU information
        com.sun.management.OperatingSystemMXBean osBean = 
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = osBean.getProcessCpuLoad() * 100;
        double systemCpuUsage = osBean.getSystemCpuLoad() * 100;
        long processCpuTime = osBean.getProcessCpuTime();
        int availableProcessors = osBean.getAvailableProcessors();
        
        Map<String, Object> cpu = new HashMap<>();
        cpu.put("processCpuUsage", String.format("%.2f%%", cpuUsage));
        cpu.put("processCpuUsageValue", cpuUsage);
        cpu.put("systemCpuUsage", String.format("%.2f%%", systemCpuUsage));
        cpu.put("systemCpuUsageValue", systemCpuUsage);
        cpu.put("processCpuTime", formatDuration(Duration.ofNanos(processCpuTime)));
        cpu.put("availableProcessors", availableProcessors);
        metrics.put("cpu", cpu);
        
        // System information
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("javaVendor", System.getProperty("java.vendor"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("osArch", System.getProperty("os.arch"));
        system.put("totalMemory", formatBytes(Runtime.getRuntime().totalMemory()));
        system.put("freeMemory", formatBytes(Runtime.getRuntime().freeMemory()));
        system.put("maxMemory", formatBytes(Runtime.getRuntime().maxMemory()));
        system.put("stack", detectStack());
        metrics.put("system", system);
        
        // Thread information
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threads = new HashMap<>();
        threads.put("threadCount", threadBean.getThreadCount());
        threads.put("peakThreadCount", threadBean.getPeakThreadCount());
        threads.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        metrics.put("threads", threads);
        
        // Application revision/version information
        Map<String, Object> appInfo = getApplicationInfo();
        metrics.put("application", appInfo);
        
        return metrics;
    }
    
    private Map<String, Object> getApplicationInfo() {
        Map<String, Object> appInfo = new HashMap<>();
        
        // Get app name
        String appName = System.getenv("CF_APP_NAME");
        if (appName == null || appName.isEmpty()) {
            appName = System.getenv("VCAP_APPLICATION");
            if (appName != null && appName.contains("\"application_name\"")) {
                try {
                    int nameIndex = appName.indexOf("\"application_name\"");
                    int colonIndex = appName.indexOf(":", nameIndex);
                    int startQuote = appName.indexOf("\"", colonIndex) + 1;
                    int endQuote = appName.indexOf("\"", startQuote);
                    if (startQuote > 0 && endQuote > startQuote) {
                        appName = appName.substring(startQuote, endQuote);
                    }
                } catch (Exception e) {
                    appName = "fs3-sample-app";
                }
            } else {
                appName = "fs3-sample-app";
            }
        }
        appInfo.put("name", appName);
        
        // Get instance info
        String instanceIndex = System.getenv("CF_INSTANCE_INDEX");
        if (instanceIndex == null || instanceIndex.isEmpty()) {
            instanceIndex = "0";
        }
        appInfo.put("instance", instanceIndex);
        
        String instanceGuid = System.getenv("CF_INSTANCE_GUID");
        if (instanceGuid != null && !instanceGuid.isEmpty()) {
            appInfo.put("instanceGuid", instanceGuid);
        }
        
        // Get version from VCAP_APPLICATION
        String vcapApp = System.getenv("VCAP_APPLICATION");
        if (vcapApp != null && !vcapApp.isEmpty()) {
            // Try to extract version
            try {
                String[] versionPatterns = {
                    "\"version\"\\s*:\\s*\"([^\"]+)\"",
                    "\"application_version\"\\s*:\\s*\"([^\"]+)\""
                };
                
                for (String pattern : versionPatterns) {
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                    java.util.regex.Matcher m = p.matcher(vcapApp);
                    if (m.find()) {
                        String version = m.group(1);
                        if (version != null && !version.isEmpty()) {
                            appInfo.put("version", version);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                // Continue
            }
        }
        
        // Get build timestamp from JAR manifest
        try {
            String buildTime = getBuildTimestamp();
            if (buildTime != null) {
                appInfo.put("buildTime", buildTime);
            }
        } catch (Exception e) {
            // Continue
        }
        
        // Get deployment time (start time of JVM) - this changes on each push/restage
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        Instant startInstant = Instant.ofEpochMilli(startTime);
        appInfo.put("deployedAt", DateTimeFormatter.ISO_INSTANT.format(startInstant));
        
        // Create a revision identifier based on deployment timestamp
        // This will change on each cf push or cf restage
        // Format: timestamp in hex (shorter) for better readability
        String revision = Long.toHexString(startTime).toUpperCase();
        appInfo.put("revision", revision);
        appInfo.put("revisionTimestamp", startTime);
        
        // Also provide a human-readable deployment time
        String deployedAtReadable = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
            startInstant.atZone(java.time.ZoneId.systemDefault()));
        appInfo.put("deployedAtReadable", deployedAtReadable);
        
        return appInfo;
    }
    
    private String getBuildTimestamp() {
        try {
            Class<?> clazz = this.getClass();
            String className = clazz.getSimpleName() + ".class";
            String classPath = clazz.getResource(className).toString();
            
            if (classPath.startsWith("jar")) {
                String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
                URL url = new URL(manifestPath);
                URLConnection connection = url.openConnection();
                Manifest manifest = new Manifest(connection.getInputStream());
                Attributes attributes = manifest.getMainAttributes();
                
                // Check for build time or implementation version
                String buildTime = attributes.getValue("Build-Time");
                if (buildTime == null) {
                    buildTime = attributes.getValue("Implementation-Version");
                }
                if (buildTime == null) {
                    buildTime = attributes.getValue("Built-By");
                }
                return buildTime;
            }
        } catch (Exception e) {
            // Return null if we can't read manifest
        }
        return null;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
        
        // Application info
        Map<String, Object> application = new HashMap<>();
        application.put("name", System.getenv().getOrDefault("CF_APP_NAME", "fs3-sample-app"));
        application.put("instance", System.getenv().getOrDefault("CF_INSTANCE_INDEX", "0"));
        application.put("stack", detectStack());
        health.put("application", application);
        
        return health;
    }
    
    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    private String detectStack() {
        // Priority 1: Check for CF stack environment variable (if available)
        String stack = System.getenv("CF_STACK");
        if (stack != null && !stack.isEmpty()) {
            return stack;
        }
        
        // Priority 2: Check /etc/os-release file for Ubuntu version (most reliable)
        // cflinuxfs4 uses Ubuntu 22.04 (jammy), cflinuxfs3 uses Ubuntu 18.04 (bionic)
        try {
            String osRelease = readOsRelease();
            if (osRelease != null) {
                if (osRelease.contains("VERSION_ID=\"22.04\"") || 
                    osRelease.contains("VERSION_ID=22.04") ||
                    osRelease.contains("jammy")) {
                    return "cflinuxfs4";
                } else if (osRelease.contains("VERSION_ID=\"18.04\"") || 
                          osRelease.contains("VERSION_ID=18.04") ||
                          osRelease.contains("bionic")) {
                    return "cflinuxfs3";
                }
            }
        } catch (Exception e) {
            // Continue to next detection method
        }
        
        // Priority 3: Check VCAP_APPLICATION for stack info (parse JSON more robustly)
        String vcapApp = System.getenv("VCAP_APPLICATION");
        if (vcapApp != null && !vcapApp.isEmpty()) {
            // Try multiple patterns to extract stack from JSON
            try {
                // Pattern 1: "stack":"cflinuxfs4"
                String[] patterns = {
                    "\"stack\"\\s*:\\s*\"([^\"]+)\"",
                    "'stack'\\s*:\\s*'([^']+)'",
                    "stack\\s*:\\s*\"([^\"]+)\""
                };
                
                for (String pattern : patterns) {
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                    java.util.regex.Matcher m = p.matcher(vcapApp);
                    if (m.find()) {
                        stack = m.group(1);
                        if (stack != null && !stack.isEmpty()) {
                            return stack;
                        }
                    }
                }
                
                // Fallback: simple string search with better parsing
                int stackIndex = vcapApp.indexOf("\"stack\"");
                if (stackIndex < 0) {
                    stackIndex = vcapApp.indexOf("'stack'");
                }
                if (stackIndex >= 0) {
                    int colonIndex = vcapApp.indexOf(":", stackIndex);
                    if (colonIndex > 0) {
                        // Find the value after colon
                        int startQuote = vcapApp.indexOf("\"", colonIndex);
                        if (startQuote < 0) {
                            startQuote = vcapApp.indexOf("'", colonIndex);
                        }
                        if (startQuote > 0) {
                            startQuote += 1; // Skip the quote
                            int endQuote = vcapApp.indexOf("\"", startQuote);
                            if (endQuote < 0) {
                                endQuote = vcapApp.indexOf("'", startQuote);
                            }
                            if (endQuote > startQuote) {
                                stack = vcapApp.substring(startQuote, endQuote);
                                if (stack != null && !stack.isEmpty()) {
                                    return stack;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Fall through to other detection methods
            }
        }
        
        // Priority 4: Infer from OS version property
        String osVersion = System.getProperty("os.version", "");
        if (osVersion.contains("22.04") || osVersion.contains("jammy")) {
            return "cflinuxfs4";
        } else if (osVersion.contains("18.04") || osVersion.contains("bionic")) {
            return "cflinuxfs3";
        }
        
        // Default fallback
        return "cflinuxfs3";
    }
    
    private String readOsRelease() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/etc/os-release"))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            return null;
        }
    }
}


