package cn.tz.cj.rule;

public enum EDBType {

    MYSQL("mysql", "com.mysql.jdbc.Driver"), SQLSERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver"), DB2("db2", "com.ibm.db2.jcc.DB2Driver");

    private String type;

    private String driverClass;

    private EDBType(String type, String driverClass) {
        this.type = type;
        this.driverClass = driverClass;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public static EDBType toEDBType(String type) {
        switch (type) {
            case "db2":
                return DB2;
            case "sqlserver":
                return SQLSERVER;
            case "oracle":
                return ORACLE;
            default:
                //mysql
                return MYSQL;
        }
    }

    public static String toString(EDBType type) {
        switch (type) {
            case DB2:
                return "db2";
            case SQLSERVER:
                return "sqlserver";
            case ORACLE:
                return "oracle";
            default:
                //mysql
                return "mysql";
        }
    }
}
