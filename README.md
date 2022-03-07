### storageExperiments

#### How to use metaTest

1. package metaTest.jar
    * ```
      ./gradlew metaTest:bootJar
      ```
2. Install `Fio` tool on your linux machine
    * ```
      dnf -y install fio
      ```
3. run jar
    * Configure test parameters. `DEPTH` is the depth of directory traversal, `NUM` is the number of tests
        + ```
          export DEPTH=10
          export NUM=10
          ```
    * ```
      java -jar metaTest.jar --depth=$DEPTH --number=$NUM 
      ```
      
#### How to use microTest

1. package micro.jar
    * ```
      ./gradlew micro:bootJar
      ```
2. run jar
    * Configure test parameters. `OPERATION` is the depth of directory traversal, `FILENAME` is the number of tests, `DEPTH` is the number of tests, `BATCH_SIZE` is the number of tests, `NUM_JOBS` is the number of tests, `RUNTIME` is the number of tests
        + ```
          export OPERATION='sequential read'
          export FILENAME='micro_test'
          export DEPTH=32
          export BATCH_SIZE=8k
          export NUM_JOBS=8
          export RUNTIME=60
          ```
    * ```
      java -jar metaTest.jar --operation=$OPERATION --filename=$FILENAME --depth=$DEPTH --batch.size=$BATCH_SIZE --num.jobs=$NUM_JOBS --runtime-$RUNTIME
      ```
      
#### How to use tpc-h

1. package tpc-h.jar
    * ```
      ./gradlew tpc-h:bootJar
      ```
2. install tpc-h tools
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
    * You should make sure you have `gcc` installed on your machine. You can install gcc on `centos8` with the following command.
        + ```
          dnf -y install gcc
          ```
    * compile
        + ```
          make
          ```
3. run tpc-h.jar
    * move `tpc-h.jar` to `dbgen` directory
    * configure test parameters.
        + ```
          SPRING_DATASOURCE_URL='jdbc:mysql://192.168.123.44:3306/'
          PRING_DATASOURCE_USERNAME=root
          SPRING_DATASOURCE_PASSWORD=9ndYiNnlmG
          SPRING_DATASOURCE_DRIVER='com.mysql.jdbc.Driver'
          data.base.size=1
          SQL="select l_returnflag, l_linestatus, sum(l_quantity) as sum_qty, sum(l_extendedprice) as sum_base_price, sum(l_extendedprice * (1 - l_discount)) as sum_disc_price, sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge, avg(l_quantity) as avg_qty, avg(l_extendedprice) as avg_price, avg(l_discount) as avg_disc, count(*) as count_order from lineitem where l_shipdate <= date'1998-12-01' - interval '90' day
          group by l_returnflag, l_linestatus order by l_returnflag, l_linestatus;"
          ```
        + ```
          java -jar tpc-h.jar \ 
              --spring.datasource.url=$SPRING_DATASOURCE_URL \ 
              --spring.datasource.username=$SPRING_DATASOURCE_USERNAME \ 
              --spring.datasource.password=$SPRING_DATASOURCE_PASSWORD \
              --spring.datasource.driver-class-name=$SPRING_DATASOURCE_DRIVER \
              --data.base.size=$DATA_BASE_SIZE \
              --sql=$SQL
          ```
          
#### Execute the above tests in batches

1. move `./batchScript/script.py` to `dbgen` directory
2. create a config.yaml like `./batchScript/config.yaml` in `dbgen` directory
3. You should make sure you have `python3` installed on your machine. You can install python3 on `centos8` with the following command.
    * ```
      dnf -y install python3
      ```
3. run
    * ```
      python3 script.py config.yaml
      ```