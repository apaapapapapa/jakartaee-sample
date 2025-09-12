ホットリロードでビルド&起動
mvn wildfly-jar:dev-watch -P wildfly-bootable-jar

普通にビルド&起動
mvn clean install -P wildfly-bootable-jar
java -jar sample-bootable.jar

