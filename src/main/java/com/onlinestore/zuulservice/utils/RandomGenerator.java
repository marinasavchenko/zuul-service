package com.onlinestore.zuulservice.utils;

import java.util.Random;

public class RandomGenerator {

	public int getRandomInt() {
		Random random = new Random();
		int randomTen = random.nextInt((10 - 1) + 1) + 1;
		return randomTen;
	}
}
