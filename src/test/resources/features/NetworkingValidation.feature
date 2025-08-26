Feature: Networking Validation Task

  Scenario Outline: Validation of public ip is not in specified range
    Given Public IP address is retrieved from  "https://ipinfo.io/ip"
    Then Public IP does not fall within the IP range "<startRange>" to "<endRange>"

    Examples:
      | startRange       | endRange     |
      | 101.33.28.0      | 101.33.29.0  |

  Scenario: Resolve domain and check port reachability
    When Domain "google-public-dns-a.google.com" is resolved
    Then the resolved IP should include "8.8.8.8"
    And port 53 on "8.8.8.8" should be reachable
    And port 80 on "8.8.8.8" should not be reachable
    And port 443 on "8.8.8.8" should not be reachable


  Scenario Outline: Validation of traceroute and hop counts
    When I perform traceroute to IP address "<ipAddress>"
    Then the target is reached within <hopCount> hops

    Examples:
      | ipAddress | hopCount |
      | 8.8.8.8   | 10     |
