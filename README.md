### storageExperiments

#### Prepare
* You should prepare a database to store your test data.
* SPRING_DATASOURCE_XXXX is your datasource.

#### How to use metaTest

1. Package metaTest.jar
    * ```
      ./gradlew metaTest:bootJar
      ```
2. Install `Fio` tool on your linux machine
    * ```
      dnf -y install fio
      ```
3. Create `metatest` table in your database
    * ```sql
      create table metatest(number int, depth int, environment varchar(255), spent int, time datetime);
      ```
4. Run jar
    * Configure test parameters especially your database url etc. `ENVIRONMENT` is used to distinguish different test environments, `DEPTH` is the depth of directory traversal, `NUM` is the number of tests
        + ```
          export ENVIRONMENT=LVM
          export SPRING_DATASOURCE_URL='jdbc:mysql://10.0.2.15:3306/test'
          export SPRING_DATASOURCE_USERNAME=root
          export SPRING_DATASOURCE_PASSWORD=SEjkRknUmE
          export SPRING_DATASOURCE_DRIVER='com.mysql.jdbc.Driver'
          export DEPTH=10
          export NUM=10
          ```
    * ```
      java -jar metaTest.jar --environment=$ENVIRONMENT --spring.datasource.url=$SPRING_DATASOURCE_URL --spring.datasource.username=$SPRING_DATASOURCE_USERNAME --spring.datasource.password=$SPRING_DATASOURCE_PASSWORD --spring.datasource.driver-class-name=$SPRING_DATASOURCE_DRIVER  --depth=$DEPTH --number=$NUM 
      ```
      
#### How to use microTest

1. Package micro.jar
    * ```
      ./gradlew micro:bootJar
      ```
2. Create `microtest` table in your database
    * ```sql
      create table microtest(operation varchar(255), environment varchar(255), iops int, bw int, filename varchar(255), depth int, batchsize int, filesize int, numjobs int, spent int, time datetime);
      ```
3. Run jar
    * Configure test parameters. `OPERATION` is the operation that need to be tested, `ENVIRONMENT` is used to distinguish different test environments, `FILENAME` is the name of test file, `DEPTH` is the depth of directory traversal, `BATCH_SIZE` is IO size, `FILE_SIZE` is the size of test file, `NUM_JOBS` is the number of threads, `RUNTIME` is 
time spent testing.
        + ```
          export ENVIRONMENT=LVM
          export SPRING_DATASOURCE_URL='jdbc:mysql://10.0.2.15:3306/test'
          export SPRING_DATASOURCE_USERNAME=root
          export SPRING_DATASOURCE_PASSWORD=SEjkRknUmE
          export SPRING_DATASOURCE_DRIVER='com.mysql.jdbc.Driver'
          export OPERATION='sequential_read'
          export FILENAME='micro_test'
          export DEPTH=32
          export BATCH_SIZE=8
          export FILE_SIZE=10
          export NUM_JOBS=8
          export RUNTIME=60
          ```
    * ```
      touch $FILENAME
      ```
    * ```
      java -jar micro.jar --environment=$ENVIRONMENT --spring.datasource.url=$SPRING_DATASOURCE_URL --spring.datasource.username=$SPRING_DATASOURCE_USERNAME --spring.datasource.password=$SPRING_DATASOURCE_PASSWORD --spring.datasource.driver-class-name=$SPRING_DATASOURCE_DRIVER  --operation=$OPERATION --filename=$FILENAME --depth=$DEPTH --batch.size=$BATCH_SIZE --file.size=$FILE_SIZE --num.jobs=$NUM_JOBS --runtime=$RUNTIME
      ```
    * ```
      rm -f $FILENAME
      ```
      
#### How to use tpc-h

1. Package tpc-h.jar
    * ```
      ./gradlew tpc-h:bootJar
      ```
