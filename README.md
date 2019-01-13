# JMedoo
Java版的Medoo查询表达式

[![Build Status](https://travis-ci.org/iliubang/JMedoo.svg?branch=master)](https://travis-ci.org/iliubang/JMedoo)
[![codecov](https://codecov.io/gh/iliubang/JMedoo/branch/master/graph/badge.svg)](https://codecov.io/gh/iliubang/JMedoo)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.iliubang/jmedoo/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.iliubang/jmedoo)

maven添加依赖

```xml
<dependency>
    <groupId>cn.iliubang</groupId>
    <artifactId>jmedoo</artifactId>
    <version>1.0.0</version>
</dependency>
```

使用MySQL的话请设置

```java
# 命令行
set SQL_MODE=ANSI_QUOTES
# jdbc
jdbc:mysql://host:port/db?sessionVariables=sql_mode=ANSI_QUOTES
```


