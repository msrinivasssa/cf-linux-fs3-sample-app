package com.tanzu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

@RestController
class HelloController {
    @GetMapping("/")
    public String hello() {
        return getHtmlPage();
    }
    
    private String getHtmlPage() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Application Metrics - TAS Sample App</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            min-height: 100vh;\n" +
                "            padding: 20px;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 1200px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            color: white;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "        .header h1 {\n" +
                "            font-size: 2.5em;\n" +
                "            margin-bottom: 10px;\n" +
                "            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);\n" +
                "        }\n" +
                "        .header p {\n" +
                "            font-size: 1.2em;\n" +
                "            opacity: 0.9;\n" +
                "        }\n" +
                "        .metrics-grid {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));\n" +
                "            gap: 20px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .metric-card {\n" +
                "            background: white;\n" +
                "            border-radius: 12px;\n" +
                "            padding: 25px;\n" +
                "            box-shadow: 0 4px 6px rgba(0,0,0,0.1);\n" +
                "            transition: transform 0.2s, box-shadow 0.2s;\n" +
                "        }\n" +
                "        .metric-card:hover {\n" +
                "            transform: translateY(-5px);\n" +
                "            box-shadow: 0 8px 12px rgba(0,0,0,0.15);\n" +
                "        }\n" +
                "        .metric-card h2 {\n" +
                "            color: #667eea;\n" +
                "            font-size: 1.3em;\n" +
                "            margin-bottom: 15px;\n" +
                "            border-bottom: 2px solid #f0f0f0;\n" +
                "            padding-bottom: 10px;\n" +
                "        }\n" +
                "        .metric-item {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            padding: 8px 0;\n" +
                "            border-bottom: 1px solid #f0f0f0;\n" +
                "        }\n" +
                "        .metric-item:last-child {\n" +
                "            border-bottom: none;\n" +
                "        }\n" +
                "        .metric-label {\n" +
                "            font-weight: 500;\n" +
                "            color: #666;\n" +
                "        }\n" +
                "        .metric-value {\n" +
                "            font-weight: 600;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .progress-bar {\n" +
                "            width: 100%;\n" +
                "            height: 20px;\n" +
                "            background: #f0f0f0;\n" +
                "            border-radius: 10px;\n" +
                "            overflow: hidden;\n" +
                "            margin-top: 10px;\n" +
                "        }\n" +
                "        .progress-fill {\n" +
                "            height: 100%;\n" +
                "            background: linear-gradient(90deg, #667eea, #764ba2);\n" +
                "            transition: width 0.3s ease;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            color: white;\n" +
                "            font-size: 0.8em;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        .status-indicator {\n" +
                "            display: inline-block;\n" +
                "            width: 12px;\n" +
                "            height: 12px;\n" +
                "            border-radius: 50%;\n" +
                "            background: #4caf50;\n" +
                "            margin-right: 8px;\n" +
                "            animation: pulse 2s infinite;\n" +
                "        }\n" +
                "        @keyframes pulse {\n" +
                "            0%, 100% { opacity: 1; }\n" +
                "            50% { opacity: 0.5; }\n" +
                "        }\n" +
                "        .refresh-info {\n" +
                "            text-align: center;\n" +
                "            color: white;\n" +
                "            margin-top: 20px;\n" +
                "            opacity: 0.8;\n" +
                "        }\n" +
                "        .error-message {\n" +
                "            background: #ffebee;\n" +
                "            color: #c62828;\n" +
                "            padding: 15px;\n" +
                "            border-radius: 8px;\n" +
                "            margin: 20px 0;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1><span class=\"status-indicator\"></span>Application Metrics</h1>\n" +
                "            <p>Java on TAS (cflinuxfs3) - Real-time System Information</p>\n" +
                "        </div>\n" +
                "        <div id=\"error-message\" class=\"error-message\" style=\"display: none;\"></div>\n" +
                "        <div class=\"metrics-grid\" id=\"metrics-grid\">\n" +
                "            <div class=\"metric-card\">\n" +
                "                <h2>⏱️ Uptime</h2>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Running Time:</span>\n" +
                "                    <span class=\"metric-value\" id=\"uptime\">Loading...</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"metric-card\">\n" +
                "                <h2>💾 Memory Usage</h2>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Heap Used:</span>\n" +
                "                    <span class=\"metric-value\" id=\"heap-used\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Heap Max:</span>\n" +
                "                    <span class=\"metric-value\" id=\"heap-max\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Heap Committed:</span>\n" +
                "                    <span class=\"metric-value\" id=\"heap-committed\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Heap Free:</span>\n" +
                "                    <span class=\"metric-value\" id=\"heap-free\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"progress-bar\">\n" +
                "                    <div class=\"progress-fill\" id=\"heap-progress\" style=\"width: 0%\">0%</div>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\" style=\"margin-top: 10px;\">\n" +
                "                    <span class=\"metric-label\">Non-Heap Used:</span>\n" +
                "                    <span class=\"metric-value\" id=\"nonheap-used\">Loading...</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"metric-card\">\n" +
                "                <h2>⚡ CPU Usage</h2>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Process CPU:</span>\n" +
                "                    <span class=\"metric-value\" id=\"cpu-process\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"progress-bar\">\n" +
                "                    <div class=\"progress-fill\" id=\"cpu-progress\" style=\"width: 0%\">0%</div>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\" style=\"margin-top: 10px;\">\n" +
                "                    <span class=\"metric-label\">System CPU:</span>\n" +
                "                    <span class=\"metric-value\" id=\"cpu-system\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Available Processors:</span>\n" +
                "                    <span class=\"metric-value\" id=\"cpu-processors\">Loading...</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"metric-card\">\n" +
                "                <h2>🧵 Threads</h2>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Thread Count:</span>\n" +
                "                    <span class=\"metric-value\" id=\"thread-count\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Peak Thread Count:</span>\n" +
                "                    <span class=\"metric-value\" id=\"thread-peak\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Daemon Threads:</span>\n" +
                "                    <span class=\"metric-value\" id=\"thread-daemon\">Loading...</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"metric-card\">\n" +
                "                <h2>🖥️ System Information</h2>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Java Version:</span>\n" +
                "                    <span class=\"metric-value\" id=\"java-version\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">Java Vendor:</span>\n" +
                "                    <span class=\"metric-value\" id=\"java-vendor\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">OS Name:</span>\n" +
                "                    <span class=\"metric-value\" id=\"os-name\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">OS Version:</span>\n" +
                "                    <span class=\"metric-value\" id=\"os-version\">Loading...</span>\n" +
                "                </div>\n" +
                "                <div class=\"metric-item\">\n" +
                "                    <span class=\"metric-label\">OS Architecture:</span>\n" +
                "                    <span class=\"metric-value\" id=\"os-arch\">Loading...</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"refresh-info\">\n" +
                "            <p>Metrics refresh every 2 seconds</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        async function fetchMetrics() {\n" +
                "            try {\n" +
                "                const response = await fetch('/api/metrics');\n" +
                "                if (!response.ok) {\n" +
                "                    throw new Error('Failed to fetch metrics');\n" +
                "                }\n" +
                "                const data = await response.json();\n" +
                "                updateMetrics(data);\n" +
                "                document.getElementById('error-message').style.display = 'none';\n" +
                "            } catch (error) {\n" +
                "                console.error('Error fetching metrics:', error);\n" +
                "                const errorDiv = document.getElementById('error-message');\n" +
                "                errorDiv.textContent = 'Error loading metrics: ' + error.message;\n" +
                "                errorDiv.style.display = 'block';\n" +
                "            }\n" +
                "        }\n" +
                "        function updateMetrics(data) {\n" +
                "            // Uptime\n" +
                "            document.getElementById('uptime').textContent = data.uptime;\n" +
                "            // Memory\n" +
                "            document.getElementById('heap-used').textContent = data.memory.heapUsed;\n" +
                "            document.getElementById('heap-max').textContent = data.memory.heapMax;\n" +
                "            document.getElementById('heap-committed').textContent = data.memory.heapCommitted;\n" +
                "            document.getElementById('heap-free').textContent = data.memory.heapFree;\n" +
                "            document.getElementById('nonheap-used').textContent = data.memory.nonHeapUsed;\n" +
                "            const heapPercent = Math.round(data.memory.heapUsagePercent);\n" +
                "            const heapProgress = document.getElementById('heap-progress');\n" +
                "            heapProgress.style.width = heapPercent + '%';\n" +
                "            heapProgress.textContent = heapPercent + '%';\n" +
                "            // CPU\n" +
                "            document.getElementById('cpu-process').textContent = data.cpu.processCpuUsage;\n" +
                "            document.getElementById('cpu-system').textContent = data.cpu.systemCpuUsage;\n" +
                "            document.getElementById('cpu-processors').textContent = data.cpu.availableProcessors;\n" +
                "            const cpuPercent = Math.round(data.cpu.processCpuUsageValue);\n" +
                "            const cpuProgress = document.getElementById('cpu-progress');\n" +
                "            cpuProgress.style.width = Math.min(cpuPercent, 100) + '%';\n" +
                "            cpuProgress.textContent = cpuPercent + '%';\n" +
                "            // Threads\n" +
                "            document.getElementById('thread-count').textContent = data.threads.threadCount;\n" +
                "            document.getElementById('thread-peak').textContent = data.threads.peakThreadCount;\n" +
                "            document.getElementById('thread-daemon').textContent = data.threads.daemonThreadCount;\n" +
                "            // System\n" +
                "            document.getElementById('java-version').textContent = data.system.javaVersion;\n" +
                "            document.getElementById('java-vendor').textContent = data.system.javaVendor;\n" +
                "            document.getElementById('os-name').textContent = data.system.osName;\n" +
                "            document.getElementById('os-version').textContent = data.system.osVersion;\n" +
                "            document.getElementById('os-arch').textContent = data.system.osArch;\n" +
                "        }\n" +
                "        // Fetch metrics immediately and then every 2 seconds\n" +
                "        fetchMetrics();\n" +
                "        setInterval(fetchMetrics, 2000);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
