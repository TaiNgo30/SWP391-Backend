FROM taingo30/java-python AS base

RUN ln -s /usr/bin/python3 /usr/bin/python

RUN apk add --no-cache build-base \
    fontconfig \
    ttf-dejavu \
    freetype

COPY src/main/python-scripts/requirements.txt /app/python-scripts/requirements.txt
RUN pip install --no-cache-dir --prefer-binary -r /app/python-scripts/requirements.txt

FROM base AS build
WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM base
WORKDIR /app

COPY --from=build /app/target/WareHouseManagementApplication-0.0.1-SNAPSHOT.jar /app/app.jar

COPY src/main/python-scripts/ /app/python-scripts/

ENTRYPOINT ["java","-jar","/app/app.jar"]

EXPOSE 6060
