import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * 测试redis缓存集群版
 */
public class TestJedis {

    @Test
    public void testJedisPool() throws InterruptedException {
        //使用JedisClient对象，需要一个Set<HostAndPort>参数
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.26.130",7001));
        nodes.add(new HostAndPort("192.168.26.130",7002));
        nodes.add(new HostAndPort("192.168.26.130",7003));
        nodes.add(new HostAndPort("192.168.26.130",7004));
        nodes.add(new HostAndPort("192.168.26.130",7005));
        nodes.add(new HostAndPort("192.168.26.130",7006));
        JedisCluster jedisCluster = new JedisCluster(nodes);
        //直接使用jedisCluster对象操作redis集群，在系统中单例存在
        jedisCluster.set("test","100");
        jedisCluster.hset("testHash","keys","200");
        System.out.println(jedisCluster.get("test"));
        System.out.println(jedisCluster.expire("test",1));
        Thread.sleep(70000);
        System.out.println("删除test之后： "+jedisCluster.get("test"));
        System.out.println(jedisCluster.hget("testHash","keys"));
        jedisCluster.expire("testHash",10);

        System.out.println(jedisCluster.hget("testHash","keys"));
        //系统关闭前，关闭jedisCluster对象
        jedisCluster.close();
    }
}
