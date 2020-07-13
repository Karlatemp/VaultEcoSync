# VaultEcoSync

This is a plugin that can synchronize economy.

Using MySQL Database

## Build.

Clone this repository into anywhere.

Download JDK 13.

Then run
```shell script
# Windows

SET "PATH=(PATH_TO_JDK13);%PATH%"

# UNIX

export PATH=(PATH_TO_JDK13):$PATH

```

Then run

```shell script
./gradlew :spigot:java8converter
```

You can find the final build in `spigot/build/libs`

## Configuration

```yaml
# Your server name,
# The name of each server should be different
server: Server0

# JDBC Configuration
connect:
  jdbc: "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=Asia/Shanghai"
  user: tester
  passwd: tester

```
