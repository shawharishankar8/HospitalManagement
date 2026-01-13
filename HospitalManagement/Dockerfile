FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the jar file
COPY ./target/HospitalManagement-1.0.0.jar app.jar

# Create the directory structure (will be overridden by volume mount)
RUN mkdir -p hospital-data/dicom

# Copy application.properties from config directory
COPY ./config/application-docker.properties /app/config/

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application-docker.properties"]