Cấu hình Backend (Spring Boot):


Mặc định ứng dụng Spring Boot chạy trên cổng 8080 .


Cung cấp file cấu hình (application.properties):
Sử dụng SQL server
Database tên CompanyTest

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.globally_quoted_identifiers=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.properties.hibernate.use_nationalized_character_data=true
 

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CompanyTest;encrypt=false;trustServerCertificate=true;useUnicode=true;characterEncoding=UTF-8
spring.datasource.username=sa
spring.datasource.password=123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

sử dụng lệnh mvn spring-boot:run để chạy chương trình



