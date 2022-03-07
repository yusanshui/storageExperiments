### storageExperiments

#### How to use metaTest

1. Package metaTest.jar
    * ```
      ./gradlew metaTest:bootJar
      ```
2. Install `Fio` tool on your linux machine
    * ```
      dnf -y install fio
      ```
3. Run jar
    * Configure test parameters. `DEPTH` is the depth of directory traversal, `NUM` is the number of tests
        + ```
          export DEPTH=10
          export NUM=10
          ```
    * ```
      java -jar metaTest.jar --depth=$DEPTH --number=$NUM 
      ```
      
#### How to use microTest

1. Package micro.jar
    * ```
      ./gradlew micro:bootJar
      ```
2. Run jar
    * Configure test parameters. `OPERATION` is the depth of directory traversal, `FILENAME` is the number of tests, `DEPTH` is the number of tests, `BATCH_SIZE` is the number of tests, `NUM_JOBS` is the number of tests, `RUNTIME` is the number of tests
        + ```
          export OPERATION='sequential_read'
          export FILENAME='micro_test'
          export DEPTH=32
          export BATCH_SIZE=8k
          export NUM_JOBS=8
          export RUNTIME=60
          ```
    * ```
      java -jar micro.jar --operation=$OPERATION --filename=$FILENAME --depth=$DEPTH --batch.size=$BATCH_SIZE --num.jobs=$NUM_JOBS --runtime=$RUNTIME
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
3. Run tpc-h.jar
    * Move `tpc-h.jar` to `dbgen` directory
    * ```
      rm -f *.tbl
      ```
    * Configure test parameters.You should replace with your parameters.
        + ```
          export SPRING_DATASOURCE_URL='jdbc:mysql://192.168.123.44:3306/'
          export SPRING_DATASOURCE_USERNAME=root
          export SPRING_DATASOURCE_PASSWORD=9ndYiNnlmG
          export SPRING_DATASOURCE_DRIVER='com.mysql.jdbc.Driver'
          export DATA_BASE_SIZE=1
          ```
        + ```
          java -jar tpc-h.jar --spring.datasource.url=$SPRING_DATASOURCE_URL --spring.datasource.username=$SPRING_DATASOURCE_USERNAME --spring.datasource.password=$SPRING_DATASOURCE_PASSWORD --spring.datasource.driver-class-name=$SPRING_DATASOURCE_DRIVER --workspace=$WORKSPACE --data.base.size=$DATA_BASE_SIZE --sql="select l_returnflag, l_linestatus, sum(l_quantity) as sum_qty, sum(l_extendedprice) as sum_base_price, sum(l_extendedprice * (1 - l_discount)) as sum_disc_price, sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge, avg(l_quantity) as avg_qty, avg(l_extendedprice) as avg_price, avg(l_discount) as avg_disc, count(*) as count_order from lineitem where l_shipdate <= date'1998-12-01' - interval '90' day group by l_returnflag, l_linestatus order by l_returnflag, l_linestatus;"
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