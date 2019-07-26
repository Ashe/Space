from openjdk:8
copy target/build/app.jar /app.jar
cmd ["/usr/local/openjdk-8/bin/java", "-cp", "app.jar", "clojure.main", "-m", "space.api.core"]
expose 3000
