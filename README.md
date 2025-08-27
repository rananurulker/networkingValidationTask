# Networking Validation Task

This repository contains an automated test suite for validating network connectivity and properties. The tests are written using the Cucumber framework, with step definitions implemented in Java, and the project is managed with Maven.

## Project Overview

The primary goal of this project is to automate the verification of various networking tasks, including:

* Validating public IP address ranges.
* Checking the reachability of domains and specific ports.
* Performing traceroute and validating hop counts.

The tests are designed to be clear and readable, using the Gherkin syntax (`.feature` files) to describe the behavior.

## Technologies Used

* **Java**: The core programming language for the test logic.
* **Maven**: Used for dependency management and building the project.
* **Cucumber**: The behavior-driven development (BDD) framework used to write and execute the tests.
* **JUnit**: The test runner used to execute the Cucumber tests.

## Prerequisites

Before you can run the tests, ensure you have the following installed:

* Java Development Kit (JDK) 8 or higher
* Maven
* Docker (if you want to run the tests inside a container)

## Getting Started

Follow these steps to set up the project locally and run the tests.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/rananurulker/networkingValidationTask.git
    cd networkingValidationTask
    ```

2.  **Build the project:**
    Navigate to the project's root directory and build it using Maven. This command will download all necessary dependencies.
    ```bash
    mvn clean install
    ```

## Running the Tests

To execute the tests, use the following Maven command:

```bash
mvn test
```

## Run with Docker  
If you prefer to run the tests inside a container, use Docker:  

1. Build the Docker image:  
   `docker build -t networking-validation .`  

2. Run the container:  
   `docker run --rm networking-validation`  
