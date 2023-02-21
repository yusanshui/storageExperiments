From centos:centos8.4.2105

RUN sed -i 's/mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-*
RUN sed -i 's|#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-*
RUN dnf -y install make gcc python3 java-1.8.0-openjdk.x86_64 fio
RUN mkdir -p /opt/tpch
COPY tpch/ /opt/tpch/
ARG TPCH_VERSION=TPC-H_Tools_v3.0.0
RUN cd /opt/tpch/$TPCH_VERSION/dbgen && make
RUN pip3 install --index https://pypi.tuna.tsinghua.edu.cn/simple  pyyaml

COPY metaTest/build/libs/metaTest.jar /opt/tpch/$TPCH_VERSION/dbgen/
COPY micro/build/libs/micro.jar /opt/tpch/$TPCH_VERSION/dbgen/
COPY tpc-h/build/libs/tpc-h.jar /opt/tpch/$TPCH_VERSION/dbgen/
COPY batchScript/script.py /opt/tpch/$TPCH_VERSION/dbgen/
#COPY batchScript/config.yaml /opt/tpch/$TPCH_VERSION/dbgen/tpchconfig

ENV TZ=Asia/Shanghai

WORKDIR /opt/tpch/$TPCH_VERSION/dbgen

CMD ["python3", "script.py", "/tpchconfig/config.yaml"]
#CMD ["sleep", "1d"]