2. Install tpc-h tools
    * [download](https://www.tpc.org/tpch/)
    * Unzip the file to the tpc-h folder, enter the dbgen directory, and modify the makefile
        + ```
          cp makefile.suite makerfile
          vi makefile
          ```
        + ```
          CC      = gcc
          # Current values for DATABASE are: INFORMIX, DB2, TDAT (Teradata)
          #                                  SQLSERVER, SYBASE, ORACLE, VECTORWISE
          # Current values for MACHINE are:  ATT, DOS, HP, IBM, ICL, MVS,
          #                                  SGI, SUN, U2200, VMS, LINUX, WIN32
          # Current values for WORKLOAD are:  TPCH
          DATABASE = MYSQL
          MACHINE  = LINUX
          WORKLOAD = TPCH
          #
          ```
    * Modify tpcd.h, add at the top of the file
        + ```
          #ifdef MYSQL
          #define GEN_QUERY_PLAN ""
          #define START_TRAN "START TRANSACTION"
          #define END_TRAN "COMMIT"
          #define SET_OUTPUT ""
          #define SET_ROWCOUNT "limit %d;\n"
          #define SET_DBASE "use %s;\n"
          #endif
          ```
    * You should make sure you have `gcc` and `make` installed on your machine. You can install gcc and make on `centos8` with the following command.
        + ```
          dnf -y install gcc make
          ```
    * Compile
        + ```
          make
          ```
2. Create `tpchtest` table in your database
    * ```sql
      create table tpchtest(sqlcmd varchar(500), environment varchar(255), spent int, time datetime);
      ```
3. Run tpc-h.jar
    * Move `tpc-h.jar` to `dbgen` directory
    * ```
      rm -f *.tbl
      ```
    * Configure test parameters. `ENVIRONMENT` is used to distinguish different test environments. You should replace with your parameters.
        + ```
          export ENVIRONMENT=LVM
          export SPRING_DATASOURCE_URL='jdbc:mysql://10.0.2.15:3306/test'
          export SPRING_DATASOURCE_USERNAME=root
          export SPRING_DATASOURCE_PASSWORD=SEjkRknUmE
          export SPRING_DATASOURCE_DRIVER='com.mysql.jdbc.Driver'
          export SPRING_DATASOURCE_TPCH_URL='jdbc:mysql://10.0.2.15:3306/'
          export SPRING_DATASOURCE_TPCH_USERNAME=root
          export SPRING_DATASOURCE_TPCH_PASSWORD=SEjkRknUmE
          export SPRING_DATASOURCE_TPCH_DRIVER='com.mysql.jdbc.Driver'
          export DATA_BASE_SIZE=1
          ```
        + ```
          java -jar tpc-h.jar --environment=$ENVIRONMENT --spring.datasource.log.jdbc-url=$SPRING_DATASOURCE_URL --spring.datasource.log.username=$SPRING_DATASOURCE_USERNAME --spring.datasource.log.password=$SPRING_DATASOURCE_PASSWORD --spring.datasource.log.driver-class-name=$SPRING_DATASOURCE_DRIVER --spring.datasource.tpchtest.jdbc-url=$SPRING_DATASOURCE_TPCH_URL --spring.datasource.tpchtest.username=$SPRING_DATASOURCE_TPCH_USERNAME --spring.datasource.tpchtest.password=$SPRING_DATASOURCE_TPCH_PASSWORD --spring.datasource.tpchtest.driver-class-name=$SPRING_DATASOURCE_TPCH_DRIVER --data.base.size=$DATA_BASE_SIZE --sql="select l_returnflag, l_linestatus, sum(l_quantity) as sum_qty, sum(l_extendedprice) as sum_base_price, sum(l_extendedprice * (1 - l_discount)) as sum_disc_price, sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge, avg(l_quantity) as avg_qty, avg(l_extendedprice) as avg_price, avg(l_discount) as avg_disc, count(*) as count_order from lineitem where l_shipdate <= date'1998-12-01' - interval '90' day group by l_returnflag, l_linestatus order by l_returnflag, l_linestatus;"
          ```
        + You can use your own sql after `--sql=`
          
#### Execute the above tests in batches

1. You should follow `install tpc-h tools` to install `dpgen`
2. Move `./batchScript/script.py` to `dbgen` directory
3. Package jar and move `jar packages` to `dbgen` directory
    * ```
      gradlew bootJar
      ```
4. Create a config.yaml like `./batchScript/config.yaml` in `dbgen` directory
5. You should make sure you have `python3` installed on your machine. You can install python3 on `centos8` with the following command.
    * ```
      dnf -y install python3
      ```
6. Run
    * ```
      python3 script.py config.yaml
      ```