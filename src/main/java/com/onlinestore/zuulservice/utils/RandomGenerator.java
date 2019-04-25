package com.onlinestore.zuulservice.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomGenerator {

	public int getRandomInt() {
		Random random = new Random();
		int randomTen = random.nextInt((10 - 1) + 1) + 1;
		return randomTen;
	}
}
