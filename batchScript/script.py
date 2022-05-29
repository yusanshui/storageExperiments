import configparser
import sys
import yaml
import os
import time
from yaml.loader import SafeLoader

def main(address):
    with open(address, 'r') as f:
        config = yaml.load(f, Loader=SafeLoader)

    environment = config.get('environment')
    spring_datasource_log_url = config.get('spring.datasource.url')
    spring_datasource_log_username = config.get('spring.datasource.username')
    spring_datasource_log_password = config.get('spring.datasource.password')
    spring_datasource_log_driver_class_name = config.get('spring.datasource.driver-class-name')

    meta_depth_list = config.get('meta.depth.list')
    meta_number_list = config.get('meta.num')
    for depth in meta_depth_list:
        for num in meta_number_list:
            cmd = 'java -jar metaTest.jar --environment=' + environment \
                  + ' --spring.datasource.url=' + spring_datasource_log_url \
                  + ' --spring.datasource.username=' + spring_datasource_log_username \
                  + ' --spring.datasource.password=' + str(spring_datasource_log_password) \
                  + ' --spring.datasource.driver-class-name=' + spring_datasource_log_driver_class_name \
                  + ' --number=' + str(num) \
                  + ' --depth=' + str(depth)
            print(cmd)
            os.system(cmd)

    micro_test_file_name = 'micro_test' + str(time.time())
    with open(micro_test_file_name, 'a'):  # Create file if does not exist
        pass
    micro_test_ops_list = ['sequential_read', 'sequential_write', 'random_read', 'random_write']
    micro_test_depth_list = config.get('micro.test.depth.list')
    micro_test_batch_size_list = config.get('micro.test.batch.size.list')
    micro_test_file_size_list = config.get('micro.test.file.size.list')
    micro_test_job_nums = config.get('micro.test.job.nums')
    micro_test_time = config.get('micro.test.time')

    for ops in micro_test_ops_list:
        for depth in micro_test_depth_list:
            for bs in micro_test_batch_size_list:
                for fs in micro_test_file_size_list:
                    cmd = 'java -jar micro.jar --environment=' + environment \
                          + ' --spring.datasource.url=' + spring_datasource_log_url \
                          + ' --spring.datasource.username=' + spring_datasource_log_username \
                          + ' --spring.datasource.password=' + str(spring_datasource_log_password) \
                          + ' --spring.datasource.driver-class-name=' + spring_datasource_log_driver_class_name \
                          + ' --operation=' + ops \
                          + ' --filename=micro/' + micro_test_file_name \
                          + ' --depth=' + str(depth) \
                          + ' --batch.size=' + str(bs) \
                          + ' --file.size=' + str(fs) \
                          + ' --num.jobs=' + str(micro_test_job_nums) \
                          + ' --runtime=' + str(micro_test_time)
                    print(cmd)
                    os.system(cmd)
    os.remove(micro_test_file_name)

    tpch_test_spring_datasource_url = config.get('spring.datasource.tpchtest.jdbc-url')
    tpch_test_spring_datasource_username = config.get('spring.datasource.tpchtest.username')
    tpch_test_spring_datasource_password = config.get('spring.datasource.tpchtest.password')
    tpch_test_spring_driver_class_name = config.get('spring.datasource.tpchtest.driver-class-name')
    tpch_test_num = config.get('tpch.test.num')
    tpch_test_database_size_list = config.get('tpch.test.database.size.list')
    tpch_test_sql_list = config.get('tpch.test.sql.list')

    for sql in tpch_test_sql_list:
        for databasesize in tpch_test_database_size_list:
            for i in range(tpch_test_num):
                for root, dirs, files in os.walk('./'):
                    for name in files:
                        if(name.endswith(".tbl")):
                            os.remove(os.path.join(root, name))
                cmd = 'java -jar tpc-h.jar --environment=' + str(environment) \
                      + ' --spring.datasource.log.jdbc-url=' + str(spring_datasource_log_url) \
                      + ' --spring.datasource.log.username=' + str(spring_datasource_log_username) \
                      + ' --spring.datasource.log.password=' + str(spring_datasource_log_password) \
                      + ' --spring.datasource.log.driver-class-name=' + str(spring_datasource_log_driver_class_name) \
                      + ' --spring.datasource.tpchtest.jdbc-url=' + str(tpch_test_spring_datasource_url) \
                      + ' --spring.datasource.tpchtest.username=' + str(tpch_test_spring_datasource_username) \
                      + ' --spring.datasource.tpchtest.password=' + str(tpch_test_spring_datasource_password) \
                      + ' --spring.datasource.tpchtest.driver-class-name=' + str(tpch_test_spring_driver_class_name) \
                      + ' --data.base.size=' + str(databasesize) \
                      + ' --sql=\"' + str(sql) + '\"'
                print(cmd)
                os.system(cmd)

if __name__ == "__main__":
    main(sys.argv[1])