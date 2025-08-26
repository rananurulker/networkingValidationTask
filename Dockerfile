
FROM openjdk:17-jdk-slim

# Install necessary system packages for networking tools
RUN apt-get update && apt-get install -y \
    maven \
    traceroute \
    iputils-ping \
    dnsutils \
    curl \
    wget \
    net-tools \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven configuration files first (for better layer caching)
COPY pom.xml ./
COPY src/ ./src/

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:resolve

# Copy the rest of the application
COPY . .

# Default command to run the tests
CMD ["mvn", "test"]