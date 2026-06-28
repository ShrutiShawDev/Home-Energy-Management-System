package com.shruti.homeenergy.userservice;

import com.shruti.homeenergy.userservice.entity.User;
import com.shruti.homeenergy.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UserServiceApplicationTests {
	public static final int NUMBER_OF_USERS = 10;
	@Autowired
	private UserRepository userRepository;
	@Test
	void contextLoads() {
	}

	@Disabled
	@Test
	void addUsersToDB(){
		for(int i =1; i<= NUMBER_OF_USERS; i++){
			var user = User.builder()
					.name("User"+i)
					.surname("Surname"+i)
					.email("user"+i+"@example.com")
					.address(i+" Example st")
					.alerting(i%2==0)
					.energyAlertingThreshold(100.0+i)
					.build();
			userRepository.save(user);
		}

		log.info("User Repository populated successfully");
	}

}
