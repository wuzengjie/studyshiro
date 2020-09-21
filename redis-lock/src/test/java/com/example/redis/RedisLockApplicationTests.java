package com.example.redis;

import com.example.redis.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RedisLockApplicationTests {

	@Autowired
	private RedisUtil redisUtil;

	@Test
	void contextLoads() {
		List<String> stringList=new ArrayList<>();
		stringList.add("张三");
		stringList.add("李四");
		//System.out.println(redisUtil.setList("myList",stringList));
		System.out.println("-------------------------");
		System.out.println(redisUtil.getFirstVal("myList"));
		System.out.println(redisUtil.popLeftVal("myList"));
	}

}
