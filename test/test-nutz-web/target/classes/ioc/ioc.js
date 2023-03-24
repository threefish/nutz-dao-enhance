var ioc = {
    dataSource: {
        type: "com.zaxxer.hikari.HikariDataSource",
        fields: {
            jdbcUrl: "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8",
            driverClassName: "com.mysql.jdbc.Driver",
            username: "root",
            password: "123456",
            connectionTestQuery: "SELECT 1",
            maxPoolSize: "5"
        }
    },
    conf: {
        type: "org.nutz.ioc.impl.PropertiesProxy",
        fields: {
            paths: ["conf/"],
        }
    }
};
