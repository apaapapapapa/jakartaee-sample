ホットリロードでビルド&起動
mvn -P wildfly-bootable-jar -Dapp.db.dir="$(pwd)/db" wildfly-jar:dev-watch
mvn -P wildfly-bootable-jar -Dapp.db.dir="$PWD\db" wildfly-jar:dev-watch


普通にビルド&起動
mvn clean install -P wildfly-bootable-jar -Dapp.db.dir="$(pwd)/db"
java -jar sample-bootable.jar

java -jar ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar
