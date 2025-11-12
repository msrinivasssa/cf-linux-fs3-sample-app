package com.tanzu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MetricsController {

    @GetMapping("/api/metrics")
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
        metrics.put("system", system);
        
        // Thread information
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threads = new HashMap<>();
        threads.put("threadCount", threadBean.getThreadCount());
        threads.put("peakThreadCount", threadBean.getPeakThreadCount());
        threads.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        metrics.put("threads", threads);
        
        return metrics;
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
}

