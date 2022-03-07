import configparser
import sys
import yaml
import os
import time
from yaml.loader import SafeLoader

def main(address):
    with open(address, 'r') as f:
        config = yaml.load(f, Loader=SafeLoader)

    meta_depth_list = config.get('meta.depth.list')
    meta_number_list = config.get('meta.num')
    for depth in meta_depth_list:
        for num in meta_number_list:
            cmd = 'java -jar metaTest.jar --number=' + str(num) + ' --depth=' + str(depth)
            print(cmd)

    micro_test_file_name = 'micro_test' + str(time.time())
    with open(micro_test_file_name, 'a'):  # Create file if does not exist
        pass
    micro_test_ops_list = ['sequential read', 'sequential write', 'random read', 'random write']
    micro_test_depth_list = config.get('micro.test.depth.list')
    micro_test_batch_size_list = config.get('micro.test.batch.size.list')
    micro_test_job_nums = config.get('micro.test.job.nums')
    micro_test_time = config.get('micro.test.time')

    for ops in micro_test_ops_list:
        for depth in micro_test_depth_list:
            for bs in micro_test_batch_size_list:
                cmd = 'java -jar microtest.jar --operation=' + ops \
                      + ' --filename=' + micro_test_file_name \
                      + ' --depth=' + str(depth) \
                      + ' --batch.size=' + bs \
                      + ' --num.jobs=' + str(micro_test_job_nums) \
                      + ' --runtime=' + str(micro_test_time)
                print(cmd)
    os.remove(micro_test_file_name)

    tpch_test_spring_datasource_url = config.get('spring.datasource.url')
    tpch_test_spring_datasource_username = config.get('spring.datasource.username')
    tpch_test_spring_datasource_password = config.get('spring.datasource.password')
    tpch_test_spring_driver_class_name = config.get('spring.datasource.driver-class-name')
    tpch_test_num = config.get('tpch.test.num')
    tpch_test_database_size_list = config.get('tpch.test.database.size.list')
    tpch_test_sql_list = config.get('tpch.test.sql.list')

    for sql in tpch_test_sql_list:
        for databasesize in tpch_test_database_size_list:
            for i in range(tpch_test_num):
                cmd = 'java -jar tpc-h.jar --spring.datasource.url=' + tpch_test_spring_datasource_url \
                      + ' --spring.datasource.username=' + tpch_test_spring_datasource_username \
                      + ' --spring.datasource.password=' + str(tpch_test_spring_datasource_password) \
                      + ' --spring.datasource.driver-class-name=' + tpch_test_spring_driver_class_name \
                      + ' --data.base.size=' + str(databasesize) \
                      + ' --sql=' + sql
                print(cmd)

if __name__ == "__main__":
    main(sys.argv[1])