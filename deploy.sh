docker pull registry.cn-shanghai.aliyuncs.com/v_swarm/fast:1.0.0-SNAPSHOT

docker login registry.cn-shanghai.aliyuncs.com
18674020827
docker@2020

------快加速----------
docker run -d \
-p 9077:9077 \
--restart=always \
--network=v_overlay \
--log-driver="none" \
-v /docker/config/hb:/config \
-v /docker/logs/hb:/logs \
-v /docker/data/hb:/data \
--name=hb \
--ip 10.10.1.66 \
-e "JAVA_OPTS=-Xms512m -Xmx512m" \
-e "spring.profiles.active=hb" \
-e "plat.redis.suffix=" \
--init \
registry.cn-shanghai.aliyuncs.com/hb_swarm/hb-service:1.0.0-SNAPSHOT


docker run -d -p 9077:9077 --restart=always --network=v_overlay --log-driver="none" -v /docker/config/hb:/config -v /docker/logs/hb:/logs -v /docker/data/hb:/data --name=hb --ip 10.10.1.66 -e "JAVA_OPTS=-Xms1g -Xmx1g" -e "spring.profiles.active=hb" -e "plat.redis.suffix=" --init registry.cn-shanghai.aliyuncs.com/hb_swarm/hb-service:1.0.0-SNAPSHOT

