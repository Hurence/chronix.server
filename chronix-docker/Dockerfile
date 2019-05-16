FROM solr:8
LABEL maintainer="thomas.bailet@hurence.com"
COPY  --chown=solr:0 ./chronix/conf /opt/solr-8.0.0/server/solr/configsets/chronix/conf
COPY  --chown=solr:0 ./chronix/lib /var/solr/data/lib