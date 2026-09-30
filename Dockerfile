FROM eclipse-temurin:21-jre

WORKDIR /app

# Install Python + pip
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    rm -rf /var/lib/apt/lists/*

# Install Python libraries needed by prediction script
RUN pip3 install --break-system-packages \
    scikit-learn \
    pandas \
    joblib

# Spring Boot application
COPY target/*.jar app.jar

# Python prediction code/model
COPY python/predict.py /app/python/predict.py
COPY python/fraud_model_v2.pkl /app/python/fraud_model_v2.pkl

ENTRYPOINT ["java", "-jar", "app.jar"]