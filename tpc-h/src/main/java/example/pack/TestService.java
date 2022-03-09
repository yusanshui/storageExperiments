package example.pack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestService implements CommandLineRunner {
    @Autowired
    @Qualifier("tpchtestJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("logJdbcTemplate")
    private JdbcTemplate logJdbcTemplate;

    @Value("${data.base.size}")
    int value;

    @Value("${sql}")
    String sql;

    @Value("${environment}")
    String environment;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        String command = "./dbgen -s " + value;
        System.out.println(command);
        System.out.println("start to creat data......");
        Process exec = Runtime.getRuntime().exec(command);
        exec.waitFor();
        if(exec.exitValue() != 0){
            throw new Exception("failed to create data exitValue " + exec.exitValue());
        }
        System.out.println("successfully to created data");
        //create database
        String dataBase = "tpch_" + System.currentTimeMillis();
        System.out.println("start to create database ");
        jdbcTemplate.execute("create database " + dataBase);
        System.out.println("successfully create database " + dataBase);
        jdbcTemplate.execute("use " + dataBase + ";");
        //create table
        System.out.println("start to create tables......");
        jdbcTemplate.execute("create table nation (n_nationkey integer not null,name char(25) not null, n_regionkey integer not null, n_comment varchar(152));");
        jdbcTemplate.execute("create table region (r_regionkey integer not null,r_name char(25) not null, r_comment varchar(152));");
        jdbcTemplate.execute("create table part (p_partkey integer not null, p_name varchar(55) not null,p_mfgr char(25) not null, p_brand char(10) not null,p_type varchar(25) not null, p_size integer not null, p_container char(10) not null, p_retailprice decimal(15,2) not null, p_comment varchar(23) not null)");
        jdbcTemplate.execute("create table supplier (s_suppkey integer not null, s_name char(25) not null, s_address varchar(40) not null, s_nationkey integer not null, s_phone char(15) not null, s_acctbal decimal(15,2) not null, s_comment varchar(101) not null);");
        jdbcTemplate.execute("create table partsupp (ps_partkey integer not null, ps_suppkey integer not null, ps_availqty integer not null, ps_supplycost decimal(15,2) not null, ps_comment varchar(199) not null);");
        jdbcTemplate.execute("create table customer (c_custkey integer not null, c_name varchar(25) not null, c_address varchar(40) not null, c_nationkey integer not null, c_phone char(15) not null, c_acctbal decimal(15,2) not null, c_mktsegment char(10) not null,c_comment varchar(117) not null);");
        jdbcTemplate.execute("create table orders (o_orderkey integer not null, o_custkey integer not null, o_orderstatus char(1) not null, o_totalprice decimal(15,2) not null, o_orderdate date not null, o_orderpriority char(15) not null, o_clerk char(15) not null, o_shippriority integer not null, o_comment varchar(79) not null);");
        jdbcTemplate.execute("create table lineitem (l_orderkey integer not null, l_partkey integer not null, l_suppkey integer not null, l_linenumber integer not null, l_quantity decimal(15,2) not null, l_extendedprice decimal(15,2) not null, l_discount decimal(15,2) not null, l_tax decimal(15,2) not null, l_returnflag char(1) not null, l_linestatus char(1) not null,l_shipdate date not null, l_commitdate date not null, l_receiptdate date not null, l_shipinstruct char(25) not null, l_shipmode char(10) not null, l_comment varchar(44) not null);");
        //set foreign key
        jdbcTemplate.execute("alter table region add primary key (r_regionkey);");
        jdbcTemplate.execute("alter table nation add primary key (n_nationkey);");
        jdbcTemplate.execute("alter table nation add foreign key nation_fk1 (n_regionkey) references region(r_regionkey);");
        jdbcTemplate.execute("alter table part add primary key (p_partkey);");
        jdbcTemplate.execute("alter table supplier add primary key (s_suppkey);");
        jdbcTemplate.execute("alter table supplier add foreign key supplier_fk1 (s_nationkey) references nation(n_nationkey);");
        jdbcTemplate.execute("alter table partsupp add primary key (ps_partkey,ps_suppkey);");
        jdbcTemplate.execute("alter table customer add primary key (c_custkey);");
        jdbcTemplate.execute("alter table customer add foreign key customer_fk1 (c_nationkey) references nation(n_nationkey);");
        jdbcTemplate.execute("alter table lineitem add primary key (l_orderkey,l_linenumber);");
        jdbcTemplate.execute("alter table orders add primary key (o_orderkey);");
        jdbcTemplate.execute("alter table partsupp add foreign key partsupp_fk1 (ps_suppkey) references supplier(s_suppkey);");
        jdbcTemplate.execute("alter table partsupp add foreign key partsupp_fk2 (ps_partkey) references part(p_partkey);");
        jdbcTemplate.execute("alter table orders add foreign key orders_fk1 (o_custkey) references customer(c_custkey);");
        jdbcTemplate.execute("alter table lineitem add foreign key lineitem_fk1 (l_orderkey) references orders(o_orderkey);");
        jdbcTemplate.execute("alter table lineitem add foreign key lineitem_fk2 (l_partkey,l_suppkey) references partsupp(ps_partkey,ps_suppkey);");
        System.out.println("successfully created tables");
        //load data
        System.out.println("start to load data......");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0;");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" + "customer.tbl' INTO TABLE customer FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" + "lineitem.tbl' INTO TABLE lineitem FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" + "nation.tbl' INTO TABLE nation FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" +  "orders.tbl' INTO TABLE orders FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" + "partsupp.tbl' INTO TABLE partsupp FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" + "part.tbl' INTO TABLE part FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" +  "region.tbl' INTO TABLE region FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("LOAD DATA LOCAL INFILE '" +  "supplier.tbl' INTO TABLE supplier FIELDS TERMINATED BY '|' LINES TERMINATED BY '|\\n';");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1;");
        System.out.println("successfully load data");
        System.out.println("start to test.......");
        long start=System.currentTimeMillis();
        jdbcTemplate.execute(sql);
        long end = System.currentTimeMillis();
        System.out.println("sql execution time: " + (end - start) + "ms");
        //delete database
        jdbcTemplate.execute("use test;");
        jdbcTemplate.execute("drop database " + dataBase);

        sql = sql.replace("'", "''");
        logJdbcTemplate.execute("insert into tpchtest values('" + sql + "', '" + environment + "', " + (end - start) + ", now());");
        System.out.println("successfully drop database " + dataBase);
        context.close();
    }
}
