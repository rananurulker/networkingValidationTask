package stepDefinitions;

import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NetworkingSteps {

    private Scenario scenario;
    private String publicIP;
    private String resolvedIP;
    private int hopsCount;
    private String traceOutput;

    @io.cucumber.java.Before
    public void setUp(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("Public IP address is retrieved from  {string}")
    public void publicIPAddressIsRetrievedFrom(String url) {
        publicIP = RestAssured
                .given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .asString()
                .trim();

        scenario.log("Public IP: " + publicIP);
    }

    @Then("Public IP does not fall within the IP range {string} to {string}")
    public void publicIPDoesNotFallWithinTheIPRangeTo(String startRange, String endRange) {
        long ipNum = ipToLong(publicIP);
        long start = ipToLong(startRange);
        long end = ipToLong(endRange);

        scenario.log("Numeric IP: " + publicIP + " | Allowed Range: " + startRange + "-" + endRange);

        Assert.assertFalse("Public IP falls within the specified range",
                ipNum >= start && ipNum <= end);

    }

    @When("Domain {string} is resolved to IP {string}")
    public void domainIsResolvedToIP(String domain, String ipAddress) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(domain);
        resolvedIP = inetAddress.getHostAddress();
        scenario.log("Resolved " + domain + " to " + resolvedIP);
        scenario.log("Expected IP: " + ipAddress + " | Actual IP: " + resolvedIP);
        Assert.assertEquals("Domain did not resolve correctly", ipAddress, resolvedIP);

    }

    @Then("port {int} on {string} should be reachable")
    public void portShouldBeReachable(int port, String ip) throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 2000);
            scenario.log("Port is reachable " + ip + ":" + port);
            Assert.assertTrue(socket.isConnected());
        }
    }

    @Then("port {int} on {string} should not be reachable")
    public void portShouldNotBeReachable(int port, String ip) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 2000);
            Assert.fail("Port " + port + " should not be reachable");
        } catch (Exception e) {
            scenario.log("Port is not reachable " + ip + ":" + port);
        }
    }

    @When("I perform traceroute to IP address {string}")
    public void iPerformTracerouteToIPAddress(String ipAddress) throws Exception {

        List<String> command = new ArrayList<>();
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        command.add(isWindows ? "tracert" : "traceroute");
        command.add(ipAddress);

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        StringBuilder sb = new StringBuilder();
        int hops = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scenario.log("LINE " + line);
                sb.append(line).append(System.lineSeparator());

                // Match only real hop lines: start with a number followed by whitespace
                if (line.matches("^\\s*\\d+\\s+.*")) {
                    hops++;
                    // Check if hop contains the final target (only after hops started)
                    if (line.contains(ipAddress)) {
                        scenario.log("Reached target: " + ipAddress);
                        break;
                    }
                }
            }
        }

        hopsCount = hops;
        traceOutput = sb.toString();

        scenario.attach(traceOutput.getBytes(StandardCharsets.UTF_8), "text/plain", "traceroute.log");
        scenario.log("Traceroute hop count: " + hopsCount);

    }

    @Then("the target is reached within {int} hops")
    public void theTargetIsReachedWithinHops(int hopCount) {
        Assert.assertTrue("Target not reached within " + hopCount + " hops!", hopsCount <= hopCount);
    }

    private long ipToLong(String ipAddress) {
        String[] ipParts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | (Integer.parseInt(ipParts[i]) & 0xFF);
        }
        return result;
    }

}
