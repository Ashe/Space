from openjdk:8
copy target/build/space.jar /space.jar
cmd ["/usr/local/openjdk-8/bin/java", "-cp", "space.jar", "clojure.main", "-m", "space.api.core", "db", "5432"]
