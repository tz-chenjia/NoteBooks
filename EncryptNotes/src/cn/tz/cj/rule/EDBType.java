package cn.tz.cj.rule;

public enum EDBType {

    MYSQL("mysql", "com.mysql.jdbc.Driver", 3306), SQLSERVER("sqlserver", "net.sourceforge.jtds.jdbc.Driver", 1433),
    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", 1521);

    private String type;

    private String driverClass;

    private int port;

    private EDBType(String type, String driverClass, int port) {
        this.port = port;
        this.type = type;
        this.driverClass = driverClass;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public int getPort(){
        return port;
    }

    public static EDBType toEDBType(String type) {
        switch (type) {
            case "sqlserver":
                return SQLSERVER;
            case "oracle":
                return ORACLE;
            default:
                //mysql
                return MYSQL;
        }
    }

    public String getType() {
        return type;
    }
}
