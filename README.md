# JMedoo

Java 版的 Medoo 查询表达式

[![Build Status](https://travis-ci.org/liubang/JMedoo.svg?branch=master)](https://travis-ci.org/liubang/JMedoo)
[![codecov](https://codecov.io/gh/liubang/JMedoo/branch/master/graph/badge.svg)](https://codecov.io/gh/liubang/JMedoo)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.iliubang/jmedoo/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.iliubang/jmedoo)

maven 添加依赖

```xml
<dependency>
    <groupId>cn.iliubang</groupId>
    <artifactId>jmedoo</artifactId>
    <version>1.0.0</version>
</dependency>
```

使用 MySQL 的话请设置

```java
# 命令行
set SQL_MODE=ANSI_QUOTES
# jdbc
jdbc:mysql://host:port/db?sessionVariables=sql_mode=ANSI_QUOTES
```
