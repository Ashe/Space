from openjdk:8
copy target/build/app.jar /app.jar
cmd ["/usr/local/openjdk-8/bin/java", "-cp", "app.jar", "clojure.main", "-m", "server.space.core"]
expose 8080
