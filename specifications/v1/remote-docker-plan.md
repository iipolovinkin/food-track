# Remote Docker Server Configuration for Testcontainers

This document provides a complete guide on how to configure Testcontainers to run on a remote Docker server for the FoodTrack analytics event tracking system.

## Overview

This plan details the steps required to run Testcontainers-based integration tests against a remote Docker server instead of the local Docker daemon. This is useful for CI/CD environments, shared test infrastructure, or when local Docker resources are limited.

## Prerequisites

- Access to a remote Docker server with TCP connections enabled
- Testcontainers dependencies already included in the project (already present in pom.xml)
- Existing integration tests with Testcontainers annotations (already implemented in EventControllerIntegration2Test.java)

## Configuration Steps

### 1. Configure the Remote Docker Daemon

On the remote Docker server:

1. Edit the Docker daemon configuration file (usually `/etc/docker/daemon.json`):
   ```json
   {
     "hosts": ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2375"]
   }
   ```
   
   - Or modify the Docker service to listen on TCP:
   ```bash
   sudo systemctl edit docker
   ```
   Add the following content:
   ```
   [Service]
   ExecStart=
   ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock
   ```

2. Open firewall ports:
   ```bash
   sudo ufw allow 2375/tcp
   # or with iptables
   sudo iptables -A INPUT -p tcp --dport 2375 -j ACCEPT
   ```

3. Restart Docker daemon:
   ```bash
   sudo systemctl restart docker
   ```

> **Security Note**: For production environments, use TLS encryption instead of plain TCP connections. This example is for development purposes.

### 2. Configure Testcontainers to Use Remote Docker Daemon

Testcontainers automatically detects the Docker host through environment variables or system properties. You can configure it in several ways:

#### Method 1: System Properties
Pass the Docker host as a system property when running tests:
```bash
mvn test -Ddocker.host=tcp://your-remote-docker-server:2375
```

#### Method 2: Testcontainers Configuration File
Create a file called `.testcontainers.properties` in your user home directory or in your classpath:
```
docker.host=tcp://your-remote-docker-server:2375
```

#### Method 3: Environment Variables
Set environment variables before running tests:
```bash
export DOCKER_HOST=tcp://your-remote-docker-server:2375
export DOCKER_CLIENT_TIMEOUT=120
```

### 3. Environment Variables for Remote Docker Connection

- `DOCKER_HOST`: Specifies the location of the Docker daemon
  ```
  DOCKER_HOST=tcp://your-remote-docker-server:2375
  ```

- `DOCKER_CERT_PATH`: Path to Docker certificate files (for TLS if enabled)
  ```
  DOCKER_CERT_PATH=/path/to/docker/certs
  ```

- `DOCKER_TLS_VERIFY`: Enable/disable TLS verification
  ```
  DOCKER_TLS_VERIFY=1  # Enable TLS verification
  ```

- `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE`: Override the Docker socket path if needed
  ```
  TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
  ```

- `DOCKER_CLIENT_TIMEOUT`: Set Docker client timeout (in seconds)
  ```
  DOCKER_CLIENT_TIMEOUT=120
  ```

- `TESTCONTAINERS_RYUK_DISABLED`: Disable Ryuk container cleanup if needed
  ```
  TESTCONTAINERS_RYUK_DISABLED=true
  ```

### 4. Testing the Remote Docker Configuration

#### Run Specific Test
```bash
export DOCKER_HOST=tcp://your-remote-docker-server:2375
mvn clean test -Dtest=EventControllerIntegration2Test
```

#### Run All Container Tests
Using Maven Failsafe plugin (as configured in pom.xml):
```bash
mvn clean verify -Dgroups=container
```

#### Alternative Maven Command
```bash
mvn clean test -Dtest=EventControllerIntegration2Test -Ddocker.host=tcp://your-remote-docker-server:2375
```

### 5. Verification Steps

1. Monitor the test execution for Testcontainers logs showing connection to the remote Docker daemon
2. Look for log messages such as:
   ```
   Connected to docker:
     Server Version: 20.x.x
     API Version: 1.xx
     Operating System: Ubuntu 20.04 (containerd)
     Total Memory: 7986 MB
   ```
3. Verify that tests complete successfully with PostgreSQL container running on the remote server

### 6. Troubleshooting

- If you encounter connection timeouts, increase the client timeout:
  ```bash
  mvn clean test -Dtest=EventControllerIntegration2Test -Ddocker.client.timeout=180
  ```

- Verify Docker connectivity to the remote server:
  ```bash
  docker -H tcp://your-remote-docker-server:2375 ps
  ```

- For network issues, ensure the remote Docker server is accessible and the firewall rules are properly configured

## Integration with Existing Tests

The existing `EventControllerIntegration2Test.java` file is already properly configured for Testcontainers usage with:
- `@Testcontainers` annotation
- `@Container` PostgreSQL container definition
- `@DynamicPropertySource` for dynamic property configuration
- Proper Docker image specification (`postgres:15`)

No code changes are required to the test file to enable remote Docker usage - only the environment configuration needs to be modified.

## Security Considerations

1. Use TLS encryption for production environments
2. Implement proper firewall rules to restrict access to port 2375
3. Use VPN or private networks for sensitive environments
4. Regularly rotate certificates and credentials
5. Limit access to the Docker daemon to authorized users only

## CI/CD Integration

For CI/CD environments, add the environment variables to your pipeline configuration:

```yaml
env:
  DOCKER_HOST: tcp://your-remote-docker-server:2375
  DOCKER_CLIENT_TIMEOUT: 120
```

This allows your integration tests to run consistently across different environments while utilizing centralized Docker resources.