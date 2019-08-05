from openjdk:8
copy target/build/space.jar /space.jar
copy target/public/js/space.js /resources/public/js/space.js
copy resources/public/ /resources/public/
cmd ["/usr/local/openjdk-8/bin/java", "-cp", "space.jar", "clojure.main", "-m", "space.site.clj.core"]
